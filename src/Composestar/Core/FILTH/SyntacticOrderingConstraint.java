package Composestar.Core.FILTH;

import java.util.*;

public class SyntacticOrderingConstraint
{
	private ArrayList postconstraints;

	private String left;

	public SyntacticOrderingConstraint(String left)
	{
		this.left = left;
		this.postconstraints = new ArrayList();
	}

	public void addRightFilterModule(String fm)
	{
		this.postconstraints.add(fm);
	}

	public Iterator getRightFilterModules()
	{
		return this.postconstraints.iterator();
	}

	public String getLeft()
	{
		return this.left;
	}

	public String toString()
	{
		return ("Constraint: " + left + " before " + postconstraints);
	}
}
