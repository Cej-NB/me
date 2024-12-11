package com.cej.service.impl;

import com.cej.service.MqService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqServiceImpl implements MqService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void send() {
        rabbitTemplate.convertAndSend("cej-me","hello");
    }
}
