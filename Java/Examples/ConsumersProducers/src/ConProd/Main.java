package ConProd;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Buffer<Object> buffer = new Buffer<Object>(3);
		Thread p1 = new Thread(new Producer(buffer, 10));
		p1.start();
		
		Thread p2 = new Thread(new Producer(buffer, 10));
		p2.start();
		
		Thread c1 = new Thread(new Consumer(buffer, 20));
		c1.start();
	}

}
