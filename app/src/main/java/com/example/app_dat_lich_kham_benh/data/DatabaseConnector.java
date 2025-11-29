package com.example.app_dat_lich_kham_benh.data;

import com.example.app_dat_lich_kham_benh.BuildConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String URL = "jdbc:mysql://mysql-dee55b2-appdatlichkhambenh1.l.aivencloud.com:22586/appkhambenh?useUnicode=true&characterEncoding=UTF-8&ssl-mode=REQUIRED";
    
    private static final String USER = "avnadmin";
    
    private static final String PASSWORD = BuildConfig.DB_PASSWORD;

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
}
