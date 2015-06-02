package controllers.api;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import jobs.ProcessGtfsSnapshotExport;
import models.Snapshot;
import models.transit.Stop;

import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;

import controllers.Base;
import controllers.Application;
import controllers.Secure;
import controllers.Security;
import datastore.GlobalTx;
import datastore.VersionedDataStore;
import play.Play;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import utils.JacksonSerializers;

@With(Secure.class)
public class SnapshotController extends Controller {
	@Before
	static void initSession() throws Throwable {
		 
		if(!Security.isConnected() && !Application.checkOAuth(request, session))
			Secure.login();
	}
	
	public static void getSnapshot(String agencyId, String id) throws IOException {
		GlobalTx gtx = VersionedDataStore.getGlobalTx();
		
		try {
			if (id != null) {
				Tuple2<String, Integer> sid = JacksonSerializers.Tuple2IntDeserializer.deserialize(id);
				if (gtx.snapshots.containsKey(sid))
					renderJSON(Base.toJson(gtx.snapshots.get(sid), false));
				else
					notFound();
				
				return;
			}
			else {
				if (agencyId == null)
					agencyId = session.get("agencyId");
				
				if (agencyId == null) {
					badRequest();
					return;
				}
				
				Collection<Snapshot> snapshots = gtx.snapshots.subMap(new Tuple2(agencyId, null), new Tuple2(agencyId, Fun.HI)).values();
				renderJSON(Base.toJson(snapshots, false));
			} 
		} finally {
			gtx.rollback();
		}
	}
	
	public static void createSnapshot () {
		GlobalTx gtx = null;
		try {
			// create a dummy snapshot from which to get values
			Snapshot original = Base.mapper.readValue(params.get("body"), Snapshot.class);
			Snapshot s = VersionedDataStore.takeSnapshot(original.agencyId, original.name);
			s.validFrom = original.validFrom;
			s.validTo = original.validTo;
			gtx = VersionedDataStore.getGlobalTx();
			
			// the snapshot we have just taken is now current; make the others not current
			for (Snapshot o : gtx.snapshots.subMap(new Tuple2(s.agencyId, null), new Tuple2(s.agencyId, Fun.HI)).values()) {
				if (o.id.equals(s.id))
					continue;
				
				Snapshot cloned = o.clone();
				cloned.current = false;
				gtx.snapshots.put(o.id, cloned);
			}
			
			gtx.commit();
			
			renderJSON(Base.toJson(s, false));
		} catch (IOException e) {
			e.printStackTrace();
			badRequest();
			if (gtx != null) gtx.rollbackIfOpen();
		}
	}
	
	public static void restoreSnapshot (String id) {
		Tuple2<String, Integer> decodedId;
		try {
			decodedId = JacksonSerializers.Tuple2IntDeserializer.deserialize(id);
		} catch (IOException e1) {
			badRequest();
			return;
		}
		
		GlobalTx gtx = VersionedDataStore.getGlobalTx();
		Snapshot local;
		try {
			if (!gtx.snapshots.containsKey(decodedId)) {
				notFound();
				return;
			}
			
			local = gtx.snapshots.get(decodedId);
			
			List<Stop> stops = VersionedDataStore.restore(local);
			
			// the snapshot we have just restored is now current; make the others not current
			for (Snapshot o : gtx.snapshots.subMap(new Tuple2(local.agencyId, null), new Tuple2(local.agencyId, Fun.HI)).values()) {
				if (o.id.equals(local.id))
					continue;
				
				Snapshot cloned = o.clone();
				cloned.current = false;
				gtx.snapshots.put(o.id, cloned);
			}
			
			Snapshot clone = local.clone();
			clone.current = true;
			gtx.snapshots.put(local.id, clone);
			gtx.commit();
			
			renderJSON(Base.toJson(stops, false));
		} catch (IOException e) {
			e.printStackTrace();
			badRequest();
		} finally {
			gtx.rollbackIfOpen();
		}
	}

	/** Export a snapshot as GTFS */
	public static void exportSnapshot (String id) {
		Tuple2<String, Integer> decodedId;
		try {
			decodedId = JacksonSerializers.Tuple2IntDeserializer.deserialize(id);
		} catch (IOException e1) {
			badRequest();
			return;
		}

		GlobalTx gtx = VersionedDataStore.getGlobalTx();
		Snapshot local;
		try {
			if (!gtx.snapshots.containsKey(decodedId)) {
				notFound();
				return;
			}

			local = gtx.snapshots.get(decodedId);

			File out = new File(Play.configuration.getProperty("application.publicDataDirectory"), "gtfs_" + Application.nextExportId.incrementAndGet() + ".zip");

			new ProcessGtfsSnapshotExport(local, out).run();

			redirect(Play.configuration.getProperty("application.appBase") + "/public/data/"  + out.getName());
		} finally {
			gtx.rollbackIfOpen();
		}
	}
}
