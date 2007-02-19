/**
 * Created by IntelliJ IDEA.
 * User: reddog
 * Date: 18-feb-2007
 * Time: 20:15:38
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    public static void main(String[] arg){
        Object sender = "Sender";
        Object target = "Target";
        String selector = "call";
        Object[] args = {};

        Composestar.RuntimeCore.CODER.DebuggerRuntime.messageSent(sender,selector,args,target);
        Composestar.RuntimeCore.CODER.DebuggerRuntime.filterAcceptedMessage(sender,selector,args,target);
        Composestar.RuntimeCore.CODER.DebuggerRuntime.filterRejectedMessage(sender,selector,args,target);
        Composestar.RuntimeCore.CODER.DebuggerRuntime.messageDelivered(sender,selector,args,target);
        Composestar.RuntimeCore.CODER.DebuggerRuntime.messageReturn(sender,selector,args,target);
        Composestar.RuntimeCore.CODER.DebuggerRuntime.filterAcceptedReturn(sender,selector,args,target);
        Composestar.RuntimeCore.CODER.DebuggerRuntime.filterRejectedReturn(sender,selector,args,target);
        Composestar.RuntimeCore.CODER.DebuggerRuntime.messageReturned(sender,selector,args,target);
    }
}
