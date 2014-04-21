package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import data.Graph;
import data.OsmSource;
import data.OsmVersion;
import play.*;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.data.*;
import static play.data.Form.*;
import util.GraphDataManager;
import util.OsmDataManager;
import views.html.osm.*;

public class Osm extends Controller {
  
	final static Form<OsmSource> osmForm = form(OsmSource.class);
    
    public static Result index() {
        
    	List<OsmSource> osmList = Application.osmDataManager.getOsm();
    	
    	return ok(index.render(osmList));
    }
    
    public static Result source(String sourceId) {
        
    	OsmSource osmSource = Application.osmDataManager.getOsmById(sourceId);
    	
    	List<OsmVersion> versions = osmSource.getVersions();
    	
    	return ok(source.render(osmSource, versions));
    }
    
    public static Result create() {
            	 
    	List<OsmSource> osmList = Application.osmDataManager.getOsm();
    	
    	return ok(create.render(osmForm));
    }
    
    public static Result submit() {
    	  	
    	Form<OsmSource> filledForm = osmForm.bindFromRequest();
    	
    	if( !filledForm.hasErrors() ) {

    		MultipartFormData body = request().body().asMultipartFormData();
    		FilePart osmFile = body.getFile("osmFile");
    		
    		if (osmFile != null) {
    			
    			String fileName = osmFile.getFilename();
    		    String contentType = osmFile.getContentType(); 
    		    File file = osmFile.getFile();
    		    
    		    filledForm.value().get().addFile(file);
    		    
    		    Application.osmDataManager.loadOsm();
    		
    		    return redirect(controllers.routes.Osm.source(filledForm.value().get().id));
    		}
    		else {
    			filledForm.error("Missing file");
    		}
    	}
    
    	return ok(create.render(filledForm));
    }
    
    public static Result update() {
    	
    	MultipartFormData body = request().body().asMultipartFormData();
    	FilePart osmFile = body.getFile("osmFile");
    	String[] osmId = body.asFormUrlEncoded().get("osmSourceId");
    	
    	
    	OsmSource selectedOsmSource = Application.osmDataManager.getOsmById(osmId[0]);
    	
    	if (osmFile != null) {
			
		    File file = osmFile.getFile();
		    
		    selectedOsmSource.addFile(file);
		    
		    Application.osmDataManager.loadOsm();
		
		    return redirect(controllers.routes.Osm.source(selectedOsmSource.id));
		}
		
    	return redirect(controllers.routes.Osm.index());
    	
    }
    
    public static Result select(String osmSourceId, String versionId) {
    	
    	OsmSource osmSource = Application.osmDataManager.getOsmById(osmSourceId);
    	
    	if(osmSource == null)
    		return redirect(controllers.routes.Osm.index());
    	
    	OsmVersion version = osmSource.getVersion(versionId);
    	
    	if(version == null)
    		return redirect(controllers.routes.Osm.index());
    	
    	osmSource.setVersion(versionId);
    	
    	return redirect(controllers.routes.Osm.source(osmSource.id));
    }
  
}
