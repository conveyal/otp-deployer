package controllers;

import java.util.ArrayList;
import java.util.List;

import data.Graph;
import data.Gtfs;
import data.OsmSource;
import play.*;
import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import util.GraphDataManager;
import util.OsmDataManager;
import views.html.graphs.*;

public class Graphs extends Controller {
  
	final static Form<Graph> graphForm = form(Graph.class);
    
    public static Result index() {
        
    	List<Graph> graphList = Application.graphDataManager.getGraphs();
    	
    	return ok(index.render(graphList));
    }
    
    public static Result create() {
            	 
    	List<Graph> graphList = Application.graphDataManager.getGraphs();
    	
    	return ok(create.render(graphList, graphForm));
    }
    
    public static Result submit() {
    	
    	List<Graph> graphList = Application.graphDataManager.getGraphs();
    	
    	Form<Graph> filledForm = graphForm.bindFromRequest();
    	
    	if( !filledForm.hasErrors() ) {
    		filledForm.value().get().save();
    		Application.graphDataManager.loadGraphs();
    		
    		return redirect(controllers.routes.Graphs.graph(filledForm.value().get().id));		
    	}
    
    	return ok(create.render(graphList, filledForm));
    }
     
    public static Result graph(String graphId) {
    	
    	List<Graph> graphList = Application.graphDataManager.getGraphs();
    
    	Graph selectedGraph = Application.graphDataManager.getGraphById(graphId);
    	
    	List<data.Gtfs> gtfsList = Application.gtfsDataManager.getGtfs();
    	
    	List<data.OsmSource> osmSources = Application.osmDataManager.getOsm();
    	
    	if(selectedGraph == null)
    		return redirect(controllers.routes.Graphs.index());
    	
    	return ok(graph.render(selectedGraph, graphList, graphId, gtfsList, selectedGraph.selectedGtfs(), osmSources, selectedGraph.selectedOsm()));
    }
    
    public static Result refresh() {
    	
    	Application.graphDataManager.loadGraphs();
    	
    	return redirect(controllers.routes.Graphs.index());
    }
  
    public static Result addGtfs() {
    	
    	String[] graphId = request().body().asFormUrlEncoded().get("graphId");
    	String[] gtfsId = request().body().asFormUrlEncoded().get("gtfsId");
    	
    	Graph selectedGraph = Application.graphDataManager.getGraphById(graphId[0]);
    	
    	if(selectedGraph == null)
    		return redirect(controllers.routes.Graphs.index());
    	
    	Gtfs selectedGtfs = Application.gtfsDataManager.getGtfsById(gtfsId[0]);
    	
    	selectedGraph.addGtfs(selectedGtfs);
    	
    	return redirect(controllers.routes.Graphs.graph(graphId[0]));	
    }
    
    public static Result addOsm() {
    	
    	String[] graphId = request().body().asFormUrlEncoded().get("graphId");
    	String[] osmId = request().body().asFormUrlEncoded().get("osmSourceId");
    	
    	Graph selectedGraph = Application.graphDataManager.getGraphById(graphId[0]);
    	
    	if(selectedGraph == null)
    		return redirect(controllers.routes.Graphs.index());
    	
    	OsmSource selectedOsmSource = Application.osmDataManager.getOsmById(osmId[0]);
    	
    	selectedGraph.addOsm(selectedOsmSource);
    	
    	return redirect(controllers.routes.Graphs.graph(graphId[0]));	
    }

    public static Result webhook(String id) {

        List<Graph> graphList = Application.graphDataManager.getGraphs();

        return ok(id);

    }
    
}
