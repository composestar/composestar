/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
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
package Composestar.DotNET2.LOLA.metamodel;

import Composestar.Core.LOLA.metamodel.InvalidModelException;
import Composestar.Core.LOLA.metamodel.LanguageModel;

public class DotNETLanguageModel extends LanguageModel
{
	public DotNETLanguageModel()
	{
		super();
	}

	/**
	 * Creates specifications for all language units that can occur in .NET, and
	 * the relations between them.
	 */
	@Override
	public void createMetaModel() throws InvalidModelException
	{
		mcNamespace = Composestar.Core.LAMA.LangNamespace.class;
		mcClass = Composestar.DotNET2.LAMA.DotNETType.class;
		mcInterface = Composestar.DotNET2.LAMA.DotNETType.class;
		mcType = Composestar.DotNET2.LAMA.DotNETType.class;
		mcMethod = Composestar.DotNET2.LAMA.DotNETMethodInfo.class;
		mcField = Composestar.DotNET2.LAMA.DotNETFieldInfo.class;
		mcParameter = Composestar.DotNET2.LAMA.DotNETParameterInfo.class;
		mcAnnotation = Composestar.DotNET2.LAMA.DotNETType.class;
		super.createMetaModel();
	}
}
