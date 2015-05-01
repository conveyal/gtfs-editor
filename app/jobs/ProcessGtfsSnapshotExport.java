package jobs;

import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.mapdb.Fun.Tuple2;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.onebusaway.gtfs.serialization.GtfsWriter;

import com.conveyal.gtfs.GTFSFeed;
import com.conveyal.gtfs.model.CalendarDate;
import com.conveyal.gtfs.model.Entity;
import com.conveyal.gtfs.model.Frequency;
import com.conveyal.gtfs.model.Service;
import com.conveyal.gtfs.model.Shape;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mchange.v2.c3p0.impl.DbAuth;
import com.vividsolutions.jts.geom.Coordinate;

import datastore.VersionedDataStore;
import datastore.AgencyTx;
import datastore.GlobalTx;
import models.transit.Agency;
import models.transit.AttributeAvailabilityType;
import models.transit.Route;
import models.transit.ScheduleException;
import models.transit.ServiceCalendar;
import models.transit.Stop;
import models.transit.StopTime;
import models.transit.TripDirection;
import models.transit.TripPattern;
import models.transit.TripPatternStop;
import models.transit.Trip;
import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import utils.DirectoryZip;
import utils.GeoUtils;

public class ProcessGtfsSnapshotExport implements Runnable {
	private Collection<String> agencies;
	private File output;
	private LocalDate startDate;
	private LocalDate endDate;
	
