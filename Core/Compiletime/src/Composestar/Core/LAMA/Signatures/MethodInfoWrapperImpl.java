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

package Composestar.Core.LAMA.Signatures;

import Composestar.Core.LAMA.MethodInfo;

/**
 * Contains signature information about a MethodInfo structure in relation to a
 * concern
 * 
 * @author Michiel Hendriks
 */
public class MethodInfoWrapperImpl implements MethodInfoWrapper
{
	private static final long serialVersionUID = -1662333594134019298L;

	/**
	 * The method info structure
	 */
	protected MethodInfo methodInfo;

	/**
	 * The relation for this method
	 */
	protected MethodRelation relation = MethodRelation.NORMAL;

	/**
	 * The current status of this wrapper
	 */
	protected MethodStatus status = MethodStatus.UNKNOWN;

	/**
	 * @param mi
	 * @param rel
	 * @param stat
	 * @throws NullPointerException
	 */
	public MethodInfoWrapperImpl(MethodInfo mi, MethodStatus stat) throws NullPointerException
	{
		if (mi == null)
		{
			throw new NullPointerException("MethodInfo can not be null");
		}
		methodInfo = mi;
		status = stat;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2Impl.Signatures.MethodInfoWrapper#
	 * getMethodInfo()
	 */
	public MethodInfo getMethodInfo()
	{
		return methodInfo;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2Impl.Signatures.MethodInfoWrapper#getRelation
	 * ()
	 */
	public MethodRelation getRelation()
	{
		return relation;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2Impl.Signatures.MethodInfoWrapper#setRelation
	 * (Composestar.Core.CpsRepository2.Signatures.MethodRelation)
	 */
	public void setRelation(MethodRelation value)
	{
		relation = value;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2Impl.Signatures.MethodInfoWrapper#getStatus
	 * ()
	 */
	public MethodStatus getStatus()
	{
		return status;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2Impl.Signatures.MethodInfoWrapper#setStatus
	 * (Composestar.Core.CpsRepository2.Signatures.MethodStatus)
	 */
	public void setStatus(MethodStatus value)
	{
		status = value;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(relation.name());
		sb.append("] ");
		sb.append(methodInfo.toString());
		return sb.toString();
	}

}
