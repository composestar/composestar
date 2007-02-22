package Composestar.RuntimeDotNET.FLIRT;

import Composestar.RuntimeCore.FLIRT.PlatformProvider;
import Composestar.RuntimeCore.FLIRT.Debugger.Debugger;
import Composestar.RuntimeCore.CODER.*;
import Composestar.RuntimeCore.Utils.*;
import Composestar.RuntimeDotNET.FLIRT.Filtertypes.*;
import Composestar.RuntimeDotNET.Interface.*;
import Composestar.RuntimeDotNET.Utils.*;

import Composestar.Core.RepositoryImplementation.DataMap;
import Composestar.Core.RepositoryImplementation.LegacyDataMap;

public class DotNETPlatformProvider extends PlatformProvider
{
	public DotNETPlatformProvider()
	{
		DataMap.setDataMapClass(LegacyDataMap.class);
	}

	public void instantiatePlatform()
	{
		new DotNETObjectInterface();
		new DotNETInvoker();
		new DotNETFilterFactory();
	}

	public RepositoryDeserializer getRepositoryDeserializer()
	{
		return new DotNETRepositoryDeserializer();
	}

	public Debugger getDebugger()
	{
		return DebuggerFactory.getDebugger();
	}
}


