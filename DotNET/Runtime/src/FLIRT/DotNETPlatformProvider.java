package Composestar.RuntimeDotNET.FLIRT;

import Composestar.RuntimeCore.FLIRT.PlatformProvider;
import Composestar.RuntimeCore.CODER.*;
import Composestar.RuntimeCore.Utils.*;
import Composestar.RuntimeDotNET.FLIRT.Filtertypes.*;
import Composestar.RuntimeDotNET.Interface.*;
import Composestar.RuntimeDotNET.Utils.*;

public class DotNETPlatformProvider extends PlatformProvider
{
	public DotNETPlatformProvider()
	{
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
}


