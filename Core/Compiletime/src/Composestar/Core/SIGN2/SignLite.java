package Composestar.Core.SIGN2;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Resources.CommonResources;

public class SignLite implements CTCommonModule
{
	/**
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources.CommonResources)
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		// No lite version of sign yet.
		Sign sign = new Sign();
		sign.run(resources);
		return ModuleReturnValue.Ok;
	}
}
