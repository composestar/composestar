/*
 * Created on Oct 25, 2004
 *
 * @author wilke
 */
package Composestar.Core.LOLA.metamodel;

public class RelationType
{
	private LanguageUnitType otherType;

	private String name;

	private boolean unique;

	public static final boolean UNIQUE = true;

	public static final boolean MULTIPLE = false;

	public RelationType(String name, boolean unique)
	{
		this.name = name;
		otherType = null;
		this.unique = unique;
	}

	public RelationType(String name, LanguageUnitType otherType, boolean unique)
	{
		this(name, unique);
		this.otherType = otherType;
	}

	public RelationType(ERelationType type, LanguageUnitType otherType, boolean unique)
	{
		this(type.toString(), otherType, unique);
	}

	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return Returns the otherType.
	 */
	public LanguageUnitType getOtherType()
	{
		return otherType;
	}

	/**
	 * @param otherType The otherType to set.
	 */
	public void setOtherType(LanguageUnitType otherType)
	{
		this.otherType = otherType;
	}

	/**
	 * @return Returns the unique.
	 */
	public boolean isUnique()
	{
		return unique;
	}

	/**
	 * @param unique The unique to set.
	 */
	public void setUnique(boolean unique)
	{
		this.unique = unique;
	}
}
