package controllers;

import com.google.common.collect.Maps;

import datastore.AgencyTx;
import datastore.GlobalTx;
import datastore.VersionedDataStore;
import jobs.GisExport;
import jobs.ProcessGtfsSnapshotExport;
import jobs.ProcessGtfsSnapshotMerge;
import models.Account;
import models.OAuthToken;
import models.transit.*;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import play.Play;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Scope.Session;
import play.mvc.With;
import utils.Auth0UserProfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@With(Secure.class)
public class Application extends Controller {
    /** used to give almost-friendly names to exported files */
    public static AtomicLong nextExportId = new AtomicLong(1);

    @Before
    static void initSession() throws Throwable {

        GlobalTx tx = VersionedDataStore.getGlobalTx();
        try {
            Agency[] agencies = new Agency[0];

            // pass auth-related config args to client
            renderArgs.put("managerUrl", Play.configuration.getProperty("application.managerUrl"));
            renderArgs.put("auth0Domain", Play.configuration.getProperty("application.auth0Domain"));
            renderArgs.put("auth0ClientId", Play.configuration.getProperty("application.auth0ClientId"));
            renderArgs.put("projectId", Play.configuration.getProperty("application.projectId"));

            System.out.println("app username = " + session.get("username"));
            System.out.println("app path = " + request.path);
            if(Security.isConnected()) {
                renderArgs.put("user", Security.connected());

                Account account = tx.accounts.get(Security.connected());
                String projectID = Play.configuration.getProperty("application.projectId");
				String token = session.get("token");
                System.out.println("application can see token: " + token);

                Auth0UserProfile userProfile = null;
				try {
					userProfile = Auth0Controller.getUserInfo(token);
					System.out.println("got userinfo for " + userProfile.getEmail());
				}
				catch (Exception e) {
					e.printStackTrace();
					System.out.println("Error in user auth, redirecting to /auth0logout");
					redirect("/auth0logout");
				}

                agencies = tx.agencies.values().toArray(new Agency[tx.agencies.size()]);

                String editableFeeds = session.get("editableFeeds");

                List<Agency> filteredAgencies= new ArrayList<Agency>();

                if(editableFeeds != null) {
                    System.out.println("filtering agencies" + editableFeeds);
                    
                    String[] edIds = editableFeeds.split(",");
                    for(Agency agency : agencies) {
                        if(Arrays.asList(edIds).contains(agency.sourceId) || Arrays.asList(edIds).contains("*")) {
                            filteredAgencies.add(agency);
                        }
                    }
                }
                
                agencies = filteredAgencies.toArray(new Agency[filteredAgencies.size()]);
				
                /*if(userProfile == null) {
	            if(account == null && tx.accounts.size() == 0) {
	            	Bootstrap.index();
	            }
	            if(userProfile.canAdministerProject(projectID))
					if(account.admin != null && account.admin)
	            	agencies = tx.agencies.values().toArray(new Agency[tx.agencies.size()]);
	            else {
					agencies = new Agency[] { tx.agencies.get(account.agencyId) };
	            	agencies = new Agency[] { tx.agencies.get(userProfile.getManagedFeeds(projectID)) };
	            }*/

                renderArgs.put("agencies", agencies);
            }
	        else {
	
//	        	if(tx.accounts.size() == 0)
//	        		Bootstrap.index();
//	        	else {
	        	    System.out.println("logging in to " + request.path);
	        	    Secure.login(request.path);
//	        	}
	        	
	        	return;
	        }
	    	
	    	Arrays.sort(agencies);

	        if(session.get("agencyId") == null && agencies.length >0) {
	            
	        	Agency agency = agencies[0];
	
	            session.put("agencyId", agency.id);
	            session.put("agencyName", agency.name);
	            session.put("lat", agency.defaultLat);
	            session.put("lon", agency.defaultLon);
	            session.put("zoom", 12); 
	            
	        }
	        
	        // Make a map for the user interface
	        Map<String, Agency> agencyMap = new HashMap<String, Agency>();
	        
	        for (Agency agency : agencies) {
	        	agencyMap.put(agency.id, agency);
	        }
	        
	        // used to render agency names in templates
	        renderArgs.put("agenciesJson", Base.toJson(agencyMap, false));
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
		if (currentPassword == null || newPassword == null) {
			render();
			return;
		}
		
		GlobalTx tx = VersionedDataStore.getGlobalTx();
		Account acct = tx.accounts.get(Security.connected()).clone();
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
    		Collection<RouteType> routeTypes = tx.routeTypes.values();
    		String managerUrl = Play.configuration.getProperty("application.managerUrl");
    		render(routeTypes, managerUrl);
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
    	
    	LocalDate startDate;
    	LocalDate endDate;
    	
        if (calendarFrom == null)
            startDate = new LocalDate().minusDays(2);
        else
        	startDate = new LocalDate(calendarFrom, DateTimeZone.UTC);
        
        if (calendarTo == null)
            endDate = new LocalDate().plusMonths(2);
        else
        	endDate = new LocalDate(calendarTo, DateTimeZone.UTC);
        
        File out = new File(Play.configuration.getProperty("application.publicDataDirectory"), "gtfs_" + nextExportId.incrementAndGet() + ".zip");
        
        new ProcessGtfsSnapshotExport(agencySelect, out, startDate, endDate, false).run();
        
        redirect(Play.configuration.getProperty("application.appBase") + "/public/data/"  + out.getName());
    }
    
    public static void exportGis() {
    	GlobalTx tx = VersionedDataStore.getGlobalTx();
    	
    	try {
    		Collection<Agency> agencyObjects = tx.agencies.values();
    		render(agencyObjects);
    	} finally {
    		tx.rollback();
    	}                
    }
    
    
    public static void createGis(List<String> agencySelect, String exportType) {
		if (agencySelect.isEmpty()) {
			badRequest();
			return;
		}

    	GisExport.Type typeEnum = null;
    	
    	if(exportType.equals("routes"))
    		typeEnum = GisExport.Type.ROUTES;
    	else
    		typeEnum = GisExport.Type.STOPS;

		File outFile = new File(Play.configuration.getProperty("application.publicDataDirectory"), "gis_" + nextExportId.incrementAndGet() + ".zip");

    	new GisExport(typeEnum, outFile, agencySelect).run();

		redirect(Play.configuration.getProperty("application.appBase") + "/public/data/"  + outFile.getName());
	}
    
    public static void importGtfs() {
    	
    	render();
    }

	public static void uploadGtfs(File gtfsUpload) {

		System.out.println("uploadGtfs " + gtfsUpload);

		validation.required(gtfsUpload).message("GTFS file required.");

		if(gtfsUpload != null && !gtfsUpload.getName().contains(".zip"))
			validation.addError("gtfsUpload", "GTFS must have .zip extension.");

		if(validation.hasErrors()) {
			params.flash();
			validation.keep();
			importGtfs();
		}
		else {
			ProcessGtfsSnapshotMerge merge;
			try {
				merge = new ProcessGtfsSnapshotMerge(gtfsUpload);
				merge.run();
			}
			catch (Exception e) {
				e.printStackTrace();
				validation.addError("gtfsUpload", "Unable to process file.");
				params.flash();
				validation.keep();
				importGtfs();
				return;
			}

			// if there are multiple agencies this will pick one randomly
			search(merge.agencyId);
		}
	}

	public static void fetchGtfs(String gtfsUrl) {
		String feedVersionId = request.params.get("feedVersionId");
		String feedSourceId = request.params.get("feedSourceId");
		System.out.println("fetchGtfs " + feedVersionId);

		InputStream is = null;
		FileOutputStream fos = null;

		String tempDir = System.getProperty("java.io.tmpdir");
		String outputPath = tempDir + "/" + feedVersionId;

		try {
			//connect
			URL url = new URL("http://localhost:9000/api/manager/secure/feedversion/" + feedVersionId + "/download");
			System.out.println("downloading " + url);

			System.out.println("token= " + session.get("token"));

			URLConnection urlConn = url.openConnection();
			urlConn.setRequestProperty("Authorization", "Bearer " + session.get("token"));

			//get inputstream from connection
			is = urlConn.getInputStream();
			fos = new FileOutputStream(outputPath);

			// 4KB buffer
			byte[] buffer = new byte[4096];
			int length;

			// read from source and write into local file
			while ((length = is.read(buffer)) > 0) {
				fos.write(buffer, 0, length);
			}

			is.close();
			fos.close();

			System.out.println("wrote path " + outputPath);
			File file = new File(outputPath);
			System.out.println("qfile = " + file);

			ProcessGtfsSnapshotMerge merge;
			merge = new ProcessGtfsSnapshotMerge(file, feedSourceId);
			merge.run();

			// if there are multiple agencies this will pick one randomly
			search(merge.agencyId);


		} catch(Exception e) {
			e.printStackTrace();
		}
	}

    /** snapshots page */
    public static void snapshots() {
    	render();
    }
    
    /** schedule exceptions page */
    public static void exceptions () {
    	render();
    }
    
    /** check database integrity */
    public static void checkIntegrity () {
    	Map<String, Set<Object>> ret = Maps.newHashMap();
    	
    	ret.put("tripsDontMatchPattRoute", new HashSet<Object> ());
    	ret.put("tripsDontMatchPatt", new HashSet<Object> ());
    	ret.put("tripsWithoutStartEndTimes", new HashSet<Object> ());
    	ret.put("tripsWithLessThanTwoStops", new HashSet<Object> ());
    	
    	GlobalTx gtx = VersionedDataStore.getGlobalTx();
    	
    	for (String agencyId : gtx.agencies.keySet()) {
    		AgencyTx tx = VersionedDataStore.getAgencyTx(agencyId);
    		
    		for (Trip trip : tx.trips.values()) {
    			TripPattern p = tx.tripPatterns.get(trip.patternId);
    			
    			if (!p.routeId.equals(trip.routeId))
    				ret.get("tripsDontMatchPattRoute").add(trip);
    			
    			if (trip.stopTimes.size() != p.patternStops.size()) {
    				ret.get("tripsDontMatchPatt").add(trip);
    			}
    			else {
    				STOPTIMES: for (int i = 0; i < trip.stopTimes.size(); i++) {
    					StopTime st = trip.stopTimes.get(i);
    					TripPatternStop ps = p.patternStops.get(i);
    					
    					if (st == null)
    						continue;

    					if (!ps.stopId.equals(st.stopId)) {
    	    				ret.get("tripsDontMatchPatt").add(trip);
    	    				break STOPTIMES;
    					}
    				}
    			}
    			
    			int stopTimesSize = 0;
    			
    			StopTime lastSt = null;
    			boolean foundFirstSt = false;
    			
    			for (StopTime st : trip.stopTimes) {
    				if (st == null)
    					continue;
    				
    				if (!foundFirstSt) {
    					foundFirstSt = true;
    					if (st.arrivalTime == null || st.departureTime == null) {
    				    	ret.get("tripsWithoutStartEndTimes").add(trip);
    					}
    				}
    				
					lastSt = st;
					stopTimesSize++;
    			}
    			
    			if (stopTimesSize < 2) {
			    	ret.get("tripsWithLessThanTwoStops").add(trip);
    			}
    			
    			if (lastSt != null && (lastSt.departureTime == null || lastSt.arrivalTime == null)) {
			    	ret.get("tripsWithoutStartEndTimes").add(trip);
    			}
    		}
    		
    		tx.rollback();
    	}
    	
    	gtx.rollback();
    	
    	try {
    		renderJSON(Base.toJson(ret, true));
    	} catch (Exception e) {
    		badRequest();
    	}
    }

}