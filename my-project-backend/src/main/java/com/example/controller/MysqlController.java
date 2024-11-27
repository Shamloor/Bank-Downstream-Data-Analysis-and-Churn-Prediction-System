package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.MysqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/mysql")
public class MysqlController {

    @Autowired
    private MysqlService mysqlService;

    /**
     * 获取指定表的数据
     * @param tableName 表名
     * @return 表中数据
     */
    @PostMapping("/data/{tableName}")
    public RestBean<List<Map<String, Object>>> getTableData(@PathVariable String tableName) {
        System.out.println("Requested Table: " + tableName);
        return messageHandle(() -> {
            try {
                return mysqlService.getTableData(tableName);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 统一消息处理
     * @param action 消息处理逻辑
     * @param <T> 返回数据类型
     * @return 包装的响应数据
     */
    private <T> RestBean<T> messageHandle(Supplier<T> action) {
        try {
            T result = action.get();
            return RestBean.success(result);  // 成功的响应
        } catch (Exception e) {
            return RestBean.failure(400, "Failed due to: " + e.getMessage());  // 错误的响应
        }
    }
}