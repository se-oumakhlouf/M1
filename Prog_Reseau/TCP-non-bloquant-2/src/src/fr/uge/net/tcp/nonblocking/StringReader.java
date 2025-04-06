package fr.uge.net.tcp.nonblocking;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;


public class StringReader implements Reader<String>{
	private enum State{
		DONE, WAITING_SIZE, WAITING_STRING, ERROR
	}
	
	private State state = State.WAITING_SIZE;
	private final IntReader intReader = new IntReader();
	private final ByteBuffer internalBuffer;
	private String value;
	private int size;
	
	public StringReader() {
		this.internalBuffer = ByteBuffer.allocate(1024);
	}
	
	@Override
	public ProcessStatus process(ByteBuffer buffer) {
		if(state == State.DONE || state == State.ERROR) {
			throw new IllegalStateException();
		}
		if(state == State.WAITING_SIZE) {
			var status = intReader.process(buffer);
			switch(status) {
			case DONE:
				size = intReader.get();
				if(size < 0 || size > 1024) {
					state = State.ERROR;
					return ProcessStatus.ERROR;
				}
				internalBuffer.clear();
				state = State.WAITING_STRING;
				intReader.reset();
				break;
			case REFILL:
				return ProcessStatus.REFILL;
			case ERROR:
				state = State.ERROR;
				return ProcessStatus.ERROR;
			}
		}
		if(state == State.WAITING_STRING) {
			buffer.flip();
			try {
				int needed = size - internalBuffer.position();
                int toRead = Math.min(needed, buffer.remaining());
				if(toRead > 0) {
					var slice = buffer.slice();
					slice.limit(toRead);
					internalBuffer.put(slice);
					buffer.position(buffer.position() + toRead);
				}
			}finally {
				buffer.compact();
			}
			if(internalBuffer.position() < size) {
				return ProcessStatus.REFILL;
			}
			state = State.DONE;
			internalBuffer.flip();
			value = StandardCharsets.UTF_8.decode(internalBuffer).toString();
			return ProcessStatus.DONE;
		}
		throw new AssertionError();
	}
	
	@Override
	public String get() {
		if(state != State.DONE) {
			throw new IllegalStateException();
		}
		return value;
	}
	
	@Override
	public void reset() {
		state = State.WAITING_SIZE;
		intReader.reset();
		internalBuffer.clear();
		value = null;
	}

}
