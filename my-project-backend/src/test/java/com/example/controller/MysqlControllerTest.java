package com.example.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class MysqlControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    /**
     * 测试获取表数据的接口
     */
    @Test
    public void testGetTableData() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // 模拟动态路径变量的值
        String tableName = "ads_customer_balance_distribution";
        String url = "/api/mysql/data/" + tableName;

        mockMvc.perform(post(url) // 发送 POST 请求
                        .contentType(MediaType.APPLICATION_JSON)) // 设置 Content-Type 头
                .andDo(print()) // 打印请求和响应
                .andExpect(status().isOk()); // 期望响应状态码为 200
    }
}