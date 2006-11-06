/*
 * Created on 16-aug-2006
 *
 */
package Composestar.Core.FIRE2.util.queryengine.predicates;

import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.util.queryengine.Predicate;

public class IsState extends Predicate
{
	private ExecutionState state;

	public IsState(ExecutionState state)
	{
		this.state = state;
	}

	public void setState(ExecutionState state)
	{
		this.state = state;
	}

	public boolean isTrue(ExecutionState state)
	{
		if (state == null) return this.state == null;
		else return state.equals(this.state);
	}

	public String toString()
	{
		return super.toString() + "(isState)";
	}
}
