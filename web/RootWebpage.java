package web;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import application.Settings;

public class RootWebpage implements HttpHandler
{
	private static String webPageHTML = "";
	
	public RootWebpage() 
	{
		String inputLine;

		try 
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResource(Settings.RES_PATH + "server.html").openStream()));
			
    		while ((inputLine = in.readLine()) != null)
    		{
    			webPageHTML += inputLine;
    		}
    		
    		in.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	public void handle(HttpExchange server)
	{
		try 
		{		
			webPageHTML = webPageHTML.replaceAll("#IPADDR", Server.ipAddr);
			webPageHTML = webPageHTML.replaceAll("#PORT", String.valueOf(Server.port));

			server.sendResponseHeaders(200, webPageHTML.length());
			 
			OutputStream os = server.getResponseBody();
	        os.write(webPageHTML.toString().getBytes());
	        //os.flush();
	        os.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
