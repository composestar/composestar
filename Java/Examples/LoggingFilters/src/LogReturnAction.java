import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Java.FLIRT.Annotations.FilterActionDef;
import Composestar.Java.FLIRT.Env.CpsArbitraryValue;
import Composestar.Java.FLIRT.Env.RTMessage;
import Composestar.Java.FLIRT.Interpreter.FilterExecutionContext;

@FilterActionDef(name = "LogReturnAction", resourceOperations = "target.read;selector.read;arg.read;return.read;timer.read")
public class LogReturnAction extends LogCallAction {

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Java.FLIRT.Actions.RTFilterAction#execute(Composestar.Java.FLIRT.Env.RTMessage,
	 *      Composestar.Java.FLIRT.Interpreter.FilterExecutionContext)
	 */
	@Override
	public void execute(RTMessage matchedMessage, FilterExecutionContext context) {
		long stop = System.nanoTime();
		CpsVariable value = context.getMessage().getProperty(TIMER_PROP);
		if (value != null && value instanceof CpsArbitraryValue) {
			Object val = ((CpsArbitraryValue) value).getValue();
			if (val instanceof Long) {
				stop = stop - ((Long) val).longValue();
			} else {
				stop = -1;
			}
		} else {
			stop = -1;
		}
		System.err.println("<< [dt:" + (System.nanoTime() - stop) / 1000000
				+ "ms] " + createLogMessage(matchedMessage, context));
	}
}
