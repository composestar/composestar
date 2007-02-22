package Composestar.RuntimeCore.CODER;

import java.util.HashMap;

/**
 * Profiler
 * The profiler keeps track on the execution of the subject by using the runtime.
 * The profilter contains the execution stack of the model.
 */
public class Profiler {
    private DesignTime designTime;
    private HashMap executionStacks = new HashMap();

    public Profiler(DesignTime designTime){
        this.designTime = designTime;
    }

    private ExecutionStack getExecutionStack(){
        return getExecutionStack(Thread.currentThread());
    }

    private synchronized ExecutionStack getExecutionStack(Thread thread){
        ExecutionStack stack = (ExecutionStack) executionStacks.get(thread);
        if(stack == null){
            stack = new ExecutionStack();
            executionStacks.put(Thread.currentThread(),stack);
        }
        return stack;
    }

    public void messageSent(Object sender, String selector, Object[] args, Object target){
        ExecutionStack stack = getExecutionStack();
        ExecutionStackItem state = stack.into(sender, selector, args, target);
        designTime.event(state);
    }

    public void filterRejectedMessage(Object sender, String selector, Object[] args, Object target){
        getCurrentState().filterRejectedMessage(sender, selector, args, target);

        ExecutionStack stack = getExecutionStack();
        ExecutionStackItem state = stack.into(sender, selector, args, target);
        designTime.event(state);
    }

    public void filterAcceptedMessage(Object sender, String selector, Object[] args, Object target){
        getCurrentState().filterAcceptedMessage(sender, selector, args, target);

        ExecutionStack stack = getExecutionStack();
        ExecutionStackItem state = stack.into(sender, selector, args, target);
        designTime.event(state);
    }

    public void messageDelivered(Object sender, String selector, Object[] args, Object target){
        ExecutionStack stack = getExecutionStack();
        ExecutionStackItem state = stack.into(sender, selector, args, target);
        designTime.event(state);
    }

    public void messageReturn(Object sender, String selector, Object[] args, Object target){
        ExecutionStack stack = getExecutionStack();
        ExecutionStackItem state = stack.into(sender, selector, args, target);
        designTime.event(state);
    }

    public void filterRejectedReturn(Object sender, String selector, Object[] args, Object target){
        getCurrentState().filterRejectedReturn(sender, selector, args);

        ExecutionStack stack = getExecutionStack();
        ExecutionStackItem state = stack.into(sender, selector, args, target);
        designTime.event(state);
    }

    public void filterAcceptedReturn(Object sender, String selector, Object[] args, Object target){
        getCurrentState().filterAcceptedReturn(sender, selector, args);

        ExecutionStack stack = getExecutionStack();
        ExecutionStackItem state = stack.into(sender, selector, args, target);
        designTime.event(state);
    }

    public void messageReturned(Object sender, String selector, Object[] args, Object target){
        ExecutionStack stack = getExecutionStack();
        stack.out();

        stack = getExecutionStack();
        ExecutionStackItem state = stack.into(sender, selector, args, target);
        designTime.event(state);
    }

    public ExecutionStackItem getCurrentState(){
        return getCurrentState(Thread.currentThread());
    }

    public ExecutionStackItem getCurrentState(Thread thread){
        return getExecutionStack(thread).getNow();
    }
}
