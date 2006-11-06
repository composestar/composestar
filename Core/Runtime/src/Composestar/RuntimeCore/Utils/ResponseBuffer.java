package Composestar.RuntimeCore.Utils;

import java.util.Stack;

public class ResponseBuffer
{
	Stack buffers;

	SyncBuffer currentBuffer;

	SyncBuffer firstBuffer;

	public ResponseBuffer()
	{
		this.buffers = new Stack();
		this.currentBuffer = new SyncBuffer();
		this.firstBuffer = this.currentBuffer;
	}

	public void produceFirst(Object o)
	{
		this.firstBuffer.produce(o);
	}

	public void produce(Object o)
	{
		this.currentBuffer.produce(o);
	}

	public Object consume()
	{
		return this.currentBuffer.consume();
	}

	public void wrap()
	{
		this.buffers.push(currentBuffer);
		currentBuffer = new SyncBuffer();
	}

	public void unwrap()
	{
		this.currentBuffer = (SyncBuffer) this.buffers.pop();
	}

}
