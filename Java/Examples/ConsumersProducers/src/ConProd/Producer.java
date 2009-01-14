package ConProd;

public class Producer implements Runnable {

	protected Buffer<Object> buffer;

	protected int produce;

	public Producer(Buffer<Object> buf, int produceItems) {
		buffer = buf;
		produce = produceItems;
	}

	@Override
	public void run() {
		while (produce > 0) {
			// while (buffer.isFull()) {
			// Thread.yield();
			// }
			System.err.println("Producer::run -- producing new object");
			try {
				buffer.produce(new Object());
			} catch (Exception e) {
				System.err.println("==> " + e.toString());
				return;
			}
			--produce;
		}
	}
}
