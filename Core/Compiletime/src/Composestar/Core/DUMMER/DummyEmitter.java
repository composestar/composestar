package Composestar.Core.DUMMER;

import java.util.Set;

import Composestar.Core.Config.Project;
import Composestar.Core.Config.Source;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CommonResources;

/**
 * Interface for dummy emitters.
 */
public interface DummyEmitter
{
	/**
	 * The set of sources to create the dummies for. This set has already been
	 * filters based on the source's language and the Language settings.
	 * 
	 * @param project
	 * @param sources
	 * @throws ModuleException
	 */
	void createDummies(Project project, Set<Source> sources) throws ModuleException;

	void setCommonResources(CommonResources resc);
}
