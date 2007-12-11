/*
 * TODO Add description here
 * 
 * Created on Nov 25, 2004 by wilke
 */
package Composestar.Core.LOLA.metamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompositeRelationPredicate extends RelationPredicate
{
	protected List<RelationPredicate> containedRelationPredicates;

	public CompositeRelationPredicate(String predName, RelationPredicate rel1, RelationPredicate rel2)
	{
		this(predName, null);
		List<RelationPredicate> rels = new ArrayList<RelationPredicate>();
		rels.add(rel1);
		rels.add(rel2);
		containedRelationPredicates = rels;
	}

	public CompositeRelationPredicate(String predName, List<RelationPredicate> containedRels)
	{
		super(predName);
		containedRelationPredicates = containedRels;
	}

	/**
	 * @return Returns the containedRelationPredicates.
	 */
	public List<RelationPredicate> getContainedRelationPredicates()
	{
		return Collections.unmodifiableList(containedRelationPredicates);
	}

	/**
	 * @param containedRelationPredicates The containedRelationPredicates to
	 *            set.
	 */
	public void setContainedRelationPredicates(List<RelationPredicate> containedRelationPredicates)
	{
		this.containedRelationPredicates = containedRelationPredicates;
	}
}
