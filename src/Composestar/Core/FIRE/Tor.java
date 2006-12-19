package Composestar.Core.FIRE;

public class Tor extends FilterComposite
{
	public String toString()
	{
		return "TOR";
	}

	public StatusColumn calculateStatus(StatusColumn status, StateTable stateTable)
	{
		// Debug.out(Debug.MODE_INFORMATION, "FIRE" , "TOR");

		// Debug.out (3, "TOR start ");
		// a | | (b | c)
		//
		// [ TOR ]
		//
		// a / / b (a & ~b)\ \ c

		// a
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

		// b
		fc1.calculateStatus(status, stateTable);

		StatusColumn b;
		try
		{
			b = (StatusColumn) status.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
			b = status;
		}

		// (a & ~b)
		Logic.not(status);
		Logic.and(status, a);

		fc2.calculateStatus(status, stateTable);

		// (b | c)
		Logic.or(status, b);

		// Debug.out (3, "TOR stop ");
		return status;
	}
}
