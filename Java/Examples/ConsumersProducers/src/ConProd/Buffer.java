package ConProd;

import java.util.Stack;

public class Buffer<T> {
	protected Stack<T> data;
	protected int maxSize;

	public Buffer(int size) {
		data = new Stack<T>();
		maxSize = size;
	}

	public void produce(T item) throws IndexOutOfBoundsException {
		if (data.size() >= maxSize) {
			throw new IndexOutOfBoundsException("Buffer is full");
		}
		data.add(item);
		System.err.println(String.format("Buffer::produce() new size: %d", data
				.size()));
	}

	public T consume() throws IndexOutOfBoundsException {
		if (data.isEmpty()) {
			throw new IndexOutOfBoundsException("Buffer is empty");
		}
		T result = data.pop();
		System.err.println(String.format("Buffer::consume() new size: %d", data
				.size()));
		return result;
	}

	public boolean isEmpty() {
		// System.err.println("Buffer::isEmpty()");
		return data.isEmpty();
	}

	public boolean isFull() {
		// System.err.println("Buffer::isFull()");
		return data.size() >= maxSize;
	}
}
