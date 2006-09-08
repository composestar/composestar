package Composestar.RuntimeCore.Utils;

import java.io.PrintStream;;

/**
 * Summary description for Debug.
 */
public final class Debug {
	public static boolean SHOULD_DEBUG = true;

	private static int _currentMode = 0;
	private static PrintStream _out = System.err;


	public static final int MODE_ERROR = 0;
	public static final int MODE_CRUCIAL = 1;
	public static final int MODE_WARNING = 2;
	public static final int MODE_INFORMATION = 3;
	public static final int MODE_DEBUG = 4;

	/**
	 * @param mode
	 * @roseuid 41161EA901B7
	 */
	public static void setMode(int mode) {
		_currentMode = mode;
		if (mode > 1)
			Debug.SHOULD_DEBUG = true;
	}

	/**
	 * @return int
	 * @roseuid 41161EA901B9
	 */
	public static int getMode() {
		return _currentMode;
	}

	public static void setOut(PrintStream stream){
		_out = stream;
	}
	
	/**
	 * Print debug information to standard error output.
	 * 
	 * @param mode
	 *            Debug mode in which the information should be visible.
	 * @param module
	 *            Module name where the message occurred.
	 * @param message
	 *            Actual message to display.
	 */
	public static final void out(int mode, String module, String message) {
		if (_currentMode >= mode) {
			String modeDescription;

			switch (mode) {
			case MODE_ERROR:
				modeDescription = "error";
				break;

			case MODE_INFORMATION:
				modeDescription = "info";
				break;

			case MODE_DEBUG:
				modeDescription = "debug";
				break;

			case MODE_CRUCIAL:
				modeDescription = "crucial";
				break;

			case MODE_WARNING:
				modeDescription = "warning";
				break;

			default:
				modeDescription = "unknown";
				break;
			}
			_out.print(module);
			_out.print(" (");
			_out.print(modeDescription);
			_out.print("): ");
			_out.println(message);
		}
	}

	// You can use this instead of System.out.printline while debugging and
	// still be able to find it in debug mode 5
	public static final void programmerOut(String module, String message) {
		System.err.println(module + ": " + message);
		try {
			System.in.reset();
			System.in.read();
		} catch (Exception e) {
		}
	}
}
