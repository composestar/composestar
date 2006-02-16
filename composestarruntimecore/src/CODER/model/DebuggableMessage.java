package Composestar.RuntimeCore.CODER.Model;

/**
 * Summary description for DebuggableMessage.
 */
public interface DebuggableMessage {
    public int getDirection();

    public static final int OUTGOING = 0;
    public static final int INCOMING = 1;
}
