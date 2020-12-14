module MediaPlayerV2M3 
{
	requires javafx.graphics;
	requires javafx.fxml;
	requires javafx.controls;
	requires uk.co.caprica.vlcj;
	requires uk.co.caprica.vlcj.javafx;
	requires commons.validator;
	requires java.xml.bind;
	requires javafx.base;
	requires java.sql;
	requires java.desktop;
	
	opens application to javafx.graphics, javafx.fxml, controllers;
	opens controllers to javafx.fxml, com.sun.glass.ui;
	opens structures to javafx.graphics, ImageViewVideoSurfaceFactory;
}