package Composestar.RuntimeDotNET.FLIRT;

import Composestar.RuntimeCore.FLIRT.*;
import Composestar.RuntimeCore.Utils.*;

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
	public synchronized static void handleDotNETApplicationStart(String filename, int debug) 
	{
		handleApplicationStart(filename, debug, new DotNETPlatformProvider());
	}
}