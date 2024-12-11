package com.cej.listener;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 简单rabbit监听
 * */

@Component
public class MyListener  {

    @RabbitListener(queues = "cej-me")
    public void listener1(String msg){
        System.out.println("listener1:"+msg);
    }

    @RabbitListener(queues = "cej-me")
    public void listener2(String msg){
        System.out.println("listener2:"+msg);
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name="q1", durable = "true"),
            exchange = @Exchange(name="directEx",type= ExchangeTypes.DIRECT),
            key= {"red","blue"}
    ))
    public void listener3(String msg){
        System.out.println("listener3:"+msg);
    }

}
