package datastore;

import java.util.Collection;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import models.transit.Route;
import models.transit.ScheduleException;
import models.transit.ServiceCalendar;
import models.transit.Stop;
import models.transit.Trip;
import models.transit.TripPattern;

import org.joda.time.LocalDate;
import org.mapdb.Atomic;
import org.mapdb.BTreeMap;
import org.mapdb.Bind;
import org.mapdb.Bind.MapWithModificationListener;
import org.mapdb.DB;
import org.mapdb.Fun;
import org.mapdb.Fun.Function2;
import org.mapdb.Fun.Tuple2;

import utils.BindUtils;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/** a transaction in an agency database */
public class AgencyTx extends DatabaseTx {
	// primary datastores
	// if you add another, you MUST update SnapshotTx.java
	// if you don't, not only will your new data not be backed up, IT WILL BE THROWN AWAY WHEN YOU RESTORE! 
	public BTreeMap<String, TripPattern> tripPatterns;
	public BTreeMap<String, Route> routes;
	public BTreeMap<String, Trip> trips;
	public BTreeMap<String, ServiceCalendar> calendars;
	public BTreeMap<String, ScheduleException> exceptions;
	public BTreeMap<String, Stop> stops;
	// if you add anything here, see warning above!
	
	// secondary indices
	
	/** Set containing tuples <Route ID, Trip ID> */
	public NavigableSet<Tuple2<String, String>> tripsByRoute;
	
	/** <route ID, trip pattern ID> */
	public NavigableSet<Tuple2<String, String>> tripPatternsByRoute;
	
	/** <trip pattern ID, trip ID> */
	public NavigableSet<Tuple2<String, String>> tripsByTripPattern;
	
	/** <calendar ID, trip ID> */
	public NavigableSet<Tuple2<String, String>> tripsByCalendar;
	
	/** <calendar id, schedule exception id> */
	public NavigableSet<Tuple2<String, String>> exceptionsByCalendar;
	
	/** <<patternId, calendarId>, trip id> */
	public NavigableSet<Tuple2<Tuple2<String, String>, String>> tripsByPatternAndCalendar;
	
	/** major stops for this agency */
	public NavigableSet<String> majorStops;
	
	/** <Stop ID, Pattern ID> </Stop>trip patterns using each stop */
	public NavigableSet<Tuple2<String, String>> tripPatternsByStop;
	
	/** number of schedule exceptions on each date - this will always be null, 0, or 1, as we prevent save of others */
	public ConcurrentMap<LocalDate, Long> scheduleExceptionCountByDate;
	
	/** number of trips on each tuple2<patternId, calendar id> */
	public ConcurrentMap<Tuple2<String, String>, Long> tripCountByPatternAndCalendar;
	
	/** number of trips on each calendar */
	public ConcurrentMap<String, Long> tripCountByCalendar;
	
	/**
	 * Spatial index of stops. Set<Tuple2<Tuple2<Lon, Lat>, stop ID>>
	 * This is not a true spatial index, but should be sufficiently efficient for our purposes.
	 * Jan Kotek describes this approach here, albeit in Czech: https://groups.google.com/forum/#!msg/mapdb/ADgSgnXzkk8/Q8J9rWAWXyMJ
	 */
	public NavigableSet<Tuple2<Tuple2<Double, Double>, String>> stopsGix;
	
	/** snapshot versions. we use an atomic value so that they are (roughly) sequential, instead of using unordered UUIDs */
	private Atomic.Integer snapshotVersion;
	
	/**
	 * Create an agency tx.
	 */
    AgencyTx (DB tx) {
		super(tx);
		
		tripPatterns = getMap("tripPatterns");
		routes = getMap("routes");
		trips = getMap("trips");
		calendars = getMap("calendars");
		exceptions = getMap("exceptions");
		snapshotVersion = tx.getAtomicInteger("snapshotVersion");
		stops = getMap("stops");
		buildSecondaryIndices();
	}
	
