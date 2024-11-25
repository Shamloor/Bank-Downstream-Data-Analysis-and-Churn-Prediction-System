package com.example.service;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MysqlService {

    private static final String URL = "jdbc:mysql://192.168.8.128:3306/analysis";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    public String queryTable(String tableName) {
        StringBuilder resultBuilder = new StringBuilder();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM " + tableName;
            ResultSet resultSet = statement.executeQuery(query);

            int columnCount = resultSet.getMetaData().getColumnCount();

            // 添加表头
            for (int i = 1; i <= columnCount; i++) {
                resultBuilder.append(resultSet.getMetaData().getColumnName(i));
                if (i < columnCount) {
                    resultBuilder.append(",");
                }
            }
            resultBuilder.append("\n");

            // 添加数据行
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    resultBuilder.append(resultSet.getObject(i));
                    if (i < columnCount) {
                        resultBuilder.append(",");
                    }
                }
                resultBuilder.append("\n");
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultBuilder.toString();
    }

    public static void main(String[] args) {
        System.out.println(new MysqlService().queryTable("train"));
    }
}
