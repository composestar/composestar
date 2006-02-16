package Composestar.RuntimeCore.Utils;

/**
 * Summary description for Assertion.
 */
public class Assertion
{
	private static final boolean ASSertions = false;
	
	public static final void pre(boolean cond, String failureText)
	{
		pre(cond,"ASSERTION",failureText);
	}

	public static final void pre(boolean cond, String module, String failureText)
	{
		if(ASSertions)
		{
			if(!cond)
			{
				Debug.out(Debug.MODE_ERROR,module,"precondition failed:" + failureText);
				System.exit(1);
			}
		}
	}

	public static final void post(boolean cond, String failureText)
	{
		post(cond,"ASSERTION",failureText);
	}

	public static final void post(boolean cond, String module, String failureText)
	{
		if(ASSertions)
		{
			if(!cond)
			{
				Debug.out(Debug.MODE_ERROR,module,"postcondition failed:" + failureText);
				System.exit(1);
			}
		}
	}

}
