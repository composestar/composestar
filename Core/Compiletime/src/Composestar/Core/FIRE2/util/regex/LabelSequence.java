/*
 * Created on 7-jun-2006
 *
 */
package Composestar.Core.FIRE2.util.regex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Sequence of lablels
 * 
 * @author Arjan de Roo
 */
public class LabelSequence
{
	private List<String> labels;

	public LabelSequence()
	{
		labels = new ArrayList<String>();
	}

	public void addLabel(String label)
	{
		labels.add(label);
	}

	public void addLabels(Collection<String> lbls)
	{
		labels.addAll(lbls);
	}

	/**
	 * @deprecated use getLabelsEx()
	 * @return
	 */
	@Deprecated
	public Iterator<String> getLabels()
	{
		return getLabelsEx().iterator();
	}

	public List<String> getLabelsEx()
	{
		return Collections.unmodifiableList(labels);
	}

	public boolean isEmpty()
	{
		return labels.isEmpty();
	}

	@Override
	public String toString()
	{
		return labels.toString();
	}
}
