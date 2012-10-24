package jobs;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.serialization.GtfsReader;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mchange.v2.c3p0.impl.DbAuth;

import models.gtfs.GtfsSnapshotMerge;
import models.gtfs.GtfsSnapshotMergeTask;
import models.gtfs.GtfsSnapshotMergeTaskStatus;
import models.transit.Agency;
import models.transit.Route;
import models.transit.ServiceCalendar;
import models.transit.ServiceCalendarDate;
import models.transit.Stop;
import models.transit.StopTime;
import models.transit.TripShape;
import models.transit.Trip;


import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class BootstrapDatabase extends Job {

	public void doJob() {
		
		Logger.info("Bootstrapping Database...");
		
		try
		{
			if(Agency.count() == 0)
			{
				Fixtures.loadModels("initial-agencies-data.yml");
				
			}
		}
		catch (Exception e)
		{
			Logger.error(e.toString()); 
		}
		
				
	}

}

