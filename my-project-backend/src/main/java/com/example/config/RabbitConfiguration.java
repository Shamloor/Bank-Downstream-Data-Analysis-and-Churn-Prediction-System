package com.example.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ消息队列配置
 */
@Configuration
public class RabbitConfiguration {
    @Bean("mailQueue")
    public Queue queue(){
        return QueueBuilder
                .durable("mail")
                .build();
    }

    // 新增的db_update_queue队列配置
    @Bean("dbUpdateQueue")
    public Queue dbUpdateQueue() {
        return QueueBuilder
                .durable("db_update_queue") // 队列名称为db_update_queue
                .build();
    }
}
