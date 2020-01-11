package lx.ledgeo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import henning.leddriverj.util.Log;

public class ScoreManager {
	
	private static final String PROP_FILE_NAME = "./score.properties";
	private static final String FILE_VERSION = "1.0";
	
	private Properties properties;
	
	private ScoreManager()	{
		try {
			this.load();
		} catch (IOException e) {
			Log.error("Failed to open scoreManager. Exit...", "ScoreManager");
			Log.error(e);
			System.exit(1);
		}
	}
	private synchronized void load() throws IOException	{
		File propFile = new File(PROP_FILE_NAME);
		this.properties = new Properties();
		if (!propFile.exists())	{
			propFile.createNewFile();
			this.properties.setProperty("version", FILE_VERSION);
			Log.info("Created new properties file", "ScoreManager");
			this.properties.store(new FileOutputStream(propFile), "Unidash score file");
		}	else	{
			this.properties.load(new FileInputStream(propFile));
			Log.info("Loaded score properties", "Scoremanager");
		}
	}
	public synchronized void save() throws FileNotFoundException, IOException	{
		this.properties.store(new FileOutputStream(PROP_FILE_NAME), "Unidash score file");
	}
	public void reset() throws FileNotFoundException, IOException	{
		this.properties.clear();
		this.save();
	}
	private String getMapID(String mapName)	{
		return "score_" + mapName;
	}
	private String getStarID(String mapName)	{
		return "star_" + mapName;
	}
	
	public float getMapProgress(String mapName)	{
		String propID = this.getMapID(mapName);
		String value = this.properties.getProperty(propID);
		if (value == null)	{
			this.properties.setProperty(propID, "0.0");
			return 0.0f;
		}
		try	{
			float vF = Float.valueOf(value);
			return vF;
		}	catch (IllegalArgumentException ex)	{
			this.properties.setProperty(propID, "0.0");
			return 0.0f;
		}
	}
	public void setMapProgress(String mapName,float propgress)		{
		Log.debug("Saving progress " + propgress + " for " + mapName, "ScoreManager");
		String propID = this.getMapID(mapName);
		this.properties.setProperty(propID, String.valueOf(propgress));
		try {
			this.save();
		} catch (IOException e) {
			Log.error("Failed to save scores", "ScoreManager");
			Log.error(e);
		}
	}
	
	public boolean getStarEarned(String mapName)	{
		String propID = this.getStarID(mapName);
		String value = this.properties.getProperty(propID);
		if (value == null)	{
			this.properties.setProperty(propID, "0");
			return false;
		}
		return value.equals("1");
	}
	public void setStarEarned(String mapName,boolean e)	{
		String propID = this.getStarID(mapName);
		this.properties.setProperty(propID, e ? "1" : "0");
		try {
			this.save();
		} catch (IOException e1) {
			Log.error("Failed to save scores", "ScoreManager");
			Log.error(e1);
		}
	}
	
	private static ScoreManager inst = null;
	public static ScoreManager getInstance()	{
		if (inst == null)	{
			inst = new ScoreManager();
		}
		return inst;
	}
	
}
