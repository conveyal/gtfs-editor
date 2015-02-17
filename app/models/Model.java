package models;

import java.util.UUID;

public class Model {
	public final String id;
	
	public Model () {
		id = UUID.randomUUID().toString();
	}
	
	public Model(String id) {
		this.id = id;
	}
}
