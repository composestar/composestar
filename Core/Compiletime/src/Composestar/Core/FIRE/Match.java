package Composestar.Core.FIRE;

public class Match extends FilterLeaf
{
	Symbol symbol = null;

	public Match(Symbol _symbol)
	{
		symbol = _symbol;
	}

	public Match(Symbol _symbol, int filterNumber)
	{
		symbol = _symbol;
		setFilterNumber(filterNumber);
	}

	public String toString()
	{
		return "match(" + symbol + ')';
	}

	// TODO: implement
	public StatusColumn calculateStatus(StatusColumn status, StateTable stateTable)
	{
		// Debug.out(Debug.MODE_INFORMATION, "FIRE" ,"Match start: " +
		// symbol.name);
		if (symbol.name.equals("*"))
		{
			return status;
		}

		Logic.and(status, symbol.column);

		// Debug.out(Debug.MODE_INFORMATION, "FIRE" ,"Match stop: " +
		// symbol.name);
		return status;
	}

	public ActionNode createNode()
	{
		return new FilterActionNode("Match");
	}

}
