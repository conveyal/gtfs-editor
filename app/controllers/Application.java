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

@With(Secure.class)
public class Application extends Controller {

    @Before
    static void initSession() throws Throwable {

    	List<Agency> agencies = new ArrayList<Agency>();
    	
       if(Security.isConnected()) {
            renderArgs.put("user", Security.connected());
            
            Account account = Account.find("username = ?", Security.connected()).first();
            
            
            
            if(account.admin != null && account.admin)
            	agencies = Agency.findAll();
            else {
            	agencies.add(((Agency)Agency.findById(account.agency.id)));            	
            }
            
            renderArgs.put("agencies", agencies);
        }
        else {
        	Secure.login();
        }

        if(session.get("agencyId") == null) {
            
            Agency agency = agencies.get(0);

            session.put("agencyId", agency.id);
            session.put("agencyName", agency.name);
            session.put("lat", agency.defaultLat);
            session.put("lon", agency.defaultLon);
            session.put("zoom", 12);

            
        }
        //session.put("agencyId", null);
    }

    public static void index() {
        
        render();
    }

    public static void scaffolding() {
        render();
    }

    public static void search() {
       
        Long agencyId = Long.parseLong(session.get("agencyId"));
        Agency selectedAgency = Agency.findById(agencyId);
        List<Route> routes = Route.find("agency = ? order by routeShortName", selectedAgency).fetch();
        render(routes);
    }

    public static void route() {
    	List<RouteType> routeTypes = RouteType.findAll();
        render(routeTypes);
    }
    
    public static void manageRouteTypes() {
    	List<RouteType> routeTypes = RouteType.findAll();
        render(routeTypes);
    }

    public static void manageStopTypes() {
        List<StopType> routeTypes = StopType.findAll();
        render(routeTypes);
    }

    public static void manageAgencies() {
        List<RouteType> routeTypes = RouteType.findAll();
        render(routeTypes);
    }

    public static void setLang(String lang) {
    	Lang.change(lang);
    	ok();
    }

    public static void createAccount(String username, String password, String email, Boolean admin, Long agencyId)
	{
		if(!username.isEmpty() && !password.isEmpty() && !email.isEmpty() && Account.find("username = ?", username).first() == null )
			new Account(username, password, email, admin, agencyId);
		
		Application.index();
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