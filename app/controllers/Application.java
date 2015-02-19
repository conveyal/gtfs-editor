package controllers;

import play.*;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.*;
import play.mvc.Http.Request;
import play.mvc.Scope.Session;
import utils.tags.TimeExtensions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import jobs.ProcessGisExport;
import jobs.ProcessGtfsSnapshotExport;
import jobs.ProcessGtfsSnapshotMerge;
import models.*;
import models.VersionedDataStore.AgencyTx;
import models.VersionedDataStore.GlobalTx;
import models.transit.Route;
import models.transit.RouteType;
import models.transit.ServiceCalendar;
import models.transit.Stop;
import models.transit.StopTime;
import models.transit.StopTimePickupDropOffType;
import models.transit.StopType;
import models.transit.Agency;
import models.transit.Trip;
import models.transit.TripPattern;
import models.transit.TripPatternStop;

@With(Secure.class)
public class Application extends Controller {
    @Before
    static void initSession() throws Throwable {

    	GlobalTx tx = VersionedDataStore.getGlobalTx();
    	
    	try {
    		Agency[] agencies;
    		
	    	if(Security.isConnected()) {
	            renderArgs.put("user", Security.connected());
	            
	            Account account = tx.accounts.get(Security.connected());
	            
	            if(account == null && tx.accounts.size() == 0) {
	            	Bootstrap.index();
	            }
	            
	            if(account.admin != null && account.admin)
	            	agencies = tx.agencies.values().toArray(new Agency[tx.agencies.size()]);
	            else {
	            	agencies = new Agency[] { tx.agencies.get(account.agencyId) };           	
	            }
	            
	            renderArgs.put("agencies", agencies);
	        }
	    	else if (checkOAuth(request, session, tx)) {
	    	    renderArgs.put("user", Messages.get("secure.anonymous"));
	    	    
	    	    OAuthToken token = getToken(request, session, tx);
	    	    
	    	    if (token.agencyId != null) {
	            	agencies = new Agency[] { tx.agencies.get(token.agencyId) };           	
	    	    }
	    	    else {
	            	agencies = tx.agencies.values().toArray(new Agency[tx.agencies.size()]);
	    	    }
	    	    
	    	    renderArgs.put("agencies", agencies);
	    	}
	        else {
	
	        	if(tx.accounts.size() == 0)
	        		Bootstrap.index();
	        	else
	        	    Secure.login();
	        	
	        	return;
	        }

	        if(session.get("agencyId") == null) {
	            
	        	Agency agency = agencies[0];
	
	            session.put("agencyId", agency.id);
	            session.put("agencyName", agency.name);
	            session.put("lat", agency.defaultLat);
	            session.put("lon", agency.defaultLon);
	            session.put("zoom", 12); 
	            
	        }
	        
	        // used to render agency names in templates
	        // note that we do need to include all agencies here; it is possible to see stops from agencies you do
	        // not have permission to edit.
	        renderArgs.put("agenciesJson", Api.toJson(tx.agencies, false));
    	}
    	finally {
    		tx.rollback();
    	}
    }

    public static boolean checkOAuth(Request request, Session session) {
    	GlobalTx tx = VersionedDataStore.getGlobalTx();
    	boolean ret = checkOAuth(request, session, tx);
    	tx.rollback();
    	return ret;
    }
    
    /**
     * Check if a user has access via OAuth.
     * @param session
     * @return
     */
    public static boolean checkOAuth(Request request, Session session, GlobalTx tx) {
        if (!"true".equals(Play.configuration.getProperty("application.oauthEnabled")))
            return false;
        
        OAuthToken token = getToken(request, session, tx);
        
        if (token == null)
            return false;
        
        // check if the token is valid
        long now = new Date().getTime();
        long timeout = Long.parseLong(Play.configuration.getProperty("application.oauthKeyTimeout"));
        return token.creationDate + timeout > now;           
    }

    /**
     * Get the OAuth token from the request/session.
     * Note that we support OAuth tokens in Authorization headers, or in the session, or in the GET param oauth_token.
     */
    public static OAuthToken getToken (Request request, Session session, GlobalTx tx) {
        String token = null;
        if (request.params.get("oauth_token") != null)
            token = request.params.get("oauth_token");
        else if (request.headers.get("authorization") != null) {
            String header = request.headers.get("authorization").value();
            if (header.startsWith("Bearer "))
                token = header.replace("Bearer ", "");
        }
        // fall back on the session as a last resort, since it may be stale
        else if (session != null && session.get("oauth_token") != null)
            token = session.get("oauth_token");
        
        if (token == null)
            return null;
        
        else {
        	token = Account.hash(token);
            return tx.tokens.get(token);
        }
    }
    
