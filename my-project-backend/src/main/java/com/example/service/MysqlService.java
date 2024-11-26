package com.example.service;

import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MysqlService {

    private static final String URL = "jdbc:mysql://niit01:3306/bank_db";
    private static final String USER = "root";
    private static final String PASSWORD = "000000";

//    public String queryTable(String tableName) {
//        StringBuilder resultBuilder = new StringBuilder();
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
//            Statement statement = connection.createStatement();
//            String query = "SELECT * FROM " + tableName;
//            ResultSet resultSet = statement.executeQuery(query);
//
//            int columnCount = resultSet.getMetaData().getColumnCount();
//
//            // 添加表头
//            for (int i = 1; i <= columnCount; i++) {
//                resultBuilder.append(resultSet.getMetaData().getColumnName(i));
//                if (i < columnCount) {
//                    resultBuilder.append(",");
//                }
//            }
//            resultBuilder.append("\n");
//
//            // 添加数据行
//            while (resultSet.next()) {
//                for (int i = 1; i <= columnCount; i++) {
//                    resultBuilder.append(resultSet.getObject(i));
//                    if (i < columnCount) {
//                        resultBuilder.append(",");
//                    }
//                }
//                resultBuilder.append("\n");
//            }
//
//            resultSet.close();
//            statement.close();
//            connection.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return resultBuilder.toString();
//    }

    public List<Map<String, Object>> queryTableAsJson(String tableName) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM " + tableName;
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    row.put(resultSet.getMetaData().getColumnName(i), resultSet.getObject(i));
                }
                resultList.add(row);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    public Map<String, List<Map<String, Object>>> queryAllTablesData() {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();

        // 查询四个表
        result.put("ads_customer_value_segmentation", queryTable("ads_customer_value_segmentation"));
        result.put("ads_customer_feature_distribution", queryTable("ads_customer_feature_distribution"));
        result.put("ads_customer_churn_rate", queryTable("ads_customer_churn_rate"));
        result.put("ads_customer_balance_distribution", queryTable("ads_customer_balance_distribution"));

        return result;
    }

    private List<Map<String, Object>> queryTable(String tableName) {
        List<Map<String, Object>> tableData = new ArrayList<>();
        String query = "SELECT * FROM " + tableName;

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            // 遍历 ResultSet，将每行数据存入 Map
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                int columnCount = resultSet.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = resultSet.getMetaData().getColumnName(i);
                    Object value = resultSet.getObject(i);
                    row.put(columnName, value);
                }
                tableData.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to query table: " + tableName, e);
        }

        return tableData;
    }

    public static void main(String[] args) {
        MysqlService mysqlService = new MysqlService(); // 假设构造函数没有依赖注入
        try {
            Map<String, List<Map<String, Object>>> allData = mysqlService.queryAllTablesData();
            System.out.println("Query result: " + allData);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred while testing queryAllCharts.");
        }
    }
}
