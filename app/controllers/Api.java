package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;
import models.transit.Agency;
import models.transit.Route;
import models.transit.RouteType;

public class Api extends Controller {

	// **** agency controllers ****
	
	public static void getAgency(Long id) {
        
    	if(id != null)
    	{
    		Agency agency = Agency.findById(id);
    		if(agency != null)
    			renderJSON(agency);
    		else
    			notFound();
    	}
    	else
    		renderJSON(Agency.all().fetch());
    }
    
    public static void createAgency(String gtfsAgencyId, String name, String url, String timezone, String lang, String phone) {
        
    	Agency agency = new Agency(gtfsAgencyId, name, url, timezone, lang, phone);
    	agency.save();
    	
    	// check if gtfsAgencyId is specified, if not create from DB id
    	if(agency.gtfsAgencyId == null)
    		agency.gtfsAgencyId = "AGENCY_" + agency.id.toString(); 
    	
    	renderJSON(agency);
    	
    }
    
    
    public static void updateAgency(Long id, String gtfsAgencyId, String name, String url, String timezone, String lang, String phone) {
        
    	if(id == null)
    		badRequest();
    	
    	Agency agency = Agency.findById(id);
    	
    	if(agency == null)
    		badRequest();
    	
    	agency.gtfsAgencyId = gtfsAgencyId;
    	agency.name = name;
    	agency.url = url;
    	agency.timezone = timezone;
    	agency.lang = lang;
    	agency.phone = phone;
    	
    	renderJSON(agency);
    	
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
	
    public static void getRoute(Long id) {
        
    	if(id != null)
    	{
    		Route route = Route.findById(id);
    		if(route != null)
    			renderJSON(route);
    		else
    			notFound();
    	}
    	else
    		renderJSON(Route.all().fetch());
    }
    
    public static void createRoute(Long agencyId, String routeShortName, String routeLongName, String routeDesc, String routeType, String routeUrl, String routeColor, String routeTextColor) {
        
    	if(agencyId == null)
    		badRequest();
    	
    	Agency agency = Agency.findById(agencyId);
    	
    	if(agency == null)
    		badRequest();
    	
    	Route route = new Route(routeShortName, routeLongName, RouteType.valueOf(routeType), routeDesc,  agency);
    	route.save();
    	
    	renderJSON(route);
    	
    }
    
    
    public static void updateRoute(Long id, Long agencyId, String gtfsRouteId, String routeShortName, String routeLongName, String routeDesc, String routeType, String routeUrl, String routeColor, String routeTextColor) {
        
    	if(id == null && agencyId != null)
    		badRequest();
    	
    	Route route = Route.findById(id);
    	
    	if(route == null)
    		badRequest();
    	
    	route.gtfsRouteId = gtfsRouteId;
    	route.routeType = RouteType.valueOf(routeType);
    	route.routeDesc = routeDesc;
    	route.routeShortName = routeShortName;
    	route.routeLongName = routeLongName;
    	
    	if(route.agency.id != agencyId)
    	{
    		Agency agency = Agency.findById(agencyId);
        	
        	if(agency == null)
        		badRequest();
    		
    		route.agency = agency;
    	}
    
    	route.save();
    	
    	// check if gtfsRouteId is specified, if not create from DB id
    	if(route.gtfsRouteId == null)
    		route.gtfsRouteId = "ROUTE_" + route.id.toString(); 
    	
    	renderJSON(route);
    	
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

}