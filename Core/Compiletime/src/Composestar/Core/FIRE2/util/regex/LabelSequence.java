/*
 * Created on 7-jun-2006
 *
 */
package Composestar.Core.FIRE2.util.regex;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Arjan de Roo
 */
public class LabelSequence
{
	private ArrayList<String> labels;

	public LabelSequence()
	{
		labels = new ArrayList<String>();
	}

	public void addLabel(String label)
	{
		labels.add(label);
	}

	public Iterator<String> getLabels()
	{
		return labels.iterator();
	}

	public boolean isEmpty()
	{
		return labels.isEmpty();
	}
}
