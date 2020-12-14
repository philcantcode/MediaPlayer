package structures;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import application.NaturalOrder;

public class FolderManager 
{

	private FolderManager() 
	{

	}
	
	public enum DIRECTORY_TYPE
	{
		FILE, FOLDER, BOTH
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<File> getFileListing(String path, DIRECTORY_TYPE type) 
	{
		ArrayList<File> fileList = new ArrayList<File>();
		
		if (!new File(path).exists())
			return fileList;
				
		File[] files = new File(path).listFiles();
		Arrays.sort(files, new NaturalOrder());
					
		for (File f : files)
		{
			if (!f.isHidden() && !f.getName().startsWith("$") && !f.getName().startsWith("."))
			{
				if (f.isDirectory() && (type == DIRECTORY_TYPE.FOLDER || type == DIRECTORY_TYPE.BOTH))
				{
					fileList.add(f);
				}
				else if (f.isFile() && (type == DIRECTORY_TYPE.FILE || type == DIRECTORY_TYPE.BOTH))
				{
					fileList.add(f);
				}
			}
		}
		
		return fileList;
	}
	
	public static ArrayList<File> recursiveFileListing(String path, DIRECTORY_TYPE type, ArrayList<File> fileList) 
	{		
		if (!new File(path).exists())
			return fileList;
				
		File[] files = new File(path).listFiles();
		
		for (File f : files)
		{
			if (!f.isHidden() && !f.getName().startsWith("$") && !f.getName().startsWith("."))
			{
				if (f.isDirectory() && (type == DIRECTORY_TYPE.FOLDER || type == DIRECTORY_TYPE.BOTH))
				{
					fileList.add(f);
					recursiveFileListing(f.getAbsolutePath(), type, fileList);
				}
				else if (f.isFile() && (type == DIRECTORY_TYPE.FILE || type == DIRECTORY_TYPE.BOTH))
				{
					fileList.add(f);
				}
			}
		}
		
		return fileList;
	}
	
	public static File nextFile(Playback pb)
	{
		ArrayList<File> fileList = getFileListing(pb.parentFolder, DIRECTORY_TYPE.FILE);
				
		for (int i = 0; i < fileList.size(); i++)
		{
			if (fileList.get(i).getAbsolutePath().equals(pb.path))
			{
				if (fileList.size() > (i + 1))
				{
					return fileList.get(i + 1);
				}
			}
		}
		
		return null;
	}
	
	public static File previousFile(Playback pb)
	{	
		ArrayList<File> fileList = getFileListing(pb.parentFolder, DIRECTORY_TYPE.FILE);
		
		for (int i = 0; i < fileList.size(); i++)
		{
			if (fileList.get(i).getAbsolutePath().equals(pb.path))
			{
				if (i > 0)
				{
					return fileList.get(i - 1);
				}
			}
		}
		
		return null;
	}
}
