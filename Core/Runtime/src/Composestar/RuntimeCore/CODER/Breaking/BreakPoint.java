package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.ExecutionStackItem;

/**
 * Created by IntelliJ IDEA.
 * User: reddog
 * Date: 18-feb-2007
 * Time: 23:14:46
 * To change this template use File | Settings | File Templates.
 */
public abstract class BreakPoint {
    public abstract boolean check(ExecutionStackItem status);
}
