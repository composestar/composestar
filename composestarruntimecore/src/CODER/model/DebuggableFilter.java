package Composestar.RuntimeCore.CODER.Model;

import java.util.Dictionary;

/**
 * Summary description for DebuggableFilter.
 */
public interface DebuggableFilter {
    public boolean canAccept(DebuggableMessage message, Dictionary context);

    public DebuggableFilterType getDebuggableFilterType();

	public String getDeclerationFileName();
	public int getDeclerationLineNumber();
}
