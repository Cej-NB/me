package com.cej.rabbit;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;

@SpringBootTest
class RabbitApplicationTests {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void sendSimpleMessage() throws InterruptedException {
        Message message = MessageBuilder
                .withBody("Hello,我死没死呀".getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT) //是否持久化
                .setExpiration("20000")   //设置过期时间
                .build();
        rabbitTemplate.convertAndSend("exchange","hello", message);
    }

    @Test
    void SendDelayedMessage() throws InterruptedException {
        Message message = MessageBuilder
                .withBody("Hello,我死没死呀".getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT) //是否持久化
                .build();
        message.getMessageProperties().setDelay(5000);  //安装插件后，使用延迟交换机，设置延迟
        rabbitTemplate.convertAndSend("exchange","hello", message);
    }
}
