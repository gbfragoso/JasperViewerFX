# JasperViewerFX

The JasperViewerFX aims to avoid usage of default Swing JasperReport's viewer. This viewer use SwingFXUtils to render only one page at time and append the output image to an ImageView.

[!view.jpeg]

# Limitations

JasperReports draw engine relies on awt graphics, drawing each element on a Graphics2D canvas. The JavaFX's GraphicsContext is incompatible with Graphics2D so, even translating the drawing methods, we have memory leaks in some situations. The ImageView method has better overall performance and doesn't cause memory problems, but prevent user to click links and the zoom quality decrease when scaling images.

# Features

- Exporting for PDF, HTML, XML (Without images), XLS, XLSX;
- Zoom in / Zoom Out;
- Interface completely written in JavaFX;
- Current page property.

# Minimum setup

- commons-beanutils-1.9.3.jar
- commons-collections-3.2.2.jar
- commons-digester-2.1.jar
- commons-javaflow-20160505.jar
- commons-logging-1.1.1.jar
- itext-2.1.7.js6.jar
- jasperreports-6.0.0.jar (or above)

# Older versions

In older versions of this project the JasperPrint's generation method has been abstracted. We decided to focus on just viewing the report and let the user decide how to generate it.

# How to use

## Example with JDBC connection

```java
try {
	Connection con = new ConnectionManager().getConnection();
	JasperReport jreport = (JasperReport) JRLoader.loadObject(getClass().getResource("/org/jvfx/example/simple_report.jasper"));
	JasperPrint jprint = JasperFillManager.fillReport(jreport, null, con);
	new JasperViewerFX(primaryStage).viewReport("Simple report", jprint);
	con.close();
} catch (JRException | SQLException e) {
	e.printStackTrace();
}
```

Look [Test.java](org/jvfx/viewer/Test.java) to see this code in action and other examples as well.