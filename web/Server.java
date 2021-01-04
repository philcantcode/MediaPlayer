package web;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.sun.net.httpserver.HttpServer;

import universals.CodeLogger;
import universals.CodeLogger.DEPTH;

public class Server extends Thread
{
	public static String ipAddr = "127.0.0.1";
	public static int port = 9000;
	private static HttpServer server;

	public Server() 
	{
		try 
		{
			InetAddress inetAddress = InetAddress.getLocalHost();
			ipAddr = inetAddress.getHostAddress();
		}
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		}

		this.start();
	}

	@Override
	public void run() 
	{
		try 
		{
			server = HttpServer.create(new InetSocketAddress(port), 0);
    		CodeLogger.log("Remote Server Started on: " + ipAddr + ":" + port, DEPTH.CHILD);
    		server.createContext("/", new RootWebpage());
    		server.createContext("/head", new HeaderRequest());
    		server.createContext("/get", new GetRequest());
    		server.createContext("/post", new PostRequest());
    		server.setExecutor(null);
    		server.start();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static String getSiteAddress()
	{
		return "http://" + ipAddr + ":" + port;
	}
	
	public static void shutdown()
	{
		server.stop(0);
	}

}
