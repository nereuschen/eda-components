/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nereuschen.eda.demo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.nereuschen.eda.channel.impl.QueueChannel;
import com.nereuschen.eda.message.Message;
import com.nereuschen.eda.support.ChannelRegistry;
import com.nereuschen.eda.support.MessageBuilder;

/**
 * a demo for queueChannel
 * 
 * @author Nereus Chen (nereus.chen@gmail.com)
 */
public class QueueChannelDemo {

    /**
     * @param args
     */
    public static void main(String[] args) {
        final String channelName = "queueChannel";
        Executor executor = Executors.newCachedThreadPool();
        final CountDownLatch registerChannel = new CountDownLatch(1);

        Runnable sender = new Runnable() {
            @Override
            public void run() {
                QueueChannel queueChannel = new QueueChannel();
                // register the queueChannel
                ChannelRegistry.registerMessageChannel(channelName, queueChannel);
                registerChannel.countDown();
                // build a new message
                Message<String> message = MessageBuilder.withPayload("this is a message")
                        .setHeader("headerA", "headerValue").build();
                // send a message to the queueChannel
                queueChannel.send(message);
            }
        };

        Runnable receiver = new Runnable() {
            @Override
            public void run() {
                // lookup the queueChannl,may be an empty channel before the registried
                QueueChannel queueChannel = ChannelRegistry.lookupQueueChannel(channelName);
                // blocking until there is a message
                Message message = queueChannel.receive();
                System.out.println("Received message : " + message.toString());
            }
        };
        // send a message
        executor.execute(sender);
        // to ensure the queueChannel registried
        try {
            registerChannel.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // receive a message;
        executor.execute(receiver);
    }
}
