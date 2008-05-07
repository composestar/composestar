package Composestar.Core.FILTH;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Defines an ordering constraint. All strings are fully qualified names for the
 * filter modules.
 */
public class SyntacticOrderingConstraint implements Serializable
{
	private static final long serialVersionUID = -5700210313914764322L;

	/**
	 * Filter modules that should be on the right
	 */
	private List<String> postconstraints;

	/**
	 * The filter module on the left side
	 */
	private String left;

	public SyntacticOrderingConstraint(String inLeft)
	{
		left = inLeft;
		postconstraints = new ArrayList<String>();
	}

	/**
	 * Add a filter module that should appear on the right hand side
	 * 
	 * @param fm
	 */
	public void addRightFilterModule(String fm)
	{
		postconstraints.add(fm);
	}

	/**
	 * @return the filter modules that should be on the right
	 */
	public Iterator<String> getRightFilterModules()
	{
		return postconstraints.iterator();
	}

	/**
	 * @return the filter module that is on the left hand side
	 */
	public String getLeft()
	{
		return left;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Constraint: " + left + " before " + postconstraints;
	}
}
