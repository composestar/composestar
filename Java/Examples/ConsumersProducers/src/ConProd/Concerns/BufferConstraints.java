package ConProd.Concerns;

import Composestar.Java.FLIRT.Env.ReifiedMessage;
import ConProd.Buffer;

public class BufferConstraints {

	// TODO: this isn't thread safe

	public void produceEvent(ReifiedMessage msg) {
		System.err.println("Notifier::produceEvent");
		Buffer<?> buffer = (Buffer<?>) msg.getTarget();
		while (buffer.isFull()) {
			Thread.yield();
		}
	}

	public void consumeEvent(ReifiedMessage msg) {
		System.err.println("Notifier::consumeEvent");
		Buffer<?> buffer = (Buffer<?>) msg.getTarget();
		while (buffer.isEmpty()) {
			Thread.yield();
		}
	}
}
