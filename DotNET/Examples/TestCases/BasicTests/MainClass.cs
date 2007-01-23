using System;
using Composestar.RuntimeCore.FLIRT.Exception;

namespace BasicTests
{
	class MainClass
	{
		static void Main(string[] args)
		{
			TypeTests typeTests = new TypeTests();
			Console.Out.WriteLine("Bool:		{0}", typeTests.dummyBool());
			Console.Out.WriteLine("Byte:		{0}", typeTests.dummyByte());
			Console.Out.WriteLine("Int:		{0}", typeTests.dummyInt());
			Console.Out.WriteLine("UInt64:		{0}", typeTests.dummyUInt64());
			Console.Out.WriteLine("Float:		{0}", typeTests.dummyFloat());
			Console.Out.WriteLine("Double:		{0}", typeTests.dummyDouble());
			Console.Out.WriteLine("String:		{0}", typeTests.dummyString());
			Console.Out.WriteLine("Interface:	{0}", typeTests.dummyInterface());

			Console.Out.WriteLine("");

			FilterTests filtTests = new FilterTests();
			filtTests.func1();
			filtTests.askForHelp();
			filtTests.doStuff();

			filtTests.makeError();
			filtTests.setProduceError(true);
			try 
			{
				filtTests.makeError();
			}
			catch (Exception e)
			{
				Console.Out.WriteLine("An exception was raised.");
			}
			filtTests.setProduceError(false);

			filtTests.makeTrip();

			filtTests.makeOutsideTrip();


			FooBarQuux fbq = new FooBarQuux();
			try 
			{
				fbq.foo(); // should result in an exception
			}
			catch (ErrorFilterException e)
			{
				Console.Out.WriteLine("An ErrorFilterException was raised.");
			}
			try 
			{
				// method introduced by a concern
				fbq.quux2(); // should be quux3 but a bug in Fire or Sign prevents this
							 // because quux3 isn't added
			}
			catch (ErrorFilterException e)
			{
				Console.Out.WriteLine("An ErrorFilterException was raised.");
			}
			fbq.success();
		}
	}
}
