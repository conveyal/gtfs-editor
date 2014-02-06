package controllers;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Date;

import org.geotools.geometry.jts.JTS;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;

import controllers.Secure.Security;
import models.Account;
import models.transit.Agency;
import models.transit.GtfsRouteType;
import models.transit.Route;
import models.transit.Stop;
import models.transit.Trip;
import models.transit.TripPattern;
import models.transit.TripPatternStop;
import play.Logger;
import play.Play;
import play.mvc.*;
import play.data.validation.*;
import play.data.validation.Validation.ValidationResult;
import play.libs.*;
import play.utils.*;

public class Bootstrap extends Controller {

    public static void index() {
    	
    	if(Account.count() == 0)
    		Bootstrap.adminForm();
    	
    	else if(Agency.count() == 0)
    		Bootstrap.agencyForm();
    	
    	else 
    		Application.index();
    }
    
    public static void adminForm() {
    	
    	if(Account.count() > 0)
    		Bootstrap.agencyForm();
    	
    	render();
    	
    }
    
    public static void createAdmin(String username, String password, String password2, String email) throws Throwable {
        
    	if(Account.count() > 0 && !Play.configuration.getProperty("application.allowBootstrapAdminCreate").equals("true"))
    		Bootstrap.index();
    	
    	validation.required(username).message("Username cannot be blank.");
    	validation.required(password).message("Password cannot be blank.");
    	validation.equals(password, password2).message("Passwords do not match.");
    	
    	if(validation.hasErrors()) {
    		params.flash();
    		validation.keep();
            adminForm();
        }
       	
    	new Account(username, password, email, true, null);
    	
    	Bootstrap.index();
    }
    
    public static void agencyForm() {
    	
    	if(Account.count() == 0)
    		Bootstrap.adminForm();
    	
    	if(Agency.count() > 0)
    		Application.index();
    	
    	render();
    }
    
    public static void createAgency( String gtfsId, String name, String url, @Required String timezone, @Required String language, String phone, Double defaultLat, Double defaultLon) throws Throwable {
    	
    	if(Agency.count() > 0)
    		Bootstrap.index();
    	
    	validation.required(gtfsId).message("Agency GTFS ID cannot be blank.");
    	validation.required(name).message("Agency name cannot be blank.");
    	validation.required(url).message("Agency URL cannot be blank.");
    	
    	if(validation.hasErrors()) {
    		params.flash();
    		validation.keep();
    		agencyForm();
        }
    	
    	Agency agency = new Agency(gtfsId, name, url, timezone, language, phone);
    	
    	agency.defaultLat = defaultLat;
    	agency.defaultLon = defaultLon;
    	
    	agency.save();
    	
    	Bootstrap.index();
    }
    
    // helper bootstap function for updating from GeoServer-centric db versions
    
    public static void encodeTripShapes() {
    	
    	List<TripPattern> tps = TripPattern.findAll();
    	
    	for(TripPattern tp : tps) {
    		if(tp.shape != null && tp.encodedShape == null) {
    			tp.encodedShape = tp.shape.generateEncoded();
    			tp.save();
    		}
    	}
    	
    	String step = "Encode Trip Shapes";
    	renderTemplate("Bootstrap/dataProcessingComplete.html", step);
    }
    
    
    // sync tripshpes 
    public static void syncTripShapes() {
    	
    	List<TripPattern> tps = TripPattern.findAll();
    	
    	for(TripPattern tp : tps) {
    		if(tp.shape != null && tp.encodedShape != null) {
    			
    			tp.shape.updateShapeFromEncoded(tp.encodedShape );
        		tp.shape.save();
    			
    		}
    	}
    	
    	String step = "Sync Trip Shapes";
    	renderTemplate("Bootstrap/dataProcessingComplete.html", step);
    }
    
    
    public static void repackPatternSequences() {
    	
    	List<TripPattern> tps = TripPattern.findAll();
    	
    	for(TripPattern tp : tps) {
    		List<TripPatternStop> tpStops = tp.patternStops;
    		Collections.sort(tpStops);
    		
    		Integer stopSequence = 1;
    		
    		for(TripPatternStop tpStop : tpStops ) {
    			tpStop.stopSequence = stopSequence;
    			tpStop.save();
    			stopSequence++;
    		}
    	}
    	
    	String step = "Repack Pattern Sequences";
    	renderTemplate("Bootstrap/dataProcessingComplete.html", step);
    }	
   
