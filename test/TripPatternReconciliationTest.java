import org.junit.*;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import datastore.VersionedDataStore.AgencyTx;
import datastore.VersionedDataStore.GlobalTx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jersey.repackaged.com.google.common.collect.Lists;
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
import models.transit.TripPatternStop;
import static models.transit.TripPattern.reconcilePatternStops;

/**
 * Test that reconciling trip patterns does the right thing with stop times, etc.
 * 
 * When running this test, note that the Play test runner tends to time out and you have to open the test results
 * test-result/TripPatternReconciliationTest.(passed|failed).html
 * 
 * @author mattwigway
 *
 */
public class TripPatternReconciliationTest extends UnitTest {
    private Agency agency;
    private RouteType routeType;
    private AgencyTx atx;
    private GlobalTx gtx;
    
    @Before
    public void setUp () {
    	DB db = DBMaker.newTempFileDB()
    			.mmapFileEnable()
    			.makeTxMaker().makeTx();
    	
    	atx = new AgencyTx(db);
    	
    	DB global = DBMaker.newTempFileDB()
    			.mmapFileEnable()
    			.makeTxMaker().makeTx();
    	
    	gtx = new GlobalTx(global);
    	
    	agency = new Agency(null, "test", "http://www.example.com", "America/Chicago", "en", "5555555555");
    	gtx.agencies.put(agency.id, agency);
    }
    
    @After
    public void tearDown() {
    	atx.commit();
    	gtx.commit();
    }
    
    /** Make some dummy stops */
    private Stop[] makeStops () {        
        Stop[] ret = new Stop[20];
        
        // make some stops
        for (int i = 0; i < 20; i++) {
            // proceeding east along North Ave west of Wicker Park, Chicago, Ill.
            ret[i] = new Stop(agency, "stop_" + i, null, null, null, 41.9100, -87.713 + i * 0.001);
            gtx.stops.put(ret[i].id, ret[i]);
        }
        
        return ret;
    }
    
    private TripPattern makePattern (Stop[] stops) {
        return makePattern(stops, false);
    }
    
    /** Make a dummy pattern */
    private TripPattern makePattern (Stop[] stops, boolean loop) {
        TripPattern tp = new TripPattern();
        
        List<TripPatternStop> pss = new ArrayList<TripPatternStop>(8);
        
        // build up some pattern stops
        // stop at every other stop
        // (that seemed like a good idea at the time, but in retrospect I should have stopped at every stop)
        int j = 1;
        for (int i = 0; i < 15; i += 2) {
            TripPatternStop ps = new TripPatternStop(stops[i], 120);
            pss.add(ps);
        }
        
        if (loop) {
            TripPatternStop ps = new TripPatternStop(stops[0], 120);
            pss.add(ps);
        }
        
        tp.patternStops = pss;
        atx.tripPatterns.put(tp.id, tp);
        
        return tp;
    }
    
    private void createStopTimesForTripPattern (TripPattern tp) {
        createStopTimesForTripPattern(tp, false);
    }
    
    /** Create some stop times for the trip pattern */
    private void createStopTimesForTripPattern (TripPattern tp, boolean skipStop) {
        Route route = new Route("1", "One", routeType, null, agency);
        atx.routes.put(route.id, route);

        // 6:00 am
        int currentTime = 6 * 60 * 60;

        for (int i = 0; i < 5; i++) {
            Trip trip = new Trip();
            trip.routeId = route.id;
            trip.patternId = tp.id;
            // there is an index on this field so it can't be null. however no referential integrity checks are performed.
            trip.calendarId = "none";

            List<StopTime> sts = Lists.newArrayList();
            int idx = 0;
            for (TripPatternStop ps : tp.patternStops) {
                if (skipStop && idx++ == 5) { // stop 10
                    sts.add(null);
                    continue;
                }
                
                StopTime st = new StopTime();
                st.arrivalTime = st.departureTime = currentTime;
                st.stopId = ps.stopId;
                sts.add(st);
                
                currentTime += ps.defaultTravelTime;
            }
            
            trip.stopTimes = sts;
            atx.trips.put(trip.id, trip);
        }
    }
    
