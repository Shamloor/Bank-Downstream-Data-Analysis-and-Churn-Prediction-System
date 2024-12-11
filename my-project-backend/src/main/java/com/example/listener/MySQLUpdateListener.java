package com.example.listener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.service.UpdateService;

@Component
public class MySQLUpdateListener implements MessageListener {

    @Autowired
    private UpdateService updateService;

    @RabbitListener(queues = "db_update_queue")
    public void onMessage(Message message) {
        // 将接收到的消息转为字符串
        String receivedMessage = new String(message.getBody());

        // 将消息传给UpdateService进行处理
        updateService.processMessage(receivedMessage);
    }
}
