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

import java.util.concurrent.Future;

import com.nereuschen.eda.channel.MessageChannel;
import com.nereuschen.eda.channel.PollableChannel;
import com.nereuschen.eda.message.Message;

/**
 * @author Mark Fisher
 * @author Nereus Chen (nereus.chen@gmail.com)
 * @since 2.0
 */
public interface AsyncMessagingOperations {

	Future<?> asyncSend(Message<?> message);

	Future<?> asyncSend(MessageChannel channel, Message<?> message);
 
	Future<?> asyncConvertAndSend(Object message);

	Future<?> asyncConvertAndSend(MessageChannel channel, Object message);
 
	Future<Message<?>> asyncReceive();

	Future<Message<?>> asyncReceive(PollableChannel channel);
 
	<R> Future<R> asyncReceiveAndConvert();

	<R> Future<R> asyncReceiveAndConvert(PollableChannel channel);
 
	Future<Message<?>> asyncSendAndReceive(Message<?> requestMessage);

	Future<Message<?>> asyncSendAndReceive(MessageChannel channel, Message<?> requestMessage);
 
	<R> Future<R> asyncConvertSendAndReceive(Object request);

	<R> Future<R> asyncConvertSendAndReceive(MessageChannel channel, Object request);
 
	<R> Future<R> asyncConvertSendAndReceive(Object request, MessagePostProcessor requestPostProcessor);

	<R> Future<R> asyncConvertSendAndReceive(MessageChannel channel, Object request, MessagePostProcessor requestPostProcessor);
 
}
