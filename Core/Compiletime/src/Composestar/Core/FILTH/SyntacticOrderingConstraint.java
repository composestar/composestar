package Composestar.Core.FILTH;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SyntacticOrderingConstraint implements Serializable
{
	private static final long serialVersionUID = -5700210313914764322L;

	private List<String> postconstraints;

	private String left;

	public SyntacticOrderingConstraint(String inLeft)
	{
		left = inLeft;
		postconstraints = new ArrayList<String>();
	}

	public void addRightFilterModule(String fm)
	{
		postconstraints.add(fm);
	}

	public Iterator<String> getRightFilterModules()
	{
		return postconstraints.iterator();
	}

	public String getLeft()
	{
		return left;
	}

	@Override
	public String toString()
	{
		return "Constraint: " + left + " before " + postconstraints;
	}
}
