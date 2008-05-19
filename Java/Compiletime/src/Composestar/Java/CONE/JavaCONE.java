package Composestar.Java.CONE;

import Composestar.Core.CONE.CONE;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Resources.CommonResources;

/**
 * Module that generates code. (<b>CO</b>de ge<b>NE</b>ration)
 */
public class JavaCONE extends CONE
{

	/**
	 * Module starting point.
	 * <p>
	 * 1. runs the JavaRepositorySerializer to serialize the repository.
	 * 
	 * @see Composestar.Java.CONE.JavaRepositorySerializer
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		// Generate repository file
		JavaRepositorySerializer rs = new JavaRepositorySerializer();
		rs.run(resources);
		return ModuleReturnValue.Ok;
	}
}
