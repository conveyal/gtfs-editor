package controllers;

import play.*;
import play.mvc.*;
import play.data.binding.As;
import utils.GeoUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.*;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import  org.codehaus.jackson.map.ObjectMapper;
import org.geotools.geometry.jts.JTS;
import org.opengis.referencing.operation.MathTransform;

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
import models.transit.ServiceCalendar;
import models.transit.Stop;
import models.transit.StopTime;
import models.transit.Trip;
import models.transit.TripPattern;
import models.transit.TripPatternStop;
import models.transit.TripShape;

public class Api extends Controller {

    private static ObjectMapper mapper = new ObjectMapper();
    private static JsonFactory jf = new JsonFactory();

    private static String toJson(Object pojo, boolean prettyPrint)
            throws JsonMappingException, JsonGenerationException, IOException {
                StringWriter sw = new StringWriter();
                JsonGenerator jg = jf.createJsonGenerator(sw);
                if (prettyPrint) {
                    jg.useDefaultPrettyPrinter();
                }
                mapper.writeValue(jg, pojo);
                return sw.toString();
            }

    // **** agency controllers ****

    public static void getAgency(Long id) {
        try {
            if(id != null) {
                Agency agency = Agency.findById(id);
                if(agency != null)
                    renderJSON(Api.toJson(agency, false));
                else
                    notFound();
            }
            else {
                renderJSON(Api.toJson(Agency.all().fetch(), false));
            }
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }

    }

