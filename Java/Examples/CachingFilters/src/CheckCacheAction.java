import Composestar.Java.FLIRT.Actions.RTFilterAction;
import Composestar.Java.FLIRT.Annotations.FilterActionDef;
import Composestar.Java.FLIRT.Env.MessageState;
import Composestar.Java.FLIRT.Env.RTMessage;
import Composestar.Java.FLIRT.Interpreter.FilterExecutionContext;
import Composestar.Java.FLIRT.Interpreter.MessageFlow;

@FilterActionDef(name = "CheckCacheAction", resourceOperations = "target.read;selector.read;arg.read;cache.read;return.write")
public class CheckCacheAction extends RTFilterAction {

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Java.FLIRT.Actions.RTFilterAction#execute(Composestar.Java.FLIRT.Env.RTMessage,
	 *      Composestar.Java.FLIRT.Interpreter.FilterExecutionContext)
	 */
	@Override
	public void execute(RTMessage matchedMessage, FilterExecutionContext context) {
		if (!matchedMessage.hasState(MessageState.VOID_RETURN)) {
			try {
				Object result = CachingObject.getInstance().fetchValue(
						matchedMessage.getTarget().getObject(),
						matchedMessage.getSelector().getName(),
						matchedMessage.getArguments());
				if (result != null) {
					// cached result, return the value
					context.getMessage().setReturnValue(result);
					context.setMessageFlow(MessageFlow.RETURN);
				}
			} catch (IllegalStateException e) {
				// hmpf
			}
		}
	}

}
