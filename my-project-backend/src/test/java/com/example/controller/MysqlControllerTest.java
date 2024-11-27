package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.MysqlService;
import com.example.controller.MysqlController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MysqlControllerTest {

    @Autowired
    private MysqlController mysqlController;

    @Test
    public void testGetTableData() {
        RestBean<List<Map<String, Object>>> response = mysqlController.getTableData("modify_logs"); // 替换为实际表名
        assertNotNull(response);
        assertEquals(200, response.code());
        assertNotNull(response.data());
        System.out.println("Data: " + response.data());
    }
}