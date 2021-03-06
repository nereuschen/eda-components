/* Copyright 2002-2009 the original author or authors.
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

package com.nereuschen.eda.dispatcher;

import java.util.Iterator;
import java.util.List;

import com.nereuschen.eda.core.MessageHandler;
import com.nereuschen.eda.message.Message;
 
/**
 * Strategy for determining the iteration order of a MessageHandler list.
 * 
 * @author Mark Fisher
 * @author Nereus Chen (nereus.chen@gmail.com)
 */
public interface LoadBalancingStrategy {

	public Iterator<MessageHandler> getHandlerIterator(Message<?> message, List<MessageHandler> handlers);

}
