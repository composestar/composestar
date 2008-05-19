package Composestar.DotNET2.TYPEX;

import java.io.File;
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * TYPEX - TYPe EXpander. Expands existing classes in sources and assemblies
 * with additional methods. The set of additional methods is generated by SIGN.
 * 
 * @author Marcus Klimstra
 */
public class TYPEX implements CTCommonModule
{
	public static final String MODULE_NAME = "TYPEX";

	private static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	// @In("concerns")
	private List<Concern> concerns;

	// @In("signatures.modified")
	private boolean signaturesModified;

	// @Out("sources.expanded.files")
	private List<File> expandedSources;

	public TYPEX()
	{}

	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		concerns = resources.repository().getListOfAllInstances(Concern.class);
		signaturesModified = resources.getBoolean("signaturesmodified");

		if (!signaturesModified)
		{
			logger.info("No need to transform assemblies");
			return ModuleReturnValue.Ok;
		}

		ExpandedTypeCollector te = new ExpandedTypeCollector();
		te.process(concerns);

		AssemblyExpander ae = new AssemblyExpander(resources);
		ae.process(te.getExpandedAssemblies());

		SourceExpander se = new SourceExpander(resources);
		expandedSources = se.process(te.getExpandedSources());
		resources.put("sources.expanded.files", expandedSources);

		return ModuleReturnValue.Ok;
	}
}
