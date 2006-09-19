package Composestar.DotNET.CONE;

import Composestar.Core.CONE.CONE;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.Master.CommonResources;
import Composestar.Utils.Debug;

public class DotNETCONE extends CONE
{
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
		incre.storeHistory();
		increhistory.stop();

		// Generate repository file
		INCRETimer xmlgenerator = incre.getReporter().openProcess("CONE","Generation of repository.xml",INCRETimer.TYPE_NORMAL);
		DotNETRepositorySerializer rs = new DotNETRepositorySerializer(); 
		rs.run(resources);
		xmlgenerator.stop();

		// Generate weave specification file
		INCRETimer weave = incre.getReporter().openProcess("CONE","Generation of weave specification file",INCRETimer.TYPE_NORMAL);
		DotNETWeaveFileGenerator wg = new DotNETWeaveFileGenerator();
		wg.run(resources);
		weave.stop();
	}

}
