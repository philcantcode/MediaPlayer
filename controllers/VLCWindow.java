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
import structures.FolderManager;
import structures.Playback;
import structures.VLCPlayer;

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
	
	private boolean dontSeek = false;
	
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
				if (isPlaying && !isCursorVisible) 
				{
					Main.setCursorVis(true);
					isCursorVisible = true;
					
					if (isFullscreen)
						showMenus(true);
				}
				
				if (mouseScheduler != null)
					mouseScheduler.cancel(true);
				
				mouseScheduler = mouseTracker.schedule(() -> 
				{
					if (!blockCursorHiding && isPlaying)
					{
						Main.setCursorVis(false);
						isCursorVisible = false;
						
						if (isFullscreen)
							showMenus(false);
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
				File nextFile = FolderManager.nextFile(Main.videoWindow.getLoadedMedia());
				
        		if (nextFile != null)
        		{
        			Playback pb = Playback.findPlayback(nextFile.getName(), nextFile.getAbsolutePath());
        			initialise(pb, false);
        		}
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
		if (isPlaying)
			pausePlayback();
		else
			playPlayback();
	}
	
	public void updatePlaybackButtons()
	{
		if (isPlaying)
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
	
	public void showMenus(boolean show)
	{
		vlcBottomMenu.setVisible(show);
		vlcTopMenu.setVisible(show);
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
}
