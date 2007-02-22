package Composestar.Core.FILTH;

import java.io.Serializable;
import java.util.*;

public class SyntacticOrderingConstraint implements Serializable
{
	private static final long serialVersionUID = -5700210313914764322L;

	private List postconstraints;

	private String left;

	public SyntacticOrderingConstraint(String inLeft)
	{
		left = inLeft;
		postconstraints = new ArrayList();
	}

	public void addRightFilterModule(String fm)
	{
		postconstraints.add(fm);
	}

	public Iterator getRightFilterModules()
	{
		return postconstraints.iterator();
	}

	public String getLeft()
	{
		return left;
	}

	public String toString()
	{
		return ("Constraint: " + left + " before " + postconstraints);
	}
}
