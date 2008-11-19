import Composestar.Java.FLIRT.Actions.RTFilterAction;
import Composestar.Java.FLIRT.Annotations.FilterActionDef;
import Composestar.Java.FLIRT.Env.CpsArbitraryValue;
import Composestar.Java.FLIRT.Env.RTMessage;
import Composestar.Java.FLIRT.Interpreter.FilterExecutionContext;

@FilterActionDef(name = "LogCallAction", resourceOperations = "target.read;selector.read;arg.read;timer.write")
public class LogCallAction extends RTFilterAction {
	public static final String TIMER_PROP = "timer";

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Java.FLIRT.Actions.RTFilterAction#execute(Composestar.Java.FLIRT.Env.RTMessage,
	 *      Composestar.Java.FLIRT.Interpreter.FilterExecutionContext)
	 */
	@Override
	public void execute(RTMessage matchedMessage, FilterExecutionContext context) {

		System.err.println(createLogMessage(matchedMessage));
		context.getMessage().setProperty(TIMER_PROP,
				new CpsArbitraryValue(System.nanoTime()));
	}

	protected String createLogMessage(RTMessage matchedMessage) {
		StringBuilder sb = new StringBuilder();
		sb.append("[id:");
		sb.append(System.identityHashCode(matchedMessage.getTarget()
				.getObject()));
		sb.append("] ");
		sb.append(matchedMessage.getTarget().getObject().getClass().getName());
		sb.append('.');
		sb.append(matchedMessage.getSelector().getName());
		sb.append('(');
		int idx = 0;
		for (Object arg : matchedMessage.getArguments()) {
			if (idx > 0) {
				sb.append(", ");
			}
			++idx;
			sb.append(arg);
		}
		sb.append(')');
		if (matchedMessage.getReturnValue() != null) {
			sb.append(" = ");
			sb.append(matchedMessage.getReturnValue());
		}
		return sb.toString();
	}

}
