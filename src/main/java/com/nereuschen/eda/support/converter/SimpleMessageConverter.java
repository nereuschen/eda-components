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

package com.nereuschen.eda.support.converter;

import com.nereuschen.eda.Message;
import com.nereuschen.eda.support.MessageBuilder;

/**
 * @author Mark Fisher
 * @author Nereus Chen (nereus.chen@gmail.com)
 */
public class SimpleMessageConverter implements MessageConverter {

	@SuppressWarnings("unchecked")
	public <P> Message<P> toMessage(Object object) {
		try {
			if (object == null) {
				return null;
			}
			if (object instanceof Message<?>) {
				return (Message<P>) object;
			}
			return (Message<P>) MessageBuilder.withPayload(object).build();
		} catch (Exception e) {
			throw new MessageConversionException(
					"failed to convert object to Message", e);
		}
	}

	public <P> Object fromMessage(Message<P> message) {
		try {
			return (message != null) ? message.getPayload() : null;
		} catch (Exception e) {
			throw new MessageConversionException(message,
					"failed to convert Message to object", e);
		}
	}
}
