package application;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class Utils
{	
	public Utils()
	{

	}
	
	 public static double scale(final double val, final double max, final double min) 
	 {	 
		 if (val < 5000)
			 return 0;
		 else
			 return ((1 - 0) * (val - min) / (max - min)) + 0;
	 }
	 
	 public static int countDiv(String s)
	 {		 
		 int count = 0;
		 		 
		 for (char c : s.toCharArray())
		 {
			 if (c == File.separatorChar)
				 count++;
		 }
		 
		 return count;
	 }

	public static String getFileChecksum(File file)
	{
		StringBuilder sb = new StringBuilder();
		
		try
		{
			MessageDigest digest = MessageDigest.getInstance("MD5");
			
			FileInputStream fis = new FileInputStream(file);
		     
		    byte[] byteArray = new byte[1024];
		    int bytesCount = 0; 
		      
		    while ((bytesCount = fis.read(byteArray)) != -1) {
		        digest.update(byteArray, 0, bytesCount);
		    };
		     
		    fis.close();
		     
		    byte[] bytes = digest.digest();
		     
		    for(int i=0; i< bytes.length ;i++)
		    {
		        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		    }
		} 
		catch (NoSuchAlgorithmException | IOException e)
		{
			e.printStackTrace();
		}
	     
	   return sb.toString();
	}
	
	public static String msToTime(long millis)
	{
		return TimeUnit.MILLISECONDS.toMinutes(millis) + ":" + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
	}
	
	/* If on OS X then use the caffinate program to prevent
	 * sleeping, if on WIN then jiggle the mouce once */
	public static void preventScreenSleep(int sec)
	{
		new Thread(new Runnable() 
		{	
			@Override
			public void run()
			{
				if (determineOS() == OS.OSX)
				{
					try
					{
						Process proc = Runtime.getRuntime().exec("caffeinate -t " + sec);
					} 
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				else if (determineOS() == OS.WIN)
				{
					Robot rob;
					PointerInfo ptr = null;
					 
					try
					{
						rob = new Robot();
						rob.delay(sec / 1000); 
				        ptr = MouseInfo.getPointerInfo();
				        rob.mouseMove((int) ptr.getLocation().getX() + 1, (int) ptr.getLocation().getY() + 1);
				        rob.mouseMove((int) ptr.getLocation().getX() - 1, (int) ptr.getLocation().getY() - 1);  
					} 
					catch (AWTException e)
					{
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	public static OS determineOS()
	{
		String os = System.getProperty("os.name");
		
		if (os.equals("Mac OS X"))
			return OS.OSX;
		else
			return OS.WIN;
	}
	
	public static enum OS
	{
		OSX,WIN
	}
	
	public static void wait(int ms)
	{
		try 
		{
			Thread.sleep(ms);
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
}
