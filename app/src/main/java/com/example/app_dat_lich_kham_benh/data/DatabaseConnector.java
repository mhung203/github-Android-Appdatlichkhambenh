package com.example.app_dat_lich_kham_benh.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String URL = "jdbc:mysql://mysql-dee55b2-appdatlichkhambenh1.l.aivencloud.com:22586/appkhambenh?ssl-mode=REQUIRED";
    private static final String USER = "avnadmin";
    private static final String PASSWORD = "AVNS_G7WLaeo-m1pYBD9LOie";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
}
