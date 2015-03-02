package datastore;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import datastore.AgencyTx;
import models.transit.*;
import org.junit.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import play.test.UnitTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Test stop merging.
 * @author mattwigway
 */
public class StopMergeTest extends UnitTest {
    private static GeometryFactory gf = new GeometryFactory();

    @Test
    public void testStopMerge () {
        // build the database
        DB db = DBMaker.newHeapDB().make();
        AgencyTx atx = new AgencyTx(db);

        // make two stops that are almost identical
        // Orleans and Broadway, Baltimore, MD
        Stop stop = makeStop(39.29513, -76.59390);
        Stop stop2 = makeStop(39.2951301, -76.59390);
        Stop lastStop = makeStop(39.29532, -76.58935);

        atx.stops.put(stop.id, stop);
        atx.stops.put(stop2.id, stop2);
        atx.stops.put(lastStop.id, lastStop);

        // make some trip patterns
        TripPattern pattern1 = makePattern(stop, lastStop);
        TripPattern pattern2 = makePattern(stop2, lastStop);

        atx.tripPatterns.put(pattern1.id, pattern1);
        atx.tripPatterns.put(pattern2.id, pattern2);

        // make some trips
        Trip trip1 = makeTrip(pattern1);
        Trip trip2 = makeTrip(pattern2);

        atx.trips.put(trip1.id, trip1);
        atx.trips.put(trip2.id, trip2);

        assertEquals(1, atx.getTripPatternsByStop(stop.id).size());

        List<String> stopsToMerge = new ArrayList<String>();
        stopsToMerge.add(stop.id);
        stopsToMerge.add(stop2.id);
        Stop.merge(stopsToMerge, atx);

        trip2 = atx.trips.get(trip2.id);
        assertEquals(stop.id, trip2.stopTimes.get(0).stopId);

        pattern2 = atx.tripPatterns.get(pattern2.id);
        assertEquals(stop.id, pattern2.patternStops.get(0).stopId);

        assertFalse(atx.stops.containsKey(stop2.id));
    }

    public TripPattern makePattern (Stop... stops) {
        TripPattern ret = new TripPattern();
        ret.patternStops = new ArrayList<TripPatternStop>();

        for (Stop stop : stops) {
            TripPatternStop tps = new TripPatternStop();
            tps.stopId = stop.id;
            tps.defaultTravelTime = 60;
            tps.defaultDwellTime = 0;
            ret.patternStops.add(tps);
        }

        return ret;
    }

    public Stop makeStop (double lat, double lon) {
        Stop ret = new Stop();
        ret.location = gf.createPoint(new Coordinate(lon, lat));
        return ret;
    }

    public Trip makeTrip (TripPattern pattern) {
        Trip ret = new Trip();
        ret.patternId = pattern.id;
        ret.stopTimes = new ArrayList<StopTime>();
        ret.calendarId = "123546";

        int cumulativeTime = 36000;

        for (TripPatternStop ps :  pattern.patternStops) {
            StopTime st = new StopTime();
            cumulativeTime += ps.defaultTravelTime;
            st.arrivalTime = cumulativeTime;
            cumulativeTime += ps.defaultTravelTime;
            st.departureTime = cumulativeTime;
            st.stopId = ps.stopId;
            ret.stopTimes.add(st);
        }

        return ret;
    }
}
