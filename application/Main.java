package application;

import java.io.File;
import java.io.IOException;

import application.Settings.Key;
import controllers.DisplayWindow;
import controllers.VLCWindow;
import database.DBDelete;
import database.DBInsert;
import database.DBSelect;
import database.DBUpdate;
import database.Database;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import structures.MediaItem;
import universals.CodeLogger;
import universals.CodeLogger.DEPTH;
import web.RootWebpage;
import web.Server;


public class Main extends Application 
{
	public static DisplayWindow mainWindow = null;
	public static VLCWindow videoWindow = null;
	
	private static Stage mainStage = null;
	private static Stage vlcStage = null;
	private static Scene mainScene = null;
	private static Scene vlcScene = null;
	
	public static Server server = null;
		
	public static void main(String[] args) 
	{
		new Database();
		new DBInsert();
		new DBSelect();
		new DBUpdate();
		new DBDelete();
		new Settings();
		
		MediaItem.loadAll();
		
		if (Settings.getBool(Key.START_SERVER))
		{
			server = new Server();
		}
	
		launch(args);
	}
	
	@Override public void start(Stage mainStage) throws IOException 
	{	
		Main.mainStage = mainStage;
		Main.vlcStage = new Stage();
		/* TODO: POSSIBLE RACE CONDITION WITH SIDEPANEL - FUTURE-PHIL SHUD FIX DIS */
		BorderPane mainDisplay = FXMLLoader.load(getClass().getResource(Settings.RES_PATH + "Display.fxml"));		
		StackPane vlcDisplay = FXMLLoader.load(getClass().getResource(Settings.RES_PATH + "VLC.fxml"));
				
		Main.mainScene = new Scene(mainDisplay, 800, 600);
		Main.vlcScene = new Scene(vlcDisplay, 800, 600);
		
		Main.mainScene.getStylesheets().add(getClass().getResource(Settings.RES_PATH + "darkMode.css").toExternalForm());
		Main.vlcScene.getStylesheets().add(getClass().getResource(Settings.RES_PATH + "darkMode.css").toExternalForm());
		
		Main.mainStage.setTitle("Media Player V2M3");
		Main.mainStage.setScene(mainScene);
		Main.mainStage.setMinHeight(600);
		Main.mainStage.setMinWidth(800);
        
		Main.vlcStage.setTitle("Media Player View");
		Main.vlcStage.setScene(vlcScene);
		Main.vlcStage.setMinHeight(60);
        Main.vlcStage.setMinWidth(80);

        Main.mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() 
        {
            @Override
            public void handle(WindowEvent t) 
            {
            	Server.shutdown();
                Platform.exit();
                System.exit(0);
            }
        });
        
        Main.vlcStage.setOnCloseRequest(new EventHandler<WindowEvent>() 
        {
            @Override
            public void handle(WindowEvent t) 
            {
            	Main.vlcStage.hide();
            	Main.videoWindow.pauseVlcPlayback();
            	Main.mainWindow.populateRecentlyWatchedArea();	
            }
        });
        
        Main.vlcScene.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			KeyCode kc = event.getCode();
			
			if (kc == KeyCode.SPACE)
			{
				Main.videoWindow.togglePlayback();
				Main.videoWindow.manageVLCViewer(null, false, false);
			}
			else if (kc == KeyCode.ESCAPE)
			{
				Main.videoWindow.manageVLCViewer(false, true, true);
			}
			else if (kc == KeyCode.LEFT)
			{
				Main.videoWindow.rewind();
			}
			else if (kc == KeyCode.RIGHT)
			{
				Main.videoWindow.fastforward();
			}
			else if (kc == KeyCode.ENTER)
			{
				Main.videoWindow.skip();
			}
		});
		
		Main.vlcScene.addEventFilter(MouseEvent.MOUSE_ENTERED, event ->
		{
			Main.videoWindow.manageVLCViewer(null, true, true);
		});
		
		Main.vlcScene.addEventFilter(MouseEvent.MOUSE_EXITED, event ->
		{
			if (Main.videoWindow.isPlayingVLC)
				Main.videoWindow.manageVLCViewer(null, false, false);
		});
                        
        Main.mainStage.show();
	}
	
	@Override public void stop()
	{
		System.out.println("stopping ");
	}
	
	public static void showVLCScene(boolean show)
	{
		if (show && !vlcStage.isShowing())
			vlcStage.show();
		else if (!show && vlcStage.isShowing())
			vlcStage.hide();
	}
	
	public static boolean confirmation()
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Are you sure?");

		ButtonData bd = alert.showAndWait().get().getButtonData();
		
		if (bd.isCancelButton())
			return false;
		else
			return true;
	}
	
	public static File folderChooser(String title)
	{
		DirectoryChooser fileChooser = new DirectoryChooser();
		fileChooser.setTitle(title);
		File f = fileChooser.showDialog(Main.mainStage);
		
		return f;
	}
	
	public static void bindSizeProperty(ImageView player)
	{
		player.fitWidthProperty().bind(vlcScene.widthProperty());
		player.fitHeightProperty().bind(vlcScene.heightProperty());
	}
	
	public static void setCursorVis(boolean visible)
	{
		if (!visible)
			vlcScene.setCursor(Cursor.NONE);
		else
			vlcScene.setCursor(Cursor.DEFAULT);
		
		CodeLogger.log("Setting cursor visibility: " + visible, DEPTH.CHILD, false);
	}
	
	public static void setVLCFullscreen(boolean fullscreen)
	{
		vlcStage.setFullScreen(fullscreen);
	}
	
	public static void setTitle(String title)
	{
		Main.vlcStage.setTitle(title);
	}
}
