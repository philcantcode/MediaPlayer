package universals;

import java.util.concurrent.TimeUnit;

import universals.CodeLogger.DEPTH;

public class CodeClock 
{
	private long start = 0;
	private long end = 0;
	private long duration = 0;
	
	private boolean summary = true;
	private boolean exit = false;
	private boolean nano = false;
	
	/** Creates and starts the timer */
	public CodeClock() 
	{
		restart();
	}
	
	public CodeClock(boolean summary, boolean nano) 
	{
		this.summary = summary;
		this.nano = nano;
		
		restart();
	}
	
	/** Restarts the timer */
	public void restart()
	{
		start = System.nanoTime();
		end = 0;
		duration = 0;
	}
	
	/** Returns the current number of seconds the timer
	 * 	has ran for. */
	public long update()
	{
		return TimeUnit.MILLISECONDS.toSeconds((System.nanoTime() - start) / 1000000);
	}
	
	/** Ends the timer */
	public long end()
	{
		end = System.nanoTime();
		duration = TimeUnit.MILLISECONDS.toSeconds((end - start) / 1000000);
		
		if (summary)
			summary();
		
		return duration;
	}
	
	public void summary()
	{
		String nanoTime = Utils.DECIMAL_FORMAT.format(end - start);
		String secondsTime = Utils.DECIMAL_FORMAT.format(this.duration);
		
		CodeLogger.log("Execution Time: " + secondsTime + " (s)", DEPTH.CHILD);
		
		if (nano)
			CodeLogger.log("Execution Time: " + nanoTime + " (ns)", DEPTH.CHILD);
		
		if (exit)
		{
			CodeLogger.log("Ending Program", DEPTH.ROOT);
			System.exit(0);
		}
	}
	
	public String memory()
	{
		return formatBytes(Runtime.getRuntime().totalMemory(), false);
	}
	
	public static String formatBytes(long bytes, boolean si) 
	{
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

}
