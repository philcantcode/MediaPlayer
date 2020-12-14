package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import application.Settings.META;
import universals.CodeLogger;
import universals.CodeLogger.DEPTH;

public class DBInsert
{
	protected static Connection c = null;
	protected static PreparedStatement stmt = null;
	protected static ResultSet rs = null;
	
	public DBInsert()
	{
		c = Database.c;
		stmt = Database.stmt;
		rs = Database.rs;
	}
	
	public static boolean insertWatchFolder(String path)
	{		
	    try {
	         stmt = c.prepareStatement("INSERT INTO `watchFolders` (path) VALUES (?)");
	         stmt.setString(1, path);
	         
	         stmt.executeUpdate();

	         stmt.close();
	         c.commit();
	    } 
	    catch ( Exception e ) 
	    {
	    	CodeLogger.log(e.getClass().getName() + ": " + e.getMessage(), DEPTH.ROOT);
	    	return false;
	    }
	    
	    CodeLogger.log("Insert WatchFolder Successful", DEPTH.CHILD);
	    return true;
	}
	
	public static void insertSetting(String key, String value)
	{	
		try {
	         stmt = c.prepareStatement("INSERT INTO `settings` (key, value) VALUES (?, ?);");
	         stmt.setString(1, key);
	         stmt.setString(2, value);
	         
	         stmt.executeUpdate();

	         stmt.close();
	         c.commit();
	    } 
	    catch ( Exception e ) 
	    {
	    	CodeLogger.err(e.getClass().getName() + ": " + e.getMessage(), DEPTH.ROOT);
	    }
		
		CodeLogger.log("Insert Setting Successful", DEPTH.CHILD);
	}
	
	public static void insertMeta(String path, META meta)
	{	
		try {
	         stmt = c.prepareStatement("INSERT INTO `folderMeta` (path, type) VALUES (?, ?);");
	         stmt.setString(1, path);
	         stmt.setString(2, meta.toString());
	         
	         stmt.executeUpdate();

	         stmt.close();
	         c.commit();
	    } 
	    catch ( Exception e ) 
	    {
	    	CodeLogger.err(e.getClass().getName() + ": " + e.getMessage(), DEPTH.ROOT);
	    }
		
		CodeLogger.log("Insert Meta Successful", DEPTH.CHILD);
	}
	
	public static int insertFileTrack(String path, long dateAdded)
	{	
		int id = -1;
		
		try {
	         stmt = c.prepareStatement("INSERT INTO `fileTrack` (path, dateAdded) VALUES (?, ?);");
	         stmt.setString(1, path);
	         stmt.setLong(2, dateAdded);
	         
	         id = stmt.executeUpdate();

	         stmt.close();
	         c.commit();
	    } 
	    catch ( Exception e ) 
	    {
	    	CodeLogger.err(e.getClass().getName() + ": " + e.getMessage(), DEPTH.ROOT);
	    }
		
		CodeLogger.log("Insert fileTrack Successful: " + path, DEPTH.CHILD);
		return id;
	}
	
	public static void insertPlayback(String name, String hash, String path, String playTime, long date)
	{				
	    try {
	         stmt = c.prepareStatement("INSERT INTO `playHistory` (name, hash, path, playTime, date) VALUES (?, ?, ?, ?, ?)");
	         stmt.setString(1, name);
	         stmt.setString(2, hash);
	         stmt.setString(3, path);
	         stmt.setString(4, playTime);
	         stmt.setLong(5, date);
	         
	         stmt.executeUpdate();

	         stmt.close();
	         c.commit();
	    } 
	    catch ( Exception e ) 
	    {
	    	CodeLogger.log(e.getClass().getName() + ": " + e.getMessage(), DEPTH.ROOT);
	    }
	    
	    CodeLogger.log("Insert playHistory Successful", DEPTH.CHILD);
	}
}
