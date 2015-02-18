package models;

import java.util.UUID;

public class Model {
	/** not final for purposes of deserialization, but don't change it once data has been persisted */
	public String id;
	
	/** Note that the constructor does not set an ID, as it is called on desrialization */
	public Model () {}
	
	public void generateId () {
		id = UUID.randomUUID().toString();
	}
}
