package utils;

import java.io.IOException;

import org.mapdb.Fun.Tuple2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.google.common.io.BaseEncoding;

public class JacksonSerializers {
	private static final BaseEncoding encoder = BaseEncoding.base64Url();

	public static class Tuple2Serializer extends StdScalarSerializer<Tuple2<String, String>> {
		public Tuple2Serializer () {
			super(String.class, true);
		}
		
		@Override
		public void serialize(Tuple2<String, String> t2, JsonGenerator jgen,
				SerializerProvider arg2) throws IOException,
				JsonProcessingException {
			// TODO: escaping
			jgen.writeString(encoder.encode(t2.a.getBytes("UTF-8")) + ":" + encoder.encode(t2.b.getBytes("UTF-8")));
		}
	}
	
	public static class Tuple2Deserializer extends StdScalarDeserializer<Tuple2<String, String>> {
		public Tuple2Deserializer () {
			super(Tuple2.class);
		}
		
		@Override
		public Tuple2<String, String> deserialize(JsonParser jp,
				DeserializationContext arg1) throws IOException,
				JsonProcessingException {			
			return deserialize(jp.readValueAs(String.class));
		}
		
		public static Tuple2<String, String> deserialize (String serialized) throws IOException {
			String[] val = serialized.split(":");
			if (val.length != 2) {
				throw new IOException("Unable to parse value");
			}
			
			return new Tuple2<String, String>(new String(encoder.decode(val[0]), "UTF-8"), new String(encoder.decode(val[1]), "UTF-8"));
		}
	}
}
