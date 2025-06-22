package com.zmz.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 数据库连接工具类
 */
public class JdbcUtil {
    /**
     * 数据库连接URL
     */
    private static final String URL = "jdbc:mysql://localhost:3306/your_database?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&useSSL=false";
    
    /**
     * 数据库用户名
     */
    private static final String USERNAME = "root";
    
    /**
     * 数据库密码
     */
    private static final String PASSWORD = "root";
    
    static {
        try {
            // 加载MySQL驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 获取数据库连接
     * @return 数据库连接对象
     * @throws SQLException SQL异常
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
} 