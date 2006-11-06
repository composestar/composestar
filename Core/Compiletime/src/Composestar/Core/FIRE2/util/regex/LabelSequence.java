/*
 * Created on 7-jun-2006
 *
 */
package Composestar.Core.FIRE2.util.regex;

import java.util.Enumeration;
import java.util.Vector;

/**
 * @author Arjan de Roo
 */
public class LabelSequence
{
	private Vector resourceOperations;

	public LabelSequence()
	{
		resourceOperations = new Vector();
	}

	public void addResourceOperation(String resourceOperation)
	{
		resourceOperations.add(resourceOperation);
	}

	public Enumeration getResourceOperationSequences()
	{
		return resourceOperations.elements();
	}

	public boolean isEmpty()
	{
		return resourceOperations.isEmpty();
	}
}
