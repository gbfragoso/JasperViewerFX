/*
 * Copyright (C) 2015 Gustavo Fragoso
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
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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
 * @date Mar 07, 2016
 */
public class JasperViewerFX {
    
    private final Stage dialog;
    private Button print,save,back,firstPage,next,lastPage,zoomIn,zoomOut;
    private ImageView report;
    private TextField txtPage;
    private int reportPages;
    private int currentPage = 0;
    
    // JasperReports variables
    private JasperReport jreport;
    private JasperPrint jprint;
    
    // Zoom
    private int imageHeight=0, imageWidth=0;
    
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
            
        } catch (JRException ex) {
        }
    }
   
    private Scene createScene(){
        HBox menu = new HBox(5);
        menu.setPrefHeight(50.0);
        menu.setAlignment(Pos.CENTER);
        
        ImageView b1 = new ImageView("/org/fxutils/icons/printer.png");
        ImageView b2 = new ImageView("/org/fxutils/icons/save.png");
        ImageView b3 = new ImageView("/org/fxutils/icons/backimg.png");
        ImageView b4 = new ImageView("/org/fxutils/icons/firstimg.png");
        ImageView b5 = new ImageView("/org/fxutils/icons/nextimg.png");
        ImageView b6 = new ImageView("/org/fxutils/icons/lastimg.png");
        ImageView b7 = new ImageView("/org/fxutils/icons/zoomin.png");
        ImageView b8 = new ImageView("/org/fxutils/icons/zoomout.png");
        
        print = new Button(null,b1);
        print.setPrefSize(30, 30);
        save = new Button(null,b2);
        save.setPrefSize(30, 30);
        back = new Button(null,b3);
        back.setPrefSize(30, 30);
        firstPage = new Button(null,b4);
        firstPage.setPrefSize(30, 30);
        next = new Button(null,b5);
        next.setPrefSize(30, 30);
        lastPage = new Button(null,b6);
        lastPage.setPrefSize(30, 30);
        zoomIn = new Button(null,b7);
        zoomIn.setPrefSize(30, 30);
        zoomOut = new Button(null,b8);
        zoomOut.setPrefSize(30, 30);
        txtPage = new TextField("1");
        txtPage.setPrefSize(75,30);
        
        menu.getChildren().addAll(print,save,firstPage,back,txtPage,next,lastPage,zoomIn,zoomOut);

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

        BorderPane root = new BorderPane();
        root.setCenter(scroll);
        root.setTop(menu);
        
        Scene scene = new Scene(root,1024,768);

        return scene;
    }
    
    private void start(){
        viewPage(0);
        
        print.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                printAction();
            }
        });
        save.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                saveAction();
            }
        });
        back.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                backAction();
            }
        });
        next.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                nextAction();
            }
        });
        firstPage.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                firstPageAction();
            }
        });
        lastPage.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                lastPageAction();
            }
        });
        zoomIn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                zoomInAction();
            }
        });
        zoomOut.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                zoomOutAction();
            }
        });
        txtPage.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER){
                    try{
                        int p = Integer.parseInt(txtPage.getText());
                        if(p>reportPages){
                            txtPage.setText(Integer.toString(reportPages));
                            viewPage(reportPages-1);
                        }else{
                            viewPage(p - 1);
                        }
                    }catch(Exception e){
                        Alert dialog = new Alert(Alert.AlertType.WARNING, "Invalid number", ButtonType.OK);
                        dialog.show();
                    }
                    
                }
            }
        });
    }

    private void backAction(){
        if(currentPage - 1 > -1){
            currentPage--;
            txtPage.setText(Integer.toString(currentPage+1));
            viewPage(currentPage);
        }
    }
    
    private void firstPageAction(){
        txtPage.setText("1");
        currentPage = 0;
        viewPage(0);
    }
    
    private void lastPageAction(){
        txtPage.setText(Integer.toString(reportPages));
        currentPage = reportPages-1;
        viewPage(reportPages-1);
    }
    
    private void nextAction(){
        if(currentPage + 1 < reportPages){
            currentPage++;
            txtPage.setText(Integer.toString(currentPage+1));
            viewPage(currentPage);
        }
    }

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
            BufferedImage image = (BufferedImage)JasperPrintManager.printPageToImage(jprint, page, zoom);
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
