package Composestar.Core.DUMMER;

import java.util.Set;

import Composestar.Core.Config.Project;
import Composestar.Core.Config.Source;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Resources.CommonResources;

/**
 * For languages where no emitter is used, this is the default. Also use this
 * when dummies are already generated somewhere else (e.g. in the visual studio
 * addin)
 */
public class DefaultEmitter implements DummyEmitter
{
	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.DUMMER.DummyEmitter#createDummies(Composestar.Core.Config
	 * .Project, java.util.Set)
	 */
	public void createDummies(Project project, Set<Source> sources) throws ModuleException
	{}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.DUMMER.DummyEmitter#setCommonResources(Composestar.Core
	 * .Resources.CommonResources)
	 */
	public void setCommonResources(CommonResources resc)
	{}
}
