using System;

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

			CustomAttributes ca = new CustomAttributes();
			Console.Out.WriteLine("Default value for CustomAttributes.MyProperty is '{0}', default value is correct? {1}", ca.MyProperty, ca.MyProperty == MyEnumeration.Gigabytes);
			ca.MyProperty = MyEnumeration.Kilobytes;
			Console.Out.WriteLine("Value for CustomAttributes.MyProperty set to '"+MyEnumeration.Kilobytes+"', value now is '{0}' (check passed: {1})", ca.MyProperty, ca.MyProperty == MyEnumeration.Kilobytes);

			Console.Out.WriteLine("");

			MyValueType v = new MyValueType(1, 10);
			Console.Out.WriteLine("ValueType Test: minimum={0} (correct={1}), maximum={2} (correct={3})", v.Minimum, v.Minimum == 1, v.Maximum, v.Maximum == 10);

			Console.Out.WriteLine("");

            Console.Out.WriteLine("Argument tests for static and non-static method, see tracelog");
            MyLogger.GetInstance().Write("arg1");
            MyLogger.GetInstance().Write("arg1", 2);
            MyLogger.WriteLine("arg1");
            MyLogger.WriteLine("arg1", 2);

            Console.Out.WriteLine("");

			FilterTests filtTests = new FilterTests();
			filtTests.func1();
			filtTests.func2();
			filtTests.func3();
			Console.Out.WriteLine( filtTests.func4(3) );
			filtTests.func6();
			filtTests.func7("foo");
			filtTests.func8("non-static generic method");
			StaticFilterTests<string>.func1("static generic method");

			Console.Out.WriteLine("");
			int x;
			filtTests.func9(out x);
			Console.Out.WriteLine("value of X after calling func9:" + x);
			Console.Out.WriteLine("");

			filtTests.func10(ref x);
			Console.Out.WriteLine("value of X after calling func10:" + x);
			Console.Out.WriteLine("");


			Console.Out.WriteLine("");
			int y;
			StaticFilterTests<string>.func11(out y);
			Console.Out.WriteLine("value of Y after calling func11:" + y);
			Console.Out.WriteLine("");

			StaticFilterTests<string>.func12(ref y);
			Console.Out.WriteLine("value of Y after calling func12:" + y);
			Console.Out.WriteLine("");

			filtTests.askForHelp();
			filtTests.doStuff();

			filtTests.makeError();
			filtTests.setProduceError(true);
			try
			{
				filtTests.makeError();
			}
			catch (NotImplementedException)
			{
				Console.Out.WriteLine("A NotImplementedException was raised.");
			} 
			catch (Exception)
			{
				Console.Out.WriteLine("An exception was raised.");
			}
			filtTests.setProduceError(false);

			filtTests.makeTrip();

			filtTests.makeOutsideTrip();
			


			////// Conditional superimposition tests //////
			Console.WriteLine("#### Conditional superimposition tests ####");
			ConditionalSuperImpositionTests csiTests = new ConditionalSuperImpositionTests();
			// logging filter module enabled:
			Console.WriteLine("Logging enabled:");
			ConditionalSuperImpositionTests.isEnabled = true;
			csiTests.CondSIFunc1();
			// logging filter module disabled:
			Console.WriteLine("Logging disabled:");
			ConditionalSuperImpositionTests.isEnabled = false;
			csiTests.CondSIFunc1();

		}
	}
}
