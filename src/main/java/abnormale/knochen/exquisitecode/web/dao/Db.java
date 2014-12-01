package abnormale.knochen.exquisitecode.web.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*
    to edit the db use
    $ java -cp hsqldb-2.3.2.jar org.hsqldb.util.DatabaseManager
 */
public class Db {
    private static Connection conn;

    public static void init() throws ClassNotFoundException, SQLException {
        Class.forName("org.hsqldb.jdbc.JDBCDriver");
        conn = DriverManager.getConnection("jdbc:hsqldb:file:db/exquisite", "SA", "");
    }

    public static PreparedStatement prepareStatement(String sql) throws SQLException {
        return conn.prepareStatement(sql);
    }

    public static PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return conn.prepareStatement(sql, autoGeneratedKeys);
    }
}
