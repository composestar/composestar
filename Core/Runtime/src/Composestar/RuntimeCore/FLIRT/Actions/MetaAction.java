package Composestar.RuntimeCore.FLIRT.Actions;

import Composestar.RuntimeCore.FLIRT.Message.Message;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage;
import Composestar.RuntimeCore.FLIRT.Reflection.JoinPoint;
import Composestar.RuntimeCore.FLIRT.Reflection.JoinPointInfo;
import Composestar.RuntimeCore.FLIRT.Reflection.JoinPointInfoProxy;
import Composestar.RuntimeCore.FLIRT.Reflection.MessageInfo;
import Composestar.RuntimeCore.FLIRT.Reflection.MessageInfoProxy;
import java.lang.Thread;

/**
 * Models the action that comes from the acceptance of a message by a Meta
 * filter. The execution of this action consists on getting the ACT method and
 * invoking it with the reified message as a parameter. Once the ACT method
 * returns, the reified method is checked to see if and how it was activated.
 * Depending on this: if the message was fired, it continues its normal path; if
 * it was Replied, the return value is saved, and the message exits the
 * filtering process; and if it was sent, the callback is saved, and the message
 * is allowed to move on to the next filter.
 * 
 * @author Carlos Noguera
 * @see Composestar.RuntimeCore.FLIRT.Filtertypes.Meta
 */
public class MetaAction extends ComposeStarAction
{

	/**
	 * The act object that should treat the reified message
	 */
	// Object actObject;
	/**
	 * The name of the method in the act that is to treat the reified message
	 */
	// String actMethod;
	/**
	 * The reified version of the message that is currently being filtered
	 */
	ReifiedMessage message;

	/**
	 * Constructs a Meta action. For this is needed a reified version of the
	 * message, an act and a method in it, and whether or not the message was
	 * accepted
	 * 
	 * @param rm the reified version of the message
	 * @param act the ACT to handle the reified message
	 * @param actSelector the name of the method that handles the reified
	 *            message
	 * @param accepted says if the message was accepted or not.
	 * @roseuid 40EAA5BD0265
	 * @param m
	 */
	public MetaAction(MessageList m, ReifiedMessage rm, Object act, String actSelector, boolean accepted)
	{
		super(m, accepted);

		Object[] args = new Object[1];
		args[0] = rm;

		// moved to ReifiedMessage
		// set act method info

		// actObject = act;
		// actMethod = actSelector;

		message = rm;

		this.message.setActMethodInfo(act, actSelector, args);

		this.continueMessage = m;
	}

	/**
	 * Returns the callback associated with the execution of this action.
	 * 
	 * @return a delegate for the ACT callback method if the reified message was
	 *         sent; null otherwise public ACTcallBackMethod getCallback() {
	 *         return callback; } Executes this MetaAction This process is
	 *         explained at the beginning of this class.
	 * @return depends on the handling of the reified message by the ACT method
	 * @see Composestar.RuntimeCore.FLIRT.Filtertypes.Meta
	 * @roseuid 40EAA5BD026D
	 */
	public Object execute()
	{
		// invoke the method

		// Invoker.getInstance().invoke(actObject, actMethod, args);

		// start execution in a new thread
		Thread t = new Thread(this.message);
		t.start();

		// Store jp and m in ACT thread
		JoinPoint __jp = JoinPointInfo.getJoinPointInfo();
		JoinPointInfoProxy.updateJoinPoint(t, __jp);
		Message __m = MessageInfo.getMessageInfo();
		MessageInfoProxy.updateMessage(t, __m);

		// now block until the ACT calls send, reply or fire on the
		// ReifiedMessage
		this.message.getContinueBuffer().consume();

		// sync stuff...
		this.continueMessage.copyFromMessageList(message.getMessageListCopy());
		// this.continueMessage.setSelector(message.getSelector());
		// this.continueMessage.setTarget(message.getTarget());
		// this.continueMessage.setArguments(message.getArgs());

		this.shouldContinue = this.message.shouldContinue();

		return this.message.getReturnValue();
	}

	public MessageList getMessageToContinueWith()
	{
		return this.continueMessage;
	}
}
