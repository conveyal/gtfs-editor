package utils;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.vividsolutions.jts.geom.Point;

public class PointJsonSerializer implements JsonSerializer<Point> {

	@Override
	public JsonElement serialize(Point arg0, Type arg1,
			JsonSerializationContext arg2) {
		
		JsonObject point = new JsonObject();
		point.addProperty("x",  arg0.getX());
		point.addProperty("y",  arg0.getY());
	
		return point;
	}

}

