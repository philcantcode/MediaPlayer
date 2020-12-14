package structures;

import static uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurfaceFactory.videoSurfaceForImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import application.Main;
import application.Settings;
import application.Utils;
import application.Settings.Key;
import database.DBUpdate;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import universals.CodeLogger;
import universals.CodeLogger.DEPTH;

public class VLCPlayer
{
	private MediaPlayerFactory factory;
    private EmbeddedMediaPlayer mediaPlayer;
    protected ImageView vlcViewer;
    
    public boolean isPlaying = false;
    public boolean isFullscreen = false;
    
    private ArrayList<Playback> playlist = new ArrayList<Playback>();
    private int pid = -1;
    
    private ScheduledThreadPoolExecutor clickTracker = new ScheduledThreadPoolExecutor(1);
    ScheduledFuture<?> clickScheduler;
    
    private Timer updatePlaybackTimer;
    private static final int UPDATE_PLYBACK_INTERVAL = 10 * 1000;
    
    private Timer screenSleepTimer;
    private static final int SCREEN_SLEEP_INTERVAL = 5 * 60 * 1000;
	
	public VLCPlayer() 
	{	
		this.factory = new MediaPlayerFactory();
		this.mediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer();
		this.vlcViewer = new ImageView();
		this.mediaPlayer.videoSurface().set(videoSurfaceForImageView(this.vlcViewer));
					
		this.vlcViewer.setPreserveRatio(true);
		
		this.mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter()
		{
			@Override
            public void playing(MediaPlayer mediaPlayer) 
            {
				CodeLogger.log("Playing Media: " + playlist.get(pid).path, DEPTH.PARENT);
            	isPlaying = true;
            	
            	updatePlaybackTimer = new Timer();
            	updatePlaybackTimer.scheduleAtFixedRate(new TimerTask() 
            	{
					@Override
					public void run() 
					{
						DBUpdate.updatePlayTime(playlist.get(pid).id, playlist.get(pid).playTime, System.currentTimeMillis());
					}	
					
            	}, UPDATE_PLYBACK_INTERVAL, UPDATE_PLYBACK_INTERVAL);
            	
            	screenSleepTimer = new Timer();
            	screenSleepTimer.scheduleAtFixedRate(new TimerTask() 
            	{
					@Override
					public void run()
					{
						CodeLogger.log("Preventing Screen Sleep", DEPTH.CHILD);
						Utils.preventScreenSleep(5 * 60);
					}
				}, 0, SCREEN_SLEEP_INTERVAL);
            }

            @Override
            public void paused(MediaPlayer mediaPlayer) 
            {
            	cleanup(false);
            }

            @Override
            public void stopped(MediaPlayer mediaPlayer) 
            {
            	cleanup(true);
            }

            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) 
            {
            	playlist.get(pid).updatePlayTime(newTime);
            	
            	double scale = Utils.scale(newTime, mediaPlayer.status().length(), 0);
            	Main.videoWindow.seek(scale, newTime);
            }
            
            @Override
            public void finished(final MediaPlayer mediaPlayer) 
            {
            	cleanup(true);
            	
            	DBUpdate.updatePlayTime(playlist.get(pid).id, 0, System.currentTimeMillis());
            	
            	if (Settings.getBool(Key.AUTOPLAY))
            	{
                	File nextFile = FolderManager.nextFile(Main.videoWindow.getLoadedMedia());
    				
            		if (nextFile != null)
            		{
            			CodeLogger.log("Autoplaying: " + nextFile.getAbsolutePath(), DEPTH.CHILD);
            			
            			Playback pb = Playback.findPlayback(nextFile.getName(), nextFile.getAbsolutePath());
            			initialise(pb, false);
            		}
            		else
            		{
            			CodeLogger.log("Nothing to autoplay", DEPTH.CHILD);
            		}
            	}
            }
		});		
		
		this.vlcViewer.setOnMouseClicked(new EventHandler<MouseEvent>() 
		{
			@Override
			public void handle(MouseEvent m) 
			{
				if (m.getButton().equals(MouseButton.PRIMARY))
				{
					if (m.getClickCount() == 1)
					{
						clickScheduler = clickTracker.schedule(() -> 
						{
							if (isPlaying)
								pausePlayback();
							else
								playPlayback();
							
							Platform.runLater(new Runnable() 
							{		
								@Override
								public void run() 
								{
									Main.videoWindow.updatePlaybackButtons();
								}
							});
						}, 500, TimeUnit.MILLISECONDS);
					}
					else if (m.getClickCount() == 2)
					{
						clickScheduler.cancel(false);
						
						if (isFullscreen)
						{
							setPlayerFullscreen(false);
							Main.videoWindow.showMenus(true);
							Main.setVLCFullscreen(false);
						}
						else
						{
							setPlayerFullscreen(true);
							Main.videoWindow.showMenus(false);
							Main.setVLCFullscreen(true);
						}
					}	
				}
			}
		});
	}
	
	public void initialise(Playback pb, boolean startPaused)
	{
		cleanup(true);
		playlist.add(pb);
		pid++;
		
		Main.showVLCScene(true);
		
		mediaPlayer.submit(new Runnable() 
		{
            @Override
            public void run() 
            {
            	if (startPaused)
            	{
            		CodeLogger.log("Starting paused", DEPTH.CHILD);
            		mediaPlayer.media().startPaused(playlist.get(pid).path);
            	}
            	else
            	{
            		CodeLogger.log("Starting playing", DEPTH.CHILD);
            		mediaPlayer.media().start(playlist.get(pid).path);
            	}
            }
        });
		
		seek(playlist.get(pid).playTime);
	}
	
	/* Tasks to be performed on cleanup:
	 * 1. Stop the media from playing (optional)
	 * 2. Set the cursor visible
	 * 3. Updates the pause/resume button
	 * 4. Cleans up the focus to prevent accidently clicking buttons
	 * 5. Show VLCWindow menus
	 * 6. Cancel scheduled tasks */
	private void cleanup(boolean stopMedia)
	{
		if (stopMedia)
    		stopPlayback();
		
		Main.setCursorVis(true);
		
		Platform.runLater(new Runnable() 
		{
			@Override
			public void run() 
			{
				Main.videoWindow.updatePlaybackButtons();
				Main.videoWindow.focusCleanup();
				Main.videoWindow.showMenus(true);
			}
		});
		
		if (updatePlaybackTimer != null)
			updatePlaybackTimer.cancel();
		
		if (screenSleepTimer != null)
			screenSleepTimer.cancel();
	}
	
	protected void seek(double pos)
	{		
		CodeLogger.log("Seeking to pos: " + pos, DEPTH.CHILD);
		mediaPlayer.controls().setPosition((float) pos);
	}
	
	protected void seek(long pos)
	{
		mediaPlayer.submit(new Runnable() 
		{
            @Override
            public void run() 
            {
        		seek(Utils.scale(pos, mediaPlayer.status().length(), 0));
            }
		});
	}
	
	public void pausePlayback()
	{
		isPlaying = false;
		
		mediaPlayer.submit(new Runnable() 
		{
            @Override
            public void run() 
            {
            	mediaPlayer.controls().pause();
            }
        });
	}
	
	protected void stopPlayback()
	{
		if (isPlaying)
		{
    		isPlaying = false;
    		
    		mediaPlayer.submit(new Runnable() 
    		{
                @Override
                public void run() 
                {
                	mediaPlayer.controls().stop();
                }
            });
    		
    		Utils.wait(500);
		}
	}
	
	protected void playPlayback()
	{
		isPlaying = true;
		
		mediaPlayer.submit(new Runnable() 
		{
            @Override
            public void run() 
            {
            	mediaPlayer.controls().play();
            }
        });
	}
	
	public Playback getLoadedMedia()
	{
		return playlist.get(pid);
	}
	
	public void setPlayerFullscreen(boolean fullscreen)
	{
		isFullscreen = fullscreen;
		mediaPlayer.fullScreen().set(fullscreen);
	}
	
	public void fastforward(int sec)
	{	
		long newTime = playlist.get(pid).playTime + (sec * 1000);
		long maxTime = mediaPlayer.status().length();
		
		if (newTime > maxTime)
			newTime = maxTime;
		
		playlist.get(pid).updatePlayTime(newTime);
		
		double scale = Utils.scale(newTime, mediaPlayer.status().length(), 0);
		Main.videoWindow.seek(scale, newTime);

    	seek(newTime);
	}
	
	public void rewind(int sec)
	{
		long newTime = playlist.get(pid).playTime + (sec * -1000);
		
		if (newTime < 0)
			newTime = 0;
		
		playlist.get(pid).updatePlayTime(newTime);
		
    	double scale = Utils.scale(newTime, mediaPlayer.status().length(), 0);
    	Main.videoWindow.seek(scale, newTime);
    	
    	seek(newTime);
	}
}
