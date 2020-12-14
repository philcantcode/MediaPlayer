package universals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import universals.CodeLogger.DEPTH;

public class CSVLoader 
{
	public ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
	private static final String DELIMITER = ",";
	public static final int PRINT_INCREMENT = 10000;
	public static final String UNIVERSAL_FOLDER = "/Users/Phil/Google Drive/Programming/mac-eclipse-workspace/Universals";
	
	public boolean save = false;
	public boolean print = false;
	public boolean skipHeader = false;
	public boolean summary = false;
	public boolean trim = false;
	
	public String skipChars = "";
	public String path = null;
	
	public int rows = 0;
	public int cols = 0;
	
	public CSVLoader(String path) 
	{
		this.path = path;
	}
	
	public CSVLoader saveData()
	{
		this.save = true;
		return this;
	}
	
	public CSVLoader print()
	{
		this.print = true;
		return this;
	}
	
	public CSVLoader skipHeader()
	{
		this.skipHeader = true;
		return this;
	}
	
	public CSVLoader summary()
	{
		this.summary = true;
		return this;
	}
	
	public CSVLoader skipChars(String skipChars)
	{
		this.skipChars = skipChars;
		return this;
	}
	
	public CSVLoader trim()
	{
		this.trim = true;
		return this;
	}
	
	public CSVLoader load() 
	{
		if (summary)
			CodeLogger.log("Loading File: " + path, DEPTH.PARENT);
		
		CodeClock clock = new CodeClock(false, false);
		
		try 
		{
			BufferedReader read = new BufferedReader(new FileReader(path));

			if (skipHeader)
				read.readLine();
			
			String line = null;
			
			loop:
			while ((line = read.readLine()) != null)
			{
				ArrayList<String> row = new ArrayList<String>(cols);
				
				for (char c : skipChars.toCharArray())
				{
					if (line.length() == 0 || line.charAt(0) == c)
					{
						continue loop;
					}
				}
				
				for (String feature : line.split(DELIMITER))
				{
					if (trim)
						row.add(feature.trim());
					else
						row.add(feature);
				}
				
				if (cols == 0)
					cols = row.size();
				else
				{
					if (cols != row.size())
						CodeLogger.err("The number of columns is not consistent (" + path + ")", DEPTH.ROOT);
				}
				
				if (save)
					data.add(row);
				
				rows++;
				
				if (print)
					CodeLogger.log("[" + rows + "][" + cols + "]: " + row.toString(), DEPTH.CHILD);
				
				if (rows % PRINT_INCREMENT == 0)
					CodeLogger.log("Loaded " + rows + " Rows", DEPTH.CHILD);
			}

			read.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		if (summary)
			printSummary();
		
		clock.end();
		
		return this;
	}

	private void printSummary()
	{
		CodeLogger.log("Rows Loaded: " + rows, DEPTH.CHILD);
		CodeLogger.log("Number Features: " + cols, DEPTH.CHILD);
	}
	
	public ArrayList<String> flatten()
	{
		if (cols == 1)
		{
			ArrayList<String> flat = new ArrayList<String>();
			
			for (int i = 0; i < rows; i++)
			{
				flat.add(data.get(i).get(0));
			}
			
			return flat;
		}
		
		return null;
	}
}
