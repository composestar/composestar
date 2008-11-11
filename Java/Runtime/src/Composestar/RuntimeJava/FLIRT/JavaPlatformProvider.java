/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2005-2008 University of Twente.
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
package Composestar.RuntimeJava.FLIRT;

import Composestar.RuntimeCore.FLIRT.PlatformProvider;
import Composestar.RuntimeCore.Utils.RepositoryDeserializer;
import Composestar.RuntimeJava.Interface.JavaObjectInterface;
import Composestar.RuntimeJava.Utils.JavaInvoker;
import Composestar.RuntimeJava.Utils.JavaRepositoryDeserializer;

public class JavaPlatformProvider extends PlatformProvider
{
	private static final long serialVersionUID = -2756901970302896830L;

	protected Class<?> mainclass;

	public JavaPlatformProvider(Class<?> mclass)
	{
		mainclass = mclass;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.RuntimeCore.FLIRT.PlatformProvider#instantiatePlatform()
	 */
	@Override
	public void instantiatePlatform()
	{
		new JavaObjectInterface();
		new JavaInvoker();
		new JavaFilterFactory();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.RuntimeCore.FLIRT.PlatformProvider#getRepositoryDeserializer
	 * ()
	 */
	@Override
	public RepositoryDeserializer getRepositoryDeserializer()
	{
		return new JavaRepositoryDeserializer(mainclass);
	}

}
