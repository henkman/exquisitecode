package abnormale.knochen.exquisitecode.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// TODO: make test for this
public class Db {
	private static Connection conn;

	public static void init() throws ClassNotFoundException,SQLException {
		Class.forName("org.hsqldb.jdbc.JDBCDriver");
		conn = DriverManager.getConnection("jdbc:hsqldb:mydatabase", "SA", "");
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return conn.prepareStatement(sql);
	}
}
