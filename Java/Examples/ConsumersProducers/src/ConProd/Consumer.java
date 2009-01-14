package ConProd;

public class Consumer implements Runnable {

	protected Buffer<Object> buffer;

	protected int consume;

	public Consumer(Buffer<Object> buf, int consumeItems) {
		buffer = buf;
		consume = consumeItems;
	}

	@Override
	public void run() {
		while (consume > 0) {
			while (buffer.isEmpty()) {
				Thread.yield();
			}
			try {
				buffer.consume();
			} catch (Exception e) {
				System.err.println("==> " + e.toString());
				return;
			}
			--consume;
		}
	}
}
