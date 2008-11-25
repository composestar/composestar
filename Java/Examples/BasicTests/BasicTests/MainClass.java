package BasicTests;

import Composestar.Java.FLIRT.Actions.ErrorFilterException;

public class MainClass {
	public static void main(String[] args) {
		TypeTests typeTests = new TypeTests();
		System.out.println("Bool:		" + typeTests.dummyBool());
		System.out.println("Byte:		" + typeTests.dummyByte());
		System.out.println("Char:		" + typeTests.dummyChar());
		System.out.println("Int:		" + typeTests.dummyInt());
		System.out.println("Short:		" + typeTests.dummyShort());
		System.out.println("Long:		" + typeTests.dummyLong());
		System.out.println("Float:		" + typeTests.dummyFloat());
		System.out.println("Double:		" + typeTests.dummyDouble());
		System.out.println("String:		" + typeTests.dummyString());
		System.out.println("Interface:	" + typeTests.dummyInterface());

		System.out.println("");

		FilterTests filtTests = new FilterTests();
		filtTests.func1();
		filtTests.askForHelp();
		filtTests.doStuff();

		filtTests.makeError();
		filtTests.setProduceError(true);
		try {
			filtTests.makeError();
		} catch (Exception e) {
			System.out.println("An exception was raised.");
		}
		filtTests.setProduceError(false);

		filtTests.makeTrip();

		filtTests.makeOutsideTrip();

		FooBarQuux fbq = new FooBarQuux();
		try {
			fbq.foo(); // should not result in an exception
		} catch (ErrorFilterException e) {
			System.out.println("An ErrorFilterException was raised.");
		}
		try {
			// method introduced by a concern
			fbq.quux3();
		} catch (ErrorFilterException e) {
			System.out.println("An ErrorFilterException was raised.");
		}
		fbq.success();
	}
	
	public void nop() {
		int i = 0;
		switch (i) {
			case DummyTester.X:
				break;
			case DummyTester.Y:
				break;
		}
	}
}
