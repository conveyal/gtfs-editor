package jobs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.collection.FilteringSimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.hibernatespatial.readers.Feature;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.geotools.data.FileDataStoreFinder;

import com.mchange.v2.c3p0.impl.DbAuth;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.operation.linemerge.LineMerger;

import models.gis.GisExport;
import models.gis.GisExportStatus;
import models.gis.GisRoute;
import models.gis.GisStop;
import models.gis.GisUploadType;
import models.gtfs.GtfsSnapshotExportStatus;
import models.gtfs.GtfsSnapshotMerge;
import models.gtfs.GtfsSnapshotMergeTask;
import models.gtfs.GtfsSnapshotMergeTaskStatus;
import models.transit.Agency;
import models.transit.Route;
import models.transit.ServiceCalendar;
import models.transit.ServiceCalendarDate;
import models.transit.Stop;
import models.transit.StopTime;
import models.transit.TripPattern;
import models.transit.TripShape;
import models.transit.Trip;


import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import utils.DirectoryZip;
import utils.FeatureAttributeFormatter;

public class ProcessGisExport extends Job {

	private Long _gisExportId;

	
	public ProcessGisExport(Long gisExportId)
	{
		this._gisExportId = gisExportId;
	}
	
	public void doJob() {
		
		String exportName = "gis_" + this._gisExportId;
		
		File outputZipFile = new File(Play.configuration.getProperty("application.publicGtfsDataDirectory"), exportName + ".zip");
		
		File outputDirectory = new File(Play.configuration.getProperty("application.publicGtfsDataDirectory"), exportName);
		
		File outputShapefile = new File(outputDirectory, exportName + ".shp");
       
        try
        {
        	GisExport gisExport = null;
        	
        	while(gisExport == null)
        	{
        		gisExport = GisExport.findById(this._gisExportId);
        		Thread.sleep(1000);
        		
        		Logger.info("Waiting for gisExport object...");
        	}
        	
        	
        	if(!outputDirectory.exists())
        	{
        		outputDirectory.mkdir();
        	}
        	
			ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
			
			Map<String, Serializable> params = new HashMap<String, Serializable>();
			params.put("url", outputShapefile.toURI().toURL());
			params.put("create spatial index", Boolean.TRUE);
			
			ShapefileDataStore dataStore = (ShapefileDataStore)dataStoreFactory.createNewDataStore(params);
			dataStore.forceSchemaCRS(DefaultGeographicCRS.WGS84);

        	SimpleFeatureType STOP_TYPE = DataUtilities.createType(
                    "Stop",                 
                    "location:Point:srid=4326," + 
                    "name:String," +
                    "code:String," +
                    "desc:String," +
                    "id:String," +
                    "agency:String"     
            );
        	
        	SimpleFeatureType ROUTE_TYPE = DataUtilities.createType(
                    "Route",                   // <- the name for our feature type
                    "route:LineString:srid=4326," +
                    "patternName:String," +
                    "shortName:String," +
                    "longName:String," +
                    "desc:String," +
                    "type:String," +
                    "url:String," +
                    "routeColor:String," +
                    "routeTextColor:String," +
                    "agency:String"     
            );
        	
        	SimpleFeatureCollection collection = FeatureCollections.newCollection();

            SimpleFeatureBuilder featureBuilder = null;
            
            if(gisExport.type.equals(GisUploadType.STOPS))
            {
            	dataStore.createSchema(STOP_TYPE);
            	featureBuilder = new SimpleFeatureBuilder(STOP_TYPE);
            	
            	List<Stop> stops = Stop.find("agency in (:ids)").bind("ids", gisExport.agencies).fetch();
            	
            	for(Stop s : stops)
            	{
            		featureBuilder.add(s.locationPoint());
                    featureBuilder.add(s.stopName);
                    featureBuilder.add(s.stopCode);
                    featureBuilder.add(s.stopDesc);
                    featureBuilder.add(s.gtfsStopId);
                    featureBuilder.add(s.agency.name);
                    SimpleFeature feature = featureBuilder.buildFeature(null);
                    collection.add(feature);	
            	}
            }
            else if(gisExport.type.equals(GisUploadType.ROUTES))
            {
            	dataStore.createSchema(ROUTE_TYPE);
            	featureBuilder = new SimpleFeatureBuilder(ROUTE_TYPE);
            	
                List<Route> routes = Route.find("agency in (:ids)").bind("ids", gisExport.agencies).fetch();
            	
                // check for duplicates

                // HashMap<String, Boolean> existingRoutes = new HashMap<String,Boolean>();
                
            	for(Route r : routes)
            	{
//            		String routeId = r.routeLongName + "_" + r.routeDesc + "_ " + r.phone.id;
//            		
//            		if(existingRoutes.containsKey(routeId))
//            			continue;
//            		else
//            			existingRoutes.put(routeId, true); 
            		
            		
            		List<TripPattern> patterns = TripPattern.find("route = ?", r).fetch();
            		for(TripPattern tp : patterns)
                	{
            			if(tp.shape == null)
            				continue;
            		
            			featureBuilder.add(tp.shape.shape);
	            		featureBuilder.add(tp.name);
	            		featureBuilder.add(r.routeShortName);
	                    featureBuilder.add(r.routeLongName);
	                    featureBuilder.add(r.routeDesc);
	                    
	                    if(r.routeType != null)
	                    	featureBuilder.add(r.routeType.toString());
	                    else
	                    	featureBuilder.add("");
	                    
	                    featureBuilder.add(r.routeUrl);
	                    featureBuilder.add(r.routeColor);
	                    featureBuilder.add(r.routeTextColor);
	                    featureBuilder.add(r.agency.name);
	                    SimpleFeature feature = featureBuilder.buildFeature(null);
	                    collection.add(feature);	
                	}
            	}
            }
            else
            	throw new Exception("Unknown export type.");

            Transaction transaction = new DefaultTransaction("create");

            String typeName = dataStore.getTypeNames()[0];
            SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeName);

            if (featureSource instanceof SimpleFeatureStore) 
            {
                SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;

                featureStore.setTransaction(transaction);
               
                featureStore.addFeatures(collection);
                transaction.commit();

                transaction.close();
            } 
            else 
            {
            	throw new Exception(typeName + " does not support read/write access");
            }
            
            DirectoryZip.zip(outputDirectory, outputZipFile);
            FileUtils.deleteDirectory(outputDirectory);
			
			gisExport.status = GisExportStatus.PROCESSED;
			
			gisExport.save();
            
        }
        catch(Exception e)
        {	
        	Logger.error("Unable to process GIS export: ", e.toString());
        	e.printStackTrace();
        } 
	}
}


