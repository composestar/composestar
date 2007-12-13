/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.FILTH;

/**
 * @author Isti
 */
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.SANE.FilterModuleSuperImposition;
import Composestar.Utils.Logging.CPSLogger;

public abstract class FILTHService
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(FILTH.MODULE_NAME);

	public static FILTHService getInstance(CommonResources cr) throws ModuleException
	{
		return new FILTHServiceImpl(cr);
	}

	public abstract List<FilterModuleSuperImposition> getOrder(Concern c);

	public abstract List<List<FilterModuleSuperImposition>> getMultipleOrder(Concern c);
}
