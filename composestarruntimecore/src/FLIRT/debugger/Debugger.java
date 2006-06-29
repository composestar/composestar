package Composestar.RuntimeCore.FLIRT.Debugger;

/**
 * Summary description for Debugger.
 */
public interface Debugger {
	public static final int FILTER_ACCEPTED = 0;
	public static final int FILTER_REJECTED = 1;
	public static final int FILTER_EVALUATION_START = 2;
	public static final int MESSAGE_INTERCEPTED = 3;
	public static final int MESSAGE_PROCESSING_START = 4;

    public void start();

    public void stop();

    public void reset();

}
