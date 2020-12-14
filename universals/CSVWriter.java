package universals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/* 
 * Documentation:
 * 		CSVWriter() - Constructor takes in absolute output file path 
 * 		addRow() - Adds a row to the ArrayList<String> data accumulator
 * 		addRows() - As above
 * 		size() - Returns the number of rows in data
 * 		flush() - Appends data to the file
 * 		clearFile() - Wipes the file
 * */
public class CSVWriter 
{
	private ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
	private String path = null;
	
	public CSVWriter(String path) 
	{
		this.path = path;
	}
	
	public <T> void addRow(ArrayList<T> row)
	{
		ArrayList<String> strRow = new ArrayList<String>();
		
		for (int i = 0; i < row.size(); i++)
		{
			strRow.add(String.valueOf(row.get(i)));
		}
		
		data.add(strRow);
	}
	
	public <T> void addRows(ArrayList<ArrayList<T>> rows)
	{	
		for (int i = 0; i < rows.size(); i++)
		{
			ArrayList<String> strRow = new ArrayList<String>();
			
			for (int j = 0; j < rows.get(i).size(); j++)
			{
				strRow.add(String.valueOf(rows.get(i).get(j)));
			}
			
			data.add(strRow);
		}
	}
	
	public void addRow(String csvString)
	{
		ArrayList<String> strRow = new ArrayList<String>();
		
		for (String s : csvString.split(","))
		{
			strRow.add(s);
		}
		
		data.add(strRow);
	}
	
	public int size()
	{
		return data.size();
	}
	
	public <T> void flush(ArrayList<ArrayList<T>> rows)
	{
		addRows(rows);
		flush();
	}
	
	public void flush()
	{
		File file = new File(path);
		FileWriter fr;
		
		try 
		{
			fr = new FileWriter(file, true);
			
			for (int i = 0; i < data.size(); i++)
			{
				StringBuilder sb = new StringBuilder();
				
				for (int j = 0; j < data.get(i).size(); j++)
				{	
					sb.append(",");
					sb.append(data.get(i).get(j));
				}
				
				if (sb.length() > 0)
				{	
					fr.write(sb.toString().substring(1) + "\n");
				}
			}
			
			fr.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		data.clear();
	}
	
	public void clearFile()
	{
		try {
			FileWriter f = new FileWriter(new File(path), false);
			f.write("");
			f.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
