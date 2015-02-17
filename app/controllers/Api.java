package controllers;

import play.*;
import play.mvc.*;
import play.data.binding.As;
import play.db.jpa.JPA;
import utils.GeoUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.*;

import javax.persistence.Entity;
import javax.persistence.Query;

import static java.util.Collections.sort;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.geotools.geometry.jts.JTS;
import org.opengis.referencing.operation.MathTransform;
import org.python.google.common.collect.Collections2;

import com.google.common.base.Function;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.linearref.LengthLocationMap;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;

import models.*;
import models.transit.Agency;
import models.transit.Route;
import models.transit.RouteType;
import models.transit.ScheduleException;
import models.transit.ServiceCalendar;
import models.transit.ServiceCalendar.ServiceCalendarForPattern;
import models.transit.Stop;
import models.transit.StopTime;
import models.transit.Trip;
import models.transit.TripPattern;
import models.transit.TripPatternStop;
import models.transit.TripShape;


@With(Secure.class)
public class Api extends Controller {

	@Before
	static void initSession() throws Throwable {
		 
		if(!Security.isConnected() && !Application.checkOAuth(request, session))
			Secure.login();
		
	}
	
    public static ObjectMapper mapper = new ObjectMapper();
    private static JsonFactory jf = new JsonFactory();

    public static String toJson(Object pojo, boolean prettyPrint)
            throws JsonMappingException, JsonGenerationException, IOException {
                StringWriter sw = new StringWriter();
                JsonGenerator jg = jf.createJsonGenerator(sw);
                if (prettyPrint) {
                    jg.useDefaultPrettyPrinter();
                }
                mapper.writeValue(jg, pojo);
                return sw.toString();
            }


    // **** stop controllers ****


