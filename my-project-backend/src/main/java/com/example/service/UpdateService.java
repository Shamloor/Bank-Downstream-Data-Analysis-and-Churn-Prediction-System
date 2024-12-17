package com.example.service;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class UpdateService {

    // 假设我们将数据保存在一个列表中
    private List<String> updateMessages = new ArrayList<>();

    public void processMessage(String message) {
        //String modifiedMessage = message.replaceAll("\\\\", "");
        String modifiedMessage = message;
        System.out.println("Received message: " + modifiedMessage);

        updateMessages.add(modifiedMessage); // 将消息添加到本地列表中
    }

    // 返回最新一条消息
    public String getLatestMessage() {
        if (!updateMessages.isEmpty()) {
            return updateMessages.get(updateMessages.size() - 1); // 返回最后一条消息
//            return updateMessages.get(0); // 返回最后一条消息
        }
        return null; // 如果没有消息，返回 null
    }
}
