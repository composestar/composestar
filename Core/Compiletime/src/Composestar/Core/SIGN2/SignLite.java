package Composestar.Core.SIGN2;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;

public class SignLite implements CTCommonModule
{

	/**
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Master.CommonResources)
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		// No lite version of sign yet.
		Sign sign = new Sign();
		sign.run(resources);
	}
}
