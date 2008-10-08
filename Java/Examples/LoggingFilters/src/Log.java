import java.util.Dictionary;

import Composestar.RuntimeCore.FLIRT.Actions.ComposeStarAction;
import Composestar.RuntimeCore.FLIRT.Actions.ContinueToNextFilterAction;
import Composestar.RuntimeCore.FLIRT.Actions.MetaAction;
import Composestar.RuntimeCore.FLIRT.Annotations.FilterActionAcceptCall;
import Composestar.RuntimeCore.FLIRT.Annotations.FilterActionAcceptReturn;
import Composestar.RuntimeCore.FLIRT.Filtertypes.CustomFilter;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;

/**
 * The log filter type
 */
@FilterActionAcceptCall(operations = "target.read;selector.read;arg.read")
@FilterActionAcceptReturn(operations = "target.read;selector.read;arg.read;return.read")
public class Log extends CustomFilter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.RuntimeCore.FLIRT.Filtertypes.FilterTypeRuntime#acceptAction(Composestar.RuntimeCore.FLIRT.Message.MessageList,
	 *      Composestar.RuntimeCore.FLIRT.Message.MessageList,
	 *      java.util.Dictionary)
	 */
	@Override
	public ComposeStarAction acceptAction(MessageList arg0, MessageList arg1,
			Dictionary arg2) {
		replaceInner(arg0, arg1);
		replaceWildcards(arg0, arg1);
		return new MetaAction(arg0, arg0.reify(), Logger.getInstance(),
				"logAround", true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.RuntimeCore.FLIRT.Filtertypes.FilterTypeRuntime#rejectAction(Composestar.RuntimeCore.FLIRT.Message.MessageList,
	 *      Composestar.RuntimeCore.FLIRT.Message.MessageList,
	 *      java.util.Dictionary)
	 */
	@Override
	public ComposeStarAction rejectAction(MessageList arg0, MessageList arg1,
			Dictionary arg2) {
		// do nothing when the message is rejected
		return new ContinueToNextFilterAction(arg0, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.RuntimeCore.FLIRT.Filtertypes.FilterTypeRuntime#shouldContinueAfterAccepting()
	 */
	@Override
	public boolean shouldContinueAfterAccepting() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.RuntimeCore.FLIRT.Filtertypes.CustomFilter#getName()
	 */
	@Override
	public String getName() {
		return "Log";
	}

}