	public static void changePassword(String currentPassword, String newPassword)
	{
		GlobalTx tx = VersionedDataStore.getGlobalTx();
		Account acct = tx.accounts.get(Security.connected());
		if (acct.changePassword(currentPassword, newPassword)) {
			tx.accounts.put(acct.id, acct);
			tx.commit();
			passwordChanged();
		}
		else {
			tx.rollback();
			Application.index();
		}
	}
    
    public static void passwordChanged() {
        
        render();
    }

    public static void index() {
        
        render();
    }

    public static void scaffolding() {
        render();
    }

    /**
     * Helper to go to the search page for a particular agency (never called from the router directly).
     * @param agencyId
     */
    public static void search(String agencyId) {
    	GlobalTx tx = VersionedDataStore.getGlobalTx();
    	AgencyTx atx = null;
    	
    	try {
    		Agency selectedAgency;
	        if (agencyId == null) {
	            agencyId = session.get("agencyId");
	            selectedAgency = tx.agencies.get(agencyId);
	        }
	        else {
	            session.put("agencyId", agencyId);
	            
	            selectedAgency = tx.agencies.get(agencyId);
	            
	            session.put("agencyId", agencyId);
	            session.put("agencyName", selectedAgency.name);
	            session.put("lat", selectedAgency.defaultLat);
	            session.put("lon", selectedAgency.defaultLon);
	            session.put("zoom", 12);
	        }
	        
	        atx = VersionedDataStore.getAgencyTx(agencyId);
	        
	        render(atx.routes.values());
    	} finally {
    		if (atx != null) atx.rollback();
    		tx.rollback();
    	}
    }

    public static void route(String id) {
    	GlobalTx tx = VersionedDataStore.getGlobalTx();
    	
    	try {
    		Collection<RouteType> routeTypes = tx.routeTypes.values();
    		render(routeTypes);
    	} finally {
    		tx.rollback();
    	}
    }
    
    public static void manageRouteTypes() {
    	GlobalTx tx = VersionedDataStore.getGlobalTx();
    	
    	try {
    		render(tx.routeTypes.values());
    	} finally {
    		tx.rollback();
    	}
    }

    public static void manageStopTypes() {
    	GlobalTx tx = VersionedDataStore.getGlobalTx();
    	
    	try {
    		render(tx.routeTypes.values());
    	} finally {
    		tx.rollback();
    	}
    }

    public static void manageAgencies() {
    	GlobalTx tx = VersionedDataStore.getGlobalTx();
    	
    	try {
    		render(tx.routeTypes.values());
    	} finally {
    		tx.rollback();
    	}
    }

    public static void setLang(String lang) {
    	Lang.change(lang);
    	ok();
    }
    
    public static void setAgency(String agencyId) {
    	GlobalTx tx = VersionedDataStore.getGlobalTx();
    	
    	try {
    		if(!tx.agencies.containsKey(agencyId)) {
                badRequest();
    			return;
    		}
    		
    		Agency agency = tx.agencies.get(agencyId);
	
	        session.put("agencyId", agencyId);
	        session.put("agencyName", agency.name);
	        session.put("lat", agency.defaultLat);
	        session.put("lon", agency.defaultLon);
	        session.put("zoom", 12);
	
	        ok();
    	} finally {
    		tx.rollback();
    	}
    }

    public static void setMap(String zoom, String lat, String lon) {

        session.put("zoom", zoom);
        session.put("lat", lat);
        session.put("lon", lon);

        ok();
    }
    
    public static void timetable () {
        render();
    }
    
    public static void exportGtfs() {
    	GlobalTx tx = VersionedDataStore.getGlobalTx();
    	Collection<Agency> agencyObjects;
    	
    	try {
    		agencyObjects = tx.agencies.values();
    	} finally {
    		tx.rollback();
    	}

        render();
    }
    
