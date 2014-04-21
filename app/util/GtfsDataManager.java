package util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import data.Graph;
import data.Gtfs;
import data.OsmSource;

public class GtfsDataManager extends DataManager {
	
	private ArrayList<Gtfs> gtfsList = new ArrayList<Gtfs>();
	private HashMap<String, Gtfs> idGtfsList = new HashMap<String, Gtfs>();
	private HashMap<String, Gtfs> nameGtfsList = new HashMap<String, Gtfs>();
	
	public GtfsDataManager() {
		
		loadGtfs();
		
	}
	
	public Gtfs getGtfsById(String id) {
		
		return idGtfsList.get(id);	
		
	}
	
	public void loadGtfs() {
		
		gtfsList.clear();
		
		File rootDirectory = Gtfs.getRootDirectory();
		
		for(File gtfsDir : rootDirectory.listFiles()) {
			String id = gtfsDir.getName();
			
			Gtfs gtfs = Gtfs.load(id);
			
			if(gtfs != null) {
				gtfsList.add(gtfs);
				idGtfsList.put(gtfs.id, gtfs);
				nameGtfsList.put(gtfs.name, gtfs);
			}
		}
		
		Collections.sort(gtfsList);
	}
	
	public List<Gtfs> getGtfs() {
		
		return gtfsList;
		
	}

}
