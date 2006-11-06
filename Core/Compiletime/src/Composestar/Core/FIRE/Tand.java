package Composestar.Core.FIRE;

public class Tand extends FilterComposite
{
	public String toString()
	{
		return "TAND";
	}

	public StatusColumn calculateStatus(StatusColumn status, StateTable stateTable)
	{
		// Debug.out(Debug.MODE_INFORMATION, "FIRE" ,"Visiting Tand");
		fc1.calculateStatus(status, stateTable);
		fc2.calculateStatus(status, stateTable);

		return status;
	}
}
