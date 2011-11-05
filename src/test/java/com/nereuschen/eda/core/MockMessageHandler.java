package com.nereuschen.eda.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.nereuschen.eda.exception.MessagingException;
import com.nereuschen.eda.message.Message;

/*
 * @author Nereus Chen (nereus.chen@gmail.com)
 */
public class MockMessageHandler implements MessageHandler {

	@SuppressWarnings("rawtypes")
	private List<Message> messages = new CopyOnWriteArrayList<Message>();

	@Override
	public void handleMessage(Message<?> message) throws MessagingException {
		messages.add(message);
	}

	public int getMessageSize() {
		return messages.size();
	}

	public String getMessages() {
		return messages.toString();
	}
}
