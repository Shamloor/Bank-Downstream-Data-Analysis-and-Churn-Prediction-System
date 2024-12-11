package com.example.service;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class UpdateService {

    // 假设我们将数据保存在一个列表中
    private List<String> updateMessages = new ArrayList<>();

    public void processMessage(String message) {
        String modifiedMessage = message.replaceAll("\\\\", "");
        System.out.println("Received message: " + modifiedMessage);

        updateMessages.add(modifiedMessage); // 将消息添加到本地列表中
    }

    // 提供一个方法让Controller访问最新的消息
    public List<String> getLatestUpdates() {
        return updateMessages; // 获取所有存储的消息
    }
}
