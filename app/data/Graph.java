package data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import controllers.Application;
import play.Play;
import play.data.validation.Constraints.Required;
import util.HashUtils;
import util.JsonModel;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Graph extends JsonModel implements Comparable<Graph> {
	
	public static String TYPE = "graph";

	public String obaBundleId;
	
	public List<String> gtfsFiles = new ArrayList<String>();
	public List<String> osmFiles = new ArrayList<String>();
	
	public Graph() {
		
		super();
	}
	
	public Graph(String id, String name, String description) {
		
		super(id);  
		
		this.name = name;
		this.description = description;
		
		save();
	}
	
	public void cleanName() {
		this.name = this.name.trim().replaceAll("[^a-zA-Z0-9\\._]+", "_").toLowerCase();	
	}
	
	
	public static File getRootDirectory() {
		return JsonModel.getDirectory(null, TYPE);
	}
	
	public static File getDirectory(String id) {
		
		return  buildPath(getRootDirectory(), id);	
	}
	
	public File getDirectory() {
		
		return getDirectory(id);
	}
	
	public static File getDataPath(String id) {
		return new File(getDirectory(id), "data.json");
	}
	
	public File getDataPath() {
		
		return getDataPath(id);
	}
	
	public void addGtfs(Gtfs gtfs) {
		
		if(gtfsFiles.contains(gtfs.id))
			return;

		gtfsFiles.add(gtfs.id);
		
		save();
	}
	
	public void addOsm(OsmSource osmSource) {
		
		if(osmFiles.contains(osmSource.id))
			return;

		osmFiles.add(osmSource.id);
		
		save();
	}
	
	public List<Gtfs> selectedGtfs() {
		List<data.Gtfs> gtfsList = Application.gtfsDataManager.getGtfs();
    	
		List<data.Gtfs> selectedGtfsList = new ArrayList<data.Gtfs>();
		
		for(data.Gtfs gtfs : gtfsList) {
			if(this.gtfsFiles != null && this.gtfsFiles.contains(gtfs.id))
				selectedGtfsList.add(gtfs);
		}
		
		return selectedGtfsList;
	}
	
	public List<OsmSource> selectedOsm() {
		List<data.OsmSource> osmList = Application.osmDataManager.getOsm();
		
		List<data.OsmSource> selectedOsmList = new ArrayList<data.OsmSource>();
		
		for(data.OsmSource osm : selectedOsmList) {
			if(this.osmFiles != null && this.osmFiles.contains(osm.id))
				selectedOsmList.add(osm);
		}
		
		return selectedOsmList;
	}
		
	public void save() {
		
		cleanName();
		
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
	
	public static Graph load(String id) {
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			
			File path = getDataPath(id);
			
			Graph g = mapper.readValue(path, Graph.class);
			
			return g;
			
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
	
	public int compareTo(Graph other) {
		return name.compareTo(other.name);
	}
	
	public String validate() {
		
    	cleanName();
    	
    	if(Application.graphDataManager.nameInUse(name)) {
    		return "Name already in use";
    	}
    	
        return null;
    }
}
