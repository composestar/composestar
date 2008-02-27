import java.util.Dictionary;

import Composestar.RuntimeCore.FLIRT.Actions.ComposeStarAction;
import Composestar.RuntimeCore.FLIRT.Actions.ContinueToNextFilterAction;
import Composestar.RuntimeCore.FLIRT.Filtertypes.CustomFilter;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;

public class Invalidate extends CustomFilter {

	@Override
	public ComposeStarAction acceptAction(MessageList arg0, MessageList arg1,
			Dictionary arg2) {
		// TODO Auto-generated method stub
		return new ContinueToNextFilterAction(arg0, false);
	}

	@Override
	public ComposeStarAction rejectAction(MessageList arg0, MessageList arg1,
			Dictionary arg2) {
		return new ContinueToNextFilterAction(arg0, false);
	}

	@Override
	public boolean shouldContinueAfterAccepting() {
		return true;
	}

	@Override
	public String getName() {
		return "Invalidate";
	}

}
