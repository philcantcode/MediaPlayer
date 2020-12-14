package universals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.UrlValidator;

public class Classifier
{
	private static final String  URL_REGEX = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
	private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE);
	private static final Matcher URL_MATCHER = URL_PATTERN.matcher("");
	private static final UrlValidator URL_VALIDATOR = new UrlValidator();
	private static final String SPLIT_STRING = "[()/]";
	
	private static final String LOWER_ALPHA = "abcdefghijklmnopqrstuvwxyz";
	private static final String UPPER_ALPHA = LOWER_ALPHA.toUpperCase();
	private static final String ALL_ALPHA = LOWER_ALPHA + UPPER_ALPHA;
	private static final String NUMERIC = "0123456789";
	private static final String ALPHA_NUMERIC = NUMERIC + ALL_ALPHA;
	private static final String URL = ALPHA_NUMERIC + ";/?:@&.=+$,#";
	
	private static final SimpleDateFormat UK_DATE_FORMAT_1 = new SimpleDateFormat("dd-MM-yyyy");
	private static final SimpleDateFormat UK_DATE_FORMAT_2 = new SimpleDateFormat("dd MM yyyy");
	private static final SimpleDateFormat UK_DATE_FORMAT_3 = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat US_DATE_FORMAT_1 = new SimpleDateFormat("MM-dd-yyyy");
	private static final SimpleDateFormat US_DATE_FORMAT_2 = new SimpleDateFormat("MM dd yyyy");
	private static final SimpleDateFormat US_DATE_FORMAT_3 = new SimpleDateFormat("MM/dd/yyyy");
	private static final SimpleDateFormat TIME_FORMAT_1 = new SimpleDateFormat("HH:mm:ss:SSS");
	private static final SimpleDateFormat TIME_FORMAT_2 = new SimpleDateFormat("HH:mm:ss");
	private static final SimpleDateFormat TIME_FORMAT_3 = new SimpleDateFormat("HH:mm");
	
	private static ArrayList<SimpleDateFormat> dateTime = new ArrayList<SimpleDateFormat>();
	private static ArrayList<String> webProtocols = new ArrayList<String>();
	private static ArrayList<String> domains = null;
	private static HashMap<String, TYPE> keywords = new HashMap<String, TYPE>();
	
	public static final ArrayList<TYPE> FILTER_EMPTY_NULL_UNKNOWN = new ArrayList<TYPE>();
	public static final ArrayList<TYPE> FILTER_NON_SPECIFIC = new ArrayList<TYPE>();
	public static final ArrayList<TYPE> FILTER_NONE = new ArrayList<TYPE>();
	
	public static enum TYPE
	{
		IP, IPV4, IPV6, MAC, PORT, PROTOCOL, BSSID, SSID,
		TIMEZONE, UPTIME, DATETIME,
		VERSION, SERVICE, VENDOR, OS, CVE_VULNERABILITY,
		URL, DOMAIN, PATH,
		USER,
		NULL, UNKNOWN, EMPTY, NOOP,
		VPN,
		NUMERIC, ALPHA_NUMERIC, ALPHABETIC
	}
	
	public Classifier()
	{
		CSVLoader csv = new CSVLoader(CSVLoader.UNIVERSAL_FOLDER + "/data/domains.txt").skipChars(" /").saveData().load();
		domains = csv.flatten();
		
		dateTime.add(UK_DATE_FORMAT_1); dateTime.add(UK_DATE_FORMAT_2); dateTime.add(UK_DATE_FORMAT_3);
		dateTime.add(US_DATE_FORMAT_1); dateTime.add(US_DATE_FORMAT_2); dateTime.add(US_DATE_FORMAT_3);
		dateTime.add(TIME_FORMAT_1);	dateTime.add(TIME_FORMAT_2);	dateTime.add(TIME_FORMAT_3);
		
		webProtocols.add("http://");	webProtocols.add("https://");	webProtocols.add("file://");
		webProtocols.add("telnet://");	webProtocols.add("gopher://");
		
		keywords.put("mysql", TYPE.SERVICE);	keywords.put("sql", TYPE.SERVICE);	keywords.put("tcp", TYPE.PROTOCOL);
		keywords.put("ip", TYPE.PROTOCOL);		keywords.put("udp", TYPE.PROTOCOL);
		
		FILTER_EMPTY_NULL_UNKNOWN.add(TYPE.UNKNOWN);	
		FILTER_EMPTY_NULL_UNKNOWN.add(TYPE.NULL);	
		FILTER_EMPTY_NULL_UNKNOWN.add(TYPE.EMPTY);
		
		FILTER_NON_SPECIFIC.add(TYPE.ALPHA_NUMERIC);	
		FILTER_NON_SPECIFIC.add(TYPE.ALPHABETIC);
		FILTER_NON_SPECIFIC.add(TYPE.NUMERIC);
		FILTER_NON_SPECIFIC.addAll(FILTER_EMPTY_NULL_UNKNOWN);
	}
	
	/* One-time recursive function to identify the data types, 
	 * NULL, UNKNOWN, EMPTY only checked on final pass through */
	public ArrayList<Mapping> identifyType(String raw, boolean recursive)
	{
		ArrayList<Mapping> beliefSet = new ArrayList<Mapping>();
		Mapping map = new Mapping(raw);
				
		map.addTypes(mapKeywords(raw));
		
		if (Classifier.isNumeric(raw))
			map.addType(TYPE.NUMERIC);
		
		if (Classifier.isAlphabetic(raw))
			map.addType(TYPE.ALPHABETIC);
		
		if (Classifier.isAlphaNumeric(raw))
			map.addType(TYPE.ALPHA_NUMERIC);
		
		if (Classifier.isIP(raw))
			map.addType(TYPE.IP);
		
		if (Classifier.isIPv4(raw))
			map.addType(TYPE.IPV4);
		
		if (Classifier.isIPv6(raw))
			map.addType(TYPE.IPV6);
		
		if (Classifier.isPort(raw))
			map.addType(TYPE.PORT);
		
		if (Classifier.isMac(raw))
			map.addType(TYPE.MAC);
		
		if (Classifier.isBSSID(raw))
			map.addType(TYPE.BSSID);
		
		if (Classifier.isURL(raw))
		{
			map.addType(TYPE.URL);
			map.addType(TYPE.DOMAIN);
		}
		
		if (Classifier.isDateTime(raw))
			map.addType(TYPE.DATETIME);
		
		if (recursive)
		{
			for (String s : raw.split(SPLIT_STRING))
			{
				if (!s.equals(raw))
					beliefSet.addAll(identifyType(s, false));				
			}
			
			if (Classifier.isNull(raw))
				map.addType(TYPE.NULL);
			
			if (Classifier.isEmpty(raw))
				map.addType(TYPE.EMPTY);
			
			if (map.numTypes() == 0)
				map.addType(TYPE.UNKNOWN);			
		}
		
		if (map.numTypes() > 0)
			beliefSet.add(map);
				
		return beliefSet;
	}
	
	private static Set<TYPE> mapKeywords(String raw)
	{	
		Set<TYPE> types = new HashSet<TYPE>();
		
		if (keywords.keySet().contains(raw))
			types.add(keywords.get(raw));
		
		return types;
	}
	
	private static boolean checkLength(String raw, int max, int min)
	{
		if ((max != -1 && raw.length() > max) || (min != -1 && raw.length() < min))
			return false;
		
		return true;
	}
	
	public static boolean isIPv4(String raw)
	{
		int count = 0;
		if (!raw.contains("."))
			return false;

		if (!checkLength(raw, 15, 7))
			return false;
		
		for (char c : raw.toCharArray())
		{			
			if (c == '.' && count > 0) // If hit a . reset the count
			{
				count = 0;
				continue;
			}
			else if ((c < '0' || c > '9') && c != '*') // If char isn't [0-9 *] 
			{
				return false;
			}
			else if (count > 2) // More than 3 chars in segment 
			{
				return false;
			}			
			
			count++; 
		}
		
		return true;
	}
	
	public static boolean isIPv6(String raw)
	{
		int count = 0;
		
		if (!checkLength(raw, 39, 15))
			return false;
		
		for (char c : raw.toCharArray())
		{			
			if (c == ':' && count > 0) // If hit a . reset the count
			{
				count = 0;
				continue;
			}
			else if ((c < '0' || c > '9') && c != '*') // If char isn't [0-9 *] 
			{
				return false;
			}
			else if (count > 3) // More than 3 chars in segment 
			{
				return false;
			}			
			
			count++; 
		}
		
		return true;
	}
	
	public static boolean isIP(String raw)
	{
		if (isIPv4(raw) || isIPv6(raw))
			return true;
		
		return false;
	}
	
	public static boolean isPort(String raw)
	{		
		if (!isNumeric(raw))
			return false;
		
		if (!checkLength(raw, 5, 1))
			return false;
		
		int num = Integer.valueOf(raw);
		
		if (num < 0 || num > 65535)
			return false;
		
		return true;
	}
	
	public static boolean isNull(String raw)
	{
		if (raw == null || raw.equals(null))
			return true;
		
		return false;
	}
	
	public static boolean isEmpty(String raw)
	{
		if (raw.length() == 0)
			return true;
		
		return false;
	}
	
	public static boolean isNumeric(String raw)
	{
		if (!checkLength(raw, -1, 1))
			return false;
		
		for (char c : raw.toCharArray())
		{
			if (c < '0' || c > '9')
				return false;
		}
		
		return true;
	}
	
	public static boolean isAlphaNumeric(String raw)
	{
		return whitelist(raw, ALPHA_NUMERIC);
	}
	
	public static boolean isAlphabetic(String raw)
	{
		return whitelist(raw, ALL_ALPHA);
	}
	
	public static boolean isMac(String raw)
	{
		int count = 0;
		
		if (!checkLength(raw, 17, 17))
			return false;
		
		for (char c : raw.toCharArray())
		{
			if ((c == ':' || c == '-') && count > 0) // If hit a . reset the count
			{
				count = 0;
				continue;
			}
			else if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')))
			{
				return false;
			}
			else if (count > 1) // More than 3 chars in segment 
			{
				return false;
			}			
			
			count++; 
		}
		
		return true;
	}
	
	public static boolean isBSSID(String raw)
	{
		return isMac(raw);
	}
	
	public static boolean isURL(String raw)
	{
		if (!whitelist(raw, URL))
			return false;
				
		if (raw.contains(".") && domains.contains(raw.substring(raw.lastIndexOf(".") + 1)))
			return true;
		
		if (raw.contains("www."))
			return true;
		
		for (String web : webProtocols)
		{
			if (raw.contains(web))
				return true;
		}
		
		URL_MATCHER.reset(raw);
		String url = "";
		
	    while (URL_MATCHER.find())
	    {
	        url = raw.substring(URL_MATCHER.start(0), URL_MATCHER.end(0));
	        
	        if (URL_VALIDATOR.isValid(url))
	        {
	        	return true;
	        }
	    }
	    
	    return false;
	}
	
	public static boolean whitelist(String raw, String whitelist)
	{		
		if (raw.length() == 0)
			return false;
		
		outer:
		for (char c : raw.toCharArray())
		{
			boolean found = false;
			
			for (char w : whitelist.toCharArray())
			{
				if (c == w)
				{
					found = true;
					continue outer;
				}
			}
			
			if (!found)
				return false;
		}
	
		return true;
	}
	
	public static String extractDomainFromURL(String raw)
	{
		if (!isURL(raw))
			return null;
		
		int dotIndex = raw.lastIndexOf(".");
		int splitAt = -1;
		
		outer:
		for (int i = dotIndex + 1; i < raw.length(); i++)
		{
			char c = raw.charAt(i);
			boolean found = false;
			
			for (char alp : ALPHA_NUMERIC.toCharArray())
			{
				if (c == alp)
				{
					found = true;
					splitAt = i + 1;
					continue outer;
				}
			}
			
			if (!found)
			{
				splitAt = i;
				break outer;
			}
		}
				
		return raw.substring(0, splitAt);
	}
	
	public static boolean isDateTime(String raw)
	{
		boolean parsed = false;
		
		for (int i = 0; i < dateTime.size(); i++)
		{
			try 
			{
	            dateTime.get(i).parse(raw.trim());
	            parsed = true;
	            break;
	        }
			catch (ParseException pe) 
			{
	            continue;
	        }
		}
		
		return parsed;
	}
}
