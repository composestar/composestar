package Composestar.Core.FIRE;

public class Action extends FilterLeaf
{
	Filter filter;

	public Action(Filter _filter)
	{
		filter = _filter;
	}

	public Action(Filter _filter, int filterNumber)
	{
		filter = _filter;
		setFilterNumber(filterNumber);
	}

	public String toString()
	{
		return "action(" + filter + ')';
	}

	public Filter getFilter()
	{
		return filter;
	}

	public StatusColumn calculateStatus(StatusColumn status, StateTable stateTable)
	{
		// Debug.out(Debug.MODE_INFORMATION, "FIRE" , "Visiting action: " +
		// filter);
		return filter.calc(status, stateTable, this);
	}

	public ActionNode createNode()
	{
		return filter.createNode();
	}

}
