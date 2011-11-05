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

import com.nereuschen.eda.exception.MessagingException;
import com.nereuschen.eda.message.Message;

/**
 * @author Mark Fisher
 * @author Nereus Chen (nereus.chen@gmail.com)
 */
@SuppressWarnings("serial")
public class MessageConversionException extends MessagingException {

	public MessageConversionException(String description, Throwable cause) {
		super(description, cause);
	}

	public MessageConversionException(Message<?> failedMessage, String description, Throwable cause) {
		super(failedMessage, description, cause);
	}

}
