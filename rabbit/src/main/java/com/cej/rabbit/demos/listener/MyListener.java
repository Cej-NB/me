package com.cej.rabbit.demos.listener;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MyListener {


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name="q1", durable = "true"),
            exchange = @Exchange(name="directEx",type= ExchangeTypes.DIRECT /*,delayed = true*/),  //如果设置的是延迟交换机
            key= {"red","blue"}
    ))
    public void listener3(String msg){

        System.out.println("listener3:"+msg);
    }

    @RabbitListener(queues = "dly-hello")
    public void listener4(String msg){
        System.out.println("listener4:"+msg);
    }
}
