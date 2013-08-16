package data;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import play.data.validation.Constraints.Required;
import util.JsonModel;

public class OsmExtract extends JsonModel implements Comparable<OsmExtract> {

	public static String TYPE = "osm";
	
	public static File getRootDirectory() {
		return JsonModel.getDirectory(null, TYPE);
	}
	
	public static File getDataPath(String id) {
		return new File(getDirectory(id), "data.json");
	}
	
	public static File getDirectory(String id) {
		
		return  buildPath(getRootDirectory(), id);	
	}
	
	public File getDataPath() {
		
		return getDataPath(id);
	}
		
	public void save() {
		
		ObjectMapper mapper = new ObjectMapper();
		
		modified = new Date();
		
		try {
			
			File path = getDataPath();
			
			mapper.writeValue(path, this);
			
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static OsmExtract load(String id) {
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			
			File path = getDataPath(id);
			
			return mapper.readValue(path, OsmExtract.class);
			
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public int compareTo(OsmExtract other) {
		return name.compareTo(other.name);
	}
}