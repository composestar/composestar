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

/**
 * Repository serializer
 */
public abstract class CONE implements CTCommonModule
{
	public static final String MODULE_NAME = "CONE";

	/**
	 * Key used to store the serialized repository in the CommonResources
	 */
	public static final String REPOSITORY_FILE_KEY = "RepositoryFile";
}
