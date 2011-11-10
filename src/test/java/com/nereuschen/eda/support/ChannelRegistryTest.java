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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.concurrent.Executors;

import org.junit.Test;

import com.nereuschen.eda.channel.impl.DirectChannel;
import com.nereuschen.eda.channel.impl.ExecutorChannel;
import com.nereuschen.eda.channel.impl.PublishSubscribeChannel;
import com.nereuschen.eda.channel.impl.QueueChannel;

/**
 * @author Nereus Chen (nereus.chen@gmail.com)
 */
public class ChannelRegistryTest {

    /**
     * Test method for
     * {@link com.nereuschen.eda.support.ChannelRegistry#registerMessageChannel(java.lang.String, com.nereuschen.eda.channel.MessageChannel)}
     * .
     */
    @Test
    public void testRegisterMessageChannel() {
        String key = "queueChannel";
        String dumpKey = "dump key";
        ChannelRegistry.registerMessageChannel(key, new QueueChannel());
        assertNotNull(ChannelRegistry.lookupQueueChannel(key));
        assertNull(ChannelRegistry.lookupQueueChannel(dumpKey));
        ChannelRegistry.unregisterMessageChannel(key);
        ChannelRegistry.unregisterMessageChannel(dumpKey);
    }

    @Test
    public void testRegisterMessageChannelWithDuplicateName() {
        String key = "queueChannel";
        ChannelRegistry.registerMessageChannel(key, new QueueChannel());
        assertNotNull(ChannelRegistry.lookupQueueChannel(key));

        try {
            ChannelRegistry.registerMessageChannel(key, new QueueChannel());
            assertNotNull(ChannelRegistry.lookupQueueChannel(key));
        } catch (Exception e) {
            // TODO: handle exception
        }
        ChannelRegistry.unregisterMessageChannel(key);
    }

    /**
     * Test method for {@link com.nereuschen.eda.support.ChannelRegistry#unregisterMessageChannel(java.lang.String)}.
     */
    @Test
    public void testUnregisterMessageChannel() {
        String key = "queueChannel";
        ChannelRegistry.registerMessageChannel(key, new QueueChannel());
        assertNotNull(ChannelRegistry.lookupQueueChannel(key));
        ChannelRegistry.unregisterMessageChannel(key);
        assertNull(ChannelRegistry.lookupQueueChannel(key));
        ChannelRegistry.unregisterMessageChannel("dump key");

    }

    /**
     * Test method for {@link com.nereuschen.eda.support.ChannelRegistry#lookupMessageChannel(java.lang.String)}.
     */
    @Test
    public void testLookupMessageChannel() {
        assertNull(ChannelRegistry.lookupMessageChannel("dump key"));
        String key = "queueChannel";
        ChannelRegistry.registerMessageChannel(key, new QueueChannel());
        assertNotNull(ChannelRegistry.lookupMessageChannel(key));
        ChannelRegistry.unregisterMessageChannel(key);
    }

    /**
     * Test method for {@link com.nereuschen.eda.support.ChannelRegistry#lookupQueueChannel(java.lang.String)}.
     */
    @Test
    public void testLookupQueueChannel() {
        String key = "queueChannel";
        ChannelRegistry.registerMessageChannel(key, new QueueChannel());
        assertNotNull(ChannelRegistry.lookupQueueChannel(key));
        assertNull(ChannelRegistry.lookupQueueChannel("dump key"));
        ChannelRegistry.unregisterMessageChannel(key);
    }

    /**
     * Test method for {@link com.nereuschen.eda.support.ChannelRegistry#lookupDirectChannel(java.lang.String)}.
     */
    @Test
    public void testLookupDirectChannel() {
        String key = "directChannel";
        ChannelRegistry.registerMessageChannel(key, new DirectChannel());
        assertNotNull(ChannelRegistry.lookupDirectChannel(key));
        assertNull(ChannelRegistry.lookupDirectChannel("dump key"));
        ChannelRegistry.unregisterMessageChannel(key);
    }

    /**
     * Test method for {@link com.nereuschen.eda.support.ChannelRegistry#lookupExecutorChannel(java.lang.String)}.
     */
    @Test
    public void testLookupExecutorChannel() {
        String key = "executorChannel";
        ChannelRegistry.registerMessageChannel(key, new ExecutorChannel(Executors.newSingleThreadExecutor()));
        assertNotNull(ChannelRegistry.lookupExecutorChannel(key));
        assertNull(ChannelRegistry.lookupExecutorChannel("dump key"));
        ChannelRegistry.unregisterMessageChannel(key);
    }

    /**
     * Test method for
     * {@link com.nereuschen.eda.support.ChannelRegistry#lookupPublishSubscribeChannel(java.lang.String)}.
     */
    @Test
    public void testLookupPublishSubscribeChannel() {
        String key = "publishSubscribeChannel";
        ChannelRegistry.registerMessageChannel(key, new PublishSubscribeChannel(Executors.newSingleThreadExecutor()));
        assertNotNull(ChannelRegistry.lookupPublishSubscribeChannel(key));
        assertNull(ChannelRegistry.lookupPublishSubscribeChannel("dump key"));
        ChannelRegistry.unregisterMessageChannel(key);
    }

}
