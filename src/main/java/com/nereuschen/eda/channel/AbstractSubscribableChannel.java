/*
 * Copyright 2002-2011 the original author or authors.
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

import java.util.concurrent.atomic.AtomicInteger;

import com.nereuschen.eda.Message;
import com.nereuschen.eda.core.MessageHandler;
import com.nereuschen.eda.core.SubscribableChannel;
import com.nereuschen.eda.dispatcher.MessageDispatcher;
import com.nereuschen.eda.dispatcher.UnicastingDispatcher;
import com.nereuschen.eda.util.Assert;

/**
 * Base implementation of {@link MessageChannel} that invokes the subscribed
 * {@link MessageHandler handler(s)} by delegating to a
 * {@link MessageDispatcher}.
 * 
 * @author Mark Fisher
 * @author Oleg Zhurakousky
 * @author Nereus Chen (nereus.chen@gmail.com)
 */
public abstract class AbstractSubscribableChannel extends
		AbstractMessageChannel implements SubscribableChannel {

	private final AtomicInteger handlerCounter = new AtomicInteger();

	public boolean subscribe(MessageHandler handler) {
		MessageDispatcher dispatcher = this.getRequiredDispatcher();
		boolean added = dispatcher.addHandler(handler);
		if (added) {
			int counter = handlerCounter.incrementAndGet();
			if (logger.isInfoEnabled()) {
				logger.info("Channel '" + this.getComponentName() + "' has "
						+ counter + " subscriber(s).");
			}
		}
		return added;
	}

	public boolean unsubscribe(MessageHandler handle) {
		if (this.getRequiredDispatcher() instanceof UnicastingDispatcher) {
			handlerCounter.getAndDecrement();
		}
		return this.getRequiredDispatcher().removeHandler(handle);
	}

	@Override
	protected boolean doSend(Message<?> message, long timeout) {
		return this.getRequiredDispatcher().dispatch(message);
	}

	private MessageDispatcher getRequiredDispatcher() {
		MessageDispatcher dispatcher = this.getDispatcher();
		Assert.state(dispatcher != null, "'dispatcher' must not be null");
		return dispatcher;
	}

	protected abstract MessageDispatcher getDispatcher();

}
