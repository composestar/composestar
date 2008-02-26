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

package Composestar.CwC.LOLA.Metamodel;

import Composestar.Core.LOLA.metamodel.InvalidModelException;
import Composestar.Core.LOLA.metamodel.LanguageModel;

/**
 * @author Michiel Hendriks
 */
public class CwCLanguageModel extends LanguageModel
{
	public CwCLanguageModel()
	{}

	/**
	 * Creates specifications for all language units that can occur in java, and
	 * the relations between them.
	 */
	@Override
	public void createMetaModel() throws InvalidModelException
	{
		mcNamespace = Composestar.Core.LAMA.LangNamespace.class;
		mcClass = Composestar.CwC.LAMA.CwCFile.class;
		mcInterface = null;
		mcType = Composestar.CwC.LAMA.CwCFile.class;
		mcMethod = Composestar.CwC.LAMA.CwCFunctionInfo.class;
		mcField = Composestar.CwC.LAMA.CwCVariable.class;
		mcParameter = Composestar.CwC.LAMA.CwCParameterInfo.class;
		mcAnnotation = Composestar.CwC.LAMA.CwCAnnotationType.class;
		super.createMetaModel();
	}

}
