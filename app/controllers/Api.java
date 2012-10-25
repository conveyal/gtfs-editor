package controllers;

import play.*;
import play.mvc.*;

import java.io.IOException;
import java.util.*;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import  org.codehaus.jackson.map.ObjectMapper;

import models.*;
import models.transit.Agency;
import models.transit.Route;
import models.transit.RouteType;

public class Api extends Controller {

	private static ObjectMapper mapper = new ObjectMapper();

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

    public static void createAgency() {
        Agency agency;

        try {
            agency = mapper.readValue(params.get("body"), Agency.class);

            // check if gtfsAgencyId is specified, if not create from DB id
            if(agency.gtfsAgencyId == null)
                agency.gtfsAgencyId = "AGENCY_" + agency.id.toString();

            agency.save();
            renderJSON(agency);
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

            Agency updatedAgency = Agency.em().merge(agency);
            updatedAgency.save();
            
            renderJSON(updatedAgency);
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
        Route route;

        try {
            route = mapper.readValue(params.get("body"), Route.class);

        	if(Agency.findById(route.agency.id) == null)
        		badRequest();

        	// check if gtfsRouteId is specified, if not create from DB id
        	if(route.gtfsRouteId == null)
        		route.gtfsRouteId = "ROUTE_" + route.id.toString();
        	
            route.save();
            
            renderJSON(route);
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
            
            renderJSON(updatedRoute);
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

}