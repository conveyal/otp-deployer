package util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import data.Graph;

public class GraphDataManager extends DataManager {

	private ArrayList<Graph> graphList = new ArrayList<Graph>();
	private HashMap<String, Graph> idGraphList = new HashMap<String, Graph>();
	private HashMap<String, Graph> nameGraphList = new HashMap<String, Graph>();
	
	public GraphDataManager() {
		
		loadGraphs();
		
	}
	
	public Graph getGraphById(String id) {
		
		return idGraphList.get(id);
		
	}
	
	public void loadGraphs() {
		
		graphList.clear();
		
		File rootDirectory = Graph.getRootDirectory();
		
		for(File graph : rootDirectory.listFiles()) {
			String id = graph.getName();
			
			Graph g = Graph.load(id);
			
			if(g != null) {
				graphList.add(g);
				idGraphList.put(g.id, g);
				nameGraphList.put(g.name, g);
			}
		}
		
		Collections.sort(graphList);
	}
	
	public Boolean nameInUse(String name) {
		return nameGraphList.containsKey(name);
	}
	
	public List<Graph> getGraphs() {
		
		return graphList;
		
	}
	
}

