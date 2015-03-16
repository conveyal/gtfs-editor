package controllers.api;

import java.util.Collection;
import java.util.Set;

import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import models.transit.Route;
import controllers.Base;
import controllers.Application;
import controllers.Secure;
import controllers.Security;
import datastore.VersionedDataStore;
import datastore.AgencyTx;
import datastore.GlobalTx;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class RouteController extends Controller {
	@Before
	static void initSession() throws Throwable {
		 
		if(!Security.isConnected() && !Application.checkOAuth(request, session))
			Secure.login();
	}
	
    public static void getRoute(String id, String agencyId) {
    	if (agencyId == null) {
    		badRequest();
    		return;
    	}
    	
    	final AgencyTx tx = VersionedDataStore.getAgencyTx(agencyId);
    	
        try {      	
        	if (id != null) {
        		if (!tx.routes.containsKey(id)) {
        			tx.rollback();
        			badRequest();
        			return;
        		}
        		
        		Route route = tx.routes.get(id);
        		
        		route.addDerivedInfo(tx);
        		
        		renderJSON(Base.toJson(route, false));
        	}
        	else {
        		Route[] ret = tx.routes.values().toArray(new Route[tx.routes.size()]);
        				
        		for (Route r : ret) {
        			r.addDerivedInfo(tx);
        		}
        		
        		String json = Base.toJson(ret, false);
        		tx.rollback();
        		renderJSON(json);
        	}
        } catch (Exception e) {
        	tx.rollbackIfOpen();
        	e.printStackTrace();
        	badRequest();
        }		
    }

    public static void createRoute() {
        Route route;

        try {
            route = Base.mapper.readValue(params.get("body"), Route.class);
            
            GlobalTx gtx = VersionedDataStore.getGlobalTx();
            if (!gtx.agencies.containsKey(route.agencyId)) {
            	gtx.rollback();
            	badRequest();
            	return;
            }
            
            gtx.rollback();
   
            AgencyTx tx = VersionedDataStore.getAgencyTx(route.agencyId);
            
            if (tx.routes.containsKey(route.id)) {
            	tx.rollback();
            	badRequest();
            	return;
            }

            // check if gtfsRouteId is specified, if not create from DB id
            if(route.gtfsRouteId == null) {
                route.gtfsRouteId = "ROUTE_" + route.id;
            }
            
            tx.routes.put(route.id, route);
            tx.commit();

            renderJSON(Base.toJson(route, false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }


    public static void updateRoute() {
        Route route;

        try {
            route = Base.mapper.readValue(params.get("body"), Route.class);
   
            AgencyTx tx = VersionedDataStore.getAgencyTx(route.agencyId);
            
            if (!tx.routes.containsKey(route.id)) {
            	tx.rollback();
            	notFound();
            	return;
            }

            // check if gtfsRouteId is specified, if not create from DB id
            if(route.gtfsRouteId == null) {
                route.gtfsRouteId = "ROUTE_" + route.id;
            }
            
            tx.routes.put(route.id, route);
            tx.commit();

            renderJSON(Base.toJson(route, false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }

    public static void deleteRoute(String id, String agencyId) {
    	if (agencyId == null)
    		agencyId = session.get("agencyId");
    	
        if(id == null || agencyId == null)
            badRequest();

        AgencyTx tx = VersionedDataStore.getAgencyTx(agencyId);
        

        
        try {
            if (!tx.routes.containsKey(id)) {
            	tx.rollback();
            	notFound();
            	return;
            }
            
            Route r = tx.routes.get(id);
        	
        	// delete affected trips
            Set<Tuple2<String, String>> affectedTrips = tx.tripsByRoute.subSet(new Tuple2(r.id, null), new Tuple2(r.id, Fun.HI));
            for (Tuple2<String, String> trip : affectedTrips) {
            	tx.trips.remove(trip.b);
            }
            
            // delete affected patterns
            // note that all the trips on the patterns will have already been deleted above
            Set<Tuple2<String, String>> affectedPatts = tx.tripPatternsByRoute.subSet(new Tuple2(r.id, null), new Tuple2(r.id, Fun.HI));
            for (Tuple2<String, String> tp : affectedPatts) {
            	tx.tripPatterns.remove(tp.b);
            }
            
            tx.routes.remove(id);
            tx.commit();
            ok();
        } catch (Exception e) {
        	tx.rollback();
        	e.printStackTrace();
        	error(e);
        }
    }
}
