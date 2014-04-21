package data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;





import util.HashUtils;
import util.JsonModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OsmSource extends JsonModel implements Comparable<OsmSource> {

	public static String TYPE = "osm";
	
	public String currentVersion;
	public List<String> osmVersions = new ArrayList<String>();
	
	public static File getRootDirectory() {
		return JsonModel.getDirectory(null, TYPE);
	}
	
	public static File getDataPath(String id) {
		return new File(getDirectory(id), "data.json");
	}
	
	public static File getDirectory(String id) {
		
		return  buildPath(getRootDirectory(), id);	
	}
	
	public File getDirectory() {
		
		return getDirectory(id);
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
	
	public static OsmSource load(String id) {
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			
			File path = getDataPath(id);
			
			return mapper.readValue(path, OsmSource.class);
			
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
	public int compareTo(OsmSource other) {
		return name.compareTo(other.name);
	}
	
	public void addFile(final File  osmFile) {

		String osmHash = HashUtils.hashFile(osmFile);
		
		try {
			
			if(osmVersions.contains(osmHash)) {
				
				// switch current to stored/existing version
				setVersion(osmHash);
				
			}
			else {
				
				osmVersions.add(osmHash);
				
				// create a new version
				final File osmVersionFile = OsmVersion.getOsmPath(id, osmHash);
			
				FileUtils.copyFile(osmFile, osmVersionFile);
				
				OsmVersion summary = new OsmVersion();
				
				summary.osmId = this.id;
				summary.versionId = osmHash;
				
				summary.name = osmFile.getName();
				summary.created = new Date(osmFile.lastModified());
				summary.modified = new Date();
				
				summary.save();
				
				setVersion(osmHash);
			}
			
			save();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	public List<OsmVersion> getVersions() {
		
		List<OsmVersion> versionList = new ArrayList<OsmVersion>();
		
		for(File versionDir : OsmVersion.getRootDirectory(id).listFiles()) {
			String versionId = versionDir.getName();
			
			OsmVersion version = OsmVersion.load(id, versionId);
			
			versionList.add(version);
			
		}
		
		Collections.sort(versionList);
		
		return versionList;
	}
	
	public OsmVersion getVersion(String versionId) {
		
		return OsmVersion.load(id, versionId);
	}
	
	
	public void setVersion(String osmVersionId) {
		
		this.currentVersion = osmVersionId;
		try {
			FileUtils.copyFile(OsmVersion.getOsmPath(id, osmVersionId), new File(getDirectory(), "current_osm.pbf"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.save();
	}
	
}