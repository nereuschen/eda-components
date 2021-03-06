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

import com.nereuschen.eda.channel.impl.PublishSubscribeChannel;
import com.nereuschen.eda.core.MessageHandler;
import com.nereuschen.eda.exception.MessagingException;
import com.nereuschen.eda.message.Message;
import com.nereuschen.eda.support.ChannelRegistry;
import com.nereuschen.eda.support.MessageBuilder;

/**
 * a demo for publishSubscribeChannel
 * 
 * @author Nereus Chen (nereus.chen@gmail.com)
 */
public class PublishSubscribeChannelDemo {

    /**
     * @param args
     */
    public static void main(String[] args) {
        final String channelName = "publishSubscribeChannel";
        Executor executor = Executors.newCachedThreadPool();
        final CountDownLatch registerChannel = new CountDownLatch(1);

        Runnable sender = new Runnable() {
            @Override
            public void run() {
                PublishSubscribeChannel publishSubscribeChannel = new PublishSubscribeChannel(
                        Executors.newFixedThreadPool(2));
                // register the publishSubscribeChannel
                ChannelRegistry.registerMessageChannel(channelName, publishSubscribeChannel);
                registerChannel.countDown();
                // build five messages
                for (int i = 0; i < 5; i++) {
                    Message<String> message = MessageBuilder.withPayload("this is a message" + i)
                            .setHeader("header" + i, "headerValue" + i).build();
                    // send a message to the publishSubscribeChannel
                    publishSubscribeChannel.send(message);
                }
                System.out.println("SenderThread=" + Thread.currentThread());
            }
        };

        Runnable receiver = new Runnable() {
            @Override
            public void run() {
                // lookup the publishSubscribeChannel,may be an empty channel before the registried
                PublishSubscribeChannel publishSubscribeChannel = ChannelRegistry
                        .lookupPublishSubscribeChannel(channelName);
                // listen the message
                publishSubscribeChannel.subscribe(new MessageHandler() {
                    private String handerName = "handler1";

                    @Override
                    public void handleMessage(Message<?> message) throws MessagingException {
                        System.out.println("HandlerThread=" + Thread.currentThread() + "Handler=" + handerName
                                + ",Received message : " + message.toString());
                    }
                });
                publishSubscribeChannel.subscribe(new MessageHandler() {
                    private String handerName = "handler2";

                    @Override
                    public void handleMessage(Message<?> message) throws MessagingException {
                        System.out.println("HandlerThread=" + Thread.currentThread() + "Handler=" + handerName
                                + ",Received message : " + message.toString());
                    }
                });
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
