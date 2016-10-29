/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.AMQP.BasicProperties;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author feryandi
 */
public class Server {
    private static final String SERVER_HOST = "localhost";
    private static final String SERVER_QUEUE_NAME = "server_queue";
    private static final String SERVER_EXCHANGE_NAME = "server_exchange";
    
    Connection connection = null;
    Channel channel = null;
    QueueingConsumer consumer  = null;
    
    Service s;
    
    public Server() {
        try {
            s = new Service();            
            System.out.println(" [x] Server Service Up and Running");  
            
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(SERVER_HOST);

            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(SERVER_QUEUE_NAME, false, false, false, null);
            channel.exchangeDeclare(SERVER_EXCHANGE_NAME, "direct");
            channel.basicQos(1);
            consumer = new QueueingConsumer(channel);
            channel.basicConsume(SERVER_QUEUE_NAME, false, consumer);

            System.out.println(" [x] Server Queue for RPC Activated");            
            listen();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ignore) {}
            }
        }  
    }
    
    private void listen() {
        try {
            while (true) {
                String response = null;

                QueueingConsumer.Delivery delivery = consumer.nextDelivery();

                BasicProperties props = delivery.getProperties();
                BasicProperties replyProps = new BasicProperties
                                             .Builder()
                                             .correlationId(props.getCorrelationId())
                                             .build();

                try {
                    String message = new String(delivery.getBody(),"UTF-8");
                    JSONParser parser = new JSONParser();        
                    JSONObject p = (JSONObject) parser.parse(message);    
                    JSONObject params = (JSONObject) p.get("params");  
                    String method = (String) p.get("method");
                    
                    if (method.equals("message")) {  
                        String msg = (String) params.get("message");
                        channel.basicPublish(SERVER_EXCHANGE_NAME, 
                                (String) params.get("key"), 
                                null,
                                msg.getBytes());
                        response = "Message Sent";
                    } else {
                        response = s.execute(method, params.toJSONString());       
                    }             
                } catch (Exception e){
                    System.out.println(" [.] Exception: " + e.toString());
                    e.printStackTrace();
                    response = "";
                } finally {  
                    channel.basicPublish( "", props.getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void emit(String key, String message) throws IOException {        
        channel.basicPublish(SERVER_EXCHANGE_NAME, key, null, message.getBytes());
    }
    
}
