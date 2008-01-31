/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */

package Composestar.CwC.TYM;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import weavec.ast.PreprocessorInfoChannel;
import weavec.grammar.TranslationUnitResult;
import Composestar.Core.Config.Source;
import Composestar.Core.Resources.ModuleResourceManager;

/**
 * @author Michiel Hendriks
 */
public class WeaveCResources implements ModuleResourceManager
{
	private static final long serialVersionUID = 5385445099358783756L;

	protected transient Set<TranslationUnitResult> tunits;

	protected transient Map<TranslationUnitResult, PreprocessorInfoChannel> ppics;

	protected transient Map<TranslationUnitResult, Source> sourceMapping;

	protected int tokenNumber;

	public WeaveCResources()
	{
		tunits = new HashSet<TranslationUnitResult>();
		ppics = new HashMap<TranslationUnitResult, PreprocessorInfoChannel>();
		sourceMapping = new HashMap<TranslationUnitResult, Source>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Resources.ModuleResourceManager#getModuleName()
	 */
	public String getModuleName()
	{
		return "WeaveC";
	}

	public void addTranslationUnitResult(TranslationUnitResult tunit)
	{
		if (tunit == null)
		{
			return;
		}
		tunits.add(tunit);
	}

	public Set<TranslationUnitResult> translationUnitResults()
	{
		return Collections.unmodifiableSet(tunits);
	}

	public int getTokenNumber()
	{
		return tokenNumber;
	}

	public void setTokenNumber(int value)
	{
		tokenNumber = value;
	}

	public void addPreprocessorInfoChannel(TranslationUnitResult tunit, PreprocessorInfoChannel ppic)
	{
		ppics.put(tunit, ppic);
	}

	public PreprocessorInfoChannel getPreprocessorInfoChannel(TranslationUnitResult tunit)
	{
		return ppics.get(tunit);
	}

	public void addSourceMapping(TranslationUnitResult tunit, Source src)
	{
		sourceMapping.put(tunit, src);
	}

	public Source getSource(TranslationUnitResult tunit)
	{
		return sourceMapping.get(tunit);
	}

}
