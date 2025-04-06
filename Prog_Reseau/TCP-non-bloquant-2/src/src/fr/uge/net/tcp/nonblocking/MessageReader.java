package fr.uge.net.tcp.nonblocking;

import java.nio.ByteBuffer;

public class MessageReader implements Reader<Message> {
	private enum State {
		DONE, WAITING_LOGIN_SIZE, WAITING_LOGIN, WAITING_TEXT_SIZE, WAITING_TEXT, ERROR
	}

	private State state = State.WAITING_LOGIN_SIZE;
	private final StringReader loginReader = new StringReader();
	private final StringReader textReader = new StringReader();
	private Message value;

	@Override
	public ProcessStatus process(ByteBuffer buffer) {
		if (state == State.DONE || state == State.ERROR) {
			throw new IllegalStateException();
		}
		try {
			while (true) {
				switch (state) {
					case WAITING_LOGIN_SIZE:
					case WAITING_LOGIN:
						var loginStatus = processLogin(buffer);
						if (loginStatus != ProcessStatus.DONE) {
							return loginStatus;
						}
						state = State.WAITING_TEXT_SIZE;
						break;
					case WAITING_TEXT_SIZE:
					case WAITING_TEXT:
						var textStatus = processText(buffer);
						if (textStatus != ProcessStatus.DONE) {
							return textStatus;
						}
						state = State.DONE;
						value = new Message(loginReader.get(), textReader.get());
						return ProcessStatus.DONE;
					default:
						throw new AssertionError();
				}
			}
		} catch (IllegalStateException e) {
			state = State.ERROR;
			return ProcessStatus.ERROR;
		}
	}

	private ProcessStatus processLogin(ByteBuffer buffer) {
		var status = loginReader.process(buffer);
		if (status == ProcessStatus.ERROR) {
			state = State.ERROR;
		}
		return status;
	}

	private ProcessStatus processText(ByteBuffer buffer) {
		var status = textReader.process(buffer);
		if (status == ProcessStatus.ERROR) {
			state = State.ERROR;
		}
		return status;
	}

	@Override
	public Message get() {
		if (state != State.DONE)
			throw new IllegalArgumentException();
		return value;
	}

	@Override
	public void reset() {
		state = State.WAITING_LOGIN_SIZE;
		loginReader.reset();
		textReader.reset();
		value = null;
	}

}
