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
package com.nereuschen.eda.channel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.nereuschen.eda.Message;
import com.nereuschen.eda.core.MockMessageHandler;
import com.nereuschen.eda.message.GenericMessage;

/**
 * @author Mark Fisher
 * @author Oleg Zhurakousky
 * @author Nereus Chen (nereus.chen@gmail.com)
 */
public class DirectChannelTests {

	@Test
	public void testSend() {
		DirectChannel channel = new DirectChannel();
		MockMessageHandler handler = new MockMessageHandler();
		channel.subscribe(handler);

		Message<String> message = new GenericMessage<String>("test");
		assertTrue(channel.send(message));
		assertEquals(1, handler.getMessageSize());

	}

	@Test
	public void testSendWithMultiHandlers() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		int messageCount = 11;
		DirectChannel channel = new DirectChannel();
		MockMessageHandler handler1 = new MockMessageHandler();
		MockMessageHandler handler2 = new MockMessageHandler();
		MockMessageHandler handler3 = new MockMessageHandler();
		channel.subscribe(handler1);
		channel.subscribe(handler2);
		channel.subscribe(handler3);

		for (int i = 0; i < messageCount; i++)
			assertTrue(channel.send(new GenericMessage<String>("test-" + i)));

		latch.await(1000, TimeUnit.MILLISECONDS);
		assertTrue((handler1.getMessages().indexOf("test-9") > 0));
		assertTrue((handler2.getMessages().indexOf("test-10") > 0));
		assertTrue((handler3.getMessages().indexOf("test-8") > 0));

		assertEquals(4, handler1.getMessageSize());
		assertEquals(4, handler2.getMessageSize());
		assertEquals(3, handler3.getMessageSize());

	}

	@Test
	public void testSendInSeparateThread() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		final DirectChannel channel = new DirectChannel();
		MockMessageHandler handler1 = new MockMessageHandler();
		MockMessageHandler handler2 = new MockMessageHandler();
		MockMessageHandler handler3 = new MockMessageHandler();
		channel.subscribe(handler1);
		channel.subscribe(handler2);
		channel.subscribe(handler3);

		int threads = 11;
		for (int i = 0; i < threads; i++) {
			final Message<String> message = new GenericMessage<String>("test-"
					+ i);
			new Thread(new Runnable() {
				public void run() {
					channel.send(message);
				}
			}, "test-thread").start();
		}

		latch.await(1000, TimeUnit.MILLISECONDS);

		assertTrue((handler1.getMessages().indexOf("test-9") > 0));
		assertTrue((handler2.getMessages().indexOf("test-10") > 0));
		assertTrue((handler3.getMessages().indexOf("test-8") > 0));

		assertEquals(4, handler1.getMessageSize());
		assertEquals(4, handler2.getMessageSize());
		assertEquals(3, handler3.getMessageSize());
	}

}
