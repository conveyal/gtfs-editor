package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jobs.ProcessGtfsSnapshotExport;
import models.gtfs.GtfsSnapshotExport;
import models.gtfs.GtfsSnapshotExportCalendars;
import models.transit.Agency;
import play.Play;
import play.mvc.Controller;

// not authenticated, see comment below
public class Export extends Controller {
    /**
     * Build a GTFS file for anyone, authenticated or otherwise.
     * FetchGTFS requires no auth, but is presumably blocked by the frontend proxy server. CreateGTFS requires
     * auth, so can be left unblocked.
     * 
     * @param agencySelect
     * @param calendarFrom
     * @param calendarTo
     */

    public static void fetchGtfs (List<Long> agencySelect, Long calendarFrom, Long calendarTo) {
        // reasonable defaults: now to 2 months from now (more or less)
        if (calendarFrom == null)
            calendarFrom = new Date().getTime();
        
        if (calendarTo == null)
            calendarTo = new Date().getTime() + 2 * 31 * 24 * 60 * 60 * 1000;
        
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

    
        GtfsSnapshotExportCalendars calendarEnum;
        calendarEnum = GtfsSnapshotExportCalendars.CURRENT_AND_FUTURE;
        
        Date calendarFromDate = new Date(calendarFrom);
        Date calendarToDate = new Date(calendarTo);
        
        GtfsSnapshotExport snapshotExport = new GtfsSnapshotExport(agencyObjects, calendarEnum, calendarFromDate, calendarToDate, "");
        
        ProcessGtfsSnapshotExport exportJob = new ProcessGtfsSnapshotExport(snapshotExport.id);
        
        // running as a sync task for now -- needs to be async for processing larger feeds.
        exportJob.doJob(); 
        
        redirect(Play.configuration.getProperty("application.appBase") + "/public/data/"  + snapshotExport.getZipFilename());
    }
}
