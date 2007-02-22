package Composestar.RuntimeJava.FLIRT;

import Composestar.Core.RepositoryImplementation.DataMap;
import Composestar.Core.RepositoryImplementation.DataMapImpl;
import Composestar.RuntimeCore.FLIRT.PlatformProvider;
import Composestar.RuntimeCore.Utils.RepositoryDeserializer;
import Composestar.RuntimeJava.Interface.JavaObjectInterface;
import Composestar.RuntimeJava.Utils.JavaInvoker;
import Composestar.RuntimeJava.Utils.JavaRepositoryDeserializer;

public class JavaPlatformProvider extends PlatformProvider
{

	public JavaPlatformProvider()
	{
		DataMap.setDataMapClass(DataMapImpl.class);
	}

	public void instantiatePlatform()
	{
		new JavaObjectInterface();
		new JavaInvoker();
		new JavaFilterFactory();
	}

	public RepositoryDeserializer getRepositoryDeserializer()
	{
		return new JavaRepositoryDeserializer();
	}

}
