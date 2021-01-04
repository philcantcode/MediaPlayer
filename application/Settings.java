package application;

import java.util.ArrayList;
import java.util.HashMap;

import database.DBInsert;
import database.DBSelect;
import database.DBUpdate;
import structures.Playback;
import universals.CodeLogger;
import universals.CodeLogger.DEPTH;

public class Settings
{
	public static HashMap<Key, String> settings = new HashMap<Key, String>();
	public static ArrayList<String> images = new ArrayList<String>();
	public static ArrayList<String> movies = new ArrayList<String>();
	
	public static final String RES_PATH = "/res/";
	
	public static final int SKIP_AMT_SEC = 10;
	
	public Settings()
	{
		images.add("png"); images.add("jpg"); images.add("jpeg"); images.add("gif");
		movies.add("mp4"); movies.add("avi"); movies.add("mkv");
		
		DBSelect.loadSettings();	
		
		if (!settings.keySet().contains(Key.AUTOPLAY))
			DBInsert.insertSetting(Key.AUTOPLAY.toString(), "false");
		
		if (!settings.keySet().contains(Key.FULLSCREEN_START))
			DBInsert.insertSetting(Key.FULLSCREEN_START.toString(), "false");
		
		if (!settings.keySet().contains(Key.MINIMISE_MAIN_WIN))
			DBInsert.insertSetting(Key.MINIMISE_MAIN_WIN.toString(), "false");
		
		if (!settings.keySet().contains(Key.START_SERVER))
			DBInsert.insertSetting(Key.START_SERVER.toString(), "true");
		
		Playback.folderMeta = DBSelect.loadFolderMeta();
	}
	
	public static enum Key
	{
		AUTOPLAY, FULLSCREEN_START, MINIMISE_MAIN_WIN, START_SERVER
	}
	
	public static enum FILETYPE
	{
		IMAGE, MOVIE, UNKNOWN, SUBTITLE, FOLDER
	}
	
	public static enum META
	{
		SERIES, TITLES, CATEGORIES
	}
	
	public static META getMeta(String meta)
	{
		for (META m : META.values())
		{
			if (m.toString().equals(meta))
				return m;
		}
		
		return null;
	}
	
	public static FILETYPE extension(String ext)
	{
		ext = ext.toLowerCase();
		
		if (movies.contains(ext))
			return FILETYPE.MOVIE;
		
		if (movies.contains(ext))
			return FILETYPE.IMAGE;
		
		if (movies.contains(ext))
			return FILETYPE.SUBTITLE;
		
		return FILETYPE.UNKNOWN;
	}
	
	public static void set(Key key, Object s)
	{
		String str = String.valueOf(s);
		
		if (!settings.get(key).equals(str))
		{
			settings.put(key, str);
			DBUpdate.updateSettings(key.toString(), str);
		}
	}
	
	public static String get(Key key)
	{
		return settings.get(key);
	}
	
	public static double getFloat(Key key)
	{
		return Double.valueOf(settings.get(key));
	}
	
	public static int getInt(Key key)
	{
		return Integer.valueOf(settings.get(key));
	}
	
	public static boolean getBool(Key key)
	{
		return Boolean.valueOf(settings.get(key));
	}
	
	public static Key mapKey(String key)
	{
		for (Key k : Key.values())
		{
			if (k.toString().equals(key))
				return k;
		}
		
		CodeLogger.err("No Key", DEPTH.ROOT);
		return null;
	}
	
}
