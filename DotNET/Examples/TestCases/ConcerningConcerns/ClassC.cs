using System;
using Composestar.RuntimeCore.FLIRT.Message;

namespace ConcerningConcerns
{
	public class ClassC: TestsBase
	{
		public void test(ReifiedMessage rm)
		{
			report("test");
			Console.Out.WriteLine("Sender: {0}", rm.getSender());
			Console.Out.WriteLine("Target: {0}", rm.getTarget());

			try 
			{
				Console.Out.WriteLine("Sender invalid casted to ClassA: {0}", (ClassA) rm.getSender());
			}
			catch (InvalidCastException e)
			{
				Console.Out.WriteLine("Expected exception: "+e.Message);
			}

			try
			{
				Console.Out.WriteLine("Sender valid casted to ClassB: {0}", (ClassB) rm.getSender());
			}
			catch (InvalidCastException e)
			{
				Console.Out.WriteLine("Unexpected exception: "+e.Message);
			}
		}
	}
}
