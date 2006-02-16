package Composestar.Core.FILTH;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Utils.*;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.SANE.SIinfo;

import java.util.Iterator;
import java.util.List;

/**
 * 
 */
public class FILTH implements CTCommonModule
{
	/**
	 * Calculates orders of the superimposed filtermodules 
	 */
	public FILTH() 
	{
     	/* empty constructor */
	}
    
	/**
	 * 
	 */
	public void run(CommonResources resources) throws ModuleException 
	{
		/* get a INCRE instance */
		INCRE incre = INCRE.instance();
		
		INCRETimer filthinit = incre.getReporter().openProcess("FILTH","Init FILTH service",INCRETimer.TYPE_NORMAL);
		/* first set the ordering spec file!!!!! */
		resources.addResource("ConstraintFile",(Object)"XMLTest.xml");
		/* get a FILTHService instance */
		FILTHService filthservice = FILTHService.getInstance(resources);
		InnerDispatcher.getInnerDispatchReference();
		filthinit.stop();
		
		Iterator conIter = DataStore.instance().getAllInstancesOf(Concern.class);
		while( conIter.hasNext() )
		{   
			Concern c = (Concern)conIter.next();
 
			SIinfo sinfo = (SIinfo)c.getDynObject("superImpInfo");
			if(sinfo != null)
			{
				List list;
						
				if(incre.isProcessedByModule(c,"FILTH"))
				{
					/* Copy FilterModuleOrders */
					INCRETimer filthcopy = incre.getReporter().openProcess("FILTH",c.getUniqueID(),INCRETimer.TYPE_INCREMENTAL);
					filthservice.copyOperation(c,incre);
					list = (List)c.getDynObject("FilterModuleOrders");
					filthcopy.stop();

				}
				else 
				{
					/* Calculate FilterModuleOrders */
					INCRETimer filthrun = incre.getReporter().openProcess("FILTH",c.getUniqueID(),INCRETimer.TYPE_NORMAL);
					list = filthservice.getMultipleOrder(c);
					filthrun.stop();
				}
					
				if(list.size() > 1)
				{
					Debug.out(Debug.MODE_INFORMATION, "FILTH", "Encountered shared join point: "+c.getQualifiedName(),c.getDescriptionFileName());
				}
			}
		}
	}
}