    // **** trip pattern controllers ****
    public static void getTripPattern(Long id, Long routeId) {

        try {
            if(id != null)
            {
                TripPattern tripPattern = TripPattern.findById(id);
                sort(tripPattern.patternStops);
                if(tripPattern != null)
                    renderJSON(Api.toJson(tripPattern, false));
                else
                    notFound();
            }
            else if(routeId != null) {
            	
            	Route r = Route.findById(routeId);
            	
            	if(r == null)
            		badRequest();
            	
            	List<TripPattern> ret = TripPattern.find("route = ?", r).fetch();
            	
            	for (TripPattern pat : ret) {
            	    sort(pat.patternStops);
            	}
            	
            	renderJSON(Api.toJson(ret, false));
            }
            else {
                List<TripPattern> ret = TripPattern.all().fetch();
            
                for (TripPattern pat : ret) {
                    sort(pat.patternStops);
                }
                renderJSON(Api.toJson(ret, false));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }

    public static void createTripPattern() {
        TripPattern tripPattern;

        try {
            tripPattern = mapper.readValue(params.get("body"), TripPattern.class);
            
            if(tripPattern.encodedShape != null) {
            	TripShape ts = TripShape.createFromEncoded(tripPattern.encodedShape);
            	tripPattern.shape = ts;	
            }
            
            tripPattern.save();

            renderJSON(Api.toJson(tripPattern, false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }

    public static void updateTripPattern() {
        TripPattern tripPattern;
        try {
            tripPattern = mapper.readValue(params.get("body"), TripPattern.class);
            
            if(tripPattern.id == null)
                badRequest();
            
            TripPattern originalTripPattern = TripPattern.findById(tripPattern.id);
            
            if(originalTripPattern == null)
                badRequest();
            
            if(tripPattern.encodedShape != null) {
                if(originalTripPattern.shape != null) {
                    originalTripPattern.shape.updateShapeFromEncoded(tripPattern.encodedShape);
                    tripPattern.shape = originalTripPattern.shape;
                }
                else {
                    TripShape ts = TripShape.createFromEncoded(tripPattern.encodedShape);
                    tripPattern.shape = ts;
                }
            }
            
            else {
                tripPattern.shape = null;
                // need to remove old shapes...
            }
            
            
            // update stop times
            originalTripPattern.reconcilePatternStops(tripPattern);
            
            TripPattern updatedTripPattern = TripPattern.em().merge(tripPattern);
            updatedTripPattern.save();
            
            // save updated stop times
            for (Object trip : Trip.find("pattern = ?", updatedTripPattern).fetch()) {
                for (StopTime st : ((Trip) trip).getStopTimes()) {
                    st.save();
                }
            }

            Set<Long> patternStopIds = new HashSet<Long>();
            for(TripPatternStop patternStop : updatedTripPattern.patternStops) {
                patternStopIds.add(patternStop.id);
            }
            
            List<TripPatternStop> patternStops = TripPatternStop.find("pattern = ?", tripPattern).fetch();
            for(TripPatternStop patternStop : patternStops) {
                if(!patternStopIds.contains(patternStop.id))
                    patternStop.delete();
            }
            
            if(tripPattern.shape != null) {
                MathTransform mt = GeoUtils.getTransform(new Coordinate(tripPattern.shape.shape.getCoordinateN(0).y, tripPattern.shape.shape.getCoordinateN(0).x));
                GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
                Coordinate[] mCoords = tripPattern.shape.shape.getCoordinates();
                ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
                for(Coordinate mCoord : mCoords) {
                    coords.add(new Coordinate(mCoord.x, mCoord.y));
                }
                Coordinate[] coordArray = coords.toArray(new Coordinate[coords.size()]);
                LineString ls = (LineString) JTS.transform(geometryFactory.createLineString(coordArray), mt);
                LocationIndexedLine indexLine = new LocationIndexedLine(ls);
                Logger.info("length: " + ls.getLength());
                patternStops = TripPatternStop.find("pattern = ?", tripPattern).fetch();
                for(TripPatternStop patternStop : patternStops) {
                    Point p = (Point) JTS.transform(patternStop.stop.locationPoint(), mt);
                    LinearLocation l = indexLine.project(p.getCoordinate());
                    patternStop.defaultDistance = LengthLocationMap.getLength(ls, l);
                    patternStop.save();
                }
            }
            
            // make sure that things are persisted; avoid DB race conditions
            // once upon a time, there was a bug in this code that only manifested itself once the entity manager
            // had been flushed, either here or by GC.
            JPA.em().flush();
            
            renderJSON(Api.toJson(updatedTripPattern, false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }

    public static void deleteTripPattern(Long id) {
        if(id == null) {
            badRequest();
            return;
        }

        TripPattern tripPattern = TripPattern.findById(id);

        if(tripPattern == null) {
            badRequest();
            return;
        }

        tripPattern.delete();
       	
        ok();
    }
    
    public static void calcTripPatternTimes(Long id, Double velocity, int defaultDwell) {
    	
    	TripPattern tripPattern = TripPattern.findById(id);
    	
    	List<TripPatternStop> patternStops = TripPatternStop.find("pattern = ? ORDER BY stopSequence", tripPattern).fetch();
    	
    	Double distanceAlongLine = 0.0;
    	
        for(TripPatternStop patternStop : patternStops)
        {
        	patternStop.defaultTravelTime = (int) Math.round((patternStop.defaultDistance - distanceAlongLine) / velocity);
            patternStop.defaultDwellTime = defaultDwell;
        	
        	distanceAlongLine = patternStop.defaultDistance;
        	
        	patternStop.save();
        }
    
        ok();
    }
   
    
    // **** calendar controllers ****

    public static void getCalendar(Long id, Long agencyId, Long patternId) {
    	try {
    		if(id != null) {
    			ServiceCalendar cal = ServiceCalendar.findById(id);
    			if(cal != null)
    				renderJSON(Api.toJson(cal, false));
    			else
    				notFound();
    		}
    		else if(agencyId != null) {
    			Agency agency = Agency.findById(agencyId);
    			renderJSON(Api.toJson(ServiceCalendar.find("agency = ?", agency).fetch(), false));
    		}
    		else if (patternId != null) {
    			TripPattern p = TripPattern.findById(patternId);
    			Query q = ServiceCalendar.em().createQuery("SELECT DISTINCT t.serviceCalendar FROM Trip t WHERE pattern_id = ?");
    			q.setParameter(1, p.id);
    			List<ServiceCalendar> c = q.getResultList();
    			
    			ServiceCalendarForPattern[] ret = new ServiceCalendarForPattern[c.size()];
    			for (int i = 0; i < ret.length; i++) {
    				ret[i] = new ServiceCalendarForPattern(c.get(i), p);
    			}
    			
    			renderJSON(Api.toJson(ret, false));
    		}
    		else {
    			renderJSON(Api.toJson(ServiceCalendar.all().fetch(), false));
    		}
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }

    }

    public static void createCalendar() {
    	ServiceCalendar cal;

        try {
            cal = mapper.readValue(params.get("body"), ServiceCalendar.class);

            if(Agency.findById(cal.agency.id) == null) {
                badRequest();
                return;
            }

            cal.save();

            // check if gtfsServiceId is specified, if not create from DB id
            if(cal.gtfsServiceId == null) {
            	cal.gtfsServiceId = "CAL_" + cal.id.toString();
                cal.save();
            }
            

            renderJSON(Api.toJson(cal, false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }


    public static void updateCalendar() {
    	ServiceCalendar cal;

        try {
        	cal = mapper.readValue(params.get("body"), ServiceCalendar.class);

            if(cal.id == null || ServiceCalendar.findById(cal.id) == null) {
                badRequest();
                return;
            }

            // check if gtfsAgencyId is specified, if not create from DB id
            if(cal.gtfsServiceId == null)
            	cal.gtfsServiceId = "CAL_" + cal.id.toString();
            
            ServiceCalendar updatedCal = ServiceCalendar.em().merge(cal);
            updatedCal.save();

            renderJSON(Api.toJson(updatedCal, false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }

    public static void deleteCalendar(Long id) {
        if(id == null)
            badRequest();

        ServiceCalendar cal = ServiceCalendar.findById(id);

        if(cal == null)
            badRequest();

        cal.delete();

        ok();
    }

    // trip controllers

    public static void getTrip(Long id, Long patternId, Long calendarId, Long agencyId) {
        try {
            if(id != null)
            {
                Trip trip = Trip.findById(id);
                if(trip != null)
                    renderJSON(Api.toJson(trip, false));
                else
                    notFound();
            }
            else {

                if(agencyId != null) {
                    Agency agency = Agency.findById(agencyId);
                    renderJSON(Api.toJson(Trip.find("pattern.route.agency = ?", agency).fetch(), false));
                }
                
                else if (patternId != null && calendarId != null) {
                    TripPattern pattern = TripPattern.findById(patternId);
                    ServiceCalendar calendar = ServiceCalendar.findById(calendarId);
                    renderJSON(Api.toJson(Trip.find("byPatternAndServiceCalendar", pattern, calendar).fetch(), false));
                }
                
                else if(patternId != null) {
                    TripPattern pattern = TripPattern.findById(patternId);
                    renderJSON(Api.toJson(Trip.find("pattern = ?", pattern).fetch(), false));
                }
                else {
                    renderJSON(Api.toJson(Trip.all().fetch(), false));
                }
            }
                
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }

    }

    /**
     * When trips come back over the wire, they contain stop times directly due to hierarchical serialization. 
     */
    public static class TripWithStopTimes extends Trip {
        List<StopTimeWithDeletion> stopTimes;
        
        public Trip toTrip () {
            Trip ret = new Trip();
            ret.blockId = this.blockId;
            ret.endTime = this.endTime;
            ret.gtfsTripId = this.gtfsTripId;
            ret.headway = this.headway;
            ret.id = this.id;
            ret.pattern = this.pattern;
            ret.route = this.route;
            ret.serviceCalendar = this.serviceCalendar;
            ret.serviceCalendarDate = this.serviceCalendarDate;
            ret.shape = this.shape;
            ret.startTime = this.startTime;
            ret.tripDescription = this.tripDescription;
            ret.tripDirection = this.tripDirection;
            ret.tripHeadsign = this.tripHeadsign;
            ret.tripShortName = this.tripShortName;
            ret.useFrequency = this.useFrequency;
            ret.wheelchairBoarding = this.wheelchairBoarding;
            return ret;
        }
    }
    
    /**
     * When StopTimes come back, they may also have the field deleted, which if true indicate that this stop time has
     * been deleted (i.e. trip no longer stops here).
     */
    public static class StopTimeWithDeletion extends StopTime {
        public Boolean deleted;
        
        public StopTime toStopTime () {
            StopTime ret = new StopTime();
            ret.id = this.id;
            ret.arrivalTime = this.arrivalTime;
            ret.departureTime = this.departureTime;
            ret.dropOffType = this.dropOffType;
            ret.patternStop = this.patternStop;
            ret.pickupType = this.pickupType;
            ret.shapeDistTraveled = this.shapeDistTraveled;
            ret.stop = this.stop;
            ret.stopHeadsign = this.stopHeadsign;
            ret.stopSequence = this.stopSequence;
            ret.trip = this.trip;
            return ret;
        }
    }
    
    public static void createTrip() {
        TripWithStopTimes tripWithStopTimes = null;
        Trip trip = null;

        try {
            try {
                tripWithStopTimes = mapper.readValue(params.get("body"), TripWithStopTimes.class);
            } catch (Exception e) {
                trip = mapper.readValue(params.get("body"), Trip.class);
            }

            if (tripWithStopTimes != null) {
                trip = tripWithStopTimes.toTrip();
            }
            
            if(Route.findById(trip.pattern.route.id) == null)
                badRequest();

            // if endtime is before start time add a day (e.g 07:00-00:30 becomes 07:00-24:30)
            if(trip != null && trip.useFrequency != null && trip.endTime != null &&  trip.useFrequency  && trip.startTime != null && trip.endTime < trip.startTime) {
            	trip.endTime += (24 * 60 * 60 );
            }
            
            trip.save();

            // check if gtfsRouteId is specified, if not create from DB id
            if(trip.gtfsTripId == null) {
                trip.gtfsTripId = "TRIP_" + trip.id.toString();
                trip.save();
            }
            
            if (tripWithStopTimes != null && tripWithStopTimes.stopTimes != null) {
                for (StopTimeWithDeletion stopTime: tripWithStopTimes.stopTimes) {
                    stopTime.trip = trip;
                    stopTime.toStopTime().save();
                }
            }

            renderJSON(Api.toJson(trip, false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }
    
    public static void updateTrip() {
        TripWithStopTimes trip;
        
        try {
            trip = mapper.readValue(params.get("body"), TripWithStopTimes.class);

            if(trip.id == null || Trip.findById(trip.id) == null)
                badRequest();
            
            // if endtime is before start time add a day (e.g 07:00-00:30 becomes 07:00-24:30)
            if(trip.useFrequency && trip.endTime < trip.startTime) {
            	trip.endTime += (24 * 60 * 60 );
            }

            // check if gtfsRouteId is specified, if not create from DB id
             if(trip.gtfsTripId == null) {
                trip.gtfsTripId = "TRIP_" + trip.id.toString();
            }

             Trip updatedTrip = Trip.em().merge(trip.toTrip());
             
            // update the stop times
            for (StopTimeWithDeletion stopTime : trip.stopTimes) {
                if (Boolean.TRUE.equals(stopTime.deleted)) {
                    StopTime.delete("id = ? AND trip = ?", stopTime.id, updatedTrip);
                }
                else {
                    StopTime updatedStopTime = StopTime.em().merge(stopTime.toStopTime());
                    // this was getting lost somehow
                    updatedStopTime.trip = updatedTrip;
                    updatedStopTime.save();
                }
            }
            
            updatedTrip.save();

            renderJSON(Api.toJson(updatedTrip, false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }

    public static void deleteTrip(Long id) {
        if(id == null)
            badRequest();

        Trip trip = Trip.findById(id);

        if(trip == null)
            badRequest();

        StopTime.delete("trip = ?", trip); 
 
        trip.delete();

        ok();
    }

    // ************ schedule exception controllers ***************
    
    /** Get all of the schedule exceptions for an agency */
    public static void getScheduleException (Long exceptionId, Long agencyId) {
    	try {
    		if (agencyId != null) {
    			Agency agency = Agency.findById(agencyId);
    			List<ScheduleException> exceptions = ScheduleException.find("agency = ?", agency).fetch();
    	
    			renderJSON(Api.toJson(exceptions, true));
    		}
    		else {
    			ScheduleException e = ScheduleException.findById(exceptionId);
    			if (e == null) {
    				notFound();
    				return;
    			}
    			
    			renderJSON(Api.toJson(e, false));    				
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    		badRequest();
    	}
    }
    
    public static void createScheduleException () {
    	try {
			ScheduleException ex = mapper.readValue(params.get("body"), ScheduleException.class);
			
			if (Agency.findById(ex.agency.id) == null) {
				badRequest();
				return;
			}
			
			ex.save();
			
			renderJSON(Api.toJson(ex, false));			
		} catch (Exception e) {
			e.printStackTrace();
			badRequest();
		}    	
    }
    
    public static void updateScheduleException () {
    	try {
			ScheduleException ex = mapper.readValue(params.get("body"), ScheduleException.class);
			
			if (ex.id == null || ScheduleException.findById(ex.id) == null) {
				badRequest();
				return;
			}
			
			ScheduleException updated = ScheduleException.em().merge(ex);
			updated.save();
			
			renderJSON(Api.toJson(updated, false));
    	} catch (Exception e) {
    		e.printStackTrace();
    		badRequest();
    	}
    }
    
    public static void deleteScheduleException (Long id) {
    	try {
			ScheduleException.<ScheduleException>findById(id).delete();
			ok();
    	} catch (Exception e) {
    		e.printStackTrace();
    		badRequest();
    	}
    }
}