package data;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import util.JsonModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OsmVersion extends JsonModel implements Comparable<OsmVersion>  {

	public String osmId;
	public String versionId;
	
	public Integer nodeCount;
	public Integer wayCount;
	
	
	public static File getRootDirectory(String osmId) {
		return JsonModel.buildPath(OsmSource.getDirectory(osmId), "version");
	}
	
	public static File getDirectory(String osmId, String versionId) {
		return JsonModel.buildPath(getRootDirectory(osmId), versionId);
	}
	
	public File getDataPath() {
		return getDataPath(osmId, versionId);
	}
	
	public static File getDataPath(String osmId, String versionId) {
		return new File(getDirectory(osmId, versionId), "data.json");
	}
	
	public static File getOsmPath(String osmId, String versionId) {
		return new File(getDirectory(osmId, versionId), "osm.pbf");
	}

	
	public String buildUrl(String file) {
		return "/data/gtfs/" + osmId + "/version/" + versionId + "/" + file;
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
	
	public static OsmVersion load(String osmId, String versionId) {
		
		if(versionId == null || versionId.isEmpty())
			return null;
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			
			File path = getDataPath(osmId, versionId);
			
			return mapper.readValue(path, OsmVersion.class);
			
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
	
	public int compareTo(OsmVersion other) {
		return other.modified.compareTo(this.modified);
	}
	
}
