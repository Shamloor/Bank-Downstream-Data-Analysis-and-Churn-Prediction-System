package com.example.controller;

import com.example.service.UpdateService;
import com.example.entity.RestBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Supplier;

@RestController
@RequestMapping("/api/update")
public class UpdateController {

    @Autowired
    private UpdateService updateService;

    /**
     * 提供给前端的接口，返回最新一条消息
     * @return 返回包含最新消息的响应数据
     */
    @GetMapping
    public RestBean<String> getLatestUpdate() {
        return messageHandle(() -> updateService.getLatestMessage());
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
