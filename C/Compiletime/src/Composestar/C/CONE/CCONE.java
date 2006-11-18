package Composestar.C.CONE;

import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.Master.*;
import Composestar.Core.CONE.CONE;

import Composestar.Utils.Debug;
import Composestar.Core.Exception.ModuleException;

import java.util.Hashtable;

public class CCONE extends CONE{

	/**
     * @param resources
     * @throws Composestar.core.Exception.ModuleException
     * @roseuid 404DB2030076
     */
    public void run(CommonResources resources) throws ModuleException
    {
    	// Create INCRE history
    	INCRE incre = INCRE.instance();
		Debug.out(Debug.MODE_DEBUG, "INCRE", "Making history for incremental compilation");
		INCRETimer increhistory = incre.getReporter().openProcess("CONE","Creation of INCRE history",INCRETimer.TYPE_OVERHEAD);
		//verwijderd maar niet zeker van incre.storeHistory();
		increhistory.stop();
		
		// Generate weave specification file
		INCRETimer weave = incre.getReporter().openProcess("CONE","Generation of weave specification file",INCRETimer.TYPE_NORMAL);
		CWeaveFileGenerator wg = new CWeaveFileGenerator();
     	wg.run(resources);
     	weave.stop();
     	
     	
    }
	
}
