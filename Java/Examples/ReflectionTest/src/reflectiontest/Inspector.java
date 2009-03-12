/**
 * 
 */
package reflectiontest;

import java.util.Arrays;

import Composestar.Java.FLIRT.Env.JoinPointContext;
import Composestar.Java.FLIRT.Env.ReifiedMessage;
import Composestar.Java.FLIRT.Reflection.JoinPointInfo;
import Composestar.Java.FLIRT.Reflection.MessageInfo;

/**
 * The inspector class. It prints the current reflection information.
 */
public class Inspector implements TestMethods {
	private static int lastInspectorId = 0;

	public synchronized static final int getNewInspectorId() {
		return ++lastInspectorId;
	}

	protected int inspectorId = getNewInspectorId();

	public Inspector() {
	}

	/**
	 * @return The id of this inspector
	 */
	public int getInspectorId() {
		return inspectorId;
	}

	/**
	 * Print a reporting line
	 * 
	 * @param str
	 */
	protected void println(String str) {
		System.out
				.println(String.format("Inspector[%d]: %s", inspectorId, str));
	}

	/**
	 * Print a reporting line using a String.format(...) syntax
	 * 
	 * @param format
	 * @param args
	 */
	protected void println(String format, Object... args) {
		println(String.format(format, args));
	}

	protected String objectToString(Object obj) {
		StringBuilder sb = new StringBuilder();
		if (obj == null) {
			sb.append("null");
		} else {
			sb.append(obj.getClass().getName());
			// String tostr = obj.toString();
			// if (tostr != null) {
			// sb.append(" ");
			// tostr = tostr.trim().replaceAll("[\\p{Cntrl}]", "");
			// if (tostr.length() > 64) {
			// sb.append(tostr.substring(0, 64));
			// sb.append("...");
			// } else {
			// sb.append(tostr);
			// }
			// }
		}
		return sb.toString();
	}

	/**
	 * Report the current reflection data
	 */
	protected void report() {
		println("JoinPointInfo.getInstance():   %s",
				objectToString(JoinPointInfo.getInstance()));
		for (Object o : JoinPointInfo.getInternals()) {
			println("JoinPointInfo.getInternals()   %s", objectToString(o));
		}
		for (Object o : JoinPointInfo.getExternals()) {
			println("JoinPointInfo.getExternals()   %s", objectToString(o));
		}

		println("MessageInfo.getSender():       %s", objectToString(MessageInfo
				.getSender()));
		println("MessageInfo.getServer():       %s", objectToString(MessageInfo
				.getServer()));
		println("MessageInfo.getSelf():         %s", objectToString(MessageInfo
				.getSelf()));
		println("MessageInfo.getInner():        %s", objectToString(MessageInfo
				.getInner()));
		println("MessageInfo.getSelectorName(): %s", MessageInfo
				.getSelectorName());
		println("MessageInfo.getArguments():    %s", Arrays
				.toString(MessageInfo.getArguments()));
		println("");
	}

	// /////////////////////////////////////////////////////////////////////////
	// Reporting methods
	// /////////////////////////////////////////////////////////////////////////

	public void advice(JoinPointContext jpc) {
		report();
	}

	public void meta(ReifiedMessage rm) {
		report();
	}

	public void foo() {
		report();
	}

	public void bar(String s) {
		report();
	}

	public Object quux(int i, Object o) {
		report();
		return null;
	}
}
