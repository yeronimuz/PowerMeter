package com.lankheet.pmagent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import io.dropwizard.lifecycle.Managed;

public class DatabaseManager implements Managed {
	private static final Logger LOG = LogManager.getLogger(DatabaseManager.class);

	private String url;
	private static Connection conn = null;
	private static Statement stmt = null;

	public DatabaseManager(String url) {
		this.url = url;
	}

	@Override
	public void start() throws Exception {
		try {
			Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
			conn = DriverManager.getConnection(url);
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
		}
	}

	@Override
	public void stop() throws Exception {
		try {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				DriverManager.getConnection(url + ";shutdown=true");
				conn.close();
			}
		} catch (SQLException ex) {
			LOG.error(ex.getMessage());
		}
	}

}
