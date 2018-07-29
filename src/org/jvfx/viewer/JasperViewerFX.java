package org.jvfx.viewer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

/**
 * An simple approach to JasperViewer in JavaFX. Based on Michael Grecol
 * approach.
 * 
 * @author Gustavo Fragoso
 * @version 3.1
 */
public class JasperViewerFX {

    private Button print, save, backPage, firstPage, nextPage, lastPage, zoomIn, zoomOut;
    private ImageView report;
    private Label lblReportPages;
    private Stage dialog;
    private TextField txtPage;

    private JasperPrint jasperPrint;

    private SimpleIntegerProperty currentPage;
    private int imageHeight = 0, imageWidth = 0, reportPages = 0;
    
    public JasperViewerFX(Stage owner) {
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(owner);
        dialog.setScene(createScene());
        dialog.getIcons().add(new Image("/org/jvfx/icons/jasperlogo.jpg"));
        
        currentPage = new SimpleIntegerProperty(this, "currentPage", 1);
    }
    
    // ***********************************************
    // Scene and button actions
    // ***********************************************
    private Scene createScene() {
        print = new Button(null, new ImageView("/org/jvfx/icons/printer.png"));
        save = new Button(null, new ImageView("/org/jvfx/icons/save.png"));
        backPage = new Button(null, new ImageView("/org/jvfx/icons/backimg.png"));
        firstPage = new Button(null, new ImageView("/org/jvfx/icons/firstimg.png"));
        nextPage = new Button(null, new ImageView("/org/jvfx/icons/nextimg.png"));
        lastPage = new Button(null, new ImageView("/org/jvfx/icons/lastimg.png"));
        zoomIn = new Button(null, new ImageView("/org/jvfx/icons/zoomin.png"));
        zoomOut = new Button(null, new ImageView("/org/jvfx/icons/zoomout.png"));

        // Pref sizes
        print.setPrefSize(30, 30);
        save.setPrefSize(30, 30);
        backPage.setPrefSize(30, 30);
        firstPage.setPrefSize(30, 30);
        nextPage.setPrefSize(30, 30);
        lastPage.setPrefSize(30, 30);
        zoomIn.setPrefSize(30, 30);
        zoomOut.setPrefSize(30, 30);
        
        backAction();
        nextAction();
        firstPageAction();
        lastPageAction();
        zoomInAction();
        zoomOutAction();
        printAction();
        saveAction();

        txtPage = new TextField("1");
        txtPage.setPrefSize(40, 30);
        txtPage.setOnAction((ActionEvent event) -> {
            try {
                int p = Integer.parseInt(txtPage.getText());
                setCurrentPage((p > 0 && p <= reportPages) ? p : 1);
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.WARNING, "Invalid number", ButtonType.OK).show();
            }
        });
        
        lblReportPages = new Label("/ 1");

        HBox menu = new HBox(5);
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(5));
        menu.setPrefHeight(50.0);
        menu.getChildren().addAll(print, save, firstPage, backPage, txtPage, lblReportPages, nextPage, lastPage, zoomIn, zoomOut);

        // This imageview will preview the pdf inside scrollpane
        report = new ImageView();
        report.setFitHeight(imageHeight);
        report.setFitWidth(imageWidth);
        
        // Centralizing the ImageView on Scrollpane
        Group contentGroup = new Group();
        contentGroup.getChildren().add(report);

        StackPane stack = new StackPane(contentGroup);
        stack.setAlignment(Pos.CENTER);
        stack.setStyle("-fx-background-color: gray");

        ScrollPane scroll = new ScrollPane(stack);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);

        BorderPane root = new BorderPane();
        root.setTop(menu);
        root.setCenter(scroll);

        return new Scene(root, 1024, 768);
    }

    private void backAction() {
        backPage.setOnAction((ActionEvent event) -> {
            int newValue = getCurrentPage() - 1;
            setCurrentPage(newValue);
            
            // Turn foward buttons on again
            if (nextPage.isDisabled()) {
                nextPage.setDisable(false);
                lastPage.setDisable(false);
            }
        });
    }

    private void firstPageAction() {
        firstPage.setOnAction((ActionEvent event) -> {
            setCurrentPage(1);
            
            // Turn foward buttons on again
            if (nextPage.isDisabled()) {
                nextPage.setDisable(false);
                lastPage.setDisable(false);
            }
        });
    }

    private void nextAction() {
        nextPage.setOnAction((ActionEvent event) -> {
            int newValue = getCurrentPage() + 1;
            setCurrentPage(newValue);
            
            // Turn previous button on again
            if (backPage.isDisabled()) {
                backPage.setDisable(false);
                firstPage.setDisable(false);
            }
        });
    }

    private void lastPageAction() {
        lastPage.setOnAction((ActionEvent event) -> {
            setCurrentPage(reportPages);
            
            // Turn previous button on again
            if (backPage.isDisabled()) {
                backPage.setDisable(false);
                firstPage.setDisable(false);
            }
        });
    }

    private void printAction() {
        print.setOnAction((ActionEvent event) -> {
            try {
                JasperPrintManager.printReport(jasperPrint, true);
                dialog.hide();
            } catch (JRException ex) {
                System.out.println(ex.getMessage());
            }
        });
    }

    private void saveAction() {
        save.setOnAction((ActionEvent event) -> {
            
            FileChooser chooser = new FileChooser();
            FileChooser.ExtensionFilter pdf = new FileChooser.ExtensionFilter("Portable Document Format (*.pdf)", "*.pdf");
            FileChooser.ExtensionFilter html = new FileChooser.ExtensionFilter("HyperText Markup Language", "*.html");
            FileChooser.ExtensionFilter xml = new FileChooser.ExtensionFilter("eXtensible Markup Language", "*.xml");
            FileChooser.ExtensionFilter xls = new FileChooser.ExtensionFilter("Microsoft Excel 2007", "*.xls");
            FileChooser.ExtensionFilter xlsx = new FileChooser.ExtensionFilter("Microsoft Excel 2016", "*.xlsx");
            chooser.getExtensionFilters().addAll(pdf, html, xml, xls, xlsx);
            
            chooser.setTitle("Salvar");
            chooser.setSelectedExtensionFilter(pdf);
            File file = chooser.showSaveDialog(dialog);
            
            if (file != null) {
                List<String> box = chooser.getSelectedExtensionFilter().getExtensions();
                switch (box.get(0)) {
                case "*.pdf":
                    try {
                        JasperExportManager.exportReportToPdfFile(jasperPrint, file.getPath());
                    } catch (JRException ex) {
                    }
                    break;
                case "*.html":
                    try {
                        JasperExportManager.exportReportToHtmlFile(jasperPrint, file.getPath());
                    } catch (JRException ex) {
                    }
                    break;
                case "*.xml":
                    try {
                        JasperExportManager.exportReportToXmlFile(jasperPrint, file.getPath(), false);
                    } catch (JRException ex) {
                    }
                    break;
                case "*.xls":
                    try {
                        JRXlsExporter exporter = new JRXlsExporter();
                        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file));
                        exporter.exportReport();
                    } catch (JRException ex) {
                    }
                    break;
                case "*.xlsx":
                    try {
                        JRXlsxExporter exporter = new JRXlsxExporter();
                        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file));
                        exporter.exportReport();
                    } catch (JRException ex) {
                    }
                    break;
                }
            }
        });
    }
    
    private void zoomInAction() {
        zoomIn.setOnAction((ActionEvent event) -> {
            zoom(0.15);
        });
    }
        
    private void zoomOutAction() {
        zoomOut.setOnAction((ActionEvent event) -> {
            zoom(-0.15);
        });
    }

    /**
     * Set the currentPage property and render report page 
     * @param page Page number
     */
    public void setCurrentPage(int page) {
        try {
            if(page > 0 && page <= reportPages) {
                currentPage.set(page);
                txtPage.setText(Integer.toString(page));
                
                if (page == 1) {
                    backPage.setDisable(true);
                    firstPage.setDisable(true);
                }

                if (page == reportPages) {
                    nextPage.setDisable(true);
                    lastPage.setDisable(true);
                }
                
                // Rendering the current page
                float zoom = (float) 1.33;
                BufferedImage image = (BufferedImage) JasperPrintManager.printPageToImage(jasperPrint, page - 1, zoom);
                WritableImage fxImage = new WritableImage(imageHeight, imageWidth);
                report.setImage(SwingFXUtils.toFXImage(image, fxImage));
            }
        } catch (JRException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    /**
     * Get the current page
     * @return Current page value
     */
    public int getCurrentPage() {
        return currentPage.get();
    }
    
    /**
     * Get the currentPage property
     * @return
     */
    public SimpleIntegerProperty currentPageProperty() {
        return currentPage;
    }
        
    /**
     * Load report from JasperPrint
     * @param title Dialog title
     * @param jasperPrint JasperPrint object
     */
    public void viewReport(String title, JasperPrint jasperPrint) {
        this.jasperPrint = jasperPrint;
        
        // Report rendered image properties
        imageHeight = jasperPrint.getPageHeight() + 284;
        imageWidth = jasperPrint.getPageWidth() + 201;
        reportPages = jasperPrint.getPages().size();
        lblReportPages.setText("/ " + reportPages);
        
        setCurrentPage(1);

        // With only one page those buttons are unnecessary
        if (reportPages == 1) {
            nextPage.setDisable(true);
            lastPage.setDisable(true);
        }

        dialog.setTitle(title);
        dialog.show();
    }
    
    /**
     * Scale image from ImageView
     * @param factor Zoom factor
     */
    public void zoom(double factor) {
        report.setScaleX(report.getScaleX() + factor);
        report.setScaleY(report.getScaleY() + factor);
        report.setFitHeight(imageHeight + factor);
        report.setFitWidth(imageWidth + factor);
    }

}
