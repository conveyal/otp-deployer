package util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import data.Graph;
import data.OsmExtract;

public class OsmDataManager extends DataManager {
	
	private ArrayList<OsmExtract> osmList = new ArrayList<OsmExtract>();
	private HashMap<String, OsmExtract> idOsmList = new HashMap<String, OsmExtract>();
	private HashMap<String, OsmExtract> nameOsmList = new HashMap<String, OsmExtract>();
	
	public OsmDataManager() {
		
		loadOsm();
		
	}
	
	public OsmExtract getOsmById(String id) {
		
		return idOsmList.get(id);	
		
	}
	
	public void loadOsm() {
		
		osmList.clear();
		
		File rootDirectory = OsmExtract.getRootDirectory();
		
		for(File osmDir : rootDirectory.listFiles()) {
			String id = osmDir.getName();
			
			OsmExtract osm = OsmExtract.load(id);
			
			if(osm != null) {
				osmList.add(osm);
				idOsmList.put(osm.id, osm);
				nameOsmList.put(osm.name, osm);
			}
		}
		
		Collections.sort(osmList);
	}
	
	public List<OsmExtract> getOsm() {
		
		return osmList;
		
	}

}
