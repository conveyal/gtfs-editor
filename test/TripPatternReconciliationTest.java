import org.junit.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.sort;
import play.test.*;
import models.*;
import models.transit.Agency;
import models.transit.Route;
import models.transit.RouteType;
import models.transit.Stop;
import models.transit.StopTime;
import models.transit.Trip;
import models.transit.TripPattern;
import models.transit.TripPattern.PatternStopSequenceComparator;
import models.transit.TripPattern.StopTimeSequenceComparator;
import models.transit.TripPatternStop;

/**
 * Test that reconciling trip patterns does the right thing with stop times, etc.
 * @author mattwigway
 *
 */
public class TripPatternReconciliationTest extends UnitTest {
    private Agency agency;
    private RouteType routeType;
    
    @Before
    public void setUp () {
        // Technically, this is the wrong thing to do, because it also zaps the unmanaged spatial_ref_sys table and
        // postgis views. But deleteAllModels() has a bug with sequences, and this unit test isn't using any
        // PostGIS functions.
        Fixtures.deleteDatabase();
        agency = new Agency("agency", "agency", "http://www.example.com", "America/New_York", "en", "5551234567");
        agency.save();
        routeType = new RouteType();
        routeType.save();
    }
    
    /** Make some dummy stops */
    private Stop[] makeStops () {        
        Stop[] ret = new Stop[20];
        
        // make some stops
        for (int i = 0; i < 20; i++) {
            // proceeding east along North Ave west of Wicker Park, Chicago, Ill.
            ret[i] = new Stop(agency, "stop_" + i, null, null, null, 41.9100, -87.713 + i * 0.001);
            ret[i].save();
        }
        
        return ret;
    }
    
    /** Make a dummy pattern */
    private TripPattern makePattern (Stop[] stops) {
        TripPattern tp = new TripPattern();
        
        List<TripPatternStop> pss = new ArrayList<TripPatternStop>(8);
        
        // build up some pattern stops
        // stop at every other stop
        int j = 0;
        for (int i = 0; i < 15; i += 2) {
            TripPatternStop ps = new TripPatternStop(tp, stops[i], j++, 120);
            pss.add(ps);
        }
        
        tp.patternStops = pss;
        tp.save();
        
        return tp;
    }
    
    /** Create some stop times for the trip pattern */
    private void createStopTimesForTripPattern (TripPattern tp) {
        Route route = new Route("1", "One", routeType, null, agency);
        route.save();

        // 6:00 am
        int currentTime = 6 * 60 * 60;

        for (int i = 0; i < 5; i++) {
            Trip trip = new Trip();
            trip.route = route;
            trip.pattern = tp;
            trip.save();

            for (TripPatternStop ps : tp.patternStops) {
                StopTime st = new StopTime();
                st.arrivalTime = st.departureTime = currentTime;
                st.stopSequence = ps.stopSequence;
                st.stop = ps.stop;
                st.patternStop = ps;
                st.trip = trip;
                st.save();

                currentTime += ps.defaultTravelTime;
            }
        }
    }
    
    @Test
    public void testStopAdditionInMiddle () {        
        Stop[] stops = makeStops();
        
        // build a pattern with a trip and a few stoptimes
        TripPattern tp = makePattern(stops);
        createStopTimesForTripPattern(tp);
        
        // make sure the correct number of stop times were created
        assertEquals(40, StopTime.count());
        
        // make a new pattern
        TripPattern tp2 = makePattern(stops);
        
        assertEquals(8, tp.patternStops.size());
        
        // check that it worked
        Collection<Trip> trips = Trip.find("pattern = ?", tp).fetch();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.getStopTimes();
            
            assertEquals(8, stopTimes.size());
            
            sort(stopTimes, new StopTimeSequenceComparator());
            
            assertEquals(stops[14].id, stopTimes.get(7).stop.id);
            // it was seven before
            assertEquals((Integer) 7, stopTimes.get(7).stopSequence);
        }
        
        // add a pattern stop
        TripPatternStop tps2 = new TripPatternStop(tp2, stops[5], 4, 180);
        
        // update stop sequences
        // it's ok to assume order here since we made the stops a moment ago, in order
        for (int i = 4; i < tp2.patternStops.size(); i++) {
            tp2.patternStops.get(i).stopSequence++;
        }
        
        // order shouldn't matter; test this implicitly
        tp2.patternStops.add(tps2);

        tp.reconcilePatternStops(tp2);
        
        // check that the stop was added
        // this is actually not done in reconcilePatternStops; it only updates the stop times
        //assertEquals(9, tp.patternStops.size());
        
        // only now sort the patternstops
        sort(tp2.patternStops, new PatternStopSequenceComparator());
        
        assertEquals(stops[5].id, tp2.patternStops.get(4).stop.id);
        
        // check that no stop times were added, but that stop sequences were updated
        assertEquals(40, StopTime.count());
        
        trips = Trip.find("pattern = ?", tp).fetch();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.getStopTimes();
            
            assertEquals(8, stopTimes.size());
            
            sort(stopTimes, new StopTimeSequenceComparator());
            
            // it's still the seventh stoptime, since there is no 4.
            assertEquals(stops[14].id, stopTimes.get(7).stop.id);
            // it was seven before
            assertEquals((Integer) 8, stopTimes.get(7).stopSequence);
        }
    }
    
    @Test
    public void testStopAdditionAtEnd () {        
        Stop[] stops = makeStops();
        
        // build a pattern with a trip and a few stoptimes
        TripPattern tp = makePattern(stops);
        createStopTimesForTripPattern(tp);
        
        // make sure the correct number of stop times were created
        assertEquals(40, StopTime.count());
        
        // make a new pattern
        TripPattern tp2 = makePattern(stops);
        
        assertEquals(8, tp.patternStops.size());
        
        // check that it worked
        Collection<Trip> trips = Trip.find("pattern = ?", tp).fetch();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.getStopTimes();
            
            assertEquals(8, stopTimes.size());
            
            sort(stopTimes, new StopTimeSequenceComparator());
            
            assertEquals(stops[14].id, stopTimes.get(7).stop.id);
            // it was seven before
            assertEquals((Integer) 7, stopTimes.get(7).stopSequence);
        }
        
        // add a pattern stop at the end
        // note that stop sequence is nonconsecutive
        TripPatternStop tps2 = new TripPatternStop(tp2, stops[5], 12, 180);

        tp2.patternStops.add(tps2);

        tp.reconcilePatternStops(tp2);
        
        // make sure that the stoptimes haven't changed one iota
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.getStopTimes();
            
            assertEquals(8, stopTimes.size());
            
            sort(stopTimes, new StopTimeSequenceComparator());
            
            assertEquals(stops[14].id, stopTimes.get(7).stop.id);
            // it was seven before
            assertEquals((Integer) 7, stopTimes.get(7).stopSequence);
        }
    }
}
