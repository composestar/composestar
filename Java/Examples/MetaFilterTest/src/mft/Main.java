package mft;

public class Main {

	public static void testAndPrint(String name, String value, String expected) {
		System.out.print(name);
		System.out.print(": ");
		System.out.print(value);
		if (value != null && !value.equals(expected)) {
			System.out.print(String.format(" (Error! Expected: %s)", expected));
		} else if (value == null && expected != null) {
			System.out.print(" (Error! Expected null)");
		}
		System.out.println();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Meta Filter Tests");
		System.out.println();

		Subject s = new Subject();

		testAndPrint("doNothing ", s.doNothing(), Subject.RETURN_VALUE);
		testAndPrint("doProceed ", s.doProceed(), Subject.RETURN_VALUE);
		testAndPrint("doReply   ", s.doReply(), MetaTarget.RETURN_VALUE);
		testAndPrint("doReply2  ", s.doReply2(), null);
		testAndPrint("doRespond ", s.doRespond(), MetaTarget.RETURN_VALUE);
		testAndPrint("doRespond2", s.doRespond2(), null);
	}
}
