package controllers;

import java.util.ArrayList;
import java.util.List;

import data.Graph;

import play.*;
import play.mvc.*;
import play.data.*;
import static play.data.Form.*;

import util.GraphDataManager;
import util.GtfsDataManager;
import util.OsmDataManager;
import views.html.app.*;

public class Application extends Controller {
	
	static public GraphDataManager graphDataManager = new GraphDataManager();
	static public OsmDataManager   osmDataManager 	= new OsmDataManager();
	static public GtfsDataManager  gtfsDataManager 	= new GtfsDataManager();
	
    public static Result index() {
    	
    	List<Graph> graphList = graphDataManager.getGraphs();
    	
    	return ok(index.render(graphList, null));
  
    }
    

  
}