	public void buildSecondaryIndices () {
		// build secondary indices
		// we store indices in the mapdb not because we care about persistence, but because then they
		// will be managed within the context of MapDB transactions
		tripsByRoute = getSet("tripsByRoute");
		
		// bind the trips to the routes
		Bind.secondaryKeys(trips, tripsByRoute, new Fun.Function2<String[], String, Trip>() {

			@Override
			public String[] run(String tripId, Trip trip) {
				return new String[] { trip.routeId };
			}
		});
		
		tripPatternsByRoute = getSet("tripPatternsByRoute");
		Bind.secondaryKeys(tripPatterns, tripPatternsByRoute, new Fun.Function2<String[], String, TripPattern>() {

			@Override
			public String[] run(String tripId, TripPattern trip) {
				// TODO Auto-generated method stub
				return new String[] { trip.routeId };
			}
		});
		
		tripsByTripPattern = getSet("tripsByTripPattern");
		Bind.secondaryKeys(trips, tripsByTripPattern, new Fun.Function2<String[], String, Trip> () {

			@Override
			public String[] run(String tripId, Trip trip) {
				// TODO Auto-generated method stub
				return new String[] { trip.patternId };
			}				
		});
		
		tripsByCalendar = getSet("tripsByCalendar");
		Bind.secondaryKeys(trips, tripsByCalendar, new Fun.Function2<String[], String, Trip> () {

			@Override
			public String[] run(String tripId, Trip trip) {
				// TODO Auto-generated method stub
				return new String[] { trip.calendarId };
			}				
		});
		
		exceptionsByCalendar = getSet("exceptionsByCalendar");
		Bind.secondaryKeys(exceptions, exceptionsByCalendar, new Fun.Function2<String[], String, ScheduleException> () {

			@Override
			public String[] run(String key, ScheduleException ex) {
				if (ex.customSchedule == null) return new String[0];
				
				return ex.customSchedule.toArray(new String[ex.customSchedule.size()]);
			}
			
		});
		
		tripsByPatternAndCalendar = getSet("tripsByPatternAndCalendar");
		Bind.secondaryKeys(trips, tripsByPatternAndCalendar, new Fun.Function2<Tuple2<String, String>[], String, Trip>() {

			@Override
			public Tuple2<String, String>[] run(String key, Trip trip) {
				return new Tuple2[] { new Tuple2(trip.patternId, trip.calendarId) };
			}
		});
		
		majorStops = getSet("majorStops");
		BindUtils.subsetIndex(stops, majorStops, new Fun.Function2<Boolean, String, Stop>  (){
			@Override
			public Boolean run(String key, Stop val) {
				// TODO Auto-generated method stub
				return val.majorStop != null && val.majorStop;
			}
			
		});
		
		tripPatternsByStop = getSet("tripPatternsByStop");
		Bind.secondaryKeys(tripPatterns, tripPatternsByStop, new Fun.Function2<String[], String, TripPattern>() {
			@Override
			public String[] run(String key, TripPattern tp) {
				String[] stops = new String[tp.patternStops.size()];

				for (int i = 0; i < stops.length; i++) {
					stops[i] = tp.patternStops.get(i).stopId;
				}

				return stops;
			}
		});
		
		tripCountByPatternAndCalendar = getMap("tripCountByPatternAndCalendar");
		Bind.histogram(trips, tripCountByPatternAndCalendar, new Fun.Function2<Tuple2<String, String>, String, Trip>() {

			@Override
			public Tuple2<String, String> run(String tripId, Trip trip) {
				return new Tuple2(trip.patternId, trip.calendarId);
			}
		});
		
		scheduleExceptionCountByDate = getMap("scheduleExceptionCountByDate");
		BindUtils.multiHistogram(exceptions, scheduleExceptionCountByDate, new Fun.Function2<LocalDate[], String, ScheduleException> () {

			@Override
			public LocalDate[] run(String id, ScheduleException ex) {
				return ex.dates.toArray(new LocalDate[ex.dates.size()]);
			}
			
		});
		
		tripCountByCalendar = getMap("tripCountByCalendar");
		Bind.histogram(trips, tripCountByCalendar, new Fun.Function2<String, String, Trip>() {

			@Override
			public String run(String key, Trip trip) {
				// TODO Auto-generated method stub
				return trip.calendarId;
			}
		});
		
		// "spatial index"
		stopsGix = getSet("stopsGix");
		Bind.secondaryKeys(stops, stopsGix, new Function2<Tuple2<Double, Double>[], String, Stop> () {

			@Override
			public Tuple2<Double, Double>[] run(
					String stopId, Stop stop) {
				return new Tuple2[] { new Tuple2(stop.location.getX(), stop.location.getY()) }; 
			}
							
		});
	}

