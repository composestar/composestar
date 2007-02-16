/*
 * TODO Add description here
 * 
 * Created on Nov 25, 2004 by wilke
 */
package Composestar.Core.LOLA.metamodel;

import java.util.Collection;
import java.util.Vector;

public class CompositeRelationPredicate extends RelationPredicate
{
	Vector containedRelationPredicates;

	public CompositeRelationPredicate(String predName, RelationPredicate rel1, RelationPredicate rel2)
	{
		this(predName, null);
		Vector rels = new Vector();
		rels.add(rel1);
		rels.add(rel2);
		this.containedRelationPredicates = rels;
	}

	public CompositeRelationPredicate(String predName, Vector containedRels)
	{
		super(predName);
		this.containedRelationPredicates = containedRels;
	}

	/**
	 * @return Returns the containedRelationPredicates.
	 */
	public Collection getContainedRelationPredicates()
	{
		return containedRelationPredicates;
	}

	/**
	 * @param containedRelationPredicates The containedRelationPredicates to
	 *            set.
	 */
	public void setContainedRelationPredicates(Vector containedRelationPredicates)
	{
		this.containedRelationPredicates = containedRelationPredicates;
	}
}