    /**
     * Build a GTFS file with the specified agency IDs.
     * 
     * @param agencySelect
     * @param calendarFrom
     * @param calendarTo
     */
    public static void createGtfs(List<String> agencySelect, Long calendarFrom, Long calendarTo) {
        // reasonable defaults: now to 2 months from now (more or less)
        if (calendarFrom == null)
            calendarFrom = new Date().getTime();
        
        if (calendarTo == null)
            calendarTo = new Date().getTime() + 2L * 31L * 24L * 60L * 60L * 1000L;
        
        /*List<Agency> agencyObjects = new ArrayList<Agency>(); 
        
        if(agencySelect != null && agencySelect.size() > 0) {

            for(String agencyId : agencySelect) {
                
                Agency a = Agency.findById(agencyId);
                if(a != null)
                        agencyObjects.add(a);
            
            }
        }
        else 
            agencyObjects = Agency.findAll();

    
        GtfsSnapshotExportCalendars calendarEnum;
        calendarEnum = GtfsSnapshotExportCalendars.CURRENT_AND_FUTURE;
        
        Date calendarFromDate = new Date(calendarFrom);
        Date calendarToDate = new Date(calendarTo);
        
        GtfsSnapshotExport snapshotExport = new GtfsSnapshotExport(agencyObjects, calendarEnum, calendarFromDate, calendarToDate, "");
        
        ProcessGtfsSnapshotExport exportJob = new ProcessGtfsSnapshotExport(snapshotExport.id);
        
        // running as a sync task for now -- needs to be async for processing larger feeds.
        exportJob.doJob(); 
        
        redirect(Play.configuration.getProperty("application.appBase") + "/public/data/"  + snapshotExport.getZipFilename());*/
    }
    
    public static void exportGis(List<Long> agencySelect) {
    	GlobalTx tx = VersionedDataStore.getGlobalTx();
    	
    	try {
    		Collection<Agency> agencyObjects = tx.agencies.values();
    		render(agencyObjects);
    	} finally {
    		tx.rollback();
    	}                
    }
    
    
    public static void createGis(List<Long> agencySelect, String exportType) {
    	/*
    	List<Agency> agencyObjects = new ArrayList<Agency>(); 
        
        if(agencySelect != null || agencySelect.size() > 0) {

            for(Long agencyId : agencySelect) {
                
            	Agency a = Agency.findById(agencyId);
                if(a != null)
                	agencyObjects.add(a);
            
            }
        }
        else 
            agencyObjects = Agency.findAll();
    	
    	GisUploadType typeEnum = null;
    	
    	if(exportType.equals("routes"))
    		typeEnum = GisUploadType.ROUTES;
    	else
    		typeEnum = GisUploadType.STOPS;
    	
    	GisExport gisExport = new GisExport(agencyObjects, typeEnum, "");
    	
    	ProcessGisExport exportJob = new ProcessGisExport(gisExport.id);
    	
    	exportJob.doJob();
    	
    	redirect(Play.configuration.getProperty("application.appBase") + "/public/data/"  + gisExport.getFilename());
    	*/
             
    }
    
    public static void importGtfs() {
    	
    	render();
    }
    
    public static void uploadGtfs(File gtfsUpload) {
   
    	validation.required(gtfsUpload).message("GTFS file required.");
    	
    	if(gtfsUpload != null && !gtfsUpload.getName().contains(".zip"))
    		validation.addError("gtfsUpload", "GTFS must have .zip extension.");
    	
    	if(validation.hasErrors()) {
    		params.flash();
    		validation.keep();
    		importGtfs();
        }
    	else {
    /*		
    		GtfsSnapshot snapshot = new GtfsSnapshot(gtfsUpload.getName(), new Date(), GtfsSnapshotSource.UPLOAD);
    		snapshot.save();
    		
    		FileOutputStream fileOutputStream;
			try {
				
				File fileOut = new File(Play.configuration.getProperty("application.publicDataDirectory"), snapshot.getFilename());
				
				gtfsUpload.renameTo(fileOut);
        
			}	
			catch (Exception e) {
				
				validation.addError("gtfsUpload", "Unable to process file.");
				params.flash();
	    		validation.keep();
	    		importGtfs();
			}
            
			snapshot.save();
	        GtfsSnapshotMerge merge = new GtfsSnapshotMerge(snapshot);
	        merge.save();
	        
	        ProcessGtfsSnapshotMerge mergeJob = new ProcessGtfsSnapshotMerge(merge.id);
	        mergeJob.doJob(); 
			
			//valdiateGtfs(snapshot.id);
			 *
			 */
    	}
    	
    }

    /** schedule exceptions page */
    public static void exceptions () {
    	render();
    }

}