package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import data.Graph;
import data.GtfsVersion;

import play.*;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.data.*;
import static play.data.Form.*;

import util.GraphDataManager;
import util.OsmDataManager;
import views.html.gtfs.*;

public class Gtfs extends Controller {
  
	final static Form<data.Gtfs> gtfsForm = form(data.Gtfs.class);
    
    public static Result index() {
        
    	List<data.Gtfs> gtfsList = Application.gtfsDataManager.getGtfs();
    	
    	return ok(index.render(gtfsList));
    }
    
    public static Result create() {
            	 
    	List<Graph> graphList = Application.graphDataManager.getGraphs();
    	
    	return ok(create.render(gtfsForm));
    }
    
    public static Result submit() {
    	  	
    	Form<data.Gtfs> filledForm = gtfsForm.bindFromRequest();
    	
    	if( !filledForm.hasErrors() ) {

    		MultipartFormData body = request().body().asMultipartFormData();
    		FilePart gtfsFile = body.getFile("gtfsFile");
    		
    		if (gtfsFile != null) {
    			
    			String fileName = gtfsFile.getFilename();
    		    String contentType = gtfsFile.getContentType(); 
    		    File file = gtfsFile.getFile();
    		    
    		    filledForm.value().get().addFile(file);
    		    
    		    Application.gtfsDataManager.loadGtfs();
    		
    		    return redirect(controllers.routes.Gtfs.gtfs(filledForm.value().get().id));
    		}
    		else {
    			filledForm.error("Missing file");
    		}
    	}
    
    	return ok(create.render(filledForm));
    }
     
    public static Result gtfs(String gtfsId) {
    	
    	data.Gtfs selectedGtfs = Application.gtfsDataManager.getGtfsById(gtfsId);
    	
    	if(selectedGtfs == null)
    		return redirect(controllers.routes.Gtfs.index());
    	
    	List<GtfsVersion> versions = selectedGtfs.getVersions();
    	
    	return ok(gtfs.render(selectedGtfs, versions));
    }
    
    public static Result gtfsDetail(String gtfsId, String versionId) {
    	
    	data.Gtfs selectedGtfs = Application.gtfsDataManager.getGtfsById(gtfsId);
    	
    	if(selectedGtfs == null)
    		return redirect(controllers.routes.Gtfs.index());
    	
    	GtfsVersion version = selectedGtfs.getVersion(versionId);
    	
    	return ok(gtfsDetail.render(selectedGtfs, version));
    }
    
    
    public static Result select(String gtfsId, String versionId) {
    	
    	data.Gtfs selectedGtfs = Application.gtfsDataManager.getGtfsById(gtfsId);
    	
    	if(selectedGtfs == null)
    		return redirect(controllers.routes.Gtfs.index());
    	
    	GtfsVersion version = selectedGtfs.getVersion(versionId);
    	
    	if(version == null)
    		return redirect(controllers.routes.Gtfs.index());
    	
    	selectedGtfs.setVersion(versionId);
    	
    	return redirect(controllers.routes.Gtfs.gtfs(selectedGtfs.id));
    }
    
    public static Result update() {
    	
    	MultipartFormData body = request().body().asMultipartFormData();
    	FilePart gtfsFile = body.getFile("gtfsFile");
    	String[] gtfsId = body.asFormUrlEncoded().get("gtfsId");
    	
    	
    	data.Gtfs selectedGtfs = Application.gtfsDataManager.getGtfsById(gtfsId[0]);
    	
    	if (gtfsFile != null) {
			
		    File file = gtfsFile.getFile();
		    
		    selectedGtfs.addFile(file);
		    
		    Application.gtfsDataManager.loadGtfs();
		
		    return redirect(controllers.routes.Gtfs.gtfs(selectedGtfs.id));
		}
		
    	return redirect(controllers.routes.Gtfs.index());
    	
    }
    
    public static Result refresh() {
    	
    	Application.graphDataManager.loadGraphs();
    	
    	return redirect(controllers.routes.Graphs.index());
    }

}
