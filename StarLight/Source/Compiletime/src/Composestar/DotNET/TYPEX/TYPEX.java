package Composestar.DotNET.TYPEX;

import java.io.File;
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Logging.CPSLogger;

/**
 * TYPEX - TYPe EXpander.
 * Expands existing classes in sources and assemblies with additional methods.
 * The set of additional methods is generated by SIGN.
 * 
 * @author Marcus Klimstra
 */
public class TYPEX implements CTCommonModule
{
	public static final String MODULE_NAME = "TYPEX";
	
	private static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	//@In("concerns")
	private List<Concern> concerns;
	
	//@In("signatures.modified")
	private boolean signaturesModified;
	
	//@Out("sources.expanded.files")
	private List<File> expandedSources;
	
	public TYPEX()
	{
	}

	public void run(CommonResources resources) throws ModuleException
	{
		concerns = DataStore.instance().getListOfAllInstances(Concern.class);
		signaturesModified = resources.getBoolean("signaturesmodified");

		run();
		
		resources.addResource("sources.expanded.files", expandedSources);
	}
	
	private void run() throws ModuleException
	{
		if (!signaturesModified)
		{
			logger.info("No need to transform assemblies");
			return;
		}
		
		ExpandedTypeCollector te = new ExpandedTypeCollector();
		te.process(concerns);
		
		AssemblyExpander ae = new AssemblyExpander();
		ae.process(te.getExpandedAssemblies());
		
		SourceExpander se = new SourceExpander();
		expandedSources = se.process(te.getExpandedSources());
	}
}