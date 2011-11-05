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

package com.nereuschen.eda.core.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.nereuschen.eda.channel.MessageChannel;
import com.nereuschen.eda.channel.PollableChannel;
import com.nereuschen.eda.core.AsyncMessagingOperations;
import com.nereuschen.eda.core.MessagePostProcessor;
import com.nereuschen.eda.message.Message;
import com.nereuschen.eda.util.Assert;

/**
 * @author Mark Fisher
 * @author Nereus Chen (nereus.chen@gmail.com)
 * @since 2.0
 */
public class AsyncMessagingTemplate extends MessagingTemplate implements
		AsyncMessagingOperations {

	private volatile ExecutorService executor = Executors.newFixedThreadPool(2);

	public void setExecutor(Executor executor) {
		Assert.notNull(executor, "executor must not be null");
		this.executor = (ExecutorService) executor;
	}

	public Future<?> asyncSend(final Message<?> message) {
		return this.executor.submit(new Runnable() {
			public void run() {
				send(message);
			}
		});
	}

	public Future<?> asyncSend(final MessageChannel channel,
			final Message<?> message) {
		return this.executor.submit(new Runnable() {
			public void run() {
				send(channel, message);
			}
		});
	}

	public Future<?> asyncConvertAndSend(final Object object) {
		return this.executor.submit(new Runnable() {
			public void run() {
				convertAndSend(object);
			}
		});
	}

	public Future<?> asyncConvertAndSend(final MessageChannel channel,
			final Object object) {
		return this.executor.submit(new Runnable() {
			public void run() {
				convertAndSend(channel, object);
			}
		});
	}

	public Future<Message<?>> asyncReceive() {
		return this.executor.submit(new Callable<Message<?>>() {
			public Message<?> call() throws Exception {
				return receive();
			}
		});
	}

	public Future<Message<?>> asyncReceive(final PollableChannel channel) {
		return this.executor.submit(new Callable<Message<?>>() {
			public Message<?> call() throws Exception {
				return receive(channel);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <R> Future<R> asyncReceiveAndConvert() {
		return this.executor.submit(new Callable<R>() {
			public R call() throws Exception {
				return (R) receiveAndConvert();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <R> Future<R> asyncReceiveAndConvert(final PollableChannel channel) {
		return this.executor.submit(new Callable<R>() {
			public R call() throws Exception {
				return (R) receiveAndConvert(channel);
			}
		});
	}

	public Future<Message<?>> asyncSendAndReceive(
			final Message<?> requestMessage) {
		return this.executor.submit(new Callable<Message<?>>() {
			public Message<?> call() throws Exception {
				return sendAndReceive(requestMessage);
			}
		});
	}

	public Future<Message<?>> asyncSendAndReceive(final MessageChannel channel,
			final Message<?> requestMessage) {
		return this.executor.submit(new Callable<Message<?>>() {
			public Message<?> call() throws Exception {
				return sendAndReceive(channel, requestMessage);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <R> Future<R> asyncConvertSendAndReceive(final Object request) {
		return this.executor.submit(new Callable<R>() {
			public R call() throws Exception {
				return (R) convertSendAndReceive(request);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <R> Future<R> asyncConvertSendAndReceive(
			final MessageChannel channel, final Object request) {
		return this.executor.submit(new Callable<R>() {
			public R call() throws Exception {
				return (R) convertSendAndReceive(channel, request);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <R> Future<R> asyncConvertSendAndReceive(final Object request,
			final MessagePostProcessor requestPostProcessor) {
		return this.executor.submit(new Callable<R>() {
			public R call() throws Exception {
				return (R) convertSendAndReceive(request, requestPostProcessor);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <R> Future<R> asyncConvertSendAndReceive(
			final MessageChannel channel, final Object request,
			final MessagePostProcessor requestPostProcessor) {
		return this.executor.submit(new Callable<R>() {
			public R call() throws Exception {
				return (R) convertSendAndReceive(channel, request,
						requestPostProcessor);
			}
		});
	}
}
