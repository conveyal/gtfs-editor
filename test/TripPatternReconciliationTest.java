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
        int j = 0;
        for (int i = 0; i < 15; i += 2) {
            TripPatternStop ps = new TripPatternStop(tp, stops[i], j++, 120);
            pss.add(ps);
        }
        
        if (loop) {
            TripPatternStop ps = new TripPatternStop(tp, stops[0], j++, 120);
            pss.add(ps);
        }
        
        tp.patternStops = pss;
        tp.save();
        
        return tp;
    }
    
    private void createStopTimesForTripPattern (TripPattern tp) {
        createStopTimesForTripPattern(tp, false);
    }
    
    /** Create some stop times for the trip pattern */
    private void createStopTimesForTripPattern (TripPattern tp, boolean skipStop) {
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
                if (skipStop && ps.stopSequence.equals(5)) // stop 10
                    continue;
                
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
    
    @Test
    public void testStopRemovalInMiddle () {
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
        
        // zap the third stop (which is stop 4)
        TripPatternStop removed = tp2.patternStops.remove(2);
        
        // make sure we got the indices right (this is more a test of the test)
        assertEquals(stops[4].id, removed.stop.id);
        
        tp.reconcilePatternStops(tp2);
        
        // make sure that stop times got deleted
        assertEquals(35, StopTime.count());
        
        // check that the right stop times got deleted and that sequences were repacked
        trips = Trip.find("pattern = ?", tp).fetch();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.getStopTimes();
            
            assertEquals(7, stopTimes.size());
            
            sort(stopTimes, new StopTimeSequenceComparator());
            
            int previousStopSeq = -1;
            
            for (StopTime st : stopTimes) {
                // is this the stop that was supposed to be deleted?
                assertNotSame(stops[4].id, st.stop.id);
                assertNotSame(removed.id, st.patternStop.id);
                
                // are stop sequences repacked correctly?
                assertEquals(++previousStopSeq, (int) st.stopSequence);
            }
        }
    }
    
    @Test
    public void testStopRemovalAtEnd () {
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
        
        // zap the last stop (which is stop 14)
        TripPatternStop removed = tp2.patternStops.remove(7);
        
        // make sure we got the indices right (this is more a test of the test)
        assertEquals(stops[14].id, removed.stop.id);
        
        tp.reconcilePatternStops(tp2);
        
        // make sure that stop times got deleted
        assertEquals(35, StopTime.count());
        
        // check that the right stop times got deleted and that sequences were repacked
        trips = Trip.find("pattern = ?", tp).fetch();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.getStopTimes();
            
            assertEquals(7, stopTimes.size());
            
            sort(stopTimes, new StopTimeSequenceComparator());
            
            int previousStopSeq = -1;
            
            for (StopTime st : stopTimes) {
                // is this the stop that was supposed to be deleted?
                assertNotSame(stops[14].id, st.stop.id);
                assertNotSame(removed.id, st.patternStop.id);
                
                // are stop sequences repacked correctly?
                assertEquals(++previousStopSeq, (int) st.stopSequence);
            }
        }
    }
    
    @Test
    public void testStopTranspositionMoveLeft () {
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
            assertEquals((Integer) 7, stopTimes.get(7).stopSequence);
        }
        
        // move one stop
        TripPatternStop toMove = tp2.patternStops.remove(4);
        toMove.stopSequence = 6;
        tp2.patternStops.add(6, toMove);
        tp2.patternStops.get(7).stopSequence = 7;
        
        tp.reconcilePatternStops(tp2);
        
        // make sure that everything looks right
        trips = Trip.find("pattern = ?", tp).fetch();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.getStopTimes();
            
            assertEquals(8, stopTimes.size());
            
            sort(stopTimes, new StopTimeSequenceComparator());
            
            int expectedStopSequence = 0;
            
            for (StopTime st : stopTimes) {
                if (expectedStopSequence == 6) {
                    // this is the moved stop
                    assertEquals(toMove.stop.id, st.stop.id);
                }
                else if (expectedStopSequence == 2) {
                    assertEquals(stops[4].id, st.stop.id);
                }
                else if (expectedStopSequence == 4) {
                    // this should be the former stop 5
                    assertEquals(stops[10].id, st.stop.id);
                }
                else if (expectedStopSequence == 7) {
                    // this should still be the last stop from before
                    assertEquals(stops[14].id, st.stop.id);
                }
                
                assertEquals(expectedStopSequence ++, (int) st.stopSequence);
            }
        }
    }
    
    @Test
    public void testStopRemovalWhenAStopIsSkipped () {
       Stop[] stops = makeStops();
        
        // build a pattern with a trip and a few stoptimes, but skipping stop 10
        TripPattern tp = makePattern(stops);
        createStopTimesForTripPattern(tp, true);
        
        // make sure the correct number of stop times were created
        assertEquals(35, StopTime.count());
        
        // make a new pattern
        TripPattern tp2 = makePattern(stops);
        
        assertEquals(8, tp.patternStops.size());
        
        // check that it worked
        Collection<Trip> trips = Trip.find("pattern = ?", tp).fetch();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.getStopTimes();
            
            assertEquals(7, stopTimes.size());
            
            sort(stopTimes, new StopTimeSequenceComparator());
            
            for (StopTime st : stopTimes) {
                assertNotSame(stops[10].id, st.stop.id);
                assertNotSame(5, st.stopSequence);
            }
        }
        
        // zap the sixth stop (which is stop 4)
        TripPatternStop removed = tp2.patternStops.remove(6);
        
        // make sure we got the indices right (this is more a test of the test)
        assertEquals(stops[12].id, removed.stop.id);
        
        tp.reconcilePatternStops(tp2);
        
        // make sure that stop times got deleted
        assertEquals(30, StopTime.count());
        
        // check that the right stop times got deleted and that sequences were repacked
        trips = Trip.find("pattern = ?", tp).fetch();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.getStopTimes();
            
            assertEquals(6, stopTimes.size());
            
            sort(stopTimes, new StopTimeSequenceComparator());
            
            int expectedStopSeq = 0;
            
            for (StopTime st : stopTimes) {
                // is this the stop that was supposed to be deleted?
                assertNotSame(stops[12].id, st.stop.id);
                assertNotSame(removed.id, st.patternStop.id);
                
                // is this the stop that was originally skipped?
                assertNotSame(stops[10].id, st.stop.id);
                assertNotSame(5, st.stopSequence);
                
                // skip the stop sequence that was skipped
                if (expectedStopSeq == 5)
                    expectedStopSeq++;
                
                // are stop sequences repacked correctly?
                assertEquals(expectedStopSeq++, (int) st.stopSequence);
            }
        }
    }
    
    @Test
    public void testStopAdditionWhenAStopIsSkipped () {
        Stop[] stops = makeStops();
        
        // build a pattern with a trip and a few stoptimes, but skipping stop 10
        TripPattern tp = makePattern(stops);
        createStopTimesForTripPattern(tp, true);
        
        // make sure the correct number of stop times were created
        assertEquals(35, StopTime.count());
        
        // make a new pattern
        TripPattern tp2 = makePattern(stops);
        
        assertEquals(8, tp.patternStops.size());
        
        // check that it worked
        Collection<Trip> trips = Trip.find("pattern = ?", tp).fetch();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.getStopTimes();
            
            assertEquals(7, stopTimes.size());
            
            sort(stopTimes, new StopTimeSequenceComparator());
            
            for (StopTime st : stopTimes) {
                assertNotSame(stops[10].id, st.stop.id);
                assertNotSame(5, st.stopSequence);
            }
        }
        
        // add a pattern stop at the end
        // note that stop sequence is nonconsecutive
        TripPatternStop tps2 = new TripPatternStop(tp2, stops[5], 12, 180);

        tp2.patternStops.add(tps2);

        tp.reconcilePatternStops(tp2);
        
        // stop times should not have changed
        assertEquals(35, StopTime.count());
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.getStopTimes();
            
            assertEquals(7, stopTimes.size());
            
            sort(stopTimes, new StopTimeSequenceComparator());
            
            int expectedStopSeq = 0;
            
            for (StopTime st : stopTimes) {
           
                // is this the stop that was originally skipped?
                assertNotSame(stops[10].id, st.stop.id);
                assertNotSame(5, st.stopSequence);
                
                // skip the stop sequence that was skipped
                if (expectedStopSeq == 5)
                    expectedStopSeq++;
                
                // are stop sequences repacked correctly?
                assertEquals(expectedStopSeq++, (int) st.stopSequence);
            }
        }
    }
    
    // move a stop one position left
    @Test
    public void testStopTranspositionSingleLeft () {
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
            assertEquals((Integer) 7, stopTimes.get(7).stopSequence);
        }
        
        // move one stop
        TripPatternStop toMove = tp2.patternStops.remove(4);
        toMove.stopSequence = 3;
        tp2.patternStops.add(3, toMove);
        
        // fix up later stop times
        for (int i = 4; i < tp2.patternStops.size(); i++)  {
            tp2.patternStops.get(i).stopSequence++;
        }
        
        tp.reconcilePatternStops(tp2);
        
        // make sure that everything looks right
        trips = Trip.find("pattern = ?", tp).fetch();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.getStopTimes();
            
            assertEquals(8, stopTimes.size());
            
            sort(stopTimes, new StopTimeSequenceComparator());
            
            int expectedStopSequence = 0;
            
            for (StopTime st : stopTimes) {
                if (expectedStopSequence == 1) {
                    // this is before the moved stop
                    assertEquals(stops[2].id, st.stop.id);
                }
                else if (expectedStopSequence == 3) {
                    // this is the moved stop
                    assertEquals(stops[8].id, st.stop.id);
                }
                else if (expectedStopSequence == 4) {
                    // this should be the former stop 3
                    assertEquals(stops[6].id, st.stop.id);
                }
                else if (expectedStopSequence == 7) {
                    // this should still be the last stop from before
                    assertEquals(stops[14].id, st.stop.id);
                }
                
                assertEquals(expectedStopSequence ++, (int) st.stopSequence);
            }
        }
    }
    
    @Test
    public void testTranspositionLeftWhenAStopIsSkipped () {
        Stop[] stops = makeStops();
        
        // build a pattern with a trip and a few stoptimes, but skipping stop 10
        TripPattern tp = makePattern(stops);
        createStopTimesForTripPattern(tp, true);
        
        // make sure the correct number of stop times were created
        assertEquals(35, StopTime.count());
        
        // make a new pattern
        TripPattern tp2 = makePattern(stops);
        
        assertEquals(8, tp.patternStops.size());
        
        // check that it worked
        Collection<Trip> trips = Trip.find("pattern = ?", tp).fetch();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.getStopTimes();
            
            assertEquals(7, stopTimes.size());
            
            sort(stopTimes, new StopTimeSequenceComparator());
            
            for (StopTime st : stopTimes) {
                assertNotSame(stops[10].id, st.stop.id);
                assertNotSame(5, st.stopSequence);
            }
        }
        
        // move a stop
        TripPatternStop toMove = tp2.patternStops.remove(6);
        toMove.stopSequence = 4;
        tp2.patternStops.add(4, toMove);
        
        assertEquals(stops[12].id, toMove.stop.id);
        
        // fix stop sequences (generally this would be done in javascript on the client)
        // note that this leaves stop sequences non-consecutive, which is a supported case
        for (int i = 5; i < tp2.patternStops.size(); i++) {
            tp2.patternStops.get(i).stopSequence++;
        }
        
        tp.reconcilePatternStops(tp2);
        
        // stop times should not have changed
        assertEquals(35, StopTime.count());
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.getStopTimes();
            
            assertEquals(7, stopTimes.size());
            
            sort(stopTimes, new StopTimeSequenceComparator());
            
            int expectedStopSeq = 0;
            
            for (StopTime st : stopTimes) {
           
                // is this the stop that was originally skipped?
                assertNotSame(stops[10].id, st.stop.id);
                assertNotSame(6, st.stopSequence);
                
                // skip the stop sequence that was skipped
                // it was stop sequence 5 but then we moved a stop before it
                if (expectedStopSeq == 6)
                    expectedStopSeq++;
                
                // is the moved stop moved?
                if (expectedStopSeq == 4) 
                    // the stop that was formerly 6
                    assertEquals(stops[12].id, st.stop.id);
                
                if (expectedStopSeq == 7)
                    // after from
                    assertEquals(stops[14].id, st.stop.id);
                
                if (expectedStopSeq == 5)
                    // between to and from
                    // the stop formerly known as the fourth stop
                    assertEquals(stops[8].id, st.stop.id);
                
                if (expectedStopSeq == 0)
                    // beginning
                    assertEquals(stops[0].id, st.stop.id);
                
                // are stop sequences repacked correctly?
                assertEquals(expectedStopSeq++, (int) st.stopSequence);
            }
        }
    }
    
    @Test
    public void testStopRemovalOnLoopRoute () {
        Stop[] stops = makeStops();
        
        // build a loop pattern with a trip and a few stoptimes
        TripPattern tp = makePattern(stops, true);
        createStopTimesForTripPattern(tp);
        
        // make sure the correct number of stop times were created
        assertEquals(45, StopTime.count());
        
        // make a new pattern
        TripPattern tp2 = makePattern(stops, true);
        
        assertEquals(9, tp.patternStops.size());
        assertEquals(9, tp2.patternStops.size());
        
        // check that it worked
        Collection<Trip> trips = Trip.find("pattern = ?", tp).fetch();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.getStopTimes();
            
            assertEquals(9, stopTimes.size());
            
            sort(stopTimes, new StopTimeSequenceComparator());
            
            assertEquals(stops[0].id, stopTimes.get(0).stop.id);
            assertEquals(stops[0].id, stopTimes.get(8).stop.id);
        }
        
        // remove the loop stop
        TripPatternStop toMove = tp2.patternStops.remove(0);
        assertEquals(stops[0].id, toMove.stop.id);
        
        // fix stop sequences (generally this would be done in javascript on the client)
        // note that this leaves stop sequences non-consecutive, which is a supported case
        for (int i = 5; i < tp2.patternStops.size(); i++) {
            tp2.patternStops.get(i).stopSequence++;
        }
        
        tp.reconcilePatternStops(tp2);
        
        // some stop times should have been removed
        assertEquals(40, StopTime.count());
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.getStopTimes();
            
            assertEquals(8, stopTimes.size());
            
            sort(stopTimes, new StopTimeSequenceComparator());
            
            int expectedStopSeq = 0;
            
            for (StopTime st : stopTimes) {
                if (expectedStopSeq == 7) {
                    assertEquals(stops[0].id, st.stop.id);
                }
                else {
                    // even numbered stops, except the (removed) 0th one.
                    assertEquals(stops[expectedStopSeq * 2 + 2].id, st.stop.id);
                }
                
                assertEquals(expectedStopSeq ++, (int) st.stopSequence);
            }
        }
    }
    
    @Test
    public void testMakePatternLoopRoute () {
        Stop[] stops = makeStops();
        
        // build a pattern with a trip and a few stoptimes
        TripPattern tp = makePattern(stops, false);
        createStopTimesForTripPattern(tp);
        
        // make sure the correct number of stop times were created
        assertEquals(40, StopTime.count());
        
        // make a loop pattern
        TripPattern tp2 = makePattern(stops, true);
        
        assertEquals(8, tp.patternStops.size());
        assertEquals(9, tp2.patternStops.size());
        
        // check that it worked
        Collection<Trip> trips = Trip.find("pattern = ?", tp).fetch();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.getStopTimes();
            
            assertEquals(8, stopTimes.size());
        }
        
        tp.reconcilePatternStops(tp2);
        
        // stop times should be same
        assertEquals(40, StopTime.count());
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.getStopTimes();
            
            assertEquals(8, stopTimes.size());
            
            sort(stopTimes, new StopTimeSequenceComparator());
            
            int expectedStopSeq = 0;

            for (StopTime st : stopTimes) {
                // even numbered stops
                assertEquals(stops[expectedStopSeq * 2].id, st.stop.id);
                assertEquals(expectedStopSeq ++, (int) st.stopSequence);
            }
        }
    }
    
    @Test
    public void testMoveLoopStop () {
        Stop[] stops = makeStops();
        
        // build a loop pattern with a trip and a few stoptimes
        TripPattern tp = makePattern(stops, true);
        createStopTimesForTripPattern(tp);
        
        // make sure the correct number of stop times were created
        assertEquals(45, StopTime.count());
        
        // make a new pattern
        TripPattern tp2 = makePattern(stops, true);
        
        assertEquals(9, tp.patternStops.size());
        assertEquals(9, tp2.patternStops.size());
        
        // check that it worked
        Collection<Trip> trips = Trip.find("pattern = ?", tp).fetch();
        
        for (Trip t : trips) {
            List<StopTime> stopTimes = t.getStopTimes();
            
            assertEquals(9, stopTimes.size());
            
            sort(stopTimes, new StopTimeSequenceComparator());
            
            assertEquals(stops[0].id, stopTimes.get(0).stop.id);
            assertEquals(stops[0].id, stopTimes.get(8).stop.id);
        }
        
        // move the loop stop
        TripPatternStop toMove = tp2.patternStops.remove(8);
        assertEquals(stops[0].id, toMove.stop.id);
        toMove.stopSequence = 3;
        tp2.patternStops.add(3, toMove);
        
        for (int i = 4; i < 9; i++) {
            tp2.patternStops.get(i).stopSequence++;
        }
        
        // fix stop sequences (generally this would be done in javascript on the client)
        // note that this leaves stop sequences non-consecutive, which is a supported case
        for (int i = 5; i < tp2.patternStops.size(); i++) {
            tp2.patternStops.get(i).stopSequence++;
        }
        
        tp.reconcilePatternStops(tp2);

        for (Trip t : trips) {
            List<StopTime> stopTimes = t.getStopTimes();
            
            assertEquals(9, stopTimes.size());
            
            sort(stopTimes, new StopTimeSequenceComparator());
            
            int expectedStopSeq = 0;
            
            for (StopTime st : stopTimes) {
                if (expectedStopSeq == 3) {
                    assertEquals(stops[0].id, st.stop.id);
                }
                else if (expectedStopSeq < 3) {
                    assertEquals(stops[expectedStopSeq * 2].id, st.stop.id);
                }
                else {
                    assertEquals(stops[expectedStopSeq * 2 - 2].id, st.stop.id);                    
                }
                
                assertEquals(expectedStopSeq ++, (int) st.stopSequence);
            }
        }
    }
}
