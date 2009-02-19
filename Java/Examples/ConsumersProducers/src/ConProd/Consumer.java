package ConProd;

public class Consumer implements Runnable
{

	protected Buffer<Object> buffer;

	protected int consume;

	public Consumer(Buffer<Object> buf, int consumeItems)
	{
		buffer = buf;
		consume = consumeItems;
	}

	@Override
	public void run()
	{
		while (consume > 0)
		{
			while (buffer.isEmpty())
			{
				Thread.yield();
			}
			System.err.println(String.format("Consumer(%d)::run -- consuming object (%d left)", System
					.identityHashCode(this), consume - 1));
			try
			{
				buffer.consume();
			}
			catch (Exception e)
			{
				System.err.println(String.format("==> Consumer(%d) exception", System.identityHashCode(this)));
				e.printStackTrace();
				continue;
			}
			--consume;
		}
	}
}
