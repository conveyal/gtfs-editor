package controllers;

import play.*;
import play.mvc.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import  org.codehaus.jackson.map.ObjectMapper;

import models.*;
import models.transit.Agency;
import models.transit.Route;
import models.transit.RouteType;
import models.transit.Stop;

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

    public static void getRoute(Long id) {
        try {
            if(id != null)
            {
                Route route = Route.findById(id);
                if(route != null)
                    renderJSON(Api.toJson(route, false));
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
    public static void getStop(Long id) {

        try {
            if(id != null)
            {
                Stop stop = Stop.findById(id);
                if(stop != null)
                    renderJSON(Api.toJson(stop, false));
                else
                    notFound();
            }
            else
                renderJSON(Api.toJson(Stop.all().fetch(), false));
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
}