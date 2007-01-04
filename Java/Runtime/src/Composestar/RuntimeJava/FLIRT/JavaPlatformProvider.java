package Composestar.RuntimeJava.FLIRT;

import Composestar.RuntimeCore.FLIRT.PlatformProvider;
import Composestar.RuntimeCore.Utils.*;
import Composestar.RuntimeJava.Interface.JavaObjectInterface;
import Composestar.RuntimeJava.Utils.*;

public class JavaPlatformProvider extends PlatformProvider
{

	public JavaPlatformProvider()
	{}

	public void instantiatePlatform()
	{
		new JavaObjectInterface();
		new JavaInvoker();
		new JavaFilterFactory();
		new JavaThreadPool();
	}

	public RepositoryDeserializer getRepositoryDeserializer()
	{
		return new JavaRepositoryDeserializer();
	}

}
