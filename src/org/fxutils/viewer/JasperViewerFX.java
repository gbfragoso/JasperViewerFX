/*
 * Copyright (C) 2017 Gustavo Fragoso
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fxutils.viewer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;

/**
 * An simple approach to JasperViewer in JavaFX. Based on Michael Grecol approach.
 * @author Gustavo Fragoso
 * @date Aug 09, 2017
 */
public class JasperViewerFX {
    
    private final Stage dialog;
    private Button print,save,backPage,firstPage,nextPage,lastPage,zoomIn,zoomOut;
    private Label bottomLabel;
    private ImageView report;
    private TextField txtPage;
        
    // JasperReports variables
    private JasperReport jreport;
    private JasperPrint jprint;
    
    private int imageHeight = 0, imageWidth = 0, reportPages = 0;
    
    // Property
    private IntegerProperty currentPage;
	
    public JasperViewerFX(Stage owner, String title, String jasper, HashMap params, Connection con){
        
        // Initializing window
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(owner);
        dialog.setScene(createScene());
        dialog.setTitle(title);
        
        try {            
            URL arquivo = getClass().getResource(jasper);
            jreport = (JasperReport) JRLoader.loadObject(arquivo);
            jprint = JasperFillManager.fillReport(jreport, params, con);
            
            imageHeight = jprint.getPageHeight()+284;
            imageWidth = jprint.getPageWidth()+201;
            reportPages = jprint.getPages().size();
            
        } catch (JRException ex){
        }
    }
    
    public JasperViewerFX(Stage owner, String title, String jasper, HashMap params, JRBeanCollectionDataSource source){
        
        // Initializing window
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(owner);
        dialog.setScene(createScene());
        dialog.setTitle(title);
        
        try {
            URL arquivo = getClass().getResource(jasper);
            jreport = (JasperReport) JRLoader.loadObject(arquivo);
            jprint = JasperFillManager.fillReport(jreport, params, source);
            
			imageHeight = jprint.getPageHeight()+284;
            imageWidth = jprint.getPageWidth()+201;
            reportPages = jprint.getPages().size();
            
        } catch (JRException ex){
        }
    }

    // ***********************************************
    // Property
    // ***********************************************
    public void setCurrentPage(int page){
        currentPage.set(page);
        viewPage(page);
    }
    
    public int getCurrentPage(){
        return currentPage.get();
    }
    
    // ***********************************************
    // Methods
    // ***********************************************
    private Scene createScene(){
        HBox menu = new HBox(5);
		menu.setAlignment(Pos.CENTER);
        menu.setPrefHeight(50.0);
        
        // Menu's buttons
        print = new Button(null, new ImageView("/org/fxutils/icons/printer.png"));
        save = new Button(null, new ImageView("/org/fxutils/icons/save.png"));
        backPage = new Button(null, new ImageView("/org/fxutils/icons/backimg.png"));
        firstPage = new Button(null, new ImageView("/org/fxutils/icons/firstimg.png"));
        nextPage = new Button(null, new ImageView("/org/fxutils/icons/nextimg.png"));        
        lastPage = new Button(null, new ImageView("/org/fxutils/icons/lastimg.png"));
        zoomIn = new Button(null, new ImageView("/org/fxutils/icons/zoomin.png"));
        zoomOut = new Button(null,new ImageView("/org/fxutils/icons/zoomout.png"));       
        
        // Pref sizes
        print.setPrefSize(30, 30);
        save.setPrefSize(30, 30);
        backPage.setPrefSize(30, 30);
        firstPage.setPrefSize(30, 30);
        nextPage.setPrefSize(30, 30);
        lastPage.setPrefSize(30, 30);
        zoomIn.setPrefSize(30, 30);
        zoomOut.setPrefSize(30, 30);
        
        txtPage = new TextField("1");
        txtPage.setPrefSize(75,30);
        
        menu.getChildren().addAll(print,save,firstPage,backPage,txtPage,nextPage,lastPage,zoomIn,zoomOut);

        // This imageview will preview the pdf inside scrollpane
        report = new ImageView();
        report.setFitHeight(imageHeight);
        report.setFitWidth(imageWidth);
            
        Group contentGroup = new Group();
        contentGroup.getChildren().add(report);
        
        StackPane stack = new StackPane(contentGroup);
        stack.setAlignment(Pos.CENTER);
        stack.setStyle("-fx-background-color: gray");
        
        ScrollPane scroll = new ScrollPane(stack);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
	
		// Bottom label
        bottomLabel = new Label();
	
        BorderPane root = new BorderPane();
        root.setCenter(scroll);
        root.setTop(menu);
        root.setBottom(bottomLabel);
        
        Scene scene = new Scene(root,1024,768);

        return scene;
    }
    
