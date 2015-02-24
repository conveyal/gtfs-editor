package models;

import java.io.File;
import java.io.Serializable;

import org.mapdb.Fun.Tuple2;

import utils.JacksonSerializers;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Represents a snapshot of an agency database.
 * @author mattwigway
 *
 */
public class Snapshot implements Serializable {	
	/** The version of this snapshot */
	public int version;
	
	/** The name of this snapshot */
	public String name;
	
	/** ID: agency ID, version */
    @JsonSerialize(using=JacksonSerializers.Tuple2IntSerializer.class)
    @JsonDeserialize(using=JacksonSerializers.Tuple2IntDeserializer.class)
	public Tuple2<String, Integer> id;
	
	/** The agency associated with this */
	public String agencyId;
	
	/** the date/time this snapshot was taken (millis since epoch) */
	public long snapshotTime;
	
	public Snapshot (String agencyId, int version) {
		this.agencyId = agencyId;
		this.version = version;
		this.id = new Tuple2(agencyId, version);
	}
}
