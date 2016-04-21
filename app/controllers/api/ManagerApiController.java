package controllers.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jobs.ProcessGtfsSnapshotExport;
import models.Snapshot;
import models.transit.Agency;
import models.transit.Stop;

import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;

import controllers.Base;
import controllers.Application;
import controllers.Secure;
import controllers.Security;
import datastore.GlobalTx;
import datastore.VersionedDataStore;
import play.Logger;
import play.Play;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import play.mvc.Http;

import utils.JacksonSerializers;

public class ManagerApiController extends Controller {

    // todo: Auth0 authentication

    @Before
    public static void setCORS()  {
        Http.Header origin = new Http.Header();
        origin.name = "Access-Control-Allow-Origin";
        origin.values = new ArrayList<String>();
        origin.values.add("*");
        Http.Response.current().headers.put("Access-Control-Allow-Origin",origin);

        Http.Header headers = new Http.Header();
        headers.name = "Access-Control-Allow-Headers";
        headers.values = new ArrayList<String>();
        headers.values.add("Origin, X-Requested-With, Content-Type, Accept, Authorization");
        Http.Response.current().headers.put("Access-Control-Allow-Headers",headers);
    }

    public static void options() {
    }

    public static void getSnapshot(String sourceId, String id) throws IOException {
        GlobalTx gtx = VersionedDataStore.getGlobalTx();

        System.out.println("getSnapshot for " +sourceId);
        try {
            if (id != null) {
                Tuple2<String, Integer> sid = JacksonSerializers.Tuple2IntDeserializer.deserialize(id);
                if (gtx.snapshots.containsKey(sid))
                    renderJSON(Base.toJson(gtx.snapshots.get(sid), false));
                else
                    notFound();

                return;
            } else {
                Collection<Snapshot> snapshots = new ArrayList<>();
                Collection<Snapshot> allSnapshots;

                allSnapshots = gtx.snapshots.values();
                for(Snapshot snapshot : allSnapshots) {
                    Agency agency = gtx.agencies.get(snapshot.agencyId);
                    if(agency == null || agency.sourceId == null) continue;
                    if(agency.sourceId.equals(sourceId)) {
                        System.out.println("found!");
                        snapshots.add(snapshot);
                    }
                }

                renderJSON(Base.toJson(snapshots, false));
            }
        } finally {
            gtx.rollback();
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