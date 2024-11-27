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

    /**
     * 查询指定表的数据
     * @param tableName 表名
     * @return 表中数据
     */
    public List<Map<String, Object>> getTableData(String tableName) throws SQLException {
        List<Map<String, Object>> resultList = new ArrayList<>();

        String query = "SELECT * FROM " + tableName; // 注意表名不能使用占位符
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                resultList.add(row);
            }
        }

        return resultList;
    }
}