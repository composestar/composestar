package Composestar.RuntimeCore.CODER;

import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: reddog
 * Date: 18-feb-2007
 * Time: 21:05:03
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionStack extends Stack{
    public ExecutionStackItem into(Object sender, String selector, Object[] arg, Object target){
        ExecutionStackItem state = new ExecutionStackItem(sender,selector,arg,target);
        super.add(state);
        return state;
    }

    public ExecutionStackItem  out(){
        super.pop();
        return getNow();
    }

    public ExecutionStackItem getNow(){
        if(super.isEmpty()){
            Object[] args= {};
            ExecutionStackItem state = new ExecutionStackItem(null,"main",args,null);
            super.add(state);
            return state;
        }
        return (ExecutionStackItem) super.peek();
    }
}
