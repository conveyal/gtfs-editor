package controllers;

import play.*;
import play.i18n.Lang;
import play.mvc.*;

import java.util.*;

import models.*;
import models.transit.Route;
import models.transit.RouteType;
import models.transit.StopType;
import models.transit.Agency;

//@With(Secure.class)
public class Application extends Controller {

    @Before
    static void initSession() throws Throwable {

       /* if(Security.isConnected()) {
            renderArgs.put("user", Security.connected());
        }
        else {
        	Secure.login();
        }*/
        	

        if(session.get("agencyId") == null) {
            
            List<Agency> agencies = Agency.findAll();
            Agency agency = agencies.get(1);

            session.put("agencyId", agency.id);
            session.put("agencyName", agency.name);
            session.put("lat", agency.defaultLat);
            session.put("lon", agency.defaultLon);
            session.put("zoom", 12);

            
        }
        //session.put("agencyId", null);
    }

    public static void index() {
        List<Agency> agencies = Agency.findAll();
        render(agencies);
    }

    public static void scaffolding() {
        List<Agency> agencies = Agency.findAll();
        render(agencies);
    }

    public static void search() {
        List<Agency> agencies = Agency.findAll();
        
        Long agencyId = Long.parseLong(session.get("agencyId"));
        Agency selectedAgency = Agency.findById(agencyId);
        List<Route> routes = Route.find("agency = ? order by routeShortName", selectedAgency).fetch();
        render(agencies, routes);
    }

    public static void route() {
        List<Agency> agencies = Agency.findAll();
    	List<RouteType> routeTypes = RouteType.findAll();
        render(agencies, routeTypes);
    }
    
    public static void manageRouteTypes() {
        List<Agency> agencies = Agency.findAll();
    	List<RouteType> routeTypes = RouteType.findAll();
        render(agencies, routeTypes);
    }

    public static void manageStopTypes() {
        List<Agency> agencies = Agency.findAll();
        List<StopType> routeTypes = StopType.findAll();
        render(agencies, routeTypes);
    }

    public static void manageAgencies() {
        List<Agency> agencies = Agency.findAll();
        List<RouteType> routeTypes = RouteType.findAll();
        render(agencies, routeTypes);
    }

    public static void setLang(String lang) {
    	Lang.change(lang);
    	ok();
    }

    public static void setAgency(Long agencyId) {
        Agency agency = Agency.findById(agencyId);

        if(agency == null)
            badRequest();

        session.put("agencyId", agencyId);
        session.put("agencyName", agency.name);
        session.put("lat", agency.defaultLat);
        session.put("lon", agency.defaultLon);
        session.put("zoom", 12);

        ok();
    }

    public static void setMap(String zoom, String lat, String lon) {


        session.put("zoom", zoom);
        session.put("lat", lat);
        session.put("lon", lon);

        ok();
    }
}