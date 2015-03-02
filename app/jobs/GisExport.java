package jobs;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import datastore.AgencyTx;
import datastore.GlobalTx;
import datastore.VersionedDataStore;
import models.transit.*;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.google.common.io.Files;
import sun.misc.Version;
import utils.DirectoryZip;

/** Export routes or stops as a shapefile */
public class GisExport implements Runnable {
	File file;
	Type type;
	Collection<String> agencyIds;
	
	public GisExport(Type type, File file, Collection<String> agencyIds) {
		this.type = type;
		this.file = file;
		this.agencyIds = agencyIds;
	}
	
	@Override
	public void run() {
		File outDir = Files.createTempDir();
		File outShp = new File(outDir, file.getName().replaceAll("\\.zip", "") + ".shp");

		GlobalTx gtx = VersionedDataStore.getGlobalTx();
		AgencyTx atx = null;
    	try {
			ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
			
			Map<String, Serializable> params = new HashMap<String, Serializable>();
			params.put("url", outShp.toURI().toURL());
			params.put("create spatial index", Boolean.TRUE);
    		
			ShapefileDataStore dataStore = (ShapefileDataStore)dataStoreFactory.createNewDataStore(params);
			dataStore.forceSchemaCRS(DefaultGeographicCRS.WGS84);
    		
			SimpleFeatureType STOP_TYPE = DataUtilities.createType(
			        "Stop",                 
			        "the_geom:Point:srid=4326," +
			        "name:String," +
			        "code:String," +
			        "desc:String," +
			        "id:String," +
			        "agency:String"     
			);
    	
	    	SimpleFeatureType ROUTE_TYPE = DataUtilities.createType(
	                "Route",                   // <- the name for our feature type
	                "the_geom:LineString:srid=4326," +
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
	    	
	    	SimpleFeatureCollection collection;
        	
        	SimpleFeatureType collectionType;

            SimpleFeatureBuilder featureBuilder = null;
            
            List<SimpleFeature> features = new ArrayList<SimpleFeature>();

			if (type.equals(Type.STOPS)) {
				collectionType = STOP_TYPE;
				dataStore.createSchema(STOP_TYPE);
				featureBuilder = new SimpleFeatureBuilder(STOP_TYPE);

				for (String agencyId : agencyIds) {
					Agency agency = gtx.agencies.get(agencyId);

					atx = VersionedDataStore.getAgencyTx(agencyId);
					for (Stop s : atx.stops.values()) {
						featureBuilder.add(s.location);
						featureBuilder.add(s.stopName);
						featureBuilder.add(s.stopCode);
						featureBuilder.add(s.stopDesc);
						featureBuilder.add(s.getGtfsId());
						featureBuilder.add(agency.name);
						SimpleFeature feature = featureBuilder.buildFeature(null);
						features.add(feature);
					}

					atx.rollback();
				}
			} else if (type.equals(Type.ROUTES)) {
				collectionType = ROUTE_TYPE;
				dataStore.createSchema(ROUTE_TYPE);
				featureBuilder = new SimpleFeatureBuilder(ROUTE_TYPE);

				GeometryFactory gf = new GeometryFactory();

				for (String agencyId : agencyIds) {
					Agency agency = gtx.agencies.get(agencyId);

					atx = VersionedDataStore.getAgencyTx(agencyId);

					// we loop over trip patterns. Note that this will yield several lines for routes that have
					// multiple patterns. There's no real good way to reconcile the shapes of multiple patterns.
					for (TripPattern tp : atx.tripPatterns.values()) {
						LineString shape;
						if (tp.shape != null) {
							shape = tp.shape;
						} else {
							// build the shape from the stops
							Coordinate[] coords = new Coordinate[tp.patternStops.size()];

							for (int i = 0; i < coords.length; i++) {
								coords[i] = atx.stops.get(tp.patternStops.get(i).stopId).location.getCoordinate();
							}

							shape = gf.createLineString(coords);
						}

						Route r = atx.routes.get(tp.routeId);

						featureBuilder.add(shape);
						featureBuilder.add(tp.name);
						featureBuilder.add(r.routeShortName);
						featureBuilder.add(r.routeLongName);
						featureBuilder.add(r.routeDesc);

						if (r.routeTypeId != null)
							featureBuilder.add(gtx.routeTypes.get(r.routeTypeId).toString());
						else
							featureBuilder.add("");

						featureBuilder.add(r.routeUrl);
						featureBuilder.add(r.routeColor);
						featureBuilder.add(r.routeTextColor);
						featureBuilder.add(agency.name);
						SimpleFeature feature = featureBuilder.buildFeature(null);
						features.add(feature);
					}

					atx.rollback();
				}
			}
			else
				throw new IllegalStateException("Invalid type");

			// save the file
			collection = new ListFeatureCollection(collectionType, features);

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

			// zip the file
			DirectoryZip.zip(outDir, file);

			// clean up
			for (File f : outDir.listFiles()) {
				f.delete();
			}
			outDir.delete();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (gtx != null) gtx.rollback();
			if (atx != null) atx.rollbackIfOpen();
		}
	}

	public static enum Type { ROUTES, STOPS };
}
