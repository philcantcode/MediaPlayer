package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import universals.CodeLogger;
import universals.CodeLogger.DEPTH;

public class DBUpdate
{
	protected static Connection c = null;
	protected static PreparedStatement stmt = null;
	protected static ResultSet rs = null;
	
	public DBUpdate()
	{
		c = Database.c;
		stmt = Database.stmt;
		rs = Database.rs;
	}
	
	public static void updatePlayTime(int id, long playTime, long time)
	{		
		try {
			stmt = c.prepareStatement("UPDATE `playHistory` SET playTime = ?, date = ? WHERE id = ?;");
			stmt.setString(1, String.valueOf(playTime));
			stmt.setLong(2, time);
			stmt.setString(3, String.valueOf(id));
	         
			stmt.executeUpdate();

			stmt.close();
			c.commit();
	    } 
	    catch ( Exception e ) 
	    {
	    	CodeLogger.err(e.getClass().getName() + ": " + e.getMessage(), DEPTH.ROOT);
	    }
		
		CodeLogger.log("Update PlayTime Successful: " + id + " > " + playTime, DEPTH.CHILD);
	}
	
	public static void updateHash(int id, String hash)
	{		
		try {
			stmt = c.prepareStatement("UPDATE `playHistory` SET hash = ? WHERE id = ?;");
			stmt.setString(1, String.valueOf(hash));
			stmt.setString(2, String.valueOf(id));
	         
			stmt.executeUpdate();

			stmt.close();
			c.commit();
	    } 
	    catch ( Exception e ) 
	    {
	    	CodeLogger.err(e.getClass().getName() + ": " + e.getMessage(), DEPTH.ROOT);
	    }
		
		CodeLogger.log("Update Hash Successful: " + id + " > " + hash, DEPTH.CHILD);
	}
	
	public static void updateSettings(String key, String value)
	{
		try {
			stmt = c.prepareStatement("UPDATE `settings` SET value = ? WHERE key = ?;");
			stmt.setString(1, value);
			stmt.setString(2, key);
	         
			stmt.executeUpdate();

			stmt.close();
			c.commit();
	    } 
	    catch ( Exception e ) 
	    {
	    	CodeLogger.err(e.getClass().getName() + ": " + e.getMessage(), DEPTH.ROOT);
	    }
		
		CodeLogger.log("Update Setting Successful (" + key + " : " + value  + ")", DEPTH.CHILD);
	}

}
