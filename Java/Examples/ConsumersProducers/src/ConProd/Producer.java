package ConProd;

public class Producer implements Runnable
{

	protected Buffer<Object> buffer;

	protected int produce;

	public Producer(Buffer<Object> buf, int produceItems)
	{
		buffer = buf;
		produce = produceItems;
	}

	@Override
	public void run()
	{
		while (produce > 0)
		{
			// while (buffer.isFull()) {
			// Thread.yield();
			// }
			System.err.println(String.format("Producer(%d)::run -- producing new object (%d left)", System
					.identityHashCode(this), produce - 1));
			try
			{
				buffer.produce(new Object());
			}
			catch (Exception e)
			{
				System.err.println(String.format("==> Producer(%d) exception", System.identityHashCode(this)));
				e.printStackTrace();
				continue;
			}
			--produce;
		}
	}
}
