/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pat.rmq;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.AMQP.BasicProperties;
import java.util.UUID;


/**
 *
 * @author feryandi
 */
public class RPCClient {
    private Connection connection;
    private Channel channel;
    private QueueingConsumer consumer;
    
    /* Client Queue */
    private String replyQueue;
    
    /* Server Queue */
    private String requestQueueName = "rpc_queue";
    

    public RPCClient() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();

        replyQueue = channel.queueDeclare().getQueue();
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(replyQueue, true, consumer);
    }

    public String call(String method, String message) throws Exception {
        String response = null;
        String corrId = UUID.randomUUID().toString();

        BasicProperties props = new BasicProperties
                                    .Builder()
                                    .correlationId(corrId)
                                    .replyTo(replyQueue)
                                    .build();

        channel.basicPublish("", method, props, message.getBytes("UTF-8"));

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response = new String(delivery.getBody(),"UTF-8");
                break;
            }
        }

        return response;
    }

    public void close() throws Exception {
        connection.close();
    }

    public static void main(String[] argv) {
        RPCClient fibonacciRpc = null;
        String response = null;
        try {
            fibonacciRpc = new RPCClient();

            System.out.println(" [x] Requesting fib(30)");
            response = fibonacciRpc.call("30");
            System.out.println(" [.] Got '" + response + "'");
        }
        catch  (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (fibonacciRpc!= null) {
                try {
                    fibonacciRpc.close();
                }
                catch (Exception ignore) {}
            }
        }
    }
}
