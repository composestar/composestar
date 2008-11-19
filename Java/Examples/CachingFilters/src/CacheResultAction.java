import Composestar.Java.FLIRT.Actions.RTFilterAction;
import Composestar.Java.FLIRT.Annotations.FilterActionDef;
import Composestar.Java.FLIRT.Env.MessageState;
import Composestar.Java.FLIRT.Env.RTMessage;
import Composestar.Java.FLIRT.Interpreter.FilterExecutionContext;

@FilterActionDef(name = "CacheResultAction", resourceOperations = "target.read;selector.read;arg.read;cache.write")
public class CacheResultAction extends RTFilterAction {

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
				CachingObject.getInstance().storeValue(
						matchedMessage.getTarget().getObject(),
						matchedMessage.getSelector().getName(),
						matchedMessage.getArguments(),
						context.getMessage().getReturnValue());
			} catch (IllegalStateException e) {
				// hmpf
			}
		}
	}

}
