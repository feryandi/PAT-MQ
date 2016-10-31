/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author feryandi
 */
public class Client {
    public static Client client = new Client();
    
    private Connection connection;
    private Channel channel;
    private QueueingConsumer consumer;
    
    /* Client Queue */
    private String CLIENT_RPC_QUEUE;
    private String CLIENT_MSG_QUEUE;
    
    /* Server Queue */
    private static final String SERVER_HOST = "localhost";
    private String SERVER_QUEUE_NAME = "server_queue";
    private static final String SERVER_EXCHANGE_NAME = "server_exchange";
    
    /* Message Memory */
    
    /* Identity */
    public int id = -1;
    public String userid = null;
    
    public Client() {        
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(SERVER_HOST);
            connection = factory.newConnection();
            channel = connection.createChannel();
            
            channel.exchangeDeclare(SERVER_EXCHANGE_NAME, "direct");
            CLIENT_RPC_QUEUE = channel.queueDeclare().getQueue();
            CLIENT_MSG_QUEUE = channel.queueDeclare().getQueue();
            consumer = new QueueingConsumer(channel);
            
            Consumer econsumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
                }
            };
            
            channel.basicConsume(CLIENT_RPC_QUEUE, true, consumer);
            channel.basicConsume(CLIENT_MSG_QUEUE, true, econsumer);
            
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static Client getInstance() {
        return client;
    }
    
    public String call(String message) throws Exception {
        String response = null;
        String corrId = UUID.randomUUID().toString();

        AMQP.BasicProperties props = new AMQP.BasicProperties
                                    .Builder()
                                    .correlationId(corrId)
                                    .replyTo(CLIENT_RPC_QUEUE)
                                    .build();

        channel.basicPublish("", SERVER_QUEUE_NAME, props, message.getBytes("UTF-8"));

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response = new String(delivery.getBody(),"UTF-8");
                break;
            }
        }

        return response;
    }

    public void bind(String key) {
        try {        
            channel.queueBind(CLIENT_MSG_QUEUE, SERVER_EXCHANGE_NAME, key);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void close() throws Exception {
        connection.close();
    }
    
}