    public static void listReversedTripShapes() {
    	
    	List<TripPattern> tps = TripPattern.findAll();
    	
    	for(TripPattern tp : tps) {
    		List<TripPatternStop> tpStops = tp.patternStops;
    		
    		Collections.sort(tpStops);
    		
    		Coordinate tpsc1 = tpStops.get(0).stop.locationPoint().getCoordinate();
    		Coordinate tpsc2 = tpStops.get(tpStops.size() - 1).stop.locationPoint().getCoordinate();
    		
    		Coordinate sc1 = tp.shape.shape.getCoordinateN(0);
    		Coordinate sc2 = tp.shape.shape.getCoordinateN(tp.shape.shape.getNumPoints() -1);
    		
    		try {
				Double distance1a = JTS.orthodromicDistance(tpsc1,sc1,org.geotools.referencing.crs.DefaultGeographicCRS.WGS84);
				Double distance1b = JTS.orthodromicDistance(tpsc1,sc2,org.geotools.referencing.crs.DefaultGeographicCRS.WGS84);
				
				Double distance2a = JTS.orthodromicDistance(tpsc2,sc2,org.geotools.referencing.crs.DefaultGeographicCRS.WGS84);
				Double distance2b = JTS.orthodromicDistance(tpsc2,sc1,org.geotools.referencing.crs.DefaultGeographicCRS.WGS84);
				
				
				if(distance2a > (distance2b * 5) || distance1a > (distance1b * 5))
					play.Logger.info(distance1a + "|"  + distance1b + "--" + distance2a + "|"  + distance2b + " " + tp.route.agency.gtfsAgencyId + " " + tp.route.routeShortName + " "  + tp.route.routeLongName + " " +  tp.name);
				//else
				//	play.Logger.info(distance2a + "|"  + distance2b + " " + tp.route.agency.gtfsAgencyId + " " + tp.route.routeShortName + " "  + tp.route.routeLongName + " " +  tp.name);
			} catch (TransformException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	
    	
    	}
    	
    	String step = "List Reversed Trip Shapes";
    	renderTemplate("Bootstrap/dataProcessingComplete.html", step);
    }

    public static void updateFrequencySettings() {
    	Trip.em().createNativeQuery("update trip set usefrequency = false where usefrequency is null").executeUpdate();
    	Trip.em().createNativeQuery("update trippattern set usefrequency = false where usefrequency is null").executeUpdate();
    
    	String step = "Update Frequency Settings";
    	renderTemplate("Bootstrap/dataProcessingComplete.html", step);
    }
    
    public static void assignRouteTypes() {
        
    	List<Object[]> result = Route.em().createNativeQuery("SELECT id, routetype, routetype_id FROM route;").getResultList();

    	for(Object[] o : result) {
    		Logger.info(o[0].toString() + " " + o[1].toString());
    		
    		GtfsRouteType gtfsRouteType = GtfsRouteType.valueOf(o[1].toString());
    
    		Integer gtfsTypeId;
    		
    		switch(gtfsRouteType)
        	{
        		case TRAM:
        			gtfsTypeId = 0;
        			break;
        		case SUBWAY:
        			gtfsTypeId = 1;
        			break;
        		case RAIL:
        			gtfsTypeId = 2;
        			break;
        		case BUS:
        			gtfsTypeId = 3;
        			break;
        		case FERRY:
        			gtfsTypeId = 4;
        			break;
        		case CABLECAR:
        			gtfsTypeId = 5;
        			break;
        		case GONDOLA:
        			gtfsTypeId = 6;
        			break;
        		case FUNICULAR:
        			gtfsTypeId = 7;
        			break;
        		default:
        			gtfsTypeId = null;
        			break;
        	}
    		
    		Route r = Route.findById(((BigInteger)o[0]).longValue());
    		r.routeType = Route.mapGtfsRouteType(gtfsTypeId);
    		
    		r.save();
    	}	
    	
    	String step = "Assign Route Types";
    	renderTemplate("Bootstrap/dataProcessingComplete.html", step);
    }   
}

