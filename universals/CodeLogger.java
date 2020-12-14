package universals;

import java.util.HashMap;

public class CodeLogger 
{	
	private static int depth = 0;
	private static HashMap<String, Integer> depthLookup = new HashMap<String, Integer>();
	
	public CodeLogger() 
	{
		
	}
	
	public static void err(Object msg, DEPTH d)
	{
		logPrint(msg, d, true, true);
		logPrint("PROGRAM QUITTING", DEPTH.ROOT, true, true);
		System.exit(0);
	}
	
	public static void log(Object msg, DEPTH d)
	{
		logPrint(msg, d, true, true);
	}
	
	public static void log(Object msg, DEPTH d, boolean print)
	{
		if (print)
			logPrint(msg, d, print, true);
	}
	
	public static void log(Object msg, DEPTH d, boolean print, boolean newLine)
	{
		logPrint(msg, d, print, newLine);
	}
	
	private static void logPrint(Object msg, DEPTH d, boolean print, boolean newLine)
	{
		String classLine = null;
		String classCaller = substringBetween(Thread.currentThread().getStackTrace()[3].toString(), "(", ")");
		classLine = classCaller;
		classCaller = classCaller.replace(".java", "");
		classCaller = classCaller.split(":")[0];
		int calcDepth = -1;
		
		if (depthLookup.keySet().contains(classLine))
		{
			if (d == DEPTH.PARENT)
				System.out.println();
			
			calcDepth = depthLookup.get(classLine);
			depth = calcDepth;
		}
		else
		{
			 calcDepth = calcDepth(d);
			 depthLookup.put(classLine, calcDepth);
		}
		
		if (newLine)
			System.out.printf("%s[%s] %s\n", Utils.repeat(calcDepth * 4, " "), classCaller, msg);
		else
			System.out.printf("%s[%s] %s", Utils.repeat(calcDepth * 4, " "), classCaller, msg);
	}
	
	private static int calcDepth(DEPTH d)
	{		
		int retDepth = -1;
		
		if (d == DEPTH.ROOT)
		{
			retDepth = 0;
		}
		else if (d == DEPTH.PARENT)
		{
			System.out.println();
			retDepth = depth;
			depth++;
		}
		else if (d == DEPTH.CHILD)
		{
			retDepth = depth;
		}	
		
		return retDepth;
	}
	
	public enum DEPTH 
	{
		ROOT,
		PARENT,
		CHILD
	}
	
	/**
     * <p>Gets the String that is nested in between two Strings.
     * Only the first match is returned.</p>
     *
     * <p>A {@code null} input String returns {@code null}.
     * A {@code null} open/close returns {@code null} (no match).
     * An empty ("") open and close returns an empty string.</p>
     *
     * <pre>
     * StringUtils.substringBetween("wx[b]yz", "[", "]") = "b"
     * StringUtils.substringBetween(null, *, *)          = null
     * StringUtils.substringBetween(*, null, *)          = null
     * StringUtils.substringBetween(*, *, null)          = null
     * StringUtils.substringBetween("", "", "")          = ""
     * StringUtils.substringBetween("", "", "]")         = null
     * StringUtils.substringBetween("", "[", "]")        = null
     * StringUtils.substringBetween("yabcz", "", "")     = ""
     * StringUtils.substringBetween("yabcz", "y", "z")   = "abc"
     * StringUtils.substringBetween("yabczyabcz", "y", "z")   = "abc"
     * </pre>
     *
     * @param str  the String containing the substring, may be null
     * @param open  the String before the substring, may be null
     * @param close  the String after the substring, may be null
     * @return the substring, {@code null} if no match
     * @since 2.0
     */
    public static String substringBetween(final String str, final String open, final String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        final int start = str.indexOf(open);
        if (start != -1) {
            final int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

}
