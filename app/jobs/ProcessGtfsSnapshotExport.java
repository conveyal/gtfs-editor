package jobs;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.zip.ZipFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.onebusaway.gtfs.serialization.GtfsWriter;

import com.mchange.v2.c3p0.impl.DbAuth;
import com.vividsolutions.jts.geom.Coordinate;

import models.gtfs.GtfsSnapshotExport;
import models.gtfs.GtfsSnapshotExportStatus;
import models.gtfs.GtfsSnapshotMerge;
import models.gtfs.GtfsSnapshotMergeTask;
import models.gtfs.GtfsSnapshotMergeTaskStatus;
import models.transit.Agency;
import models.transit.Route;
import models.transit.ServiceCalendar;
import models.transit.ServiceCalendarDate;
import models.transit.ServiceCalendarDateType;
import models.transit.Stop;
import models.transit.StopTime;
import models.transit.TripPatternStop;
import models.transit.TripShape;
import models.transit.Trip;


import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import utils.DirectoryZip;

public class ProcessGtfsSnapshotExport extends Job {

	private Long _gtfsSnapshotExportId;
	
	public ProcessGtfsSnapshotExport(Long gtfsSnapshotExportId)
	{
		this._gtfsSnapshotExportId = gtfsSnapshotExportId;
	}
	
