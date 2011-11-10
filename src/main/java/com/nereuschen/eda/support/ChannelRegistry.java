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
package com.nereuschen.eda.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nereuschen.eda.channel.MessageChannel;
import com.nereuschen.eda.channel.impl.DirectChannel;
import com.nereuschen.eda.channel.impl.ExecutorChannel;
import com.nereuschen.eda.channel.impl.PublishSubscribeChannel;
import com.nereuschen.eda.channel.impl.QueueChannel;
import com.nereuschen.eda.util.Assert;

/**
 * registry/unregistry/lookup channel
 * 
 * @author Nereus Chen (nereus.chen@gmail.com)
 */
public class ChannelRegistry {

    private static Map<String, MessageChannel> channels = new ConcurrentHashMap<String, MessageChannel>();

    private ChannelRegistry() {

    }

    /**
     * Registers an messageChannel in the registry with a key.
     * 
     * @param key the key to store the value against. This is a non-null value
     * @param messageChannel the messageChannel to store in the registry. This is a non-null value
     * @throws IllegalArgumentException if an object with the same key already exists
     */
    public synchronized static void registerMessageChannel(String key, MessageChannel messageChannel) {
        Assert.notNull(key, "key must not be null");
        Assert.notNull(messageChannel, "messageChannel must not be null");
        Assert.isNull(channels.get(key), "another messageChannel has existed with the same");
        channels.put(key, messageChannel);
    }

    /**
     * remove an object by name from the registry.
     * 
     * @param key the name or key of the object to remove from the registry
     * @throws RegistrationException if there is a problem unregistering the object. Typically this will be because
     *             the object's lifecycle threw an exception
     */
    public static void unregisterMessageChannel(String key) {
        Assert.notNull(key, "key must not be null");
        channels.remove(key);
    }

    /**
     * lookup a messageChannel with the key
     * 
     * @param key the key for a messageChannel
     * @return a messageChannel
     */
    public static MessageChannel lookupMessageChannel(String key) {
        return channels.get(key);
    }

    /**
     * lookup a messageChannel with the key
     * 
     * @param key the key of the messageChannel to lookup
     * @param requiredType type the messageChannel must match. Can be an interface or superclass
     *            of the actual class, or <code>null</code> for any match. For example, if the value
     *            is <code>MessageChannel</code>, this method will succeed whatever the class of the
     *            returned instance.
     * @return a messageChannel
     */
    @SuppressWarnings("unchecked")
    private static <T> T lookupMessageChannel(String key, Class<T> requiredType) {
        Assert.notNull(key, "key must not be null");
        Assert.notNull(requiredType, "requiredType must not be null");
        MessageChannel channel = channels.get(key);
        if (channel != null && requiredType.isAssignableFrom(channel.getClass())) {
            return (T) channel;
        } else {
            return null;
        }

    }

    /**
     * lookup a queueChannel with the key
     * 
     * @param key the key for a queueChannel
     * @return a queueChannel
     */
    public static QueueChannel lookupQueueChannel(String key) {
        return lookupMessageChannel(key, QueueChannel.class);
    }

    /**
     * lookup a directChannel with the key
     * 
     * @param key the key for a directChannel
     * @return a directChannel
     */
    public static DirectChannel lookupDirectChannel(String key) {
        return lookupMessageChannel(key, DirectChannel.class);
    }

    /**
     * lookup a QueueMessageChannel with the key
     * 
     * @param key the key for a QueueMessageChannel
     * @return a QueueMessageChannel
     */
    public static ExecutorChannel lookupExecutorChannel(String key) {
        return lookupMessageChannel(key, ExecutorChannel.class);
    }

    /**
     * lookup a publishSubscribeChannel with the key
     * 
     * @param key the key for a publishSubscribeChannel
     * @return a publishSubscribeChannel
     */
    public static PublishSubscribeChannel lookupPublishSubscribeChannel(String key) {
        return lookupMessageChannel(key, PublishSubscribeChannel.class);
    }

}