    @Test
    public void testStopAdditionInMiddle () {        
        Stop[] stops = makeStops();
        
        // build a pattern with a trip and a few stoptimes
        TripPattern tp = makePattern(stops);
        createStopTimesForTripPattern(tp);
        
        // make a new pattern
        TripPattern tp2 = makePattern(stops);
        
        assertEquals(8, tp.patternStops.size());
        
        // check that it worked
        Collection<Trip> trips = atx.trips.values();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
            assertEquals(8, stopTimes.size());
                        
            assertEquals(stops[14].id, stopTimes.get(7).stopId);
        }
        
        // add a pattern stop
        TripPatternStop tps2 = new TripPatternStop(stops[5], 180);
        
        tp2.patternStops.add(4, tps2);

        reconcilePatternStops(tp, tp2, atx);
        
        assertEquals(stops[5].id, tp2.patternStops.get(4).stopId);
        
        // check that no stop times were added, but that stop sequences were updated
        trips = atx.trips.values();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
            assertEquals(9, stopTimes.size());
                        
            // make sure it moved
            assertEquals(stops[14].id, stopTimes.get(8).stopId);
            
            // make sure there is a blank
            assertEquals(null, stopTimes.get(4));
        }
    }
    
    @Test
    public void testStopAdditionAtEnd () {        
        Stop[] stops = makeStops();
        
        // build a pattern with a trip and a few stoptimes
        TripPattern tp = makePattern(stops);
        createStopTimesForTripPattern(tp);
        
        // make a new pattern
        TripPattern tp2 = makePattern(stops);
        
        assertEquals(8, tp.patternStops.size());
        
        // check that it worked
        Collection<Trip> trips = atx.trips.values();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
            assertEquals(8, stopTimes.size());
            
            assertEquals(stops[14].id, stopTimes.get(7).stopId);
        }
        
        // add a pattern stop at the end
        TripPatternStop tps2 = new TripPatternStop(stops[5], 180);

        tp2.patternStops.add(tps2);

        reconcilePatternStops(tp, tp2, atx);
        
        // make sure that the stoptimes haven't changed one iota
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
            assertEquals(9, stopTimes.size());            
            assertEquals(stops[14].id, stopTimes.get(7).stopId);
            assertNull(stopTimes.get(8));
        }
    }
    
    @Test
    public void testStopRemovalInMiddle () {
       Stop[] stops = makeStops();
        
        // build a pattern with a trip and a few stoptimes
        TripPattern tp = makePattern(stops);
        createStopTimesForTripPattern(tp);
        
        // make a new pattern
        TripPattern tp2 = makePattern(stops);
        
        assertEquals(8, tp.patternStops.size());
        
        // check that it worked
        Collection<Trip> trips = atx.trips.values();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
            assertEquals(8, stopTimes.size());
                        
            assertEquals(stops[14].id, stopTimes.get(7).stopId);
        }
        
        // zap the third stop (which is stop 4)
        TripPatternStop removed = tp2.patternStops.remove(2);
        
        // make sure we got the indices right (this is more a test of the test)
        assertEquals(stops[4].id, removed.stopId);
        
        reconcilePatternStops(tp, tp2, atx);
        
        // make sure that stop times got deleted
        
        // check that the right stop times got deleted and that stops are still in order
        trips = atx.trips.values();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
            assertEquals(7, stopTimes.size());
                        
            int previousDep = 0;
            for (StopTime st : stopTimes) {
                // is this the stop that was supposed to be deleted?
                assertNotSame(stops[4].id, st.stopId);
                assertTrue(st.departureTime > previousDep);
                previousDep = st.departureTime;
            }
        }
    }
    
    @Test
    public void testStopRemovalAtEnd () {
        Stop[] stops = makeStops();
        
        // build a pattern with a trip and a few stoptimes
        TripPattern tp = makePattern(stops);
        createStopTimesForTripPattern(tp);;
        
        // make a new pattern
        TripPattern tp2 = makePattern(stops);
        
        assertEquals(8, tp.patternStops.size());
        
        // check that it worked
        Collection<Trip> trips = atx.trips.values();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
            assertEquals(8, stopTimes.size());
                        
            assertEquals(stops[14].id, stopTimes.get(7).stopId);
        }
        
        // zap the last stop (which is stop 14)
        TripPatternStop removed = tp2.patternStops.remove(7);
        
        // make sure we got the indices right (this is more a test of the test)
        assertEquals(stops[14].id, removed.stopId);
        
        reconcilePatternStops(tp, tp2, atx);
        
        
        // check that the right stop times got deleted and that sequences were repacked
        trips = atx.trips.values();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
            assertEquals(7, stopTimes.size());
                        
            int previousDep = 0;
            
            for (StopTime st : stopTimes) {
                // is this the stop that was supposed to be deleted?
                assertNotSame(stops[14].id, st.stopId);
                assertTrue(st.departureTime > previousDep);
                previousDep = st.departureTime;
            }
        }
    }
    
    @Test
    public void testStopTranspositionMoveLeft () {
       Stop[] stops = makeStops();
        
        // build a pattern with a trip and a few stoptimes
        TripPattern tp = makePattern(stops);
        createStopTimesForTripPattern(tp);
                
        // make a new pattern
        TripPattern tp2 = makePattern(stops);
        
        assertEquals(8, tp.patternStops.size());
        
        // check that it worked
        Collection<Trip> trips = atx.trips.values();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
            assertEquals(8, stopTimes.size());
                        
            assertEquals(stops[14].id, stopTimes.get(7).stopId);
        }
        
        // move one stop
        TripPatternStop toMove = tp2.patternStops.remove(4);
        tp2.patternStops.add(6, toMove);
        
        reconcilePatternStops(tp, tp2, atx);
        
        // make sure that everything looks right
        trips = atx.trips.values();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
            assertEquals(8, stopTimes.size());
                        
            assertEquals(toMove.stopId, stopTimes.get(6).stopId);
            assertEquals(stops[4].id, stopTimes.get(2).stopId);
            assertEquals(stops[10].id, stopTimes.get(4).stopId);
            assertEquals(stops[14].id, stopTimes.get(7).stopId);
        }
    }
    
    @Test
    public void testStopRemovalWhenAStopIsSkipped () {
       Stop[] stops = makeStops();
        
        // build a pattern with a trip and a few stoptimes, but skipping stop 10
        TripPattern tp = makePattern(stops);
        createStopTimesForTripPattern(tp, true);
        
        // make a new pattern
        TripPattern tp2 = makePattern(stops);
        
        assertEquals(8, tp.patternStops.size());
        
        // check that it worked
        Collection<Trip> trips = atx.trips.values();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
            assertEquals(8, stopTimes.size());
            
            int idx = 0;
            for (StopTime st : stopTimes) {
            	if (idx++ == 5)
            		assertNull(st);
            	else
            		assertNotSame(stops[10].id, st.stopId);
            }
        }
        
        // zap the sixth stop (which is stop 4)
        TripPatternStop removed = tp2.patternStops.remove(6);
        
        // make sure we got the indices right (this is more a test of the test)
        assertEquals(stops[12].id, removed.stopId);
        
        reconcilePatternStops(tp, tp2, atx);
        
        // check that the right stop times got deleted and that sequences were repacked
        trips = atx.trips.values();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
            assertEquals(7, stopTimes.size());
            
            int idx = 0;
            
            for (StopTime st : stopTimes) {
            	// skip the stop sequence that was skipped
                if (idx++ == 5) {
                	assertNull(st);
                	continue;
                }
            	
                // is this the stop that was supposed to be deleted?
                assertNotSame(stops[12].id, st.stopId);
                
                // is this the stop that was originally skipped?
                assertNotSame(stops[10].id, st.stopId);
            }
        }
    }
    
    @Test
    public void testStopAdditionWhenAStopIsSkipped () {
        Stop[] stops = makeStops();
        
        // build a pattern with a trip and a few stoptimes, but skipping stop 10
        TripPattern tp = makePattern(stops);
        createStopTimesForTripPattern(tp, true);
        
        // make a new pattern
        TripPattern tp2 = makePattern(stops);
        
        assertEquals(8, tp.patternStops.size());
        
        // check that it worked
        Collection<Trip> trips = atx.trips.values();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
            assertEquals(8, stopTimes.size());
            
            assertEquals(stops[0].id, stopTimes.get(0).stopId);
            assertEquals(stops[2].id, stopTimes.get(1).stopId);
            assertEquals(stops[4].id, stopTimes.get(2).stopId);
            assertEquals(stops[6].id, stopTimes.get(3).stopId);
            assertEquals(stops[8].id, stopTimes.get(4).stopId);
            assertNull(stopTimes.get(5));
            assertEquals(stops[12].id, stopTimes.get(6).stopId);
            assertEquals(stops[14].id, stopTimes.get(7).stopId);
        }
        
        // add a pattern stop at the end
        TripPatternStop tps2 = new TripPatternStop(stops[5], 180);

        tp2.patternStops.add(tps2);

        reconcilePatternStops(tp, tp2, atx);
        
        // stop times should not have changed
        trips = atx.trips.values();
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
            assertEquals(9, stopTimes.size());
                        
            assertEquals(stops[0].id, stopTimes.get(0).stopId);
            assertEquals(stops[2].id, stopTimes.get(1).stopId);
            assertEquals(stops[4].id, stopTimes.get(2).stopId);
            assertEquals(stops[6].id, stopTimes.get(3).stopId);
            assertEquals(stops[8].id, stopTimes.get(4).stopId);
            assertNull(stopTimes.get(5));
            assertEquals(stops[12].id, stopTimes.get(6).stopId);
            assertEquals(stops[14].id, stopTimes.get(7).stopId);
            assertNull(stopTimes.get(8));
        }
    }
    
    // move a stop one position left
    @Test
    public void testStopTranspositionSingleLeft () {
       Stop[] stops = makeStops();
        
        // build a pattern with a trip and a few stoptimes
        TripPattern tp = makePattern(stops);
        createStopTimesForTripPattern(tp);
        
        // make a new pattern
        TripPattern tp2 = makePattern(stops);
        
        assertEquals(8, tp.patternStops.size());
        
        // check that it worked
        Collection<Trip> trips = atx.trips.values();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
            assertEquals(8, stopTimes.size());
                        
            assertEquals(stops[14].id, stopTimes.get(7).stopId);
        }
        
        // move one stop
        TripPatternStop toMove = tp2.patternStops.remove(4);
        tp2.patternStops.add(3, toMove);

        reconcilePatternStops(tp, tp2, atx);
        
        // make sure that everything looks right
        trips = atx.trips.values();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
            assertEquals(8, stopTimes.size());

            assertEquals(stops[0].id, stopTimes.get(0).stopId);
            assertEquals(stops[2].id, stopTimes.get(1).stopId);
            assertEquals(stops[4].id, stopTimes.get(2).stopId);
            assertEquals(stops[8].id, stopTimes.get(3).stopId);
            assertEquals(stops[6].id, stopTimes.get(4).stopId);
            assertEquals(stops[10].id, stopTimes.get(5).stopId);
            assertEquals(stops[12].id, stopTimes.get(6).stopId);
            assertEquals(stops[14].id, stopTimes.get(7).stopId);
        }
    }
    
    @Test
    public void testTranspositionLeftWhenAStopIsSkipped () {
        Stop[] stops = makeStops();
        
        // build a pattern with a trip and a few stoptimes, but skipping stop 10
        TripPattern tp = makePattern(stops);
        createStopTimesForTripPattern(tp, true);
        
        // make a new pattern
        TripPattern tp2 = makePattern(stops);
        
        assertEquals(8, tp.patternStops.size());
        
        // check that it worked
        Collection<Trip> trips = atx.trips.values();
        
        for (Trip t : trips) {
        	List<StopTime> stopTimes = t.stopTimes;
        	assertEquals(8, stopTimes.size());
            assertEquals(stops[0].id, stopTimes.get(0).stopId);
            assertEquals(stops[2].id, stopTimes.get(1).stopId);
            assertEquals(stops[4].id, stopTimes.get(2).stopId);
            assertEquals(stops[6].id, stopTimes.get(3).stopId);
            assertEquals(stops[8].id, stopTimes.get(4).stopId);
            assertNull(stopTimes.get(5));
            assertEquals(stops[12].id, stopTimes.get(6).stopId);
            assertEquals(stops[14].id, stopTimes.get(7).stopId);
        }
        
        // move a stop
        TripPatternStop toMove = tp2.patternStops.remove(6);
        tp2.patternStops.add(4, toMove);
        
        assertEquals(stops[12].id, toMove.stopId);
        
        reconcilePatternStops(tp, tp2, atx);
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
            assertEquals(8, stopTimes.size());
            
            // 0 - 3: same
            assertEquals(stops[0].id, stopTimes.get(0).stopId);
            assertEquals(stops[2].id, stopTimes.get(1).stopId);
            assertEquals(stops[4].id, stopTimes.get(2).stopId);
            assertEquals(stops[6].id, stopTimes.get(3).stopId);
            
            // 4: formerly 6
            assertEquals(stops[12].id, stopTimes.get(4).stopId);
            assertEquals(stops[8].id, stopTimes.get(5).stopId);
            // skipped
            assertNull(stopTimes.get(6));
            assertEquals(stops[14].id, stopTimes.get(7).stopId);
        }
    }
    
    @Test
    public void testStopRemovalOnLoopRoute () {
        Stop[] stops = makeStops();
        
        // build a loop pattern with a trip and a few stoptimes
        TripPattern tp = makePattern(stops, true);
        createStopTimesForTripPattern(tp);
        
        // make a new pattern
        TripPattern tp2 = makePattern(stops, true);
        
        assertEquals(9, tp.patternStops.size());
        assertEquals(9, tp2.patternStops.size());
        
        // check that it worked
        Collection<Trip> trips = atx.trips.values();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
            assertEquals(9, stopTimes.size());
            
            assertEquals(stops[0].id, stopTimes.get(0).stopId);
            assertEquals(stops[0].id, stopTimes.get(8).stopId);
        }
        
        // remove the loop stop
        TripPatternStop toMove = tp2.patternStops.remove(0);
        assertEquals(stops[0].id, toMove.stopId);
        
        reconcilePatternStops(tp, tp2, atx);
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
           assertEquals(stops[2].id, stopTimes.get(0).stopId);
           assertEquals(stops[4].id, stopTimes.get(1).stopId);
           assertEquals(stops[6].id, stopTimes.get(2).stopId);
           assertEquals(stops[8].id, stopTimes.get(3).stopId);
           assertEquals(stops[10].id, stopTimes.get(4).stopId);
           assertEquals(stops[12].id, stopTimes.get(5).stopId);
           assertEquals(stops[14].id, stopTimes.get(6).stopId);
           assertEquals(stops[0].id, stopTimes.get(7).stopId);
        }
    }
    
    @Test
    public void testMakePatternLoopRoute () {
        Stop[] stops = makeStops();
        
        // build a pattern with a trip and a few stoptimes
        TripPattern tp = makePattern(stops, false);
        createStopTimesForTripPattern(tp);
        
        // make a loop pattern
        TripPattern tp2 = makePattern(stops, true);
        
        assertEquals(8, tp.patternStops.size());
        assertEquals(9, tp2.patternStops.size());
        
        // check that it worked
        Collection<Trip> trips = atx.trips.values();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
            assertEquals(8, stopTimes.size());
        }
        
        reconcilePatternStops(tp, tp2, atx);
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
            assertEquals(9, stopTimes.size());
                        
            int idx = 0;

            for (int i = 0; i < 7; i++) {
                // even numbered stops
                assertEquals(stops[idx++ * 2].id, stopTimes.get(i).stopId);
            }
            
            assertNull(stopTimes.get(8));
        }
    }
    
    @Test
    public void testMoveLoopStop () {
        Stop[] stops = makeStops();
        
        // build a loop pattern with a trip and a few stoptimes
        TripPattern tp = makePattern(stops, true);
        createStopTimesForTripPattern(tp);
        
        // make a new pattern
        TripPattern tp2 = makePattern(stops, true);
        
        assertEquals(9, tp.patternStops.size());
        assertEquals(9, tp2.patternStops.size());
        
        // check that it worked
        Collection<Trip> trips = atx.trips.values();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
            assertEquals(9, stopTimes.size());
                        
            assertEquals(stops[0].id, stopTimes.get(0).stopId);
            assertEquals(stops[0].id, stopTimes.get(8).stopId);
        }
        
        // move the loop stop
        TripPatternStop toMove = tp2.patternStops.remove(8);
        assertEquals(stops[0].id, toMove.stopId);
        tp2.patternStops.add(3, toMove);

        reconcilePatternStops(tp, tp2, atx);

        for (Trip t : trips) {
            List<StopTime> stopTimes = t.stopTimes;
            
            assertEquals(9, stopTimes.size());
            
            int idx = 0;
            
            for (StopTime st : stopTimes) {
                if (idx == 3) {
                    assertEquals(stops[0].id, st.stopId);
                }
                else if (idx < 3) {
                    assertEquals(stops[idx * 2].id, st.stopId);
                }
                else {
                    assertEquals(stops[idx * 2 - 2].id, st.stopId);                    
                }
                
                idx++;
            }
        }
    }
}
