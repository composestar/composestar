/*
 * Created on Dec 2, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package Composestar.Core.CKRET;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.LAMA.Annotation;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.Master.Config.ModuleSettings;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.*;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.Exception.ModuleException;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CKRET implements CTCommonModule {

	public static final String REPOSITORY_KEY = "Composestar.Core.CKRET.Repostitory";

	public static int MODE = 0;

	public static String[] MODES = {"NORMAL","REDUNDANT","PROGRESSIVE"};

    private static Reporter reporter;
	
	private String reportFile = "";

    protected static final int PROGRESSIVE = 2;
    protected static final int REDUNDANT = 1;
    protected static final int NORMAL = 0;

    /* (non-Javadoc)
	 * @see Composestar.Core.Master.coreModule#run(Composestar.Core.Master.CommonResources)
	 */
	public void run(CommonResources resources) throws ModuleException {

        ArrayList conflictingConcerns = new ArrayList();
        INCRE incre = INCRE.instance();
        CommonResources resources1 = resources;

        // make sure it has been initialized at least once...
		try
		{
			INCRETimer initckret = incre.getReporter().openProcess("CKRET","Initializing CKRET repository",INCRETimer.TYPE_NORMAL);
			Repository.instance().init();
			initckret.stop();
		}
		catch(ModuleException me)
		{
			Debug.out(Debug.MODE_WARNING,"CKRET",me.getMessage());
			return;
		}

		// fetch the ckret runmode
		try
		{
			ModuleSettings module = Configuration.instance().getModuleSettings().getModule("CKRET");
			int mode = Integer.parseInt(module.getProperty("mode"));
			if( mode < 3 && mode > -1 )
			{
				Debug.out(Debug.MODE_INFORMATION,"CKRET","CKRET mode set to " + MODES[mode]);
				MODE = mode;
			}
			else
			{
				Debug.out(Debug.MODE_WARNING,"CKRET","Unknown CKRET mode: " + mode + ", CKRET will run in " + MODES[MODE] + " mode");
			}
		}
		catch(Exception e)
		{
			Debug.out(Debug.MODE_WARNING,"CKRET","Failed to fetch CKRET mode, CKRET will run in " + MODES[MODE] + " mode");
		}
		
		try
		{
			String basedir =  Configuration.instance().getPathSettings().getPath("Base");
			File file = new File(basedir+"analyses/");
			if(!file.exists())
			{
				file.mkdir();
			}
			if(file.isDirectory())
			{
				reportFile = file.getAbsolutePath() + "\\CKRET.html";

				String cssFile = "file://"+Configuration.instance().getPathSettings().getPath("Base") + "CKRET.css";
				if( !(new File(cssFile).exists()))
				{
					cssFile = "file://"+Configuration.instance().getPathSettings().getPath("Composestar") + "CKRET.css";
				}

				reporter = new HTMLReporter(reportFile, cssFile, resources);
				reporter.open();
				Debug.out(Debug.MODE_DEBUG,"CKRET","CKRET report file (" + reportFile + ") created...");
			}
		}
		catch(Exception e){
			e.getMessage();
			throw new ModuleException("CKRET","CKRET report file creation failed (" + reportFile + "), with reason: " + e.getMessage());
		}
		
		Iterator conIter = DataStore.instance().getAllInstancesOf(Concern.class);

		while (conIter.hasNext()) {
			Concern concern = (Concern) conIter.next();

			if (concern.getDynObject("superImpInfo") != null) {
				if(incre.isProcessedByModule(concern,"CKRET"))
				{
					this.copyOperation(concern);
				}
				else 
				{
					INCRETimer ckretrun = incre.getReporter().openProcess("CKRET",concern.getUniqueID(),INCRETimer.TYPE_NORMAL);
					this.run(concern);
					ckretrun.stop();
				}
			}
		}
		
		getReporter().close();
	}
	
	private void run(Concern concern) throws ModuleException {

		getReporter().openConcern(concern);
		
		FilterModuleOrder singleOrder = (FilterModuleOrder) concern.getDynObject("SingleOrder");
		if( singleOrder != null )
		{
			// ok need to do some checking
			ConcernAnalysis oa = new ConcernAnalysis(concern);
			List fmolist = (List) concern.getDynObject("FilterModuleOrders");
			
			switch(CKRET.MODE)
			{
				case NORMAL: // NORMAL
					if( !oa.checkOrder(singleOrder, true) )
					{
						Debug.out(Debug.MODE_WARNING,"CKRET","Semantic conflict(s) detected on concern " + concern.getQualifiedName(),reportFile);
					}
					break;
				
				case REDUNDANT: // REDUNDANT
					if( !oa.checkOrder(singleOrder, true) )
					{
						Debug.out(Debug.MODE_WARNING,"CKRET","Semantic conflict(s) detected on concern " + concern.getQualifiedName(),reportFile);
					}
					for( Iterator fmoit = fmolist.iterator(); fmoit.hasNext(); )
					{
						LinkedList order = (LinkedList) fmoit.next();
						FilterModuleOrder fmo = new FilterModuleOrder(order);
												
						if( !fmo.equals(singleOrder))
						{
							oa.checkOrder(fmo,false);
						}
					}
					break;
				
				case PROGRESSIVE: // PROGRESSIVE
					boolean foundGoodOrder = oa.checkOrder(singleOrder, true);

					for( Iterator fmoit = fmolist.iterator(); fmoit.hasNext(); )
					{
						LinkedList order = (LinkedList) fmoit.next();
						FilterModuleOrder fmo = new FilterModuleOrder(order);
						if( !fmo.equals(singleOrder))
						{
							if( oa.checkOrder(fmo,!foundGoodOrder) )
							{
								if( !foundGoodOrder ) 
								{
									// so this is the first good order found...
									foundGoodOrder = true;
									concern.addDynObject("SingleOrder",fmo);
									Debug.out(Debug.MODE_INFORMATION,"CKRET","Selected filtermodule order for concern " + concern.getQualifiedName() + ':');
									Debug.out(Debug.MODE_INFORMATION,"CKRET", '\t' + fmo.toString());
								} 
							}
						}
					}
					if( !foundGoodOrder )
					{
						Debug.out(Debug.MODE_WARNING,"CKRET","Unable to find a filtermodule order without conflicts for concern:");
						Debug.out(Debug.MODE_WARNING,"CKRET", '\t' + concern.getQualifiedName());
					}
					
					break;
               default: //OOPS
                    Debug.out(Debug.MODE_WARNING,"CKRET","Unknown mode used");
                    break;
			}
		}

		getReporter().closeConcern();
	}

	private void copyOperation(Concern concern) throws ModuleException
	{
		INCRE incre = INCRE.instance();
		INCRETimer ckretcopy = incre.getReporter().openProcess("CKRET",concern.getUniqueID(),INCRETimer.TYPE_INCREMENTAL);

		// set singleorder from previous CKRET run
		Concern oldconcern = (Concern)incre.findHistoryObject(concern);
		concern.addDynObject("SingleOrder",oldconcern.getDynObject("SingleOrder"));
				
		//get CKRET reports and let HTMLReporter print them
		ArrayList reports;
		reports = (ArrayList)oldconcern.getDynObject("CKRETReports");
					
		if(reports!=null)
		{
			Debug.out(Debug.MODE_INFORMATION,"INCRE","Skipping CKRET run for "+oldconcern.getQualifiedName());
			getReporter().openConcern(oldconcern);
			Iterator repItr = reports.iterator();
			while(repItr.hasNext())
			{
				CKRETReport report = (CKRETReport)repItr.next();
				getReporter().reportOrder(report.getOrder(),report.getAnalysis(),report.getSelected(),true);
			}
			getReporter().closeConcern();
			concern.addDynObject("CKRETReports", reports);
		}
					
		ckretcopy.stop();
	}

	public ArrayList getSemanticAnnotations(PrimitiveConcern pc)
	{
		return getSemanticAnnotations((Concern)pc);
	}
	
	public ArrayList getSemanticAnnotations(CpsConcern cps)
	{
		return getSemanticAnnotations((Concern)cps);
	}
	
	public ArrayList getSemanticAnnotations(Concern c)
	{
		ArrayList annos = new ArrayList();
		INCRE incre = INCRE.instance();
		DataStore ds = incre.getCurrentRepository();
		
		// iterate over concerns
		Iterator iterConcerns = ds.getAllInstancesOf(Concern.class);
		while ( iterConcerns.hasNext() ){
			Concern concern = (Concern)iterConcerns.next();
			Type type = (Type)concern.getPlatformRepresentation();
			if(type==null) continue;
			// iterate over methods
			Iterator methods = type.getMethods().iterator();
			while(methods.hasNext()){
				MethodInfo method = (MethodInfo)methods.next();
				// iterate over annotations
				Iterator annotations = method.getAnnotations().iterator();
				while(annotations.hasNext()){
					Annotation anno = (Annotation)annotations.next();
					if(anno.getType().getUnitName().endsWith("Semantics"))
						annos.add(anno);
				}
			}
		}
		
		return annos;
	}
	
	/*
	public static void printState(ActionNode node)
	{
		System.out.print(node.getSelector());
		System.out.print(" ");
		Symbol[] conditions = node.getConditions();
		for( int i = 0; i < conditions.length; i++ )
		{
			System.out.print("[" + conditions[i].getName() + "]");
		}
		System.out.println();
	}
	*/
	
	/*
	protected static List getFilterList(List filterModules)
	{
		List list = new ArrayList();
		
		Iterator itr = filterModules.iterator();
		while (itr.hasNext())
		{
			String name = (String) itr.next();
		
			FilterModule fm = (FilterModule) (DataStore.instance()).getObjectByID(name);
			Iterator ifItr = fm.inputFilters.iterator();
		
			while (ifItr.hasNext())
			{
				Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter f = (Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter) ifItr.next();
				list.add(f);
			}	
		}
		return list;
	}
	*/

	/*
	public static Repository getRepository()
	{
		Repository repository = (Repository) DataStore.instance().getObjectByID(REPOSITORY_KEY);
		if( repository == null )
			DataStore.instance().addObject(REPOSITORY_KEY, repository);
		return repository;
	}
	*/

	protected static Reporter getReporter()
	{
		return reporter;
	}

}
