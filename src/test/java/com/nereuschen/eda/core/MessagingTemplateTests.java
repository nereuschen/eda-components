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

package com.nereuschen.eda.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.nereuschen.eda.Message;
import com.nereuschen.eda.channel.AbstractMessageChannel;
import com.nereuschen.eda.channel.DirectChannel;
import com.nereuschen.eda.channel.MessageChannel;
import com.nereuschen.eda.channel.QueueChannel;
import com.nereuschen.eda.exception.MessagingException;
import com.nereuschen.eda.handler.AbstractReplyProducingMessageHandler;
import com.nereuschen.eda.message.GenericMessage;
import com.nereuschen.eda.support.MessageBuilder;
import com.nereuschen.eda.support.converter.SimpleMessageConverter;

/**
 * @author Mark Fisher
 * @author Nereus Chen (nereus.chen@gmail.com)
 */
public class MessagingTemplateTests {

	@SuppressWarnings("unused")
	private QueueChannel requestChannel;

	@Before
	public void setUp() {
		this.requestChannel = new QueueChannel();
	}

	@After
	public void tearDown() {

	}

	@Test
	public void send() {
		MessagingTemplate template = new MessagingTemplate();
		QueueChannel channel = new QueueChannel();
		template.send(channel, new GenericMessage<String>("test"));
		Message<?> reply = channel.receive(0);
		assertNotNull(reply);
		assertEquals("test", reply.getPayload());
	}

	@Test
	public void sendWithDefaultChannelProvidedBySetter() {
		QueueChannel channel = new QueueChannel();
		MessagingTemplate template = new MessagingTemplate();
		template.setDefaultChannel(channel);
		template.send(new GenericMessage<String>("test"));
		Message<?> reply = channel.receive(0);
		assertNotNull(reply);
		assertEquals("test", reply.getPayload());
	}

	@Test
	public void sendWithDefaultChannelProvidedByConstructor() {
		QueueChannel channel = new QueueChannel();
		MessagingTemplate template = new MessagingTemplate(channel);
		template.send(new GenericMessage<String>("test"));
		Message<?> reply = channel.receive(0);
		assertNotNull(reply);
		assertEquals("test", reply.getPayload());
	}

	@Test
	public void sendWithExplicitChannelTakesPrecedenceOverDefault() {
		QueueChannel explicitChannel = new QueueChannel();
		QueueChannel defaultChannel = new QueueChannel();
		MessagingTemplate template = new MessagingTemplate(defaultChannel);
		template.send(explicitChannel, new GenericMessage<String>("test"));
		Message<?> reply = explicitChannel.receive(0);
		assertNotNull(reply);
		assertEquals("test", reply.getPayload());
		assertNull(defaultChannel.receive(0));
	}

	@Test(expected = IllegalStateException.class)
	public void sendWithoutChannelArgFailsIfNoDefaultAvailable() {
		MessagingTemplate template = new MessagingTemplate();
		template.send(new GenericMessage<String>("test"));
	}

	@Test
	public void receive() {
		QueueChannel channel = new QueueChannel();
		channel.send(new GenericMessage<String>("test"));
		MessagingTemplate template = new MessagingTemplate();
		Message<?> reply = template.receive(channel);
		assertEquals("test", reply.getPayload());
	}

	@Test
	public void receiveWithDefaultChannelProvidedBySetter() {
		QueueChannel channel = new QueueChannel();
		channel.send(new GenericMessage<String>("test"));
		MessagingTemplate template = new MessagingTemplate();
		template.setDefaultChannel(channel);
		Message<?> reply = template.receive();
		assertEquals("test", reply.getPayload());
	}

	@Test
	public void receiveWithDefaultChannelProvidedByConstructor() {
		QueueChannel channel = new QueueChannel();
		channel.send(new GenericMessage<String>("test"));
		MessagingTemplate template = new MessagingTemplate(channel);
		Message<?> reply = template.receive();
		assertEquals("test", reply.getPayload());
	}

