/*
 * TODO Add description here
 * 
 * Created on Oct 26, 2004 by wilke
 */
package Composestar.Core.LOLA.metamodel;

public class RelationPredicate
{
	private RelationType[] relationTypes;

	private String[] varNames;

	private String predicateName;

	public RelationPredicate(String predName)
	{
		predicateName = predName;
	}

	public RelationPredicate(String predName, RelationType type1, String var1, RelationType type2, String var2)
	{
		relationTypes = new RelationType[2];
		relationTypes[0] = type1;
		relationTypes[1] = type2;
		varNames = new String[2];
		varNames[0] = var1;
		varNames[1] = var2;
		predicateName = predName;
	}

	public String getVarName(int argPos)
	{
		return varNames[argPos - 1];
	}

	public RelationType getRelationType(int argPos)
	{
		return relationTypes[argPos - 1];
	}

	/**
	 * @return Returns the predicateName.
	 */
	public String getPredicateName()
	{
		return predicateName;
	}

	/**
	 * @param predicateName The predicateName to set.
	 */
	public void setPredicateName(String predicateName)
	{
		this.predicateName = predicateName;
	}
}
