package Composestar.Visualization.Model.Cells.FlowChart;

import java.util.List;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction;
import Composestar.Core.FIRE2.model.ExecutionTransition;


/**
 * An special execution edge that contains an Message Change Behavior
 * annotation.
 * 
 * @author Michiel Hendriks
 */
public class MCBEdge extends ExecCollectionEdge
{
	private static final long serialVersionUID = -3406105925717936307L;

	public MCBEdge()
	{
		super();
	}

	public MCBEdge(ExecCollectionEdge base)
	{
		super(base);
	}

	public MCBEdge(List<ExecutionTransition> inExecList, List<FilterAction> inActions)
	{
		super(inExecList, inActions);
	}

}