	public void doJob() {
		
		GtfsSnapshotExport snapshotExport = null;
		while(snapshotExport == null)
		{
			snapshotExport = GtfsSnapshotExport.findById(this._gtfsSnapshotExportId);
			Logger.warn("Waiting for snapshotExport to save...");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try 
		{
			GtfsWriter writer = new GtfsWriter();
			GtfsDaoImpl store = new GtfsDaoImpl();
			
			File gtfsDirectory = new File(Play.configuration.getProperty("application.publicDataDirectory"), snapshotExport.getDirectory());
			File gtfsZip = new File(Play.configuration.getProperty("application.publicDataDirectory"), snapshotExport.getDirectory() + ".zip");
			
			writer.setOutputLocation(gtfsDirectory);
		
			HashMap<Long, org.onebusaway.gtfs.model.Stop> stopList = new HashMap<Long, org.onebusaway.gtfs.model.Stop>();
			HashMap<Long, org.onebusaway.gtfs.model.Route> routeList = new HashMap<Long, org.onebusaway.gtfs.model.Route>();
			HashMap<Long, org.onebusaway.gtfs.model.AgencyAndId> shapeList = new HashMap<Long, org.onebusaway.gtfs.model.AgencyAndId>();
		
			
			for(Agency agency : snapshotExport.agencies)
			{
				org.onebusaway.gtfs.model.Agency a = new org.onebusaway.gtfs.model.Agency();
				
				String gtfsAgencyId = agency.id.toString();
				
				
				if(agency.gtfsAgencyId != null && !agency.gtfsAgencyId.isEmpty())
					gtfsAgencyId = agency.gtfsAgencyId;
				
				a.setId(gtfsAgencyId);
				a.setName(agency.name);
				a.setUrl(agency.url);
				a.setTimezone(agency.timezone);
				
				store.saveEntity(a);
						
				List<ServiceCalendar> calendars = ServiceCalendar.find("agency = ?", agency).fetch();
				
				for(ServiceCalendar calendar : calendars)
				{
					org.onebusaway.gtfs.model.ServiceCalendar c = new org.onebusaway.gtfs.model.ServiceCalendar();
					
					AgencyAndId calendarId = new AgencyAndId(); 
					
					calendarId.setAgencyId(gtfsAgencyId);
					calendarId.setId(calendar.getId().toString());
				
					c.setServiceId(calendarId);
					
					c.setStartDate(new ServiceDate(snapshotExport.calendarFrom)); // calendar.startDate

					c.setEndDate(new ServiceDate(snapshotExport.calendarTo)); // calendar.endDate
					
					c.setMonday(calendar.monday? 1 : 0);
					c.setTuesday(calendar.tuesday? 1 : 0);
					c.setWednesday(calendar.wednesday? 1 : 0);
					c.setThursday(calendar.thursday? 1 : 0);
					c.setFriday(calendar.friday? 1 : 0);
					c.setSaturday(calendar.saturday? 1 : 0);
					c.setSunday(calendar.sunday? 1 : 0);
				
					store.saveEntity(c);
					
					List<ServiceCalendarDate> calendarDates = ServiceCalendarDate.find("calendar = ?", calendar).fetch();
					
					for(ServiceCalendarDate calendarDate : calendarDates)
					{
						org.onebusaway.gtfs.model.ServiceCalendarDate cDate = new org.onebusaway.gtfs.model.ServiceCalendarDate();
						
						cDate.setServiceId(calendarId);
						
						cDate.setDate(new ServiceDate(calendarDate.date));
						
						cDate.setExceptionType(calendarDate.exceptionType == ServiceCalendarDateType.ADDED? 1 : 0);
						
						store.saveEntity(cDate);
					}
					
					List<Trip> trips = Trip.find("serviceCalendar = ?", calendar).fetch();
					
					for(Trip trip : trips)
					{	
						List<TripPatternStop> patternStopTimes = TripPatternStop.find("pattern = ? order by stopSequence", trip.pattern).fetch();
						
						if(trip.useFrequency == null || patternStopTimes == null || (trip.useFrequency && patternStopTimes.size() == 0) || !trip.pattern.route.agency.id.equals(agency.id) || (trip.useFrequency && trip.headway.equals(0)) || (trip.useFrequency && trip.startTime.equals(trip.endTime)))
							continue;

						if(!routeList.containsKey(trip.pattern.route.id))
						{
							Route route = trip.pattern.route;
							
							org.onebusaway.gtfs.model.Route r = new org.onebusaway.gtfs.model.Route();
							
							AgencyAndId routeId = new AgencyAndId(); 
							
							routeId.setAgencyId(gtfsAgencyId);
							
							if(route.gtfsRouteId != null && !route.gtfsRouteId.isEmpty())
								routeId.setId(route.gtfsRouteId);
							else
								routeId.setId(route.id.toString());
							Logger.info(gtfsAgencyId + " " + routeId);
							r.setId(routeId);
							r.setAgency(a);
							
							if(route.routeColor != null && !route.routeColor.isEmpty())
								r.setColor(route.routeColor.replace("#", ""));
							
							if(route.routeDesc != null)
								r.setDesc(route.routeDesc.replace("\n", "").replace("\r", ""));
							
							r.setLongName(route.routeLongName);
							r.setShortName(route.routeShortName);
							r.setType(Route.mapGtfsRouteType(route.routeType));
							r.setUrl(route.routeUrl);
							
							store.saveEntity(r);
							
							routeList.put(route.id, r);
						}
						
						if(trip.pattern.shape != null && !shapeList.containsKey(trip.pattern.shape.id))
						{
							TripShape shape = trip.pattern.shape;
							
							AgencyAndId shapeId = new AgencyAndId(); 
							
							shapeId.setAgencyId(gtfsAgencyId);
							shapeId.setId(shape.id.toString());
							
							int sequence = 0;
							
							for(Coordinate coordinate : shape.shape.getCoordinates())
							{
								org.onebusaway.gtfs.model.ShapePoint coord = new org.onebusaway.gtfs.model.ShapePoint();
								
								coord.setShapeId(shapeId);
								
								coord.setLon(coordinate.y);
								coord.setLat(coordinate.x);
								coord.setSequence(sequence);
								
								sequence++;
								
								store.saveEntity(coord);
							}
							
							shapeList.put(shape.id, shapeId);
						}
						
						
						
						
						org.onebusaway.gtfs.model.Trip t = new org.onebusaway.gtfs.model.Trip();
						
						AgencyAndId tripId = new AgencyAndId(); 
						
						tripId.setAgencyId(gtfsAgencyId);
						tripId.setId(trip.getId().toString());
						
						t.setId(tripId);
						t.setRoute(routeList.get(trip.pattern.route.id));
						t.setRouteShortName(trip.pattern.route.routeShortName);
						t.setTripHeadsign(trip.pattern.name);
						t.setServiceId(calendarId);
						
						if(trip.pattern.shape != null)
							t.setShapeId(shapeList.get(trip.pattern.shape.id));
						else
							Logger.error("trip " + trip.tripHeadsign + " is missing shape");
						
						t.setBlockId(trip.blockId);
						
						store.saveEntity(t);
						
						
						
						
						if(trip.useFrequency != null && trip.useFrequency && trip.headway > 0)
						{
							org.onebusaway.gtfs.model.Frequency f = new org.onebusaway.gtfs.model.Frequency();
											
							f.setTrip(t);
							
							f.setStartTime(trip.startTime);
							f.setEndTime(trip.endTime);
							f.setHeadwaySecs(trip.headway);
							
							store.saveEntity(f);
							
							
							
							Integer cumulativeTime = 0;
							
							for(TripPatternStop stopTime : patternStopTimes)
							{
								if(!stopList.containsKey(stopTime.stop.id))
								{
									Stop stop = stopTime.stop;
									
									AgencyAndId stopId = new AgencyAndId(); 
									
									stopId.setAgencyId(gtfsAgencyId);
							
									if(stop.gtfsStopId != null && !stop.gtfsStopId.isEmpty())
										stopId.setId(stop.gtfsStopId);
									else
										stopId.setId("STOP_" + stop.id.toString());
									
									org.onebusaway.gtfs.model.Stop s = new org.onebusaway.gtfs.model.Stop();
									
									s.setId(stopId);
									
									s.setCode(stop.stopCode);
									
									if(stop.stopName == null || stop.stopName.isEmpty())
										s.setName(stop.id.toString());
									else
										s.setName(stop.stopName.replace("\n", "").replace("\r", ""));
									
									if(stop.stopDesc != null && !stop.stopName.isEmpty())
										s.setDesc(stop.stopDesc.replace("\n", "").replace("\r", ""));
									
									s.setUrl(stop.stopUrl);
									
									s.setLat(stop.locationPoint().getX());
									s.setLon(stop.locationPoint().getY());
									
									store.saveEntity(s);
																	
									stopList.put(stop.id, s);
								}
								
								org.onebusaway.gtfs.model.StopTime st = new org.onebusaway.gtfs.model.StopTime();
								
								if(stopTime.defaultTravelTime != null) {
									
									// need to flag negative travel times in the patterns!
									if(stopTime.defaultTravelTime < 0)
										cumulativeTime -= stopTime.defaultTravelTime;
									else
										cumulativeTime += stopTime.defaultTravelTime;	
								}
									
								
								st.setArrivalTime(cumulativeTime);
								
								if(stopTime.defaultDwellTime != null) {
									
									// need to flag negative dwell times in the patterns!
									if(stopTime.defaultDwellTime < 0)
										cumulativeTime -= stopTime.defaultDwellTime;
									else
										cumulativeTime += stopTime.defaultDwellTime;
								}
									
								
								st.setDepartureTime(cumulativeTime);
								
								st.setTrip(t);
								st.setStop(stopList.get(stopTime.stop.id));
								st.setStopSequence(stopTime.stopSequence);
					
											
								store.saveEntity(st);
							}

						}
						else
						{
						
							List<StopTime> stopTimes = StopTime.find("trip = ? order by stopSequence", trip).fetch();
							
							for(StopTime stopTime : stopTimes)
							{
								if(!stopList.containsKey(stopTime.stop.id))
								{
									Stop stop = stopTime.stop;
									
									AgencyAndId stopId = new AgencyAndId(); 
									
									stopId.setAgencyId(gtfsAgencyId);
									
									if(stop.gtfsStopId != null && !stop.gtfsStopId.isEmpty())
										stopId.setId(stop.gtfsStopId);
									else
										stopId.setId(stop.id.toString());
									
									org.onebusaway.gtfs.model.Stop s = new org.onebusaway.gtfs.model.Stop();
									
									s.setId(stopId);
									
									s.setCode(stop.stopCode);
									if(stop.stopName == null || stop.stopName.isEmpty())
										s.setName(stop.id.toString());
									else
										s.setName(stop.stopName.replace("\n", "").replace("\r", ""));
									
									if(stop.stopDesc != null && !stop.stopName.isEmpty())
										s.setDesc(stop.stopDesc.replace("\n", "").replace("\r", ""));
									
									s.setUrl(stop.stopUrl);
									
									s.setLon(stop.locationPoint().getX());
									s.setLat(stop.locationPoint().getY());
									
									store.saveEntity(s);
																	
									stopList.put(stop.id, s);
								}
								
								org.onebusaway.gtfs.model.StopTime st = new org.onebusaway.gtfs.model.StopTime();
								
								if(stopTime.arrivalTime != null)
									st.setArrivalTime(stopTime.arrivalTime);
								if(stopTime.departureTime != null)
									st.setDepartureTime(stopTime.departureTime);
								
								st.setTrip(t);
								st.setStop(stopList.get(stopTime.stop.id));
								st.setStopSequence(stopTime.stopSequence);
					
											
								store.saveEntity(st);
							}
						}
					}
				}
				
			}
			
			writer.run(store);
			writer.flush();
			writer.close();
		
			DirectoryZip.zip(gtfsDirectory, gtfsZip);
			FileUtils.deleteDirectory(gtfsDirectory);
			
			snapshotExport.status = GtfsSnapshotExportStatus.SUCCESS;
			
			snapshotExport.save();
		
		}
		catch(Exception e)
		{
			Logger.error("error");
			e.printStackTrace();
		}
	}
}

