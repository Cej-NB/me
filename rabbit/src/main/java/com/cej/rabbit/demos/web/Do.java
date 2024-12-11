package com.cej.rabbit.demos.web;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeoutException;

/**
 * rabbitmq 简单的生产者-消息队列-消费者
 */
public class Do {
    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("172.26.122.133");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("123");
        for (int i = 0; i < 1000; i++){
            sendMessage(connectionFactory);
        }

        //receiveMessage(connectionFactory);

    }


    public static void  sendMessage(ConnectionFactory connectionFactory) throws IOException, TimeoutException {

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();


        //1、交换机名称 2、交换机类型 3、是否持久化 4、自动删除 5、内部使用 6、其他参数
        channel.exchangeDeclare("exchange", BuiltinExchangeType.DIRECT,true,false,false,null);



        //1、队列名称
        //2、是否支持持久化
        //3、是否独占本次连接
        //4、是否在不食用的时候，删除队列
        //5、队列其他参数
        channel.queueDeclare("queue", false, false, false, null);

        //3、路由键
        channel.queueBind("queue", "exchange", "demo");

        String message = "Hello World";
        //1、交换机名称，默认 default exchange
        //2、路由key
        //3、配置信息
        //4、消息内容
        channel.basicPublish("", "queue", null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");

        channel.close();
        connection.close();
    }

    public static void receiveMessage(ConnectionFactory connectionFactory) throws IOException, TimeoutException {
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        DefaultConsumer consumer = new DefaultConsumer(channel){
            public void handleDelivery(String consumerTag, Envelope envelope,AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body);
                System.out.println(" [x] Received '" + message + "'");
                System.out.println(LocalDateTime.now());
            }
        };

        //1、队列名称
        //2、autoAck,是否确认
        //3、callback，回调
        channel.basicConsume("queue", true, consumer);

    }
}