	public Collection<Trip> getTripsByPattern(String patternId) {
		Set<Tuple2<String, String>> matchedKeys = tripsByTripPattern.subSet(new Tuple2(patternId, null), new Tuple2(patternId, Fun.HI));
		
		return Collections2.transform(matchedKeys, new Function<Tuple2<String, String>, Trip> () {
			public Trip apply(Tuple2<String, String> input) {
				return trips.get(input.b);
			}	
		});
	}
	
	public Collection<Trip> getTripsByRoute(String routeId) {
		Set<Tuple2<String, String>> matchedKeys = tripsByRoute.subSet(new Tuple2(routeId, null), new Tuple2(routeId, Fun.HI));
		
		return Collections2.transform(matchedKeys, new Function<Tuple2<String, String>, Trip> () {
			public Trip apply(Tuple2<String, String> input) {
				return trips.get(input.b);
			}	
		});
	}
	
	public Collection<Trip> getTripsByCalendar(String calendarId) {
		Set<Tuple2<String, String>> matchedKeys = tripsByCalendar.subSet(new Tuple2(calendarId, null), new Tuple2(calendarId, Fun.HI));
		
		return Collections2.transform(matchedKeys, new Function<Tuple2<String, String>, Trip> () {
			public Trip apply(Tuple2<String, String> input) {
				return trips.get(input.b);
			}	
		});
	}
	
	public Collection<ScheduleException> getExceptionsByCalendar(String calendarId) {
		Set<Tuple2<String, String>> matchedKeys = exceptionsByCalendar.subSet(new Tuple2(calendarId, null), new Tuple2(calendarId, Fun.HI));
		
		return Collections2.transform(matchedKeys, new Function<Tuple2<String, String>, ScheduleException> () {
			public ScheduleException apply(Tuple2<String, String> input) {
				return exceptions.get(input.b);
			}	
		});
	}
	
	public Collection<Trip> getTripsByPatternAndCalendar(String patternId, String calendarId) {
		Set<Tuple2<Tuple2<String, String>, String>> matchedKeys =
				tripsByPatternAndCalendar.subSet(new Tuple2(new Tuple2(patternId, calendarId), null), new Tuple2(new Tuple2(patternId, calendarId), Fun.HI));
		
		return Collections2.transform(matchedKeys, new Function<Tuple2<Tuple2<String, String>, String>, Trip> () {
			public Trip apply(Tuple2<Tuple2<String, String>, String> input) {
				return trips.get(input.b);
			}	
		});
	}

	public Collection<Stop> getStopsWithinBoundingBox (double north, double east, double south, double west) {
		// find all the stops in this bounding box
		// avert your gaze please as I write these generic types
		Tuple2<Double, Double> min = new Tuple2<Double, Double>(west, south);
		Tuple2<Double, Double> max = new Tuple2<Double, Double>(east, north);

		Set<Tuple2<Tuple2<Double, Double>, String>> matchedKeys =
				stopsGix.subSet(new Tuple2(min, null), new Tuple2(max, Fun.HI));

		Collection<Stop> matchedStops =
				Collections2.transform(matchedKeys, new Function<Tuple2<Tuple2<Double, Double>, String>, Stop> () {

					@Override
					public Stop apply(
							Tuple2<Tuple2<Double, Double>, String> input) {
						return stops.get(input.b);
					}
				});

		return matchedStops;
	}

	public Collection<TripPattern> getTripPatternsByStop (String id) {
		Collection<Tuple2<String, String>> matchedPatterns = tripPatternsByStop.subSet(new Tuple2(id, null), new Tuple2(id, Fun.HI));
		return Collections2.transform(matchedPatterns, new Function<Tuple2<String, String>, TripPattern>() {
			public TripPattern apply(Tuple2<String, String> input) {
				return tripPatterns.get(input.b);
			}
		});
	}

	/** return the version number of the next snapshot */
	public int getNextSnapshotId () {
		return snapshotVersion.incrementAndGet();
	}
}