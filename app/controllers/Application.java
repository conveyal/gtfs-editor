package controllers;

import play.*;
import play.i18n.Lang;
import play.mvc.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.IOUtils;

import jobs.ProcessGisExport;
import jobs.ProcessGtfsSnapshotExport;
import jobs.ProcessGtfsSnapshotMerge;

import models.*;
import models.gis.GisExport;
import models.gis.GisUploadType;
import models.gtfs.GtfsSnapshot;
import models.gtfs.GtfsSnapshotExport;
import models.gtfs.GtfsSnapshotExportCalendars;
import models.gtfs.GtfsSnapshotMerge;
import models.gtfs.GtfsSnapshotSource;
import models.transit.Route;
import models.transit.RouteType;
import models.transit.StopType;
import models.transit.Agency;
import models.transit.Trip;

@With(Secure.class)
public class Application extends Controller {

    @Before
    static void initSession() throws Throwable {

    	List<Agency> agencies = new ArrayList<Agency>();
    	
    	if(Security.isConnected()) {
            renderArgs.put("user", Security.connected());
            
            Account account = Account.find("username = ?", Security.connected()).first();
            
            if(account == null && Account.count() == 0) {
            	Bootstrap.index();
            }
            
            if(account.admin != null && account.admin)
            	agencies = Agency.find("order by name").fetch();
            else {
            	agencies.add(((Agency)Agency.findById(account.agency.id)));            	
            }
            
            renderArgs.put("agencies", agencies);
        }
        else {

        	if(Account.count() == 0)
        		Bootstrap.index();
        	else
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
    }

    public static void changePassword(String currentPassword, String newPassword) {
        
        if(Security.isConnected())
        {
            if(currentPassword != null && newPassword != null)
            {
                Boolean changed = Account.changePassword(Security.connected(), currentPassword, newPassword);
                
                if(changed)
                    Application.passwordChanged();
                else
                {
                    Boolean badPassword = true;
                    render(badPassword);
                }
            }   
            else
                render();
        }
        else
            Application.index();
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

    public static void search() {
       
        Long agencyId = Long.parseLong(session.get("agencyId"));
        Agency selectedAgency = Agency.findById(agencyId);
        List<Route> routes = Route.find("agency = ? order by routeShortName", selectedAgency).fetch();
        render(routes);
    }

    public static void route(Long id) {
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
    
    public static void exportGtfs() {
    
        render();
                
    }
    
    public static void createGtfs(Long calendarFrom, Long calendarTo) {
        
    	// currently exports all agencies 
    	
        List<Agency> agencyObjects = Agency.findAll();
    
        GtfsSnapshotExportCalendars calendarEnum;
        calendarEnum = GtfsSnapshotExportCalendars.CURRENT_AND_FUTURE;
        
        Date calendarFromDate = new Date(calendarFrom);
        Date calendarToDate = new Date(calendarTo);
        
        GtfsSnapshotExport snapshotExport = new GtfsSnapshotExport(agencyObjects, calendarEnum, calendarFromDate, calendarToDate, "");
        
        ProcessGtfsSnapshotExport exportJob = new ProcessGtfsSnapshotExport(snapshotExport.id);
        
        // running as a sync task for now -- needs to be async for processing larger feeds.
        exportJob.doJob(); 
        
        redirect("/public/data/gtfs/"  + snapshotExport.getZipFilename());
    }
    
    public static void exportStopGis() {
        
    	List<Agency> agencyObjects = Agency.findAll();
    	
    	
    	GisUploadType typeEnum;
    	
    	typeEnum = GisUploadType.STOPS;
    	
    	GisExport gisExport = new GisExport(agencyObjects, typeEnum, "");
    	
    	ProcessGisExport exportJob = new ProcessGisExport(gisExport.id);
    	
    	exportJob.doJob();
    	
    	redirect("/public/data/gtfs/"  + gisExport.getFilename());
             
    }
    
    public static void exportRouteGis() {
        
    	List<Agency> agencyObjects = Agency.findAll();
    	
    	
    	GisUploadType typeEnum;
    	
    	typeEnum = GisUploadType.ROUTES;
    	
    	GisExport gisExport = new GisExport(agencyObjects, typeEnum, "");
    	
    	ProcessGisExport exportJob = new ProcessGisExport(gisExport.id);
    	
    	exportJob.doJob();
    	
    	redirect("/public/data/gtfs/"  + gisExport.getFilename());
             
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
    		
    		GtfsSnapshot snapshot = new GtfsSnapshot(gtfsUpload.getName(), new Date(), GtfsSnapshotSource.UPLOAD);
    		snapshot.save();
    		
    		FileOutputStream fileOutputStream;
			try {
				
				File fileOut = new File(Play.configuration.getProperty("application.publicGtfsDataDirectory"), snapshot.getFilename());
				
				gtfsUpload.renameTo(fileOut);
				//fileOutputStream = new FileOutputStream(fileOut);
				//IOUtils.copy(gtfsUpload, fileOutputStream);
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
    	}
    	
    }
    
    public static void valdiateGtfs(Long snapshotId) {
    	
    }
    
    public static void valdiateGtfsStatus(Long snapshotId) {
    	
    }
    
    public static void mergeGtfs(Long snapshotId) {
    	
    }


}