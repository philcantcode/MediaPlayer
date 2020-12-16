package controllers;

import java.io.IOException;
import java.io.InputStream;

import application.Settings;
import application.Settings.FILETYPE;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ButtonFactory 
{
	private Button b = new Button();
	private String name = null;

	public ButtonFactory() 
	{

	}
	
	public ButtonFactory info(String text, String toolTip)
	{
		this.name = text;
		
		b.setText(text);
		b.setWrapText(true);
		b.setPrefSize(250, 20);
		b.setTooltip(new Tooltip(toolTip));
		b.setAlignment(Pos.CENTER_LEFT);
		
		return this;
	}
	
	public ButtonFactory folder()
	{
		ImageView view = new ImageView(new Image(getClass().getResourceAsStream(Settings.RES_PATH + "folder.png")));
		view.setFitHeight(40);
		view.setPreserveRatio(true);
		
		b.setGraphic(view);
		
		return this;
	}
	
	public ButtonFactory newFolder()
	{
		ImageView view = new ImageView(new Image(getClass().getResourceAsStream(Settings.RES_PATH + "NewFolder.png")));
		view.setFitHeight(40);
		view.setPreserveRatio(true);
		
		b.setGraphic(view);
		
		return this;
	}
	
	public ButtonFactory file()
	{
		FILETYPE ext;
		
		if (!name.contains("."))
			ext = FILETYPE.UNKNOWN;
		else
			ext = Settings.extension(name.substring(name.lastIndexOf(".") + 1));
		
		Image icon = null;
		
		if (ext == FILETYPE.MOVIE)
			icon = new Image(getClass().getResourceAsStream(Settings.RES_PATH + "play.png"));
		
		if (ext == FILETYPE.IMAGE)
			icon = new Image(getClass().getResourceAsStream(Settings.RES_PATH + "image.png"));
		
		if (ext == FILETYPE.UNKNOWN)
			icon = new Image(getClass().getResourceAsStream(Settings.RES_PATH + "file.png"));
		
		ImageView view = new ImageView(icon);
		view.setFitHeight(40);
		view.setPreserveRatio(true);
		
		b.setGraphic(view);
		
		return this;
	}
	
	public Button make()
	{
		return b;
	}
}
