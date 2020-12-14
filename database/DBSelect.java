package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import application.Settings;
import application.Settings.META;
import structures.MediaItem;
import structures.Playback;
import universals.CodeLogger;
import universals.CodeLogger.DEPTH;

public class DBSelect
{
	protected static Connection c = null;
	protected static PreparedStatement ps = null;
	protected static ResultSet rs = null;
	
	public DBSelect()
	{
		c = Database.c;
		ps = Database.stmt;
		rs = Database.rs;
	}
	
	public static Playback selectPlayback(String path)
	{		
		Playback ph = null;
		
		try {
	         ps = c.prepareStatement("SELECT `id`, `name`, `hash`, `path`, `playTime`, `date` FROM `playHistory` WHERE `path` = ?;");
	         ps.setString(1, path);
	         rs = ps.executeQuery();
	         
	         while (rs.next()) 
	         {
	        	 ph = new Playback(rs.getString("id"), rs.getString("name"), rs.getString("hash"), 
	        			 rs.getString("path"), rs.getString("playTime"), rs.getLong("date"));
	         }

	         ps.close();
	         c.commit(); 
	    } 
	    catch ( Exception e ) 
	    {
	    	e.printStackTrace();
	    	CodeLogger.err(e.getClass().getName() + ": " + e.getMessage(), DEPTH.ROOT);
	    }
		
		CodeLogger.log("Select playback successful", DEPTH.CHILD);
		return ph;
	}
	
	public static META selectMeta(String path)
	{		
		Playback ph = null;
		
		try {
	         ps = c.prepareStatement("SELECT `type` FROM `folderMeta` WHERE `path` = ?;");
	         ps.setString(1, path);
	         rs = ps.executeQuery();
	         
	         while (rs.next()) 
	         {
	        	return Settings.getMeta(rs.getString("type"));
	         }

	         ps.close();
	         c.commit(); 
	    } 
	    catch ( Exception e ) 
	    {
	    	e.printStackTrace();
	    	CodeLogger.err(e.getClass().getName() + ": " + e.getMessage(), DEPTH.ROOT);
	    }
		
		CodeLogger.log("Select folderMeta successful", DEPTH.CHILD);
		return null;
	}
	
	public static void loadSettings()
	{		
		try {
	         ps = c.prepareStatement("SELECT * FROM `settings`;");
	         rs = ps.executeQuery();
	         
	         while (rs.next()) 
	         {
	        	 Settings.settings.put(Settings.mapKey(rs.getString("key")), rs.getString("value"));
	         }

	         ps.close();
	         c.commit(); 
	    } 
	    catch ( Exception e ) 
	    {
	    	e.printStackTrace();
	    	CodeLogger.err(e.getClass().getName() + ": " + e.getMessage(), DEPTH.ROOT);
	    }
		
		CodeLogger.log("Load Settings Successful", DEPTH.CHILD);
	}
	
	public static ArrayList<String> loadWatchedFolders()
	{		
		ArrayList<String> directories = new ArrayList<String>();
		
		try {
	         ps = c.prepareStatement("SELECT * FROM `watchFolders`;");
	         rs = ps.executeQuery();
	         
	         while (rs.next()) 
	         {
	        	 directories.add(rs.getString("path"));
	         }

	         ps.close();
	         c.commit(); 
	    } 
	    catch ( Exception e ) 
	    {
	    	e.printStackTrace();
	    	CodeLogger.err(e.getClass().getName() + ": " + e.getMessage(), DEPTH.ROOT);
	    }
		
		CodeLogger.log("Load watchFolders Successful", DEPTH.CHILD);
		return directories;
	}
	
	public static ArrayList<MediaItem> loadFileTrack()
	{		
		ArrayList<MediaItem> mediaList = new ArrayList<MediaItem>();
		
		try {
	         ps = c.prepareStatement("SELECT * FROM `fileTrack` ORDER BY `dateAdded` DESC;");
	         rs = ps.executeQuery();
	         
	         while (rs.next()) 
	         {
	        	 mediaList.add(new MediaItem(rs.getInt("id"), rs.getString("path"), rs.getLong("dateAdded")));
	         }

	         ps.close();
	         c.commit(); 
	    } 
	    catch ( Exception e ) 
	    {
	    	e.printStackTrace();
	    	CodeLogger.err(e.getClass().getName() + ": " + e.getMessage(), DEPTH.ROOT);
	    }
		
		CodeLogger.log("Load fileTrack Successful", DEPTH.CHILD);
		return mediaList;
	}
	
	public static HashMap<String, META> loadFolderMeta()
	{		
		HashMap<String, META> directories = new HashMap<String, META>();
		
		try {
	         ps = c.prepareStatement("SELECT * FROM `folderMeta`;");
	         rs = ps.executeQuery();
	         
	         while (rs.next()) 
	         {
	        	 directories.put(rs.getString("path"), Settings.getMeta(rs.getString("type")));
	         }

	         ps.close();
	         c.commit(); 
	    } 
	    catch ( Exception e ) 
	    {
	    	e.printStackTrace();
	    	CodeLogger.err(e.getClass().getName() + ": " + e.getMessage(), DEPTH.ROOT);
	    }
		
		CodeLogger.log("Load folderMeta Successful", DEPTH.CHILD);
		return directories;
	}
	
	public static ArrayList<Playback> loadPlaybacks()
	{		
		ArrayList<Playback> history = new ArrayList<Playback>();
		
		try {
	         ps = c.prepareStatement("SELECT `id`, `name`, `hash`, `path`, `playTime`, `date` FROM `playHistory` ORDER BY `date` DESC;");
	         rs = ps.executeQuery();
	         
	         while (rs.next()) 
	         {
	        	 history.add(new Playback(rs.getString("id"), rs.getString("name"), rs.getString("hash"), 
	        			 rs.getString("path"), rs.getString("playTime"), rs.getLong("date")));
	         }

	         ps.close();
	         c.commit(); 
	    } 
	    catch ( Exception e ) 
	    {
	    	e.printStackTrace();
	    	CodeLogger.err(e.getClass().getName() + ": " + e.getMessage(), DEPTH.ROOT);
	    }
		
		CodeLogger.log("Load All Pay Histories Successful", DEPTH.CHILD);
		return history;
	}
	
	
}
