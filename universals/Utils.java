package universals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import universals.CodeLogger.DEPTH;

public class Utils 
{	 
	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###.###");
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
	
	public static String repeat(int i, String s) 
	{
	    StringBuilder sb = new StringBuilder();
	    
	    for (int j = 0; j < i; j++)
    	{ 
    		sb.append(s);
    	}
	    
	    return sb.toString();
	}
	
	public static String leftPad(String str, int amt) 
	{
		StringBuilder sb = new StringBuilder(str.length() + amt);
		
		for (int i = 0; i < (amt - str.length()); i++)
		{
			sb.append("0");
		}
		
		sb.append(str);
		
		return sb.toString();
	}

	public static String removeAlphabetic(String s)
	{
		if (s != null)
			return s.replaceAll("[^\\d]", "" );
		else
			return "null";
	}
	
	public static void exit(String file, String err)
	{
		System.out.printf("[ERR in %s] %s", file, err);
		System.exit(0);
	}
	
	public static String flipBytes(String hex)
	{
		StringBuilder reorderedBits = new StringBuilder();
		
		for (int i = hex.length(); i > 0; i -= 2)
		{
			reorderedBits.append(hex.substring(i - 2, i));
		}
		
		return reorderedBits.toString();
	}
	
	public static void printDivider(String s)
	{
		System.out.print("-" + s);
		System.out.println(Utils.repeat(400, "-"));
	}
	
	public static String sha256Hash(String s)
	{
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return Types.bytesToHex(digest.digest(s.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void fileWiper(String file)
	{
		File f = new File(file);
		
		if (f.exists())
		{
			PrintWriter writer;
			try {
				writer = new PrintWriter(f);
				writer.print("");
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static <T> int countElementsInHashArray(HashMap<?, ArrayList<T>> data)
	{
		int count = 0;
		
		for (Object key : data.keySet())
		{
			count += data.get(key).size();
		}
		
		return count;
	}	
	
	public static void delay(int sec)
	{
		CodeLogger.log("Delaying for " + sec + " seconds ", DEPTH.CHILD, true, false);
		
		try
		{
			for (int i = 0; i < sec; i++)
			{
				System.out.print(".");
				Thread.sleep(1000);
			}
			
			System.out.print("\n");
		} 
		catch (InterruptedException e1)
		{
			e1.printStackTrace();
		}
	}
}
