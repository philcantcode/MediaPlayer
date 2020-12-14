package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import application.Settings.META;
import universals.CodeLogger;
import universals.CodeLogger.DEPTH;

public class DBDelete
{
	protected static Connection c = null;
	protected static PreparedStatement stmt = null;
	protected static ResultSet rs = null;
	
	public DBDelete()
	{
		c = Database.c;
		stmt = Database.stmt;
		rs = Database.rs;
	}
	
	public static void deletePlayback(int id)
	{		
	    try {
	         stmt = c.prepareStatement("DELETE FROM `playHistory` WHERE id = ?;");
	         stmt.setInt(1, id);
	         
	         stmt.executeUpdate();

	         stmt.close();
	         c.commit();
	    } 
	    catch ( Exception e ) 
	    {
	    	e.printStackTrace();
	    	CodeLogger.err(e.getClass().getName() + ": " + e.getMessage(), DEPTH.ROOT);
	    }
	    
	    CodeLogger.log("Delete PlayHistory (" + id + ") Successful", DEPTH.CHILD);
	}
	
	public static void deleteMeta(String path, META type)
	{		
	    try {
	         stmt = c.prepareStatement("DELETE FROM `folderMeta` WHERE path = ? AND type = ?;");
	         stmt.setString(1, path);
	         stmt.setString(2, type.toString());
	         
	         stmt.executeUpdate();

	         stmt.close();
	         c.commit();
	    } 
	    catch ( Exception e ) 
	    {
	    	e.printStackTrace();
	    	CodeLogger.err(e.getClass().getName() + ": " + e.getMessage(), DEPTH.ROOT);
	    }
	    
	    CodeLogger.log("Delete meta (" + path + ") Successful", DEPTH.CHILD);
	}
	
	public static void deleteSetting(String key)
	{
		try {
	         stmt = c.prepareStatement("DELETE FROM `settings` WHERE key = ?;");
	         stmt.setString(1, key);
	         
	         stmt.executeUpdate();

	         stmt.close();
	         c.commit();
	    } 
	    catch ( Exception e ) 
	    {
	    	e.printStackTrace();
	    	CodeLogger.err(e.getClass().getName() + ": " + e.getMessage(), DEPTH.ROOT);
	    }
		
		CodeLogger.log("Delete Setting Successful", DEPTH.CHILD);
	}
}
