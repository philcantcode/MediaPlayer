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
	requires jdk.httpserver;
	requires javafx.web;
	
	opens application to javafx.graphics, javafx.fxml, controllers;
	opens controllers to javafx.fxml, com.sun.glass.ui, javafx.web;
	opens structures to javafx.graphics, ImageViewVideoSurfaceFactory;
}