    public static void createAgency() {
        Agency agency;

        try {
            agency = mapper.readValue(params.get("body"), Agency.class);
            agency.save();

            // check if gtfsAgencyId is specified, if not create from DB id
            if(agency.gtfsAgencyId == null) {
                agency.gtfsAgencyId = "AGENCY_" + agency.id.toString();
                agency.save();
            }

            renderJSON(Api.toJson(agency, false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }


    public static void updateAgency() {
        Agency agency;

        try {
            agency = mapper.readValue(params.get("body"), Agency.class);

            if(agency.id == null || Agency.findById(agency.id) == null)
                badRequest();
            
            // check if gtfsAgencyId is specified, if not create from DB id
            if(agency.gtfsAgencyId == null)
            	agency.gtfsAgencyId = "AGENCY_" + agency.id.toString();

            Agency updatedAgency = Agency.em().merge(agency);
            updatedAgency.save();

            renderJSON(Api.toJson(updatedAgency, false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }

    public static void deleteAgency(Long id) {
        if(id == null)
            badRequest();

        Agency agency = Agency.findById(id);

        if(agency == null)
            badRequest();

        agency.delete();

        ok();
    }

 // **** route controllers ****

    public static void getRouteType(Long id) {
        try {
            if(id != null)
            {
            	RouteType routeType = RouteType.findById(id);
                if(routeType != null)
                    renderJSON(Api.toJson(routeType, false));
                else
                    notFound();
            }
            else
                renderJSON(Api.toJson(Route.all().fetch(), false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }

    }

    public static void createRouteType() {
    	RouteType routeType;

        try {
            routeType = mapper.readValue(params.get("body"), RouteType.class);

            routeType.save();
            renderJSON(Api.toJson(routeType, false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }


    public static void updateRouteType() {
    	RouteType routeType;

        try {
        	routeType = mapper.readValue(params.get("body"), RouteType.class);

            if(routeType.id == null ||RouteType.findById(routeType.id) == null)
                badRequest();

        
            RouteType updatedRouteType = RouteType.em().merge(routeType);
            updatedRouteType.save();

            renderJSON(Api.toJson(updatedRouteType, false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }

    public static void deleteRouteType(Long id) {
        if(id == null)
            badRequest();

        RouteType routeType = RouteType.findById(id);

        if(routeType == null)
            badRequest();

        routeType.delete();

        ok();
    }

    
    
    
    // **** route controllers ****

    public static void getRoute(Long id, Long agencyId) {
        try {
            if(id != null)
            {
                Route route = Route.findById(id);
                if(route != null)
                    renderJSON(Api.toJson(route, false));
                else
                    notFound();
            }
            else {
                if(agencyId != null) {
                    Agency agency = Agency.findById(agencyId);
                    renderJSON(Api.toJson(Route.find("agency = ? order by routeShortName", agency).fetch(), false));
                }
                else
                    renderJSON(Api.toJson(Route.find("order by routeShortName").fetch(), false));   
                    
            }
                
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }

    }

    public static void createRoute() {
        Route route;

        try {
            route = mapper.readValue(params.get("body"), Route.class);

            if(Agency.findById(route.agency.id) == null)
                badRequest();

            route.save();

            // check if gtfsRouteId is specified, if not create from DB id
            if(route.gtfsRouteId == null) {
                route.gtfsRouteId = "ROUTE_" + route.id.toString();
                route.save();
            }

            renderJSON(Api.toJson(route, false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }


    public static void updateRoute() {
        Route route;

        try {
            route = mapper.readValue(params.get("body"), Route.class);

            if(route.id == null || Route.findById(route.id) == null)
                badRequest();

            // check if gtfsRouteId is specified, if not create from DB id
            if(route.gtfsRouteId == null)
                route.gtfsRouteId = "ROUTE_" + route.id.toString();

            Route updatedRoute = Route.em().merge(route);
            updatedRoute.save();

            renderJSON(Api.toJson(updatedRoute, false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }

    public static void deleteRoute(Long id) {
        if(id == null)
            badRequest();

        Route route = Route.findById(id);

        if(route == null)
            badRequest();

        route.delete();

        ok();
    }

    // **** stop controllers ****
    public static void getStop(Long id, Double lat, Double lon, Boolean majorStops, Long agencyId) {

    	Agency agency = null;
    	if(agencyId != null)
    		agency = Agency.findById(agencyId);
    	
        try {
            if(id != null) {
                Stop stop = Stop.findById(id);
                if(stop != null)
                    renderJSON(Api.toJson(stop, false));
                else
                    notFound();
            }
            else if (majorStops != null && majorStops) {

                if(agency != null)
                    renderJSON(Api.toJson(Stop.find("agency = ? and majorStop = true", agency).fetch(), false));
                else
            	   renderJSON(Api.toJson(Stop.find("majorStop = true").fetch(), false));
            }
            else if (lat != null && lon != null) {
            	//GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(),4326);
            	//Geometry point  =  geometryFactory.createPoint(new Coordinate(lon,lat));

            	String point = "POINT(" + lon + " " + lat + ")";
            	
                if(agency != null)
            	    renderJSON(Api.toJson(Stop.find("agency = ? and distance(location, geomfromtext(?, 4326)) < 0.025", agency, point).fetch(), false));
                else
                    renderJSON(Api.toJson(Stop.find("distance(location, geomfromtext(?, 4326)) < 0.025", point).fetch(), false));
            }
            else {
                
                if(agency != null)
                    renderJSON(Api.toJson(Stop.find("agency = ?", agency).fetch(), false));
                else
                    renderJSON(Api.toJson(Stop.all().fetch(), false));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }

    public static void createStop() {
        Stop stop;

        try {
            stop = mapper.readValue(params.get("body"), Stop.class);

            if(Agency.findById(stop.agency.id) == null)
                badRequest();

            stop.save();

            // check if gtfsRouteId is specified, if not create from DB id
            if(stop.gtfsStopId == null) {
                stop.gtfsStopId = "STOP_" + stop.id.toString();
                stop.save();
            }

            renderJSON(Api.toJson(stop, false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }


    public static void updateStop() {
        Stop stop;

        try {
            stop = mapper.readValue(params.get("body"), Stop.class);

            if(stop.id == null || Stop.findById(stop.id) == null)
                badRequest();

            // check if gtfsRouteId is specified, if not create from DB id
            if(stop.gtfsStopId == null)
                stop.gtfsStopId = "STOP_" + stop.id.toString();

            Stop updatedStop = Stop.em().merge(stop);
            updatedStop.save();

            renderJSON(Api.toJson(updatedStop, false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }

    public static void deleteStop(Long id) {
        if(id == null)
            badRequest();

        Stop stop = Stop.findById(id);

        if(stop == null)
            badRequest();

        stop.delete();

        ok();
    }
    
    
    public static void findDuplicateStops(Long agencyId) {

    	try {    	
    		
    		List<List<Stop>> duplicateStopPairs = Stop.findDuplicateStops(BigInteger.valueOf(agencyId.longValue()));
    		renderJSON(Api.toJson(duplicateStopPairs, false));
    		
    	 } catch (Exception e) {
             e.printStackTrace();
             badRequest();
         }
    }

    public static void mergeStops(Long stop1Id, @As(",") List<String> mergedStopIds) {
        
        if(stop1Id == null)
            badRequest();

        Stop stop1 = Stop.findById(stop1Id);

        for(String stopIdStr : mergedStopIds) {

            Stop stop2 = Stop.findById(Long.parseLong(stopIdStr));

            if(stop1 == null && stop2 == null)
                badRequest();

            stop1.merge(stop2);

            ok();
        }
    }

    // **** trip pattern controllers ****
    public static void getTripPattern(Long id, Long routeId) {

        try {
            if(id != null)
            {
                TripPattern tripPattern = TripPattern.findById(id);
                if(tripPattern != null)
                    renderJSON(Api.toJson(tripPattern, false));
                else
                    notFound();
            }
            else if(routeId != null) {
            	
            	Route r = Route.findById(routeId);
            	
            	if(r == null)
            		badRequest();
            	
            	renderJSON(Api.toJson(TripPattern.find("route = ?", r).fetch(), false));
            }
            else
                renderJSON(Api.toJson(TripPattern.all().fetch(), false));
            
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

            TripPattern updatedTripPattern = TripPattern.em().merge(tripPattern);
            updatedTripPattern.save();
            
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
	            
	            Coordinate[] mCoords =  tripPattern.shape.shape.getCoordinates();
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
            
            

            renderJSON(Api.toJson(updatedTripPattern, false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }

    public static void deleteTripPattern(Long id) {
        if(id == null)
            badRequest();

        TripPattern tripPattern = TripPattern.findById(id);

        if(tripPattern == null)
            badRequest();

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

    public static void getCalendar(Long id, Long agencyId) {
        try {
            if(id != null) {
            	ServiceCalendar cal = ServiceCalendar.findById(id);
                if(cal != null)
                    renderJSON(Api.toJson(cal, false));
                else
                    notFound();
            }
            else {
                if(agencyId != null) {

                    Agency agency = Agency.findById(agencyId);
                    renderJSON(Api.toJson(ServiceCalendar.find("agency = ?", agency).fetch(), false));
                }
                else
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

            if(Agency.findById(cal.agency.id) == null)
                badRequest();

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

            if(cal.id == null || ServiceCalendar.findById(cal.id) == null)
                badRequest();

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

    // **** route controllers ****

    public static void getTrip(Long id, Long patternId, Long agencyId) {
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
                if(patternId != null) {
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

    public static void createTrip() {
        Trip trip;

        try {
            trip = mapper.readValue(params.get("body"), Trip.class);

            if(Route.findById(trip.pattern.route.id) == null)
                badRequest();

            // if endtime is before start time add a day (e.g 07:00-00:30 becomes 07:00-24:30)
            if(trip.useFrequency && trip.endTime < trip.startTime) {
            	trip.endTime += (24 * 60 * 60 );
            }
            
            trip.save();

            // check if gtfsRouteId is specified, if not create from DB id
            if(trip.gtfsTripId == null) {
                trip.gtfsTripId = "TRIP_" + trip.id.toString();
                trip.save();
            }

            renderJSON(Api.toJson(trip, false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }


    public static void updateTrip() {
        Trip trip;

        try {
            trip = mapper.readValue(params.get("body"), Trip.class);

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


            Trip updatedTrip = Trip.em().merge(trip);
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

        trip.delete();

        ok();
    }


}