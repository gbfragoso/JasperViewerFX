package org.jvfx.viewer;
	
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jvfx.database.ConnectionManager;
import org.jvfx.example.Album;
import org.jvfx.example.Artist;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class Test extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		Button complexCollectionReport = new Button("Complex report from JRBeanCollectionDataSource");
		Button simpleCollectionReport = new Button("Simple report from JRBeanCollectionDataSource");
		Button simpleJdbcReport = new Button("Simple report from JDBC connection");
		
		complexCollectionReport.setPrefWidth(300);
		simpleCollectionReport.setPrefWidth(300);
		simpleJdbcReport.setPrefWidth(300);

		simpleJdbcReport.setOnAction((ActionEvent) -> {
			try {
				Connection con = new ConnectionManager().getConnection();
				JasperReport jreport = (JasperReport) JRLoader.loadObject(getClass().getResource("/org/jvfx/example/simple_report.jasper"));
				JasperPrint jprint = JasperFillManager.fillReport(jreport, null, con);
				new JasperViewerFX(primaryStage).viewReport("Simple report", jprint);
				con.close();
			} catch (JRException | SQLException e) {
				e.printStackTrace();
			}
		});

		simpleCollectionReport.setOnAction((ActionEvent) -> {
			try {
				ObservableList<Album> collection = FXCollections.observableArrayList();
				collection.add(new Album(1, "For Those About To Rock We Salute You", 25.00));
				collection.add(new Album(2, "Balls to the Wall", 25.00));
				collection.add(new Album(3, "Restless and Wild", 25.00));
				collection.add(new Album(4, "Let There Be Rock", 25.00));
				collection.add(new Album(5, "Big Ones", 25.00));
				collection.add(new Album(6, "Jagged Little Pill", 25.00));
				collection.add(new Album(7, "Facelift", 25.00));
				collection.add(new Album(8, "Warner 25 Anos", 25.00));
				collection.add(new Album(9, "Plays Metallica By Four Cellos", 25.00));
				collection.add(new Album(10, "Audioslave", 25.00));
				collection.add(new Album(11, "Out Of Exile", 25.00));
				collection.add(new Album(12, "BackBeat Soundtrack", 25.00));
				collection.add(new Album(13, "The Best Of Billy Cobham", 25.00));

				JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(collection);
				JasperReport jreport = (JasperReport) JRLoader.loadObject(getClass().getResource("/org/jvfx/example/bean_report.jasper"));
				JasperPrint jprint = JasperFillManager.fillReport(jreport, null, source);
				new JasperViewerFX(primaryStage).viewReport("JRBeanCollectionDataSource example", jprint);
			} catch (JRException e) {
				e.printStackTrace();
			}
		});
		
		complexCollectionReport.setOnAction((ActionEvent) -> {
			try {
				List<Artist> artists = new ArrayList<>();
				ObservableList<Album> collection = FXCollections.observableArrayList();
				collection.add(new Album(1, "For Those About To Rock We Salute You", 25.00));
				collection.add(new Album(2, "Balls to the Wall", 25.00));
				collection.add(new Album(3, "Restless and Wild", 25.00));
				collection.add(new Album(4, "Let There Be Rock", 25.00));
				collection.add(new Album(5, "Big Ones", 25.00));
				collection.add(new Album(6, "Jagged Little Pill", 25.00));
				collection.add(new Album(7, "Facelift", 25.00));
				collection.add(new Album(8, "Warner 25 Anos", 25.00));
				collection.add(new Album(9, "Plays Metallica By Four Cellos", 25.00));
				collection.add(new Album(10, "Audioslave", 25.00));
				collection.add(new Album(11, "Out Of Exile", 25.00));
				collection.add(new Album(12, "BackBeat Soundtrack", 25.00));
				collection.add(new Album(13, "The Best Of Billy Cobham", 25.00));
				artists.add(new Artist("Example", collection));

				JasperReport jreport = (JasperReport) JRLoader.loadObject(getClass().getResource("/org/jvfx/example/bean_with_subreport.jasper"));
				URL subreport = getClass().getResource("/org/jvfx/example/bean_report.jasper");
				
				Map<String, Object> params = new HashMap<>();
				params.put("SUBREPORT_PATH", subreport.getPath());
				
				JasperPrint jprint = JasperFillManager.fillReport(jreport, params, new JRBeanCollectionDataSource(artists));
				new JasperViewerFX(primaryStage).viewReport("JRBeanCollectionDataSource example", jprint);
			} catch (JRException e) {
				e.printStackTrace();
			}
		});

		VBox options = new VBox(5);
		options.setAlignment(Pos.CENTER);
		options.getChildren().addAll(simpleCollectionReport, simpleJdbcReport, complexCollectionReport);

		Scene scene = new Scene(options,400,400);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
