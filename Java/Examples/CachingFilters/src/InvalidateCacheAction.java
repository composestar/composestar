import Composestar.Java.FLIRT.Actions.RTFilterAction;
import Composestar.Java.FLIRT.Annotations.FilterActionDef;
import Composestar.Java.FLIRT.Env.RTMessage;
import Composestar.Java.FLIRT.Interpreter.FilterExecutionContext;

@FilterActionDef(name = "InvalidateCacheAction", resourceOperations = "target.read;selector.read;arg.read;cache.clear")
public class InvalidateCacheAction extends RTFilterAction {

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Java.FLIRT.Actions.RTFilterAction#execute(Composestar.Java.FLIRT.Env.RTMessage,
	 *      Composestar.Java.FLIRT.Interpreter.FilterExecutionContext)
	 */
	@Override
	public void execute(RTMessage matchedMessage, FilterExecutionContext context) {
		try {
			CachingObject.getInstance().invalidate(
					matchedMessage.getTarget().getObject());
		} catch (IllegalStateException e) {
			// hmpf
		}
	}
}
