package Composestar.Core.FIRE;

public class TandNot extends FilterComposite
{
	public String toString()
	{
		return "TANDNOT";
	}

	// TODO: review
	public StatusColumn calculateStatus(StatusColumn status, StateTable stateTable)
	{

		// Debug.out(Debug.MODE_INFORMATION, "FIRE" ,"Visiting TandNot");
		fc1.calculateStatus(status, stateTable);

		StatusColumn a;
		try
		{
			a = (StatusColumn) status.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
			a = status;
		}

		// a'

		fc2.calculateStatus(status, stateTable);
		// a"

		Logic.not(status);
		Logic.and(status, a);
		// ~a" & a'

		return status;
	}
}
