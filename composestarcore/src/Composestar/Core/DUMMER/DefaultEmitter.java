package Composestar.Core.DUMMER;

import java.util.Collection;
import java.util.Iterator;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.Config.Project;
import Composestar.Core.Master.Config.Source;

public class DefaultEmitter implements DummyEmitter {

	public void createDummy(Project project, Source source, String outputFilename) throws ModuleException
	{
		/* For languages where no emitter is used, this is the default.
		 * Also use this when dummies are already generated somewhere else (e.g. in the visual studio addin)
		 */
	}

	/*
	 * Classes extending this default behaviour can override it to obtain more efficient behaviour (e.g. handle generation
	 * of multiple dummies in 1 go instead of calling createDummy for each of them)
	 */
	public void createDummies(Project project, Collection sources, Collection outputFilenames) throws ModuleException {
		if (sources.size() != outputFilenames.size())
			throw new ModuleException("Lists of source- and outputfilenames do not have equal length!", "DUMMER");
		Iterator srcIter = sources.iterator();
		Iterator outIter = outputFilenames.iterator();
		while (srcIter.hasNext())
		{
			Source src = (Source)srcIter.next();
			String output = (String)outIter.next();
			createDummy(project, src, output);			
		}
	}
}
