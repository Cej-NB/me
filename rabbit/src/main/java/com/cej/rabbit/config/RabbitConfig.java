package com.cej.rabbit.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class RabbitConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct  //该注解让创建后调用
    public void initConfig(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }
    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        //发送到交换机，成功还是失败，调用改方法

    }


    // 消息从交换机到队列失败后，才会调用该办法
    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        //发送给队列失败
        //消息后处理器
        MessagePostProcessor processor = message -> {
            message.getMessageProperties().setContentType("text/plain");
            return message;
        };
    }

}
