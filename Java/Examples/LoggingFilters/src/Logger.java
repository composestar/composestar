import java.io.PrintStream;

import Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage;

/**
 * 
 */
public class Logger {

	/**
	 * The log destination
	 */
	private static PrintStream output = System.err;

	/**
	 * Singleton construction
	 */
	private static Logger instance;

	public static Object getInstance() {
		if (instance == null) {
			instance = new Logger();
		}
		return instance;
	}

	/**
	 * @param str
	 */
	public static void setOutput(PrintStream str) {
		output = str;
	}

	/**
	 * Log around a message
	 * 
	 * @param message
	 */
	public void logAround(ReifiedMessage message) {
		long delta = System.nanoTime();
		if (output != null) {
			output.println(">> " + createLogMessage(message));
		}
		message.proceed();
		if (output != null) {
			output.println("<< [dt:" + (System.nanoTime() - delta) / 1000000
					+ "ms] " + createLogMessage(message));
		}
	}

	/**
	 * Create a string representation of the message
	 * 
	 * @param message
	 * @return
	 */
	public static final String createLogMessage(ReifiedMessage message) {
		StringBuilder sb = new StringBuilder();
		sb.append("[id:");
		sb.append(System.identityHashCode(message.getTarget()));
		sb.append("] ");
		sb.append(message.getTarget().getClass().getName());
		sb.append('.');
		sb.append(message.getSelector());
		sb.append('(');
		int idx = 0;
		for (Object arg : message.getArgs()) {
			if (idx > 0) {
				sb.append(", ");
			}
			++idx;
			sb.append(arg);
		}
		sb.append(')');
		if (message.getReturnValue() != null) {
			sb.append(" = ");
			sb.append(message.getReturnValue());
		}
		return sb.toString();
	}
}
