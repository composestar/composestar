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

package Composestar.Java.FLIRT.Env;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.Meta.SourceInformation;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;

/**
 * An arbitrary variable stored in the message.
 * 
 * @author Michiel Hendriks
 */
public class CpsArbitraryValue implements CpsVariable
{
	private static final long serialVersionUID = 1L;

	protected transient Object value;

	/**
	 * @param val
	 */
	public CpsArbitraryValue(Object val)
	{
		value = val;
	}

	/**
	 * @return
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * @param val
	 */
	public void setValue(Object val)
	{
		value = val;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.TypeSystem.CpsVariable#compatible(Composestar
	 * .Core.CpsRepository2.TypeSystem.CpsVariable)
	 */
	public boolean compatible(CpsVariable other) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.RepositoryEntity#getOwner()
	 */
	public RepositoryEntity getOwner()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.RepositoryEntity#getSourceInformation()
	 */
	public SourceInformation getSourceInformation()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.RepositoryEntity#setOwner(Composestar
	 * .Core.CpsRepository2.RepositoryEntity)
	 */
	public RepositoryEntity setOwner(RepositoryEntity newOwner)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.RepositoryEntity#setSourceInformation
	 * (Composestar.Core.CpsRepository2.Meta.SourceInformation)
	 */
	public void setSourceInformation(SourceInformation srcInfo)
	{}

	private void writeObject(ObjectOutputStream out) throws IOException
	{}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{}
}
