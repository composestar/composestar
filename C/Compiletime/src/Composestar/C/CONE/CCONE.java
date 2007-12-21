package Composestar.C.CONE;

import Composestar.Core.CONE.CONE;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Debug;

public class CCONE extends CONE
{

	/**
	 * @param resources
	 * @throws Composestar.Core.Exception.ModuleException
	 * @roseuid 404DB2030076
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		Debug.out(Debug.MODE_DEBUG, "INCRE", "Making history for incremental compilation");

		// Generate weave specification file
		CWeaveFileGenerator wg = new CWeaveFileGenerator();
		wg.run(resources);
	}

}