	@Test
	public void receiveWithExplicitChannelTakesPrecedenceOverDefault() {
		QueueChannel explicitChannel = new QueueChannel();
		QueueChannel defaultChannel = new QueueChannel();
		explicitChannel.send(new GenericMessage<String>("test"));
		MessagingTemplate template = new MessagingTemplate(defaultChannel);
		template.setReceiveTimeout(0);
		Message<?> reply = template.receive(explicitChannel);
		assertEquals("test", reply.getPayload());
		assertNull(template.receive());
	}

	@Test(expected = IllegalStateException.class)
	public void receiveWithoutChannelArgFailsIfNoDefaultAvailable() {
		MessagingTemplate template = new MessagingTemplate();
		template.receive();
	}

	@Test(expected = IllegalStateException.class)
	public void receiveWithNonPollableDefaultFails() {
		DirectChannel channel = new DirectChannel();
		MessagingTemplate template = new MessagingTemplate(channel);
		template.receive();
	}

	@Test
	public void sendAndReceive() {
		DirectChannel channel = new DirectChannel();
		channel.subscribe(new ReplyHandler());
		MessagingTemplate template = new MessagingTemplate();
		template.setReceiveTimeout(3000);
		Message<String> message = new GenericMessage<String>("test");

		Message<?> reply = template.sendAndReceive(channel, message);
		assertEquals("TEST", reply.getPayload());
	}

	@Test
	public void sendAndReceiveWithDefaultChannel() {
		DirectChannel channel = new DirectChannel();
		channel.subscribe(new ReplyHandler());
		MessagingTemplate template = new MessagingTemplate();
		template.setReceiveTimeout(3000);
		template.setDefaultChannel(channel);
		Message<?> reply = template.sendAndReceive(new GenericMessage<String>(
				"test"));
		assertEquals("TEST", reply.getPayload());
	}

	@Test
	public void sendAndReceiveWithExplicitChannelTakesPrecedenceOverDefault() {
		DirectChannel defaultChannel = new DirectChannel();
		defaultChannel.subscribe(new ReplyHandler());
		MessagingTemplate template = new MessagingTemplate(defaultChannel);
		template.setReceiveTimeout(3000);
		Message<?> message = new GenericMessage<String>("test");
		Message<?> reply = template
				.sendAndReceive(defaultChannel, message);
		assertEquals("TEST", reply.getPayload());
		 
	}

	@Test(expected = IllegalStateException.class)
	public void sendAndReceiveWithoutChannelArgFailsIfNoDefaultAvailable() {
		MessagingTemplate template = new MessagingTemplate();
		template.sendAndReceive(new GenericMessage<String>("test"));
	}

	@Test
	public void convertSendAndReceive() {
		DirectChannel defaultChannel = new DirectChannel();
		defaultChannel.subscribe(new ReplyHandler());
		MessagingTemplate template = new MessagingTemplate();
		template.setReceiveTimeout(3000);
		Object result = template.convertSendAndReceive(defaultChannel,
				"test");
		assertNotNull(result);
		assertEquals("TEST", result);
	}

	@Test
	public void convertSendAndReceiveWithDefaultChannel() {
		DirectChannel defaultChannel = new DirectChannel();
		defaultChannel.subscribe(new ReplyHandler());
		MessagingTemplate template = new MessagingTemplate();
		template.setDefaultChannel(defaultChannel);
		template.setReceiveTimeout(3000);
		Object result = template.convertSendAndReceive("test");
		assertNotNull(result);
		assertEquals("TEST", result);
	}

