package Composestar.Core.LAMA;

import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;

public abstract class FieldInfo extends ProgramElement{

	public String Name;

	public FieldInfo()
	{
		UnitRegister.instance().registerLanguageUnit(this);
	}
	
	/** Stuff for LOLA **/
	
	/* (non-Javadoc)
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitName()
	 */
	public String getUnitName()
	{
	    return Name;
	}
	
	/* (non-Javadoc)
	 * @see Composestar.CTCommon.LOLA.metamodel.LanguageUnit#getUnitType()
	 */
	public String getUnitType()
	{
	    return "Field";
	}
	
	/* (non-Javadoc)
	 * @see Composestar.Core.LAMA.ProgramElement#hasAttribute(java.lang.String)
	 */
	public boolean hasUnitAttribute(String attribute)
	{
	    return getUnitAttributes().contains(attribute);
	}
}
