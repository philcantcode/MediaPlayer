package web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import application.Main;
import controllers.VLCWindow.VIEWER;
import structures.PlaybackStatus;
import universals.CodeLogger;
import universals.CodeLogger.DEPTH;
import universals.Types;

public class GetRequest implements HttpHandler 
{
    @Override
    public void handle(HttpExchange server) throws IOException 
    {
    	// parse request
    	Map<String, Object> parameters = new HashMap<String, Object>();
        URI requestedUri = server.getRequestURI();
       
        String query = requestedUri.getRawQuery();
        parseQuery(query, parameters);

       // send response
       String response = "";
       for (String key : parameters.keySet())
       {
    	   server.sendResponseHeaders(200, response.length()); // Response headers need to be sent back fast 
    	   
    	   String action = (String) parameters.get(key);
    	   
    	   CodeLogger.log("Server Action: " + action, DEPTH.CHILD, false);
    	   
    	   if (key.equals("action"))
    	   {    	
    		   if (action.equals("pause"))
    		   {
    			   Main.videoWindow.pauseVlcPlayback();
    		   }
    		   else if (action.equals("play"))
    		   {
    			   Main.videoWindow.togglePlayback();
    		   }
    		   else if (action.equals("rewind"))
    		   {
    			   Main.videoWindow.rewind();
    		   }
    		   else if (action.equals("fastforward"))
    		   {
    			   Main.videoWindow.fastforward();
    		   }
    		   else if (action.equals("skip"))
    		   {
    			   Main.videoWindow.skip();
    		   }
    		   else if (action.equals("status"))
    		   {    			   
    			   PlaybackStatus pbs = Main.videoWindow.getStatus();
    			   
    			   response += pbs.status + ",";
    			   response += pbs.title + ",";
    			   response += pbs.playbackTime + ",";
    			   response += pbs.endTime;
    		   }
    	   }
    	   else if (key.equals("ytplay"))
    	   {
    		   if (action.length() > 0)
    		   {
        		   String url = Types.hexToAscii(action);
        		   url = url.replace("youtu.be/", "youtube.com/watch?v=");
        		   url = url.replace("/watch?", "/watch_popup?");
        		   url += "&autoplay=1&fullscreen=1";
        		   
        		   Main.videoWindow.showViewer(VIEWER.WEB, url);
    		   }
    	   }
    	   else if (key.equals("ytaction"))
    	   {
    		   if (action.equals("finish"))
    		   {
    			   Main.videoWindow.showViewer(VIEWER.VLC, null);
    		   }
    	   }
       }
    
       OutputStream os = server.getResponseBody();
       os.write(response.toString().getBytes());
       os.close();
    }
    
    public static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException 
    {
    	if (query != null) 
    	{
    		String pairs[] = query.split("[&]");
    	                 
    		for (String pair : pairs) 
    		{
    			String param[] = pair.split("[=]");

    			String key = null;
    	        String value = null;

    	        if (param.length > 0) 
    	        {
    	        	key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
    	        }
                 
    	        if (param.length > 1) 
    	        {
    	        	value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
    	        }
    	        
    	        if (parameters.containsKey(key)) 
    	        {
    	        	Object obj = parameters.get(key);
    	                                   
    	        	if (obj instanceof List<?>) 
    	        	{
    	        		List<String> values = (List<String>) obj;
    	                values.add(value);
    	        	} 
    	        	else if (obj instanceof String) 
    	        	{
    	        		List<String> values = new ArrayList<String>();
    	                values.add((String) obj);
    	                values.add(value);
    	                                            
    	                parameters.put(key, values);
    	        	}
    	        } 
    	        else 
    	        {
    	        	parameters.put(key, value);
    	        }
    		}
    	}
    }
}