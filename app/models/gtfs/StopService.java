package models.gtfs;

import java.util.List;

import javax.persistence.EntityManager;

public class StopService {
  protected EntityManager em;

  public StopService(EntityManager em) {
    this.em = em;
  }

  public void addStop(Long id, org.onebusaway.gtfs.model.Stop gtfsStop) {
	  em.createNativeQuery("INSERT INTO stop (id, locationtype, parentstation, stopcode, stopdesc, stopid, stopname, stopurl, zoneid,  location)" +
      "       VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?")
        .setParameter(1, id)
        .setParameter(1,  gtfsStop.getLocationType())
        .setParameter(1,  gtfsStop.getParentStation())
        .setParameter(1,  gtfsStop.getCode())
        .setParameter(1,  gtfsStop.getDesc())
        .setParameter(1,  gtfsStop.getId().toString())
        .setParameter(1,  gtfsStop.getName())
        .setParameter(1,  gtfsStop.getUrl())
        .setParameter(1,  gtfsStop.getZoneId())
        .setParameter(1,  "POINT(" + gtfsStop.getLat() + " " + gtfsStop.getLon() + ")")
        .executeUpdate();

}
}