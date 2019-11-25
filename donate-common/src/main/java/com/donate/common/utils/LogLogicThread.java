package com.donate.common.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.AMQP.BasicProperties;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LogLogicThread implements Runnable {
    public static ConcurrentLinkedQueue<String> logQueue = new ConcurrentLinkedQueue();

    public LogLogicThread() {
    }

    public void run() {
        Connection conn = null;
        Channel channel = null;
        String mqName = ApiSetting.getLogMQ();
        String message = "";
        Boolean durable = false;

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(ApiSetting.getLogHost());
            factory.setUsername(ApiSetting.getLogUserName());
            factory.setPassword(ApiSetting.getLogPassword());
            factory.setAutomaticRecoveryEnabled(true);
            conn = factory.newConnection();
            channel = conn.createChannel();
            channel.queueDeclare(mqName, durable, false, false, (Map)null);

            while(logQueue != null && !logQueue.isEmpty()) {
                message = (String)logQueue.poll();
                if (message != null && message != "") {
                    channel.basicPublish("", mqName, (BasicProperties)null, message.getBytes("utf-8"));
                    message = "";
                }
            }
        } catch (Exception var15) {
            if (logQueue.size() > 10000) {
                logQueue = new ConcurrentLinkedQueue();
            } else if (message != null && message != "") {
                logQueue.offer(message);
            }
        } finally {
            try {
                if (channel != null) {
                    if (channel.isOpen()) {
                        channel.close();
                    }

                    channel.abort();
                }

                if (conn != null) {
                    if (conn.isOpen()) {
                        conn.close();
                    }

                    conn.abort();
                }
            } catch (Exception var14) {
                ;
            }

            LogLogic.IsRunning = false;
        }

    }
}
