package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.geotools.geojson.geom.GeometryJSON;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.serialization.GtfsReader;

import akka.util.Timeout;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import play.data.validation.Constraints.Required;
import play.libs.Akka;
import play.libs.F.Promise;
import scala.concurrent.duration.Duration;
import util.HashUtils;
import util.JsonModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Gtfs extends JsonModel implements Comparable<Gtfs> {

	public static Long 		MAX_PROCESSING_TIME = 60*10*1000l;
	public static String 	TYPE = "gtfs";

	//GtfsVersion summary = new GtfsVersion();
	
	public String currentVersion = "";

	public Boolean apiManaged = false;
	
	public List<String> gtfsVersions = new ArrayList<String>();
	
	
	public static File getRootDirectory() {
		return JsonModel.getDirectory(null, TYPE);
	}
	
	public static File getDataPath(String id) {
		return new File(getDirectory(id), "data.json");
	}
	
	public static File getDirectory(String id) {
		
		if(id == null)
			return null;
		
		return  buildPath(getRootDirectory(), id);	
	}
	
	public File getDirectory() {
		
		return getDirectory(id);
	}
	
	public File getDataPath() {
		
		return getDataPath(id);
	}
	
	public List<GtfsVersion> getVersions() {
		
		List<GtfsVersion> versionList = new ArrayList<GtfsVersion>();
		
		for(File versionDir : GtfsVersion.getRootDirectory(id).listFiles()) {
			String versionId = versionDir.getName();
			
			GtfsVersion version = GtfsVersion.load(id, versionId);
			
			versionList.add(version);
			
		}
		
		Collections.sort(versionList);
		
		return versionList;
	}
	
	public GtfsVersion getVersion(String versionId) {
		
		return GtfsVersion.load(id, versionId);
	}
	
	
	public void setVersion(String gtfsVersionId) {
		
		this.currentVersion = gtfsVersionId;
		try {
			FileUtils.copyFile(GtfsVersion.getGtfsPath(id, gtfsVersionId), new File(getDirectory(), "current_gtfs.zip"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.save();
	}

	public void checkVersion() {
		
		if(this.apiManaged) {
			
		}
	}
	
	public void addFile(final File  gtfsFile) {
		
		try {

			String gtfsHash = HashUtils.hashFile(gtfsFile);
			
			if(gtfsVersions.contains(gtfsHash)) {
				
				// switch current to stored/existing version
				setVersion(gtfsHash);
				
			}
			else {
				
				gtfsVersions.add(gtfsHash);
				
				// create a new version
				final File versionGtfsFile = GtfsVersion.getGtfsPath(id, gtfsHash);
				final File versionGeoJsonFile = GtfsVersion.getJsonPath(id, gtfsHash);
				final File versionValidationFile = GtfsVersion.getValidationPath(id, gtfsHash);
			
				FileUtils.copyFile(gtfsFile, versionGtfsFile);
				
				//Timeout timeout = new Timeout(Duration.create());
				
				//Akka.timeout(arg0, arg1, arg2)
				
				// run feed validator against versioned file
				Promise<Integer> validateGtfs = Akka.future(
				  new Callable<Integer>() {
				    public Integer call() throws IOException {
				    	
				    	
				    	String line = new File( ".", "bin/validator/feedvalidator.py").getCanonicalPath() + " -n -m -o " + versionValidationFile.getAbsolutePath() + " " + versionGtfsFile.getAbsolutePath();
						CommandLine commandLine = CommandLine.parse(line);
						DefaultExecutor executor = new DefaultExecutor();
						executor.setExitValue(1);
						
						int exitValue = executor.execute(commandLine);
				    	
				    	return exitValue;
				    }
				  });
				
				// run oba-gtfs parser against version file and extract metadata
				// also generates a geo.json export of buffered stop points
				Promise<GtfsVersion> parseGtfs = Akka.future(
				  new Callable<GtfsVersion>() {
				    public GtfsVersion call() throws IOException {
				      
				    	GtfsReader reader = new GtfsReader();
			        	GtfsDaoImpl store = new GtfsDaoImpl();
			    		reader.setInputLocation(versionGtfsFile);
			        	reader.setEntityStore(store);
			        	reader.run();
						
			        	GtfsVersion summary = new GtfsVersion();
			        	
			        	summary.agencyCount = store.getAllAgencies().size();
			        	summary.routeCount = store.getAllRoutes().size();
			        	summary.tripCount = store.getAllTrips().size();
			        	summary.stopCount = store.getAllStops().size();
			        	summary.stopTimeCount = store.getAllStopTimes().size();
			        	summary.calendarCount = store.getAllCalendars().size();
			        	
			        	// build agency names
			        	List<String> agencyNames = new ArrayList<String>();
			        	for(Agency ag : store.getAllAgencies()) {
			        		agencyNames.add(ag.getName() + " (" + ag.getId() + ")");
			        	}
			        	
			        	summary.description = StringUtils.join(agencyNames, ",");
			        	
			        	
			        	// build buffered stops polygon
			        	GeometryFactory gf = new GeometryFactory();
			        	
			        	Geometry mainPoly = null;
			        	
			        	for(Stop sp : store.getAllStops()) {
			        		
			        		Coordinate coord = new Coordinate(sp.getLon(), sp.getLat());
				            Geometry bufferedPt = gf.createPoint(coord).buffer(0.04);
				            if(mainPoly == null) mainPoly = bufferedPt;
				            else mainPoly = mainPoly.union(bufferedPt);
				            
			        	}
			        	
			        	GeometryJSON geoJson = new GeometryJSON();
			        	geoJson.write(mainPoly, versionGeoJsonFile);
			        	
			        	for(ServiceCalendar sc : store.getAllCalendars()) {
			        		
			        		if(summary.firstCalendarDate == null || sc.getStartDate().getAsDate().before(summary.firstCalendarDate))
			        			summary.firstCalendarDate = sc.getStartDate().getAsDate();
			        		
			        		if(summary.lastCalendarDate == null || sc.getEndDate().getAsDate().after(summary.lastCalendarDate))
			        			summary.lastCalendarDate = sc.getEndDate().getAsDate();
			        	}
			        	
			        	
			        	return summary;
				    }
				  });
				
				GtfsVersion summary = parseGtfs.get(MAX_PROCESSING_TIME);
				
				summary.gtfsId = this.id;
				summary.versionId = gtfsHash;
				
				summary.name = gtfsFile.getName();
				summary.created = new Date(gtfsFile.lastModified());
				summary.modified = new Date();
				
				summary.save();
				
				setVersion(gtfsHash);
			}
			
			save();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void save() {
		
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
	
	public static Gtfs load(String id) {
		
		if(id == null)
			return new Gtfs();
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			
			File path = getDataPath(id);
			
			return mapper.readValue(path, Gtfs.class);
			
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
	
	@Override
	public int compareTo(Gtfs other) {
		return name.compareTo(other.name);
	}

}