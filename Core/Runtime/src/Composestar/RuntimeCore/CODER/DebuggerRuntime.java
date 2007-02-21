package Composestar.RuntimeCore.CODER;

/**
 * DebuggerRuntime
 * The debugger runtime is the interface between the subject and the debugger
 */
public class DebuggerRuntime {
    private Profiler profiler;
    private Halter halter;
    private DesignTime designTime;
    private Publisher publisher;
    
   private DebuggerRuntime() {
       this.halter = new Halter();
       this.publisher = new Publisher();
       this.designTime = new DesignTime(this,this.publisher);
       this.publisher.setDesignTime(this.designTime);
       this.profiler = new Profiler(this.designTime);

       halter.resume();
   }

    public void suspend(){
        halter.suspend();
    }

    public void resume(){
        halter.resume();
    }

    /**
     * Thread safe singleton by using lazy loading of the class SingletonHolder
     */
   private static class SingletonHolder {
     private static DebuggerRuntime instance = new DebuggerRuntime();
   }

   public static DebuggerRuntime getInstance() {
     return SingletonHolder.instance;
   }

    public static void messageSent(Object sender, String selector, Object[] args, Object target){
        getInstance().messageSentEvent(sender,selector,args,target);
    }

    public static void filterRejectedMessage(Object sender, String selector, Object[] args, Object target){
        getInstance().filterRejectedMessageEvent(sender,selector,args,target);
    }

    public static void filterAcceptedMessage(Object sender, String selector, Object[] args, Object target){
        getInstance().filterAcceptedMessageEvent(sender,selector,args,target);
    }

    public static void messageDelivered(Object sender, String selector, Object[] args, Object target){
        getInstance().messageDeliveredEvent(sender,selector,args,target);
    }

    public static void messageReturn(Object sender, String selector, Object[] args, Object target){
        getInstance().messageReturnEvent(sender,selector,args,target);
    }

    public static void filterRejectedReturn(Object sender, String selector, Object[] args, Object target){
        getInstance().filterRejectedReturnEvent(sender,selector,args,target);
    }

    public static void filterAcceptedReturn(Object sender, String selector, Object[] args, Object target){
        getInstance().filterAcceptedReturnEvent(sender,selector,args,target);
    }

    public static void messageReturned(Object sender, String selector, Object[] args, Object target){
        getInstance().messageReturnedEvent(sender,selector,args,target);
    }

    public synchronized void messageSentEvent(Object sender, String selector, Object[] args, Object target){
        halter.checkHalt();
        profiler.messageSent(sender, selector, args,target);
    }

    public synchronized void filterRejectedMessageEvent(Object sender, String selector, Object[] args, Object target){
        halter.checkHalt();
        profiler.filterRejectedMessage(sender, selector, args,target);
    }

    public synchronized void filterAcceptedMessageEvent(Object sender, String selector, Object[] args, Object target){
        halter.checkHalt();
        profiler.filterAcceptedMessage(sender, selector, args,target);
    }

    public synchronized void messageDeliveredEvent(Object sender, String selector, Object[] args, Object target){
        halter.checkHalt();
        profiler.messageDelivered(sender, selector, args,target);
    }

    public synchronized void messageReturnEvent(Object sender, String selector, Object[] args, Object target){
        halter.checkHalt();
        profiler.messageReturn(sender, selector, args,target);
    }

    public synchronized void filterRejectedReturnEvent(Object sender, String selector, Object[] args, Object target){
        halter.checkHalt();
        profiler.filterRejectedReturn(sender, selector, args,target);
    }

    public synchronized void filterAcceptedReturnEvent(Object sender, String selector, Object[] args, Object target){
        halter.checkHalt();
        profiler.filterAcceptedReturn(sender, selector, args,target);
    }

    public synchronized void messageReturnedEvent(Object sender, String selector, Object[] args, Object target){
        halter.checkHalt();
        profiler.messageReturned(sender, selector, args,target);
    }
}