    private void start(){
		currentPage = new SimpleIntegerProperty(this, "currentPage");
        setCurrentPage(1);
		
		// Bottom label
        bottomLabel.setText("Page 1 of " + reportPages);
        
		// Visual feedback of reading progress
		currentPage.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
			bottomLabel.setText("Page " + newValue + " of " + reportPages);
			txtPage.setText(newValue.toString());
			
			if(newValue.intValue() == 1){
                backPage.setDisable(true);
                firstPage.setDisable(true);
            }
            
            if (newValue.intValue() == reportPages){
                nextPage.setDisable(true);
                lastPage.setDisable(true);
            }  
		});
	    
        // Those buttons must start disabled
        backPage.setDisable(true);
        firstPage.setDisable(true);
        
        // With only one page those buttons are unnecessary
        if(reportPages == 1){
            nextPage.setDisable(true);
            lastPage.setDisable(true);
        }
		
        backPage.setOnAction((ActionEvent event) -> {
            backAction();
        });

        firstPage.setOnAction((ActionEvent event) -> {
        	firstPageAction();
        });
        
        nextPage.setOnAction((ActionEvent event) -> {
            nextAction();
        });

        lastPage.setOnAction((ActionEvent event) -> {
            lastPageAction();
        });
        
        print.setOnAction((ActionEvent event) -> { 
            printAction();
        });
        
        save.setOnAction((ActionEvent event) -> {
            saveAction();
        });
        zoomIn.setOnAction((ActionEvent event) -> {
            zoomInAction();
        });
        
        zoomOut.setOnAction((ActionEvent event) -> {
            zoomOutAction();
        });
        
        txtPage.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    int p = Integer.parseInt(txtPage.getText());
                    if(p > reportPages){
                        setCurrentPage(reportPages);
                    }else{
						if(p > 0){
                        	setCurrentPage(p);
						}else{
							setCurrentPage(1);
						}
                    }
                } catch (NumberFormatException e) {                    
                    Alert dialog1 = new Alert(Alert.AlertType.WARNING, "Invalid number", ButtonType.OK);
                    dialog1.show();
                }
            }
        });
    }
    
    private void backAction(){
        int newValue = getCurrentPage() - 1;
        setCurrentPage(newValue);
        
        // Turn foward buttons on again
        if (nextPage.isDisabled()){
            nextPage.setDisable(false);
            lastPage.setDisable(false);
        }
    }
    
    private void firstPageAction(){
        setCurrentPage(1);
        
        // Turn foward buttons on again
        if (nextPage.isDisabled()){
            nextPage.setDisable(false);
            lastPage.setDisable(false);
        }
    }    
    
    private void nextAction(){
        int newValue = getCurrentPage() + 1;
        setCurrentPage(newValue);
        
        // Turn previous button on again
        if (backPage.isDisabled()){
            backPage.setDisable(false);
            firstPage.setDisable(false);
        }
    }
    
    private void lastPageAction(){
        setCurrentPage(reportPages);
        
        // Turn previous button on again
        if (backPage.isDisabled()){
            backPage.setDisable(false);
            firstPage.setDisable(false);
        }
    }
    
    // Calls default printer action
    private void printAction(){
        try {
            JasperPrintManager.printReport(jprint, true);
        } catch (JRException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void saveAction(){
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter pdf = new FileChooser.ExtensionFilter("Portable Document Format (*.pdf)", "*.pdf");
        FileChooser.ExtensionFilter html = new FileChooser.ExtensionFilter("HyperText Markup Language","*.html");
        FileChooser.ExtensionFilter xml = new FileChooser.ExtensionFilter("eXtensible Markup Language","*.xml");
        FileChooser.ExtensionFilter xls = new FileChooser.ExtensionFilter("Microsoft Excel 2007", "*.xls");
        FileChooser.ExtensionFilter xlsx = new FileChooser.ExtensionFilter("Microsoft Excel 2016", "*.xlsx");
        FileChooser.ExtensionFilter csv = new FileChooser.ExtensionFilter("Comma-separeted Values", "*.csv");
        chooser.getExtensionFilters().addAll(pdf, html,xml,xls,xlsx,csv);
        
        chooser.setTitle("Salvar");
        chooser.setSelectedExtensionFilter(pdf);
        File file = chooser.showSaveDialog(dialog);

        if (file != null){
            List<String> box = chooser.getSelectedExtensionFilter().getExtensions();
            switch(box.get(0)){
                case "*.pdf":
                    try {
                        JasperExportManager.exportReportToPdfFile(jprint, file.getPath());
                    }catch (JRException ex){
                    }    
                    break;
                case "*.html":
                    try {
                        JasperExportManager.exportReportToHtmlFile(jprint, file.getPath());
                    }catch (JRException ex){
                    }
                    break;
                case "*.xml":
                    try {
                        JasperExportManager.exportReportToXmlFile(jprint, file.getPath(),false);
                    }catch (JRException ex){
                    }
                    break;
                case "*.xls":
                    try {
                        JRXlsExporter exporter = new JRXlsExporter();
                        exporter.setExporterInput(new SimpleExporterInput(jprint));
                        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file));
                        exporter.exportReport();
                    }catch (JRException ex){
                    }
                    break;
                case "*.xlsx":
                    try {
                        JRXlsxExporter exporter = new JRXlsxExporter();
                        exporter.setExporterInput(new SimpleExporterInput(jprint));
                        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file));
                        exporter.exportReport();
                    }catch (JRException ex){
                    }
                    break;
                case "*.csv":
                    try {
                        JRCsvExporter exporter = new JRCsvExporter();
                        exporter.setExporterInput(new SimpleExporterInput(jprint));
                        exporter.setExporterOutput(new SimpleWriterExporterOutput(file));
                        exporter.exportReport();
                    }catch (JRException ex){
                    }
                    break;
            }         
        }        
    }
    
    public void show(){
        if(reportPages > 0){
            start();
            dialog.show();
        }
        else{
            Alert aviso = new Alert(Alert.AlertType.INFORMATION, "We found 0 entries for this report", ButtonType.CLOSE);
            aviso.setHeaderText("Sorry");
            aviso.show();
        } 
    }
    
    private void viewPage(int page){
        try {
            float zoom = (float) 1.33;
            BufferedImage image = (BufferedImage)JasperPrintManager.printPageToImage(jprint, page - 1, zoom);
            WritableImage fxImage = new WritableImage(imageHeight,imageWidth);
            report.setImage(SwingFXUtils.toFXImage(image, fxImage));
        } catch (JRException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void zoomInAction(){
        report.setScaleX(report.getScaleX()+0.15);
        report.setScaleY(report.getScaleY()+0.15);
        report.setFitHeight(imageHeight+0.15);
	report.setFitWidth(imageWidth+0.15);
    }
    
    private void zoomOutAction(){
        report.setScaleX(report.getScaleX()-0.15);
        report.setScaleY(report.getScaleY()-0.15);
        report.setFitHeight(imageHeight-0.15);
	report.setFitWidth(imageWidth-0.15);
    }
    
}
