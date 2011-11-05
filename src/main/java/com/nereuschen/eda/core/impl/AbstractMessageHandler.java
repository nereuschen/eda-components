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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nereuschen.eda.core.MessageHandler;
import com.nereuschen.eda.exception.MessageHandlingException;
import com.nereuschen.eda.exception.MessagingException;
import com.nereuschen.eda.message.Message;
import com.nereuschen.eda.util.Assert;
import com.nereuschen.eda.util.Orderable;
import com.nereuschen.eda.util.Ordered;

/**
 * Base class for MessageHandler implementations that provides basic validation
 * and error handling capabilities. Asserts that the incoming Message is not
 * null and that it does not contain a null payload. Converts checked exceptions
 * into runtime {@link MessagingException}s.
 * 
 * @author Mark Fisher
 * @author Oleg Zhurakousky
 * @author Nereus Chen (nereus.chen@gmail.com)
 */
public abstract class AbstractMessageHandler implements MessageHandler,
		Orderable {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
 
	private volatile int order = Ordered.LOWEST_PRECEDENCE;

	public void setOrder(int order) {
		this.order = order;
	}

	public int getOrder() {
		return this.order;
	}

	public String getComponentType() {
		return "message-handler";
	}

	public final void handleMessage(Message<?> message) {
		Assert.notNull(message, "Message must not be null");
		Assert.notNull(message.getPayload(), "Message payload must not be null");
		if (this.logger.isDebugEnabled()) {
			this.logger.debug(this + " received message: " + message);
		}
		try {

			this.handleMessageInternal(message);
		} catch (Exception e) {
			if (e instanceof MessagingException) {
				throw (MessagingException) e;
			}
			throw new MessageHandlingException(message,
					"error occurred in message handler [" + this + "]", e);
		}
	}

	protected abstract void handleMessageInternal(Message<?> message)
			throws Exception;

}
