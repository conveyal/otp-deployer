package data;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import util.JsonModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GtfsVersion extends JsonModel implements Comparable<GtfsVersion>  {

	public String gtfsId;
	public String versionId;
	
	public Integer agencyCount;
	public Integer routeCount;
	public Integer tripCount;
	public Integer stopCount;
	public Integer stopTimeCount;
	public Integer calendarCount;
	
	public Date firstCalendarDate;
	public Date lastCalendarDate;
	
	
	public static File getRootDirectory(String gtfsId) {
		return JsonModel.buildPath(Gtfs.getDirectory(gtfsId), "version");
	}
	
	public static File getDirectory(String gtfsId, String versionId) {
		return JsonModel.buildPath(getRootDirectory(gtfsId), versionId);
	}
	
	public File getDataPath() {
		return getDataPath(gtfsId, versionId);
	}
	
	public static File getDataPath(String gtfsId, String versionId) {
		return new File(getDirectory(gtfsId, versionId), "data.json");
	}
	
	public static File getGtfsPath(String gtfsId, String versionId) {
		return new File(getDirectory(gtfsId, versionId), "gtfs.zip");
	}
	
	public static File getJsonPath(String gtfsId, String versionId) {
		return new File(getDirectory(gtfsId, versionId), "geo.json");
	}
	
	public static File getValidationPath(String gtfsId, String versionId) {
		return new File(getDirectory(gtfsId, versionId), "validation.html");
	}
	
	public String buildUrl(String file) {
		return "/data/gtfs/" + gtfsId + "/version/" + versionId + "/" + file;
	}
	
	public String getValidationUrl() {
		
		return buildUrl("validation.html");
	}
	
	public String getJsonUrl() {
		
		return buildUrl("geo.json");
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
	
	public static GtfsVersion load(String gtfsId, String versionId) {
		
		if(versionId == null || versionId.isEmpty())
			return null;
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			
			File path = getDataPath(gtfsId, versionId);
			
			return mapper.readValue(path, GtfsVersion.class);
			
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
	
	public int compareTo(GtfsVersion other) {
		return other.modified.compareTo(this.modified);
	}
	
}
