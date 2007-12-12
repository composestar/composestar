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

import Composestar.Core.Master.CTCommonModule;

public interface RepositorySerializer extends CTCommonModule
{
	/**
	 * Key used to store the serialized repository in the CommonResources
	 */
	static final String REPOSITORY_FILE_KEY = "RepositoryFile";
}
