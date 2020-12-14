package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import universals.CodeLogger;
import universals.CodeLogger.DEPTH;

public class Database
{

	protected static Connection c = null;
	protected static PreparedStatement stmt = null;
	protected static ResultSet rs = null;
    
	public Database()
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:elements.db");
			
			c.setAutoCommit(false);
		} 
		catch (SQLException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
		createTables();
	}
	
	public static String clean(String s)
	{
		return s;
	}
	
	public static void close()
	{
		try
		{
			stmt.close();
			c.close();
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Closed database");
	}
	
	public static void createTables()
	{	
		try {
	         String sql = "CREATE TABLE IF NOT EXISTS watchFolders " +
	                        "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
	                        " path TEXT NOT NULL)";
	         
	         stmt = c.prepareStatement(sql);
	         stmt.executeUpdate();
	         
	         
	         sql = "CREATE TABLE IF NOT EXISTS playHistory " +
                     "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     " name TEXT NOT NULL, " + 
                     " hash TEXT NOT NULL, " +
                     " path TEXT NOT NULL, " +
                     " playTime TEXT, " +
                     " date INTEGER NOT NULL)"; 
	         
	         stmt = c.prepareStatement(sql);
	         stmt.executeUpdate();
	         
	         sql = "CREATE TABLE IF NOT EXISTS folderMeta " +
                     "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     " path TEXT NOT NULL, " + 
                     " type INTEGER)"; 
	         
	         stmt = c.prepareStatement(sql);
	         stmt.executeUpdate();
	         
	         sql = "CREATE TABLE IF NOT EXISTS fileTrack " +
                     "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     " path TEXT NOT NULL, " + 
                     " dateAdded TEXT NOT NULL)"; 
	         
	         stmt = c.prepareStatement(sql);
	         stmt.executeUpdate();
	        
	         sql = "CREATE TABLE IF NOT EXISTS settings " +
                     "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     " key TEXT UNIQUE, " +
                     " value TEXT)";
	         
	         stmt = c.prepareStatement(sql);
	         stmt.executeUpdate();
	         
	         stmt.close();
	      } catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	         System.exit(0);
	      }
	     
		CodeLogger.log("Initialised database", DEPTH.CHILD);
	}

}
