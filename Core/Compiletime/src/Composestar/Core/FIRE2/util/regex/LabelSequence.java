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
 * Sequence of labels, used for resource operations
 * 
 * @author Arjan de Roo
 */
public class LabelSequence
{
	/**
	 * List of labels
	 */
	private List<String> labels;

	public LabelSequence()
	{
		labels = new ArrayList<String>();
	}

	/**
	 * Add a label to this sequence
	 * 
	 * @param label
	 */
	public void addLabel(String label)
	{
		labels.add(label);
	}

	/**
	 * Add a collection of labels
	 * 
	 * @param lbls
	 */
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

	/**
	 * @return the labels
	 */
	public List<String> getLabelsEx()
	{
		return Collections.unmodifiableList(labels);
	}

	/**
	 * @return true if the sequence is empty
	 */
	public boolean isEmpty()
	{
		return labels.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return labels.toString();
	}
}
