/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2009 University of Twente.
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

package Composestar.Java.FLIRT.Utils;

/**
 * Exception thrown by the method invoker. The cause contains the actual
 * exception. The cause will be rethrown by the message handling facility. This
 * is part of some magic to bubble the right exception to the sender.
 * 
 * @author Michiel Hendriks
 */
public class InvocationException extends RuntimeException
{
	private static final long serialVersionUID = 5134743425796283931L;

	/**
	 * Create a new invocation exception. It will unwrap existing invocation
	 * exceptions
	 * 
	 * @param cause
	 * @return
	 */
	public static final InvocationException create(Throwable cause)
	{
		if (cause instanceof InvocationException && cause.getCause() != null)
		{
			cause = cause.getCause();
		}
		return new InvocationException(cause);
	}

	protected InvocationException(Throwable cause)
	{
		super(cause);
	}
}
