package controllers;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Stack;

import application.Main;
import application.Settings;
import application.Settings.Key;
import application.Settings.META;
import database.DBDelete;
import database.DBInsert;
import database.DBSelect;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import structures.FolderManager;
import structures.FolderManager.DIRECTORY_TYPE;
import structures.MediaItem;
import structures.Playback;

public class DisplayWindow implements Initializable
{
	
	@FXML private Accordion menuAccord;											
	@FXML private BorderPane graphArea;				
	@FXML private VBox graphLoadVbox;
	@FXML private VBox fileManagerVbox;
	@FXML private ProgressBar progressBar;
	
	@FXML private Button mainNavBack;
	@FXML private TilePane mainTilePane;
	
	@FXML private Button selectFolder;
	@FXML private VBox watchedFoldersList;
	@FXML private VBox playHistoryArea;
	
	@FXML private TextField pathField;
	@FXML private ToggleButton settingsAutoPlay;
	@FXML private ToggleButton titleFolder;
	@FXML private ToggleButton seriesFolder;
	@FXML private ToggleButton categoriesFolder;
	
	@FXML private VBox recentlyAdded;
	
	private Stack<String> browsingHistory = new Stack<String>();
    
	public DisplayWindow()
	{ 
		Main.mainWindow = this;
	}

	@Override 
	public void initialize(URL location, ResourceBundle resources)
	{	
		populateWatchfolderArea();
		populateRecentlyWatchedArea();
		populateDirectoryAreaInitial();
		
		menuAccord.setExpandedPane(menuAccord.getPanes().get(1));
		
		selectFolder.setOnAction(new EventHandler<ActionEvent>() 
		{
		    @Override 
		    public void handle(ActionEvent e) 
		    {
		    	progress(0.1);
		    	File f = Main.folderChooser("Choose Folder to Track");
		    	
				if (f != null)
				{				
					DBInsert.insertWatchFolder(f.getAbsolutePath());
					populateWatchfolderArea();
					populateDirectoryAreaInitial();
				}
		    }
		});
		
		mainNavBack.setOnMouseClicked(new EventHandler<Event>() 
		{
			@Override
			public void handle(Event arg0) 
			{			
				progress(0.1);
				
				if (browsingHistory.size() == 1)
					populateDirectoryArea(browsingHistory.pop(), true);
				else if (browsingHistory.size() > 1)
				{
					browsingHistory.pop();
					populateDirectoryArea(browsingHistory.peek(), true);
				}
			}
		});
		
		settingsAutoPlay.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent event) 
			{
				if (Settings.getBool(Key.AUTOPLAY))
					Settings.set(Key.AUTOPLAY, false);
				else
					Settings.set(Key.AUTOPLAY, true);
			}
		});
		
		titleFolder.setTooltip(new Tooltip("Select this button if the folder lists movie names."));
		titleFolder.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent event) 
			{
				String f = browsingHistory.peek();
				
				if (Playback.folderMeta.containsKey(f) && Playback.folderMeta.get(f) == META.TITLES)
				{
					DBDelete.deleteMeta(f, META.TITLES);
					titleFolder.setSelected(false);
					Playback.folderMeta.remove(f, META.TITLES);
				}
				else if (Playback.folderMeta.containsKey(f))
				{
					titleFolder.setSelected(false);
				}
				else
				{
					DBInsert.insertMeta(browsingHistory.peek(), META.TITLES);
					titleFolder.setSelected(true);
					Playback.folderMeta.put(browsingHistory.peek(), META.TITLES);
				}
			}
		});
		
		titleFolder.setTooltip(new Tooltip("Select this button if the folder lists series numbers."));
		seriesFolder.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent event) 
			{
				String f = browsingHistory.peek();
				
				if (Playback.folderMeta.containsKey(f) && Playback.folderMeta.get(f) == META.SERIES)
				{
					DBDelete.deleteMeta(f, META.SERIES);
					seriesFolder.setSelected(false);
					Playback.folderMeta.remove(f, META.SERIES);
				}
				else if (Playback.folderMeta.containsKey(f))
				{
					seriesFolder.setSelected(false);
				}
				else
				{
					DBInsert.insertMeta(f, META.SERIES);
					seriesFolder.setSelected(true);
					Playback.folderMeta.put(f, META.SERIES);
				}
			}
		});
		
		categoriesFolder.setTooltip(new Tooltip("Select this button if the folder lists categories."));
		categoriesFolder.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent event) 
			{
				String f = browsingHistory.peek();
				
				if (Playback.folderMeta.containsKey(f) && Playback.folderMeta.get(f) == META.CATEGORIES)
				{
					DBDelete.deleteMeta(f, META.CATEGORIES);
					categoriesFolder.setSelected(false);
					Playback.folderMeta.remove(f, META.CATEGORIES);
				}
				else if (Playback.folderMeta.containsKey(f))
				{
					categoriesFolder.setSelected(false);
				}
				else
				{
					DBInsert.insertMeta(f, META.CATEGORIES);
					categoriesFolder.setSelected(true);
					Playback.folderMeta.put(f, META.CATEGORIES);
				}
			}
		});
		
		if (Settings.getBool(Key.AUTOPLAY))
			settingsAutoPlay.setSelected(true);
		
		setTitleSeriesFolder();
	}
	
	public void setTitleSeriesFolder()
	{

		if (Playback.folderMeta.containsKey(browsingHistory.peek()) && Playback.folderMeta.get(browsingHistory.peek()) == META.TITLES)
			titleFolder.setSelected(true);
		else
			titleFolder.setSelected(false);
		
		if (Playback.folderMeta.containsKey(browsingHistory.peek()) && Playback.folderMeta.get(browsingHistory.peek()) == META.SERIES)
			seriesFolder.setSelected(true);
		else
			seriesFolder.setSelected(false); 
		
		if (Playback.folderMeta.containsKey(browsingHistory.peek()) && Playback.folderMeta.get(browsingHistory.peek()) == META.CATEGORIES)
			categoriesFolder.setSelected(true);
		else
			categoriesFolder.setSelected(false); 
	}
	
	private void progress(final double speed)
	{
		Runnable runnable = new Runnable() 
		{
            @Override 
            public void run() 
            {
            	for (double i = 0.1; i <= 1; i += speed)
        		{
        			progressBar.setProgress(i);
        			
        			try
        			{
        				Thread.sleep(100);
        			} 
        			catch (InterruptedException e)
        			{
        				e.printStackTrace();
        			}
        			
        			progressBar.setProgress(0);
        		}
            }
        };
        
        new Thread(runnable).start();
	}

	private void populateRecentlyWatchedArea()
	{
		playHistoryArea.getChildren().clear();
		ArrayList<String> listed = new ArrayList<String>();
		
		for (Playback pb : DBSelect.loadPlaybacks())
		{
			if (!pb.isCorrectOS() || listed.contains(pb.namedFolder))
				continue;
			
			listed.add(pb.namedFolder);
			
			Button b = new ButtonFactory().info(pb.namedFolder, pb.path).make();
			b.setOnMouseClicked(event ->
			{		
				if (event.getButton() == MouseButton.PRIMARY)
				{
					Main.videoWindow.initialise(pb, false);
					populateDirectoryArea(pb.parentFolder, true);
				}
				else if (event.getButton() == MouseButton.MIDDLE)
				{
					if (Main.confirmation())
					{	
						DBDelete.deletePlayback(pb.id);
						populateRecentlyWatchedArea();
					}
				}
				else if (event.getButton() == MouseButton.SECONDARY)
				{
					populateDirectoryArea(pb.parentFolder, true);
				}
			});
			
			playHistoryArea.getChildren().add(b);			
		}
	}
	
	/* Sidepanel Listing folders and drives being watched */
	private void populateWatchfolderArea()
	{
		watchedFoldersList.getChildren().clear();
		
		for (String s : DBSelect.loadWatchedFolders())
		{
			if (s.contains(File.separator))
			{
    			Button b = new ButtonFactory().info(s, s).make();
    			b.setOnAction(new EventHandler<ActionEvent>() 
    			{
					@Override
					public void handle(ActionEvent event) 
					{
						populateDirectoryArea(s, true);
					}
				});
    			
    			MediaItem.trackDir(s);
    			
    			watchedFoldersList.getChildren().add(b);	
			}
		}
	}

	private void populateDirectoryArea(String path, boolean clear)
	{
		if (!browsingHistory.contains(path))
			browsingHistory.push(path);
		
		pathField.setText(browsingHistory.peek());
				
		if (clear)
			mainTilePane.getChildren().clear();
		
		ArrayList<File> fileList = FolderManager.getFileListing(path, DIRECTORY_TYPE.BOTH);
		
		for (File f : fileList)
		{
			ButtonFactory factory = new ButtonFactory();
			factory.info(f.getName(), f.getAbsolutePath());
			
			if (f.isFile())
				factory.file();
			else if (f.isDirectory() && !MediaItem.isNew(f.getAbsolutePath()))
				factory.folder();
			else if (f.isDirectory() && MediaItem.isNew(f.getAbsolutePath()))
				factory.newFolder();
			
			Button b = factory.make();
			b.setOnAction(new EventHandler<ActionEvent>() 
			{
				@Override
				public void handle(ActionEvent event) 
				{
					if (f.isFile())
					{
						Playback pb = Playback.findPlayback(f.getName(), f.getAbsolutePath());
						Main.videoWindow.initialise(pb, false);
						populateRecentlyWatchedArea();
					}
					else
					{
						populateDirectoryArea(f.getAbsolutePath(), true);
					}
				}
			});
			
			mainTilePane.getChildren().add(b);
		}
		
		setTitleSeriesFolder();
		MediaItem.addToRecentMediaMenu();
	}
	
	/* Initially populate the main directory window with files
	 * from the watched folders */
	private void populateDirectoryAreaInitial()
	{
		mainTilePane.getChildren().clear();
		
		for (String s : DBSelect.loadWatchedFolders())
		{
			if (s.contains(File.separator))
			{
				populateDirectoryArea(s, false);
			}
		}
		
		setTitleSeriesFolder();
	}
	
	public void addRecentlyAdded(String path, boolean addDivider, String title)
	{
		if (addDivider)
		{
			Text t = new Text(title);
			t.setFill(Color.WHITE);
			
			recentlyAdded.getChildren().add(t);
		}
		
		Button b = new ButtonFactory().info(Playback.namedFolder(path), path).make();
		recentlyAdded.getChildren().add(b);
		
		b.setOnAction(new EventHandler<ActionEvent>() 
		{
			@Override
			public void handle(ActionEvent event) 
			{
				populateDirectoryArea(path, true);
			}
		});
	}
}
