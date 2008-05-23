/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CONE;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Repository serializer. Saves the data required by the runtime to a file.
 */
@ComposestarModule(ID = ModuleNames.CONE, dependsOn = { ComposestarModule.DEPEND_ALL })
public abstract class CONE implements CTCommonModule
{
	/**
	 * Key used to store the serialized repository in the CommonResources
	 */
	public static final String REPOSITORY_FILE_KEY = "RepositoryFile";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.CONE);
}
