package util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import data.Graph;
import data.OsmSource;

public class OsmDataManager extends DataManager {
	
	private ArrayList<OsmSource> osmList = new ArrayList<OsmSource>();
	private HashMap<String, OsmSource> idOsmList = new HashMap<String, OsmSource>();
	private HashMap<String, OsmSource> nameOsmList = new HashMap<String, OsmSource>();
	
	public OsmDataManager() {
		
		loadOsm();
		
	}
	
	public OsmSource getOsmById(String id) {
		
		return idOsmList.get(id);	
		
	}
	
	public void loadOsm() {
		
		osmList.clear();
		
		File rootDirectory = OsmSource.getRootDirectory();
		
		for(File osmDir : rootDirectory.listFiles()) {
			String id = osmDir.getName();
			
			OsmSource osm = OsmSource.load(id);
			
			if(osm != null) {
				osmList.add(osm);
				idOsmList.put(osm.id, osm);
				nameOsmList.put(osm.name, osm);
			}
		}
		
		Collections.sort(osmList);
	}
	
	public List<OsmSource> getOsm() {
		
		return osmList;
		
	}

}