	@Test
	public void sendWithReturnAddress() throws InterruptedException {
		DirectChannel defaultChannel = new DirectChannel();
		defaultChannel.subscribe(new ReplyHandler());
		final List<String> replies = new ArrayList<String>(3);
		final CountDownLatch latch = new CountDownLatch(3);
		MessageChannel replyChannel = new AbstractMessageChannel() {
			@Override
			protected boolean doSend(Message<?> message, long timeout) {
				replies.add((String) message.getPayload());
				latch.countDown();
				return true;
			}
		};
		MessagingTemplate template = new MessagingTemplate();
		Message<String> message1 = MessageBuilder.withPayload("test1")
				.setReplyChannel(replyChannel).build();
		Message<String> message2 = MessageBuilder.withPayload("test2")
				.setReplyChannel(replyChannel).build();
		Message<String> message3 = MessageBuilder.withPayload("test3")
				.setReplyChannel(replyChannel).build();
		template.send(defaultChannel, message1);
		template.send(defaultChannel, message2);
		template.send(defaultChannel, message3);
		latch.await(2000, TimeUnit.MILLISECONDS);
		assertEquals(0, latch.getCount());
		assertTrue(replies.contains("TEST1"));
		assertTrue(replies.contains("TEST2"));
		assertTrue(replies.contains("TEST3"));
	}

	@Test
	public void convertAndSendToChannel() {
		MessagingTemplate template = new MessagingTemplate();
		QueueChannel channel = new QueueChannel();
		template.convertAndSend(channel, "test");
		Message<?> reply = channel.receive(0);
		assertNotNull(reply);
		assertEquals("test", reply.getPayload());
	}

	@Test
	public void convertAndSendToDefaultChannel() {
		QueueChannel channel = new QueueChannel();
		MessagingTemplate template = new MessagingTemplate();
		template.setDefaultChannel(channel);
		template.convertAndSend("test");
		Message<?> reply = channel.receive(0);
		assertNotNull(reply);
		assertEquals("test", reply.getPayload());
	}

	@Test
	public void convertAndSendWithCustomConverter() {
		MessagingTemplate template = new MessagingTemplate();
		template.setMessageConverter(new SimpleMessageConverter());
		QueueChannel channel = new QueueChannel();
		template.convertAndSend(channel, "test");
		Message<?> reply = channel.receive(0);
		assertNotNull(reply);
		assertEquals("test", reply.getPayload());
	}

	@Test
	public void receiveAndConvertFromChannel() {
		MessagingTemplate template = new MessagingTemplate();
		QueueChannel channel = new QueueChannel();
		channel.send(new GenericMessage<String>("test"));
		Object result = template.receiveAndConvert(channel);
		assertNotNull(result);
		assertEquals("test", result);
	}

	@Test
	public void receiveAndConvertFromDefaultChannel() {
		QueueChannel channel = new QueueChannel();
		channel.send(new GenericMessage<String>("test"));
		MessagingTemplate template = new MessagingTemplate();
		template.setDefaultChannel(channel);
		Object result = template.receiveAndConvert();
		assertNotNull(result);
		assertEquals("test", result);
	}

	@Test
	public void receiveAndConvertWithCustomConverter() {
		MessagingTemplate template = new MessagingTemplate();
		template.setMessageConverter(new SimpleMessageConverter());
		QueueChannel channel = new QueueChannel();
		channel.send(new GenericMessage<String>("test"));
		Object result = template.receiveAndConvert(channel);
		assertNotNull(result);
		assertEquals("test", result);
	}

	@Test
	public void convertSendAndReceiveWithCustomConverter() {
		DirectChannel defaultChannel = new DirectChannel();
		defaultChannel.subscribe(new ReplyHandler());
		MessagingTemplate template = new MessagingTemplate();
		template.setDefaultChannel(defaultChannel);
		template.setMessageConverter(new SimpleMessageConverter());
		Object result = template.convertSendAndReceive("test");
		assertNotNull(result);
		assertEquals("TEST", result);
	}

	private class ReplyHandler implements MessageHandler {

		@Override
		public void handleMessage(Message<?> message) throws MessagingException {
			MessageChannel replyChannel = (MessageChannel) message.getHeaders()
					.getReplyChannel();
			replyChannel.send(new GenericMessage<String>(message.getPayload()
					.toString().toUpperCase()));
		}
	}

	@SuppressWarnings("unused")
	private static class TestHandler extends
			AbstractReplyProducingMessageHandler {

		@Override
		public Object handleRequestMessage(Message<?> message) {
			return message.getPayload().toString().toUpperCase();
		}
	}
}
