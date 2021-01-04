package controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import application.Main;
import application.Settings;
import application.Settings.Key;
import application.Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import structures.FolderManager;
import structures.Playback;
import structures.VLCPlayer;
import universals.CodeLogger;
import universals.CodeLogger.DEPTH;

public class VLCWindow extends VLCPlayer implements Initializable
{
	@FXML StackPane vlcStack;
	
	@FXML HBox vlcBottomMenu;
	@FXML Button vlcPause;
	@FXML Slider vlcSeek;
	@FXML TextField vlcTime;
	
	@FXML HBox vlcTopMenu;
	@FXML ToggleButton vlcAutoPlay;
	@FXML Button vlcPlayNext;
	@FXML Button vlcPlayPrevious;
	
	@FXML WebView webView;
	
	private boolean dontSeek = false;
	private static VIEWER currentView = VIEWER.NONE;
	
	private boolean isCursorVisible = true;
	private boolean blockCursorHiding = false;
	private ScheduledThreadPoolExecutor mouseTracker = new ScheduledThreadPoolExecutor(1);
    ScheduledFuture<?> mouseScheduler;
    		
	public VLCWindow() 
	{
		Main.videoWindow = this;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) 
	{
		Platform.runLater(new Runnable() 
		{	
			@Override
			public void run() 
			{				
				Main.bindSizeProperty(vlcViewer);	
				vlcStack.getChildren().add(vlcViewer);	
				vlcStack.setId("blackOut");
			}
		});
		
		vlcStack.setOnMouseMoved(new EventHandler<Event>() 
		{
			@Override
			public void handle(Event event)
			{
				if (isPlayingVLC && !isCursorVisible) 
				{
					if (isFullscreen)
						manageVLCViewer(null, true, true);
				}
				
				if (mouseScheduler != null)
					mouseScheduler.cancel(true);
				
				mouseScheduler = mouseTracker.schedule(() -> 
				{
					if (!blockCursorHiding && isPlayingVLC)
					{
						if (isFullscreen)
							manageVLCViewer(null, false, false);
					}
					
				}, 2000, TimeUnit.MILLISECONDS);
			}
		});
		
		vlcSeek.setOnMousePressed(new EventHandler<MouseEvent>() 
		{
			@Override public void handle(MouseEvent event) 
			{
				dontSeek = true;
				blockCursorHiding = true;
			}
		});
		
		vlcSeek.setOnMouseReleased(new EventHandler<MouseEvent>() 
		{
			@Override public void handle(MouseEvent event) 
			{
				seek(vlcSeek.getValue());
				
				dontSeek = false;
				blockCursorHiding = false;
			}
		});
		
		vlcPause.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent arg0) 
			{
				togglePlayback();
				updatePlaybackButtons();
			}
		});
		
		vlcAutoPlay.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent event)
			{
				if (Settings.get(Key.AUTOPLAY).equals("true"))
					Settings.set(Key.AUTOPLAY, "false");
				else
					Settings.set(Key.AUTOPLAY, "true");
				
				focusCleanup();
			}
		});
		
		vlcPlayNext.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent event)
			{
				skip();
			}
		});
		
		vlcPlayPrevious.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent event)
			{
				File previousFile = FolderManager.previousFile(Main.videoWindow.getLoadedMedia());
        		
        		if (previousFile != null)
        		{
        			Playback pb = Playback.findPlayback(previousFile.getName(), previousFile.getAbsolutePath());
        			initialise(pb, false);
        		}
			}
		});
		
		if (Settings.getBool(Key.AUTOPLAY))
			vlcAutoPlay.setSelected(true);
	}
	
	public void togglePlayback()
	{
		if (isPlayingVLC)
			pauseVlcPlayback();
		else
			playPlayback();
	}
	
	public void updatePlaybackButtons()
	{
		if (isPlayingVLC)
			vlcPause.setText("Pause");
		else
			vlcPause.setText("Resume");
	}
	
	public void focusCleanup()
	{
		Platform.runLater(new Runnable() 
		{
			@Override
			public void run()
			{
				vlcStack.requestFocus();
			}
		});
	}
	
	/* Takes in a scaled pos value (0-1) and a MS time */
	public void seek(double pos, long ms)
	{
		if (dontSeek == false)
		{
			Platform.runLater(new Runnable() 
			{	
				@Override
				public void run()
				{
					vlcSeek.setValue(pos);
					vlcTime.setText(Utils.msToTime(ms));
				}
			});
		}
	}
	
	public void skip()
	{
		File nextFile = FolderManager.nextFile(Main.videoWindow.getLoadedMedia());
		
		CodeLogger.log("NEXT: " + nextFile.getName(), DEPTH.CHILD);
		
		if (nextFile != null)
		{
			Playback pb = Playback.findPlayback(nextFile.getName(), nextFile.getAbsolutePath());
			initialise(pb, false);
		}
	}
	
	/* Manager for the screen to set fullscreen, cursor and menu visibility */
	public void manageVLCViewer(Boolean setFullscreen, Boolean showCursor, Boolean showMenus)
	{
		if (setFullscreen != null)
		{
			Main.setVLCFullscreen(setFullscreen);
			setPlayerFullscreen(setFullscreen);
		}
		
		if (showCursor != null)
		{
			Main.setCursorVis(showCursor);
			isCursorVisible = showCursor;
		}
		
		if (showMenus != null)
		{
			vlcBottomMenu.setVisible(showMenus);
			vlcTopMenu.setVisible(showMenus);
		}
	}
		
	public void showViewer(VIEWER viewer, String url)
	{
		CodeLogger.log("Switching to viewer: " + viewer, DEPTH.CHILD);

		if (viewer == VIEWER.WEB)
		{			
			if (isLoadedVLC && isPlayingVLC)
				pauseVlcPlayback();
			
			Platform.runLater(new Runnable() 
			{	
				@Override
				public void run() 
				{
					vlcViewer.setVisible(false);
					webView.setVisible(true);
					
					webView.getEngine().load(url);
				}
			});	
		}
		else if (viewer == VIEWER.VLC)
		{			
			Platform.runLater(new Runnable() 
			{	
				@Override
				public void run() 
				{
					webView.getEngine().load("");
					
					webView.setVisible(false);
					vlcViewer.setVisible(true);
					
					if (isLoadedVLC)
						playPlayback();
				}
			});	
		}
	}
	
	public enum VIEWER
	{
		VLC, WEB, NONE
	}
}
