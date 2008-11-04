/*
 * Created on 18-aug-2006
 *
 */
package Composestar.Core.INLINE.highlevel;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.LAMA.MethodInfo;

public interface HighLevelInlineStrategy
{
	public void startInline(FilterModule[] filterSet, MethodInfo method);

	public void endInline();

	public void startFilterModule(FilterModule module);

	public void endFilterModule();

	public void openBranchTrue(MatchingExpression condition);

	public void closeBranchTrue();

	public void openBranchFalse();

	public void closeBranchFalse();

	public void generateAction(ExecutionState state);
}
