package util;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


import play.Play;
import play.data.validation.Constraints.Required;

public abstract class JsonModel {
	
	public String id = "";
	public Date created = null;
	public Date modified = null;
	
	@Required
	public String name;
	
	@Required
	public String description;
	
	
	public JsonModel() {
		
		String uuid = UUID.randomUUID().toString();
		this.id = HashUtils.hashString(uuid);
		this.created = new Date();
	}
	
	public JsonModel(String id) {
		
		if(id == null || id.isEmpty())
			this.id = HashUtils.hashString(UUID.randomUUID().toString());
		else
			this.id = id;

		this.created = new Date();		
		
	}
	
	protected static File getDirectory(String parent, String type) {
		
		if(parent == null || parent.isEmpty()) {
			parent = Play.application().configuration().getString("application.dataDirectory");
		}
		
		return buildPath(parent, type);
		
	}
	
	public static File buildPath(String parent, String child) {
		File dir =  new File(parent, child);
		
		if(!dir.exists())
			dir.mkdirs();
		
		return dir;
	}
	
	public static File buildPath(File parent, String child) {
		File dir =  new File(parent, child);
		
		if(!dir.exists())
			dir.mkdirs();
		
		return dir;
	}
	
}
