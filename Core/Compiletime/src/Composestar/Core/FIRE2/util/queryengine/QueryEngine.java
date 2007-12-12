package Composestar.Core.FIRE2.util.queryengine;

import java.util.List;

import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;

/**
 * All query engines should implement this interface
 * 
 * @author Arjan
 */
public interface QueryEngine
{
	public List<ExecutionState> matchingStates(ExecutionModel model, Query query);
}
