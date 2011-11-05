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

package com.nereuschen.eda.channel.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nereuschen.eda.channel.MessageChannel;
import com.nereuschen.eda.channel.interceptor.ChannelInterceptor;
import com.nereuschen.eda.exception.MessageDeliveryException;
import com.nereuschen.eda.exception.MessagingException;
import com.nereuschen.eda.message.Message;
import com.nereuschen.eda.util.Assert;

/**
 * Base class for {@link MessageChannel} implementations providing common
 * properties such as the channel name. Also provides the common functionality
 * for sending and receiving {@link Message Messages} including the invocation
 * of any {@link ChannelInterceptor ChannelInterceptors}.
 * 
 * @author Mark Fisher
 * @author Oleg Zhurakousky
 * @author Nereus Chen (nereus.chen@gmail.com)
 */
public abstract class AbstractMessageChannel implements MessageChannel {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@SuppressWarnings("unused")
    private volatile Class<?>[] datatypes = new Class<?>[] { Object.class };

	private final ChannelInterceptorList interceptors = new ChannelInterceptorList();

	private String componentName;

	public String getComponentType() {
		return "channel";
	}

	/**
	 * Specify the Message payload datatype(s) supported by this channel. If a
	 * payload type does not match directly, but the 'conversionService' is
	 * available, then type conversion will be attempted in the order of the
	 * elements provided in this array.
	 * <p>
	 * If this property is not set explicitly, any Message payload type will be
	 * accepted.
	 * 
	 * @see #setConversionService(ConversionService)
	 */
	public void setDatatypes(Class<?>... datatypes) {
		this.datatypes = (datatypes != null && datatypes.length > 0) ? datatypes
				: new Class<?>[] { Object.class };
	}

	/**
	 * Set the list of channel interceptors. This will clear any existing
	 * interceptors.
	 */
	public void setInterceptors(List<ChannelInterceptor> interceptors) {
		this.interceptors.set(interceptors);
	}

	/**
	 * Add a channel interceptor to the end of the list.
	 */
	public void addInterceptor(ChannelInterceptor interceptor) {
		this.interceptors.add(interceptor);
	}

	/**
	 * Exposes the interceptor list for subclasses.
	 */
	protected ChannelInterceptorList getInterceptors() {
		return this.interceptors;
	}

	/**
	 * Send a message on this channel. If the channel is at capacity, this
	 * method will block until either space becomes available or the sending
	 * thread is interrupted.
	 * 
	 * @param message
	 *            the Message to send
	 * 
	 * @return <code>true</code> if the message is sent successfully or
	 *         <code>false</code> if the sending thread is interrupted.
	 */
	public final boolean send(Message<?> message) {
		return this.send(message, -1);
	}

	/**
	 * Send a message on this channel. If the channel is at capacity, this
	 * method will block until either the timeout occurs or the sending thread
	 * is interrupted. If the specified timeout is 0, the method will return
	 * immediately. If less than zero, it will block indefinitely (see
	 * {@link #send(Message)}).
	 * 
	 * @param message
	 *            the Message to send
	 * @param timeout
	 *            the timeout in milliseconds
	 * 
	 * @return <code>true</code> if the message is sent successfully,
	 *         <code>false</code> if the message cannot be sent within the
	 *         allotted time or the sending thread is interrupted.
	 */
	public final boolean send(Message<?> message, long timeout) {
		Assert.notNull(message, "message must not be null");
		Assert.notNull(message.getPayload(), "message payload must not be null");
		message = this.interceptors.preSend(message, this);
		if (message == null) {
			return false;
		}
		try {
			boolean sent = this.doSend(message, timeout);
			this.interceptors.postSend(message, this, sent);
			return sent;
		} catch (Exception e) {
			if (e instanceof MessagingException) {
				throw (MessagingException) e;
			}
			throw new MessageDeliveryException(message,
					"failed to send Message to channel '"
							+ this.getComponentName() + "'", e);
		}
	}

	protected String getComponentName() {
		return componentName;
	}

	/**
	 * Subclasses must implement this method. A non-negative timeout indicates
	 * how long to wait if the channel is at capacity (if the value is 0, it
	 * must return immediately with or without success). A negative timeout
	 * value indicates that the method should block until either the message is
	 * accepted or the blocking thread is interrupted.
	 */
	protected abstract boolean doSend(Message<?> message, long timeout);

	/**
	 * A convenience wrapper class for the list of ChannelInterceptors.
	 */
	protected class ChannelInterceptorList {

		private final List<ChannelInterceptor> interceptors = new CopyOnWriteArrayList<ChannelInterceptor>();

		public boolean set(List<ChannelInterceptor> interceptors) {
			synchronized (this.interceptors) {
				this.interceptors.clear();
				return this.interceptors.addAll(interceptors);
			}
		}

		public boolean add(ChannelInterceptor interceptor) {
			return this.interceptors.add(interceptor);
		}

		public Message<?> preSend(Message<?> message, MessageChannel channel) {
			if (logger.isDebugEnabled()) {
				logger.debug("preSend on channel '" + channel + "', message: "
						+ message);
			}
			for (ChannelInterceptor interceptor : interceptors) {
				message = interceptor.preSend(message, channel);
				if (message == null) {
					return null;
				}
			}
			return message;
		}

		public void postSend(Message<?> message, MessageChannel channel,
				boolean sent) {
			if (logger.isDebugEnabled()) {
				logger.debug("postSend (sent=" + sent + ") on channel '"
						+ channel + "', message: " + message);
			}
			for (ChannelInterceptor interceptor : interceptors) {
				interceptor.postSend(message, channel, sent);
			}
		}

		public boolean preReceive(MessageChannel channel) {
			if (logger.isTraceEnabled()) {
				logger.trace("preReceive on channel '" + channel + "'");
			}
			for (ChannelInterceptor interceptor : interceptors) {
				if (!interceptor.preReceive(channel)) {
					return false;
				}
			}
			return true;
		}

		public Message<?> postReceive(Message<?> message, MessageChannel channel) {
			if (message != null && logger.isDebugEnabled()) {
				logger.debug("postReceive on channel '" + channel
						+ "', message: " + message);
			} else if (logger.isTraceEnabled()) {
				logger.trace("postReceive on channel '" + channel
						+ "', message is null");
			}
			for (ChannelInterceptor interceptor : interceptors) {
				message = interceptor.postReceive(message, channel);
				if (message == null) {
					return null;
				}
			}
			return message;
		}
	}
}
