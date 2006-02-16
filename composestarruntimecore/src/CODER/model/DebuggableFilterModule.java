package Composestar.RuntimeCore.CODER.Model;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Summary description for DebuggableFilterModule.
 */
public interface DebuggableFilterModule {
    public boolean canAccept(DebuggableMessage message, Dictionary context);

    public ArrayList getDebuggableFilters(boolean directionInput);
}
