package structures;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import application.Settings.META;
import database.DBInsert;
import database.DBSelect;
import universals.CodeLogger;
import universals.CodeLogger.DEPTH;

public class Playback 
{
	public int id = -1;
	public String name = null;
	public String hash = null;
	public String path = null;
	public long playTime = -1;
	public long date = -1;
	public long endTime = -1;
	
	public String parentFolder = null;
	public String namedFolder = null;
	public String extension = null;
	
	public static HashMap<String, META> folderMeta = new HashMap<String, META>();
	
	public Playback(String id, String name, String hash, String path, String playTime, long date) 
	{
		this.id = Integer.valueOf(id);
		this.name = name;
		this.hash = hash;
		this.path = path;
		this.playTime = Long.valueOf(playTime);
		this.date = date;
		
		if (isCorrectOS())
		{
			this.namedFolder = namedFolder(this.path);
			
			if (name.contains("."))
				extension = name.substring(name.lastIndexOf(".") + 1);
			
			if (path.contains(File.separator))
			{
				parentFolder = path.substring(0, path.lastIndexOf(File.separator));
			}
		}
	}
	
	public static String namedFolder(String path)
	{
		String[] folderSplit = path.split(Pattern.quote(File.separator));
		int seriesIndex = -1;
		int titlesIndex = -1;
		String namedFolder = "";
				
		for (String k : Playback.folderMeta.keySet())
		{
			if (path.contains(k))
			{
				if (Playback.folderMeta.get(k) == META.TITLES)
				{
					titlesIndex = k.split(Pattern.quote(File.separator)).length;
					namedFolder = folderSplit[titlesIndex];
				}
				else if (Playback.folderMeta.get(k) == META.SERIES)
				{
					seriesIndex = k.split(Pattern.quote(File.separator)).length;
					
					if (seriesIndex < folderSplit.length)
						namedFolder = folderSplit[seriesIndex];
				}
			}
		}
		
		if (seriesIndex == -1 && titlesIndex == -1)
		{
			if (folderSplit[2].startsWith("Category"))
				namedFolder = folderSplit[3];
			else
				namedFolder = folderSplit[2];
			
			CodeLogger.log("No Playback Metadata, assuming " + path + " named: " + namedFolder, DEPTH.CHILD);
		}
		else
		{
			CodeLogger.log("Metadata named " + path + ": " + namedFolder, DEPTH.CHILD, false);
		}
		
		return namedFolder;
	}
	
	public void updatePlayTime(long playTime)
	{
		this.playTime = playTime;
	}
	
	/* Finds or generates a new playback object */
	public static Playback findPlayback(String name, String path)
	{
		Playback p = DBSelect.selectPlayback(path);
		
		if (p == null)
		{
			DBInsert.insertPlayback(name, "", path, "0", System.currentTimeMillis());
			p = DBSelect.selectPlayback(path);
		}
		
		return p;
	}
	
	public boolean isCorrectOS()
	{
		if (path.contains(File.separator))
			return true;
		else
			return false;
	}
}
