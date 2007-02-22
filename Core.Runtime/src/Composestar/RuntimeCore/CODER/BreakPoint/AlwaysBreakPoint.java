package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.ExecutionStackItem;

/**
 * Created by IntelliJ IDEA.
 * User: reddog
 * Date: 19-feb-2007
 * Time: 13:59:38
 * To change this template use File | Settings | File Templates.
 */
public class AlwaysBreakPoint extends BreakPoint{
    public boolean check(ExecutionStackItem status){
        return true;
    }
}
