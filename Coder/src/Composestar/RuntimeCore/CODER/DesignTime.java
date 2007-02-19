package Composestar.RuntimeCore.CODER;

import Composestar.RuntimeCore.CODER.BreakPoint.BreakPoint;

/**
 * Created by IntelliJ IDEA.
 * User: reddog
 * Date: 18-feb-2007
 * Time: 21:27:44
 * To change this template use File | Settings | File Templates.
 */
public class DesignTime {
    private Publisher publisher;
    private BreakPoint breakpoint;
    private DebuggerRuntime runtime;

    public DesignTime (DebuggerRuntime runtime,Publisher publisher){
        this.runtime = runtime;
        this.publisher = publisher;
        this.breakpoint = publisher.getBreakPoint();
    }

    public void event(ExecutionStackItem state){
        if(breakpoint != null&&breakpoint.check(state)){
            runtime.suspend();
            publisher.hitTheBreaks(state);
        }
    }

    /**
     * Step to next
     */
    public void nextStep(){
       runtime.resume();
    }

    /**
     * Step into
     */
    public void stepInto(){
       runtime.resume();
    }

   /**
     * Step out
     */
    public void stepOut(){
       runtime.resume();
    }
}