	public ProcessGtfsSnapshotExport(Collection<String> agencies, File output, LocalDate startDate, LocalDate endDate) {
		this.agencies = agencies;
		this.output = output;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	@Override
	public void run() {
		GTFSFeed feed = new GTFSFeed();
		
		GlobalTx gtx = VersionedDataStore.getGlobalTx();
		AgencyTx atx = null;
		
		try {
			for (String agencyId : agencies) {
				Agency agency = gtx.agencies.get(agencyId);
				com.conveyal.gtfs.model.Agency gtfsAgency = agency.toGtfs();
				Logger.info("Exporting agency %s", gtfsAgency);
				atx = VersionedDataStore.getAgencyTx(agencyId);
				
				// write the agencies.txt entry
				feed.agency.put(agencyId, agency.toGtfs());
				
				// write all of the calendars and calendar dates
				for (ServiceCalendar cal : atx.calendars.values()) {
					com.conveyal.gtfs.model.Service gtfsService = cal.toGtfs(toGtfsDate(startDate), toGtfsDate(endDate));
					// note: not using user-specified IDs
					
					// add calendar dates
					for (ScheduleException ex : atx.exceptions.values()) {
						for (LocalDate date : ex.dates) {
							if (date.isBefore(startDate) || date.isAfter(endDate))
								// no need to write dates that do not apply
								continue;
							
							CalendarDate cd = new CalendarDate();
							cd.date = date;
							cd.service = gtfsService;
							cd.exception_type = ex.serviceRunsOn(cal) ? 1 : 2;
							
							if (gtfsService.calendar_dates.containsKey(date))
								throw new IllegalArgumentException("Duplicate schedule exceptions on " + date.toString());
							
							gtfsService.calendar_dates.put(date, cd);
						}
					}
					
					feed.services.put(gtfsService.service_id, gtfsService);
				}
				
				// write the routes
				for (Route route : atx.routes.values()) {
					com.conveyal.gtfs.model.Route gtfsRoute = route.toGtfs(gtfsAgency, gtx);
					feed.routes.put(route.getGtfsId(), gtfsRoute);
					
					// write the trips on those routes
					for (Trip trip : atx.getTripsByRoute(route.id)) {
						com.conveyal.gtfs.model.Trip gtfsTrip = new com.conveyal.gtfs.model.Trip();
						
						gtfsTrip.block_id = trip.blockId;
						gtfsTrip.route = gtfsRoute;
						gtfsTrip.trip_id = trip.getGtfsId();
						// not using custom ids
						gtfsTrip.service = feed.services.get(trip.calendarId);
						gtfsTrip.trip_headsign = trip.tripHeadsign;
						gtfsTrip.trip_short_name = trip.tripShortName;
						gtfsTrip.direction_id = trip.tripDirection == TripDirection.A ? 0 : 1;
						
						TripPattern pattern = atx.tripPatterns.get(trip.patternId);
						
						Tuple2<String, Integer> nextKey = feed.shapePoints.ceilingKey(new Tuple2(pattern.id, null));
						if ((nextKey == null || !pattern.id.equals(nextKey.a)) && pattern.shape != null && !pattern.useStraightLineDistances) {
							// this shape has not yet been saved
							double[] coordDistances = GeoUtils.getCoordDistances(pattern.shape);
							
							for (int i = 0; i < coordDistances.length; i++) {
								Coordinate coord = pattern.shape.getCoordinateN(i);
								Shape shape = new Shape(pattern.id, coord.y, coord.x, i + 1, coordDistances[i]);
								feed.shapePoints.put(new Tuple2(pattern.id, shape.shape_pt_sequence), shape);
							}
						}
						
						if (pattern.shape != null && !pattern.useStraightLineDistances)
							gtfsTrip.shape_id = pattern.id;
						
						if (trip.wheelchairBoarding != null) {
							if (trip.wheelchairBoarding.equals(AttributeAvailabilityType.AVAILABLE))
								gtfsTrip.wheelchair_accessible = 1;
							
							else if (trip.wheelchairBoarding.equals(AttributeAvailabilityType.UNAVAILABLE))
								gtfsTrip.wheelchair_accessible = 2;
							
							else
								gtfsTrip.wheelchair_accessible = 0;
							
						}
						else if (route.wheelchairBoarding != null) {
							if (route.wheelchairBoarding.equals(AttributeAvailabilityType.AVAILABLE))
								gtfsTrip.wheelchair_accessible = 1;
							
							else if (route.wheelchairBoarding.equals(AttributeAvailabilityType.UNAVAILABLE))
								gtfsTrip.wheelchair_accessible = 2;
							
							else
								gtfsTrip.wheelchair_accessible = 0;
							
						}
						
						feed.trips.put(gtfsTrip.trip_id, gtfsTrip);
						
						TripPattern patt = atx.tripPatterns.get(trip.patternId);
						
						Iterator<TripPatternStop> psi = patt.patternStops.iterator();
						
						int stopSequence = 1;
						
						// write the stop times
						for (StopTime st : trip.stopTimes) {
							TripPatternStop ps = psi.next();
							if (st == null)
								continue;
							
							Stop stop = atx.stops.get(st.stopId);
							
							if (!st.stopId.equals(ps.stopId)) {
								throw new IllegalStateException("Trip " + trip.id + " does not match its pattern!");
							}
							
							com.conveyal.gtfs.model.StopTime gst = new com.conveyal.gtfs.model.StopTime();
							gst.arrival_time = st.arrivalTime != null ? st.arrivalTime : Entity.INT_MISSING;
							gst.departure_time = st.departureTime != null ? st.departureTime : Entity.INT_MISSING;
							
							if (st.dropOffType != null)
								gst.drop_off_type = st.dropOffType.toGtfsValue();
							else if (stop.dropOffType != null)
								gst.drop_off_type = stop.dropOffType.toGtfsValue();
							
							if (st.pickupType != null)
								gst.pickup_type = st.pickupType.toGtfsValue();
							else if (stop.dropOffType != null)
								gst.drop_off_type = stop.dropOffType.toGtfsValue();
							
							gst.shape_dist_traveled = ps.shapeDistTraveled;
							gst.stop_headsign = st.stopHeadsign;
							gst.stop_id = stop.getGtfsId();
							
							// write the stop as needed 
							if (!feed.stops.containsKey(gst.stop_id)) {
								feed.stops.put(gst.stop_id, stop.toGtfs());
							}
							
							gst.stop_sequence = stopSequence++;
							
							if (ps.timepoint != null)
								gst.timepoint = ps.timepoint ? 1 : 0;
							else
								gst.timepoint = Entity.INT_MISSING;
		
							gst.trip_id = gtfsTrip.trip_id;
							
							feed.stop_times.put(new Tuple2(gtfsTrip.trip_id, gst.stop_sequence), gst);
						}
						
						// create frequencies as needed
						if (trip.useFrequency != null && trip.useFrequency) {
							Frequency f = new Frequency();
							f.trip = gtfsTrip;
							f.start_time = trip.startTime;
							f.end_time = trip.endTime;
							f.exact_times = 0;
							f.headway_secs = trip.headway;
							feed.frequencies.put(gtfsTrip.trip_id, f);
						}
					}
				}
			}
			
			feed.toFile(output.getAbsolutePath());
		} finally {
			gtx.rollbackIfOpen();
			if (atx != null) atx.rollbackIfOpen();
		}
	}
	
	public static int toGtfsDate (LocalDate date) {
		return date.getYear() * 10000 + date.getMonthOfYear() * 100 + date.getDayOfMonth();
	}
}

