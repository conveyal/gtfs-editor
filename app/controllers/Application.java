package controllers;

import play.*;
import play.i18n.Lang;
import play.mvc.*;
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
import models.gis.GisExport;
import models.gis.GisUploadType;
import models.gtfs.GtfsSnapshot;
import models.gtfs.GtfsSnapshotExport;
import models.gtfs.GtfsSnapshotExportCalendars;
import models.gtfs.GtfsSnapshotMerge;
import models.gtfs.GtfsSnapshotSource;
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
    
        List<Agency> agencyObjects = Agency.findAll();

        render();
                
    }
    
    public static void timetable () {
        render();
    }
    
    public static void createGtfs(List<Long> agencySelect, Long calendarFrom, Long calendarTo) {
        
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
    
    public static void exportGis(List<Long> agencySelect) {
        
        List<Agency> agencyObjects = Agency.findAll();

        render();
                
    }
    
    
    public static void createGis(List<Long> agencySelect, String exportType) {
    	
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
    	}
    	
    }
    
    public static void valdiateGtfs(Long snapshotId) {
    	
    }
    
    public static void valdiateGtfsStatus(Long snapshotId) {
    	
    }
    
    public static void mergeGtfs(Long snapshotId) {
    	
    }

    public static void createCsvSchedule(Long patternId, Long calendarId)
    {
   	 response.setHeader("Content-Disposition", "attachment; filename=\"schedule_" + patternId + ".csv\"");
   	 response.setHeader("Content-type", "text/csv");
   	 
   	 SimpleDateFormat dfTime = new SimpleDateFormat("hh:mm a");
   	 
   	 TripPattern pattern = TripPattern.findById(patternId);
   	 ServiceCalendar calendar = ServiceCalendar.findById(calendarId);
   	 
   	 // ensure that the trip pattern sequence isn't broken
   	 pattern.resequenceTripStops();
   	 
   	 List<Trip> trips  = Trip.find("pattern = ? and serviceCalendar = ? ORDER by id", pattern, calendar).fetch();
   	 List<TripPatternStop> stopList  = TripPatternStop.find("pattern = ? ORDER BY stopSequence", pattern).fetch();
   	 	
   	 StringWriter csvString = new StringWriter();
   	 CSVWriter csvWriter = new CSVWriter(csvString);
   	 
   	 String[] headerBase = "trip_id, pattern_id, block_id, headsign, short_name".split(",");
   	 String[] headerStopNames = new String[headerBase.length + stopList.size() + 1];
   	 String[] headerStopIds = new String[headerBase.length + stopList.size() + 1];
   	 String[] headerStopTravelTimes = new String[headerBase.length + stopList.size() + 1];
   	 String[] headerStopDwellTimes = new String[headerBase.length + stopList.size() + 1];
   	 String[] headerStopTravelCumulative = new String[headerBase.length + stopList.size() + 1];

   	 Integer cumulativeTravelTime = 0;
   	 
   	 headerStopNames[headerBase.length] = "stop_name";
   	 headerStopIds[headerBase.length] = "stop_id";
   	 headerStopTravelTimes[headerBase.length] = "travel_time";
   	 headerStopDwellTimes[headerBase.length] = "dwell_time";
   	 headerStopTravelCumulative[headerBase.length] = "cumulative_time";
   	 
   	 HashMap<Integer, Integer> stopColumnIndex = new HashMap<Integer, Integer>();
   	 HashMap<Long, Integer> patternStopColumnIndex = new HashMap<Long, Integer>(); 
   	 
   	 for(TripPatternStop patternStop : stopList)
   	 {	
   		 if(patternStop.defaultDwellTime == null)
   		 {
   			 patternStop.defaultDwellTime = 0;
   		 }
   		 
   		 cumulativeTravelTime = cumulativeTravelTime + patternStop.defaultTravelTime + patternStop.defaultDwellTime;
   		 Logger.info(patternStop.stopSequence.toString());
   		 headerStopNames[headerBase.length + patternStop.stopSequence + 1] = patternStop.stop.stopName;
   		 headerStopIds[headerBase.length + patternStop.stopSequence + 1] = patternStop.stop.id.toString();
   		 headerStopTravelTimes[headerBase.length + patternStop.stopSequence  + 1] = "=\"" + TimeExtensions.ccyAmount(patternStop.defaultTravelTime) + "\"";
   		 headerStopDwellTimes[headerBase.length + patternStop.stopSequence  + 1] = "=\"" + TimeExtensions.ccyAmount(patternStop.defaultDwellTime) + "\"";
   		 headerStopTravelCumulative[headerBase.length + patternStop.stopSequence  + 1] = "=\"" + TimeExtensions.ccyAmount(cumulativeTravelTime) + "\""; 
   		 
   		 stopColumnIndex.put(patternStop.stopSequence + 1, headerBase.length + patternStop.stopSequence);
   		 patternStopColumnIndex.put(patternStop.id, headerBase.length + patternStop.stopSequence);
   	 }
   	 
   	 String[] header = (String[]) ArrayUtils.addAll(headerBase, headerStopNames);
   	 
   	 csvWriter.writeNext(headerBase);
   	 csvWriter.writeNext(headerStopNames);
   	 csvWriter.writeNext(headerStopIds);
   	 csvWriter.writeNext(headerStopTravelTimes);
   	 csvWriter.writeNext(headerStopDwellTimes);
   	 csvWriter.writeNext(headerStopTravelCumulative);
   	 
   	 for(Trip trip : trips)
   	 {
   		 String[] tripTimes = new String[headerBase.length + stopList.size() + 1];
   		 
   		 tripTimes[0] = trip.id.toString();
   		 tripTimes[1] = patternId.toString();
   		 tripTimes[2] = trip.blockId;
   		 tripTimes[3] = trip.tripHeadsign;
   		 tripTimes[4] = trip.tripShortName;
   		 
   		 List<StopTime> stopTimes  = StopTime.find("trip = ? order by stopSequence", trip).fetch();
   		 
   		 try
   		 {
   			 Date startTime = dfTime.parse("00:00 AM");
   		 
   			 for(StopTime stopTime : stopTimes)
   			 {
   				 if(stopTime.departureTime != null)
   				 {
   					 Date newTime = new Date(startTime.getTime() + (stopTime.departureTime * 1000));
   					
   					 String timeString = dfTime.format(newTime);
   					 
   					 if(stopTime.pickupType != null && stopTime.pickupType.equals(StopTimePickupDropOffType.NONE))
   						 timeString += " <";
   					 
   					 if(stopTime.dropOffType != null && stopTime.dropOffType.equals(StopTimePickupDropOffType.NONE))
   						 timeString += " >";
   					 
   					 if(stopTime.patternStop == null)
   						 tripTimes[stopColumnIndex.get(stopTime.stopSequence) + 1] = timeString;
   					 else
   						 tripTimes[patternStopColumnIndex.get(stopTime.patternStop.id) + 1] = timeString;
   				 }
   				 else
   				 {
   					 String boardAlightStatus = "";
   					 
   					 if(stopTime.pickupType != null && stopTime.pickupType.equals(StopTimePickupDropOffType.NONE))
   						 boardAlightStatus += " <";
   					 
   					 if(stopTime.dropOffType != null && stopTime.dropOffType.equals(StopTimePickupDropOffType.NONE))
   						 boardAlightStatus += " >";
   					 
   					 if(stopTime.patternStop == null)
   						 tripTimes[stopColumnIndex.get(stopTime.stopSequence) + 1] = "-" + boardAlightStatus;
   					 else
   						 tripTimes[patternStopColumnIndex.get(stopTime.patternStop.id) + 1] = "-" + boardAlightStatus;
   				 }
   			 }
   		 }
   		 catch(Exception e)
   		 {
   			 Logger.error(e.toString());
   		 }
   		 
   		 csvWriter.writeNext(tripTimes);
   	 }
   	 
   	 renderText(csvString);
    }
    
    public static void uploadCsvSchedule(Long patternId, Long calendarId, String qqfile)
    {	     
   	 
		SimpleDateFormat dfTime = new SimpleDateFormat("hh:mm a");
		SimpleDateFormat dfsTime = new SimpleDateFormat("hh:mm:ss a");
		 
		TripPattern pattern = TripPattern.findById(patternId); 
		ServiceCalendar calendar = ServiceCalendar.findById(calendarId);
		
		List<Trip> trips = Trip.find("pattern = ? and serviceCalendar = ?", pattern, calendar).fetch();

		for(Trip trip : trips) {
			
			StopTime.delete("trip = ?", trip); 
		
			trip.delete();
	
		}
   	 
	   	try {
	   		
	   		String uploadName = "csv_" + patternId;
	   		
	   		File uploadedFile = new File(Play.configuration.getProperty("application.publicDataDirectory"), uploadName + ".csv");
	   		
	   		FileOutputStream fileOutputStream = new FileOutputStream(uploadedFile);
	   		IOUtils.copy(request.body, fileOutputStream);
	   		fileOutputStream.close();
           
           
           CSVReader csvReader = new CSVReader(new FileReader(uploadedFile));
           
           int lineNum = 1;
           
           HashMap<Integer, TripPatternStop> columnStopIndex = new HashMap<Integer, TripPatternStop>();
           
           HashMap<Integer, Integer> columnStopDelta = new HashMap<Integer, Integer>();
           
           Integer cumulativeTime = 0;
           
           List<TripPatternStop> patternStops = TripPatternStop.find("pattern = ? order by stopSequence", pattern).fetch();
           
           for(String[] csvLine : csvReader.readAll())
           {
           	int columnIndex = 0;
           
           	if(lineNum == 3)
           	{
           		if(!csvLine[5].equals("stop_id"))
           			throw new Exception("Invalid stop_id row.");
           		
           		for(String column : csvLine)	
           		{
           			if(columnIndex > 5)
           			{
           				Stop stop = Stop.findById(Long.parseLong(column));
           				
           				TripPatternStop patternStop = patternStops.get(0);
           				patternStops = patternStops.subList(1, patternStops.size());
           				
           				if(!patternStop.stop.id.equals(stop.id))
           					throw new Exception("Stop ID " + stop.id + "doesn't match pattern sequence for stop " + patternStop.stop.id);
           				
           				columnStopIndex.put(columnIndex, patternStop);
           				
           				cumulativeTime += patternStop.defaultDwellTime != null? patternStop.defaultDwellTime: 0;
           				cumulativeTime += patternStop.defaultTravelTime != null? patternStop.defaultTravelTime: 0	;
           				
           				columnStopDelta.put(columnIndex, new Integer(cumulativeTime));
           			}
           			
           			columnIndex++;
           		}
           	}
           	else if(lineNum > 6)
           	{
   	        	if(!csvLine[0].isEmpty())
   	        	{
   	        		Trip trip = new Trip();
   	        		
   	        		trip.pattern = pattern;
   	        		trip.serviceCalendar = calendar;
   	        		
   	        		trip.blockId = csvLine[2];
   	        		trip.tripHeadsign = csvLine[3];
   	        		trip.tripShortName = csvLine[4];
   	        		
   	        		trip.useFrequency = false;
   	        		
   	        		trip.save();
   	        		
   	        		Integer firstTimepoint = null;
   	        		Integer columnCount = 0;
   	        		Integer previousTime = 0;
   	        		Integer dayOffset = 0;
   	        		
   	        		for(String column : csvLine)	
   	        		{	
   	        			if(columnIndex > 5)
   	        			{
   	        				if(!column.isEmpty())
   	        				{
   	        					StopTime stopTime = new StopTime();
   	        					
   	        					stopTime.trip = trip;
   	        					
   	        					// check for board/alight only flag
           						if(column.contains(">")) {
           							
           							column = column.replace(">", "");
           							
           							// board only
           							stopTime.dropOffType = StopTimePickupDropOffType.NONE;
           						}
           						
           						if(column.contains("<")) {
           							
           							column = column.replace("<", "");
           							
           							// alight only
           							stopTime.pickupType = StopTimePickupDropOffType.NONE;
           						}
           						
           						column = column.trim();
   	        					
   	        					if(column.equals("+"))
   	        						stopTime.departureTime = firstTimepoint + columnStopDelta.get(columnIndex);
   	        					else if(column.equals("-"))
   	        						stopTime.departureTime = null;
   	        					else
   	        					{
   	        						Integer currentTime;
   	        							
   	        						
   	        						try
   	        						{
   	        							currentTime = (dfTime.parse(column).getHours() * 60 * 60 ) + (dfTime.parse(column).getMinutes() * 60) + (dfTime.parse(column).getSeconds());
   	        						}
   	        						catch(ParseException e)
   	        						{
   	        							try
   		        						{
   	        								currentTime = (dfsTime.parse(column).getHours() * 60 * 60 ) + (dfsTime.parse(column).getMinutes() * 60) + (dfsTime.parse(column).getSeconds());
   		        						}
   		        						catch(ParseException e2)
   		        						{
   		        							continue;
   		        						}
   		        					}
   	        						
   	        						// in case of time that decreases add a day to offset for trips that cross midnight boundary
   	        						if(previousTime > currentTime)	        							
   	        							dayOffset += 24 * 60 * 60;
   	        							
   	        						stopTime.departureTime = currentTime + dayOffset;
   	        						
   	        						previousTime = currentTime;
   	        						
   	        						if(firstTimepoint == null)
   		    	        			{
   		        						firstTimepoint = stopTime.departureTime;
   		    	        			}
   	        						
   	        					}
   	        					
   	        					stopTime.arrivalTime = stopTime.departureTime;
   	        					
   	        					stopTime.patternStop = columnStopIndex.get(columnIndex);
   	        					stopTime.stop = columnStopIndex.get(columnIndex).stop;
   	        					stopTime.stopSequence = columnCount + 1;
   	        					
   	        					stopTime.save();
   	        					
   	        					columnCount++;
   	        				}
   	        			}
   	        				
   	        			columnIndex++;
   	        		}	        		
   	        	}
           	}
           	
           	lineNum++;
           }
           
           csvReader.close();
	   	}
	   	catch(Exception e)
	   	{
	   		Logger.error(e.toString());
	   	}
	   	
	   	ok();
    }
    
    

}