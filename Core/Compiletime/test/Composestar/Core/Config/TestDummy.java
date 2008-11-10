package Composestar.Core.Config;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Resources.CommonResources;

public class TestDummy implements CTCommonModule
{
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		return ModuleReturnValue.OK;
	}
}
