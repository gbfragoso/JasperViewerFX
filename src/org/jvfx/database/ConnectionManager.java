package org.jvfx.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	private String conectionUrl = "jdbc:sqlite:src/org/jvfx/database/chinook.sqlite";
	private String driverName = "org.sqlite.JDBC";
	
	public Connection getConnection(){
        
        try{
        	Class.forName(driverName);
        	Connection con = DriverManager.getConnection(conectionUrl);

        	return con;
        }catch (SQLException | ClassNotFoundException ex) {
        	ex.printStackTrace();
        }

		return null;
	}
}
