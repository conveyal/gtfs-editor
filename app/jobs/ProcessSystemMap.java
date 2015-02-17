package jobs;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.transit.Agency;
import models.transit.Route;
import models.transit.Stop;
import models.transit.StopTime;
import models.transit.Trip;
import models.transit.TripPattern;
import models.transit.TripPatternStop;
import models.transit.TripShape;

import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import utils.StopSequence;

//@OnApplicationStart
public class ProcessSystemMap extends Job {
/*
	
	public void doJob() {
		
		Logger.info("Processing system map...");
		
		linkRouteTrips();
		
		findAgencies();
		
		findShortestTripPatterns();
	
		simplifyTripShapes();
		
		findServiceCalendars();
		
		Logger.info("Done processing system map...");
	
	}
	
	public void linkRouteTrips() {
		
		Logger.info("Linking routes to trips...");
		
		List<Trip> trips = Trip.findAll();
	
		for(Trip trip : trips)
		{
			if(trip.pattern == null)
				continue;
			
			if(trip.route != null)
				continue;
			
			trip.route = trip.pattern.route;
			
			trip.save();
		}
	}
	
	public void findAgencies() {
		
		Logger.info("Finding active agencies...");
		
		List<Agency> agencies = Agency.findAll();
	
		for(Agency agency : agencies)
		{
			agency.systemMap = false;
			
			List<Route> routes = Route.find("agency = ?", agency).fetch();
			
			for(Route route : routes)
			{
				if(Trip.count("route = ?", route) > 0)
				{
					agency.systemMap = true;
					agency.save();
				}
			}
		}
	}
	
	
	public void findServiceCalendars() {
	
		Logger.info("Finding service calendars for trip patterns...");
		
		List<TripPattern> patterns = TripPattern.findAll();
		
		for(TripPattern pattern : patterns)
		{
			Boolean weekday = false;
			Boolean saturday = false;
			Boolean sunday = false;
			
			List<Trip> trips = Trip.find("pattern = ?", pattern).fetch();
			
			for(Trip trip : trips)
			{ 
				if(trip.serviceCalendar.monday == true || trip.serviceCalendar.tuesday == true || trip.serviceCalendar.wednesday == true || trip.serviceCalendar.thursday == true || trip.serviceCalendar.friday == true)
					weekday = true;
				if(trip.serviceCalendar.saturday == true)
					saturday = true;
				if(trip.serviceCalendar.sunday == true)
					sunday = true;
			}
			
			pattern.weekday = weekday;
			pattern.saturday = saturday;
			pattern.sunday = sunday;
			
			pattern.save();
		}
		
		Logger.info("Finding service calendars for routes...");
		
		List<Route> routes = Route.findAll();
		
		for(Route route : routes)
		{
			List<TripPattern> routePatterns = TripPattern.find("route = ?", route).fetch();
			
			Boolean weekday = false;
			Boolean saturday = false;
			Boolean sunday = false;
			
			for(TripPattern pattern : routePatterns)
			{
				if(pattern.weekday == true)
					weekday = true;
				if(pattern.saturday == true)
					saturday = true;
				if(pattern.sunday == true)
					sunday = true;
			}
			
			route.weekday = weekday;
			route.saturday = saturday;
			route.sunday = sunday;
			
			route.save();
		}	
	}
	
	public void findShortestTripPatterns() {
		Logger.info("Finding longest trip patterns...");
		
		Map<Route, Map<String, ArrayList<TripPattern>>> tripPatternMap = new HashMap<Route, Map<String, ArrayList<TripPattern>>>();
		
		List<TripPattern> patterns = TripPattern.findAll();
		
		for(TripPattern pattern : patterns)
		{
			pattern.longest = false;
			pattern.save();
			
			if(!tripPatternMap.containsKey(pattern.route))
				tripPatternMap.put(pattern.route, new HashMap<String, ArrayList<TripPattern>>());
			
			if(!tripPatternMap.get(pattern.route).containsKey(pattern.name))
				tripPatternMap.get(pattern.route).put(pattern.name, new ArrayList<TripPattern>());
			
			tripPatternMap.get(pattern.route).get(pattern.name).add(pattern);	
		}
		
		for(Map<String, ArrayList<TripPattern>> routes : tripPatternMap.values())
		{
			for(ArrayList<TripPattern> groupedPatterns : routes.values())
			{
				TripPattern longestPattern = null;
				
				for(TripPattern p : groupedPatterns)
				{
					if(longestPattern == null)
						longestPattern = p;
					else
					{
						if(longestPattern.shape.shape.getLength() < p.shape.shape.getLength())
							longestPattern = p;
					}
				}
				
				longestPattern.longest = true;
				longestPattern.save();
			}
		}
	}

	public void simplifyTripShapes() {
		Logger.info("Simplifying trip shapes...");
	
		TripShape.em().createNativeQuery("UPDATE tripshape SET simpleshape = ST_Simplify(shape, 0.00001);").executeUpdate();
	}
	*/
}

