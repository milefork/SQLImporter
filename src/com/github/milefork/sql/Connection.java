package com.github.milefork.sql;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Connection {
	
	private java.sql.Connection c;
	
	public Connection(String className, String connUrl) throws SQLException {
		loadDriver(className);
		setConnection(DriverManager.getConnection(connUrl));
	}
	
	private void loadDriver(String className) {
		try {
			Class.forName(className).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	public java.sql.Connection getConnection() {
		return c;
	}

	public void setConnection(java.sql.Connection c) {
		this.c = c;
	}

}
