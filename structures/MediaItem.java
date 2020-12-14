package structures;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import application.Main;
import database.DBInsert;
import database.DBSelect;
import structures.FolderManager.DIRECTORY_TYPE;
import universals.CodeLogger;
import universals.CodeLogger.DEPTH;

public class MediaItem 
{
	private int id = -1;
	private String path = null;
	private long dateAdded = -1;
	
	public static ArrayList<MediaItem> mediaList = new ArrayList<MediaItem>();
	private static final long MONTH = 2629746000l;
	private static final long WEEK = 604800000l;
	private static final long DAY =  86400000l;
	
	private static boolean firstDay = false;
	private static boolean firstWeek = false;
	private static boolean firstMonth = false;
	
	public MediaItem(int id, String path, long dateAdded) 
	{
		this.id = id;
		this.path = path;
		this.dateAdded = dateAdded;
	}
	
	public MediaItem(String path, long dateAdded) 
	{
		this.path = path;
		this.dateAdded = dateAdded;
		
		if (!itemExists())
		{
			this.id = DBInsert.insertFileTrack(this.path, this.dateAdded);
			mediaList.add(this);
		}
	}
	
	public static void addToRecentMediaMenu()
	{
		for (MediaItem m : mediaList)
		{
			if (!new File (m.path).exists())
				continue;
			
    		long timeAgo = System.currentTimeMillis() - m.dateAdded;
    		
    		if (timeAgo > 0 && timeAgo < DAY)
    		{
    			if (!firstDay)
    			{
    				firstDay = true;
    				
    				Main.mainWindow.addRecentlyAdded(m.path, true, "Today");
    			}
    			else 
    			{
    				Main.mainWindow.addRecentlyAdded(m.path, false, null);
    			}
    		}
    		else if (timeAgo > 0 && timeAgo < WEEK)
    		{
    			if (!firstWeek)
    			{
    				firstWeek = true;
    				
    				Main.mainWindow.addRecentlyAdded(m.path, true, "This Week");
    			}
    			else 
    			{
    				Main.mainWindow.addRecentlyAdded(m.path, false, null);
    			}
    		}
		}
	}
	
	private boolean itemExists()
	{
		for (int i = 0; i < mediaList.size(); i++)
		{
			if (mediaList.get(i).id == this.id || mediaList.get(i).path.equals(this.path))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static void loadAll()
	{
		mediaList.clear();
		mediaList.addAll(DBSelect.loadFileTrack());
	}
	
	public static void trackDir(String dir)
	{
		CodeLogger.log("Tracking directory: " + dir, DEPTH.CHILD);
		
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{				
				ArrayList<File> fileList = new ArrayList<File>();	
				fileList = FolderManager.recursiveFileListing(dir, DIRECTORY_TYPE.FOLDER, fileList);
				
				for (File f : fileList)
				{
					try 
					{
						BasicFileAttributes attr = Files.readAttributes(Path.of(f.getAbsolutePath()), BasicFileAttributes.class);
						long dateAdded = attr.creationTime().to(TimeUnit.MILLISECONDS);
						new MediaItem(f.getAbsolutePath(), dateAdded);
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	public static boolean isNew(String path)
	{
		for (int i = 0; i < mediaList.size(); i++)
		{
			if (mediaList.get(i).path.equals(path))
			{
				long time = System.currentTimeMillis() - mediaList.get(i).dateAdded;
				
				if (time > 0 && time < WEEK)
					return true;
			}
		}
		
		return false;
	}

}
