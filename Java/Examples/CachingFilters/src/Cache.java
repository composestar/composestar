import java.util.Dictionary;

import Composestar.RuntimeCore.FLIRT.Actions.ComposeStarAction;
import Composestar.RuntimeCore.FLIRT.Actions.ContinueToNextFilterAction;
import Composestar.RuntimeCore.FLIRT.Actions.MetaAction;
import Composestar.RuntimeCore.FLIRT.Annotations.FilterActionAcceptCall;
import Composestar.RuntimeCore.FLIRT.Annotations.FilterActionAcceptReturn;
import Composestar.RuntimeCore.FLIRT.Filtertypes.CustomFilter;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;

@FilterActionAcceptCall(operations = "target.read;selector.read;arg.read;cache.read;return.write")
@FilterActionAcceptReturn(operations = "target.read;selector.read;arg.read;cache.write")
public class Cache extends CustomFilter {

	@Override
	public ComposeStarAction acceptAction(MessageList arg0, MessageList arg1,
			Dictionary arg2) {
		replaceInner(arg0, arg1);
		replaceWildcards(arg0, arg1);
		return new MetaAction(arg0, arg0.reify(), CachingObject.getInstance(),
				"storeValue", true);
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
		return "Cache";
	}

}
