package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.MysqlService;
import com.example.service.SSHService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/mysql")
public class MysqlController {

    @Autowired
    private MysqlService mysqlService;

    @GetMapping("/table/{tablename:.+}")
    public ResponseEntity<RestBean<String>> executeSSHStatus(@PathVariable String tablename) {
        return ResponseEntity.ok(
                messageHandle(() -> mysqlService.queryTable(tablename))  // 返回文件内容
        );}

    private <T> RestBean<T> messageHandle(Supplier<T> action) {
        try {
            T result = action.get();
            return RestBean.success(result);  // 成功的响应
        } catch (Exception e) {
            return RestBean.failure(400, "Failed due to: " + e.getMessage());  // 错误的响应
        }
    }

}