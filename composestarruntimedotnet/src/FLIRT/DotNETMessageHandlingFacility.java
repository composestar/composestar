package Composestar.RuntimeDotNET.FLIRT;

import Composestar.RuntimeCore.FLIRT.MessageHandlingFacility;
import Composestar.RuntimeCore.Utils.RepositoryDeserializer;

public class DotNETMessageHandlingFacility extends MessageHandlingFacility
{
	public DotNETMessageHandlingFacility()
	{
	}

	/**
	 * When the application starts this method is called to deserialize and link the compile time structure to the runtime
	 * @param filename String The location of the xml file
	 * @param debug int The debug level
	 */
	public static void handleDotNETApplicationStart(String filename, int debug) 
	{
		boolean debugInterface = false;//DebuggerFactory.checkDebugInterfaceSetting();
		handleDotNETApplicationStart(filename, debug, debugInterface);
	}

	public synchronized static void handleDotNETApplicationStart(String filename, int debug, boolean debugInterface)
	{
		handleApplicationStart(filename, debug, debugInterface, new DotNETPlatformProvider());
	}
}