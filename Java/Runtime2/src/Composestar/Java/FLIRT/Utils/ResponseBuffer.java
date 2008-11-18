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
package Composestar.Java.FLIRT.Utils;

import java.util.Stack;

/**
 * @author Michiel Hendriks
 */
public class ResponseBuffer
{
	/**
	 * stack of "wrapper" buffers
	 */
	protected Stack<SyncBuffer<Object>> buffers;

	/**
	 * The current active buffer
	 */
	protected SyncBuffer<Object> currentBuffer;

	/**
	 * The first buffer
	 */
	protected SyncBuffer<Object> firstBuffer;

	public ResponseBuffer()
	{
		buffers = new Stack<SyncBuffer<Object>>();
		currentBuffer = new SyncBuffer<Object>();
		firstBuffer = currentBuffer;
	}

	public void produceFirst(Object o)
	{
		firstBuffer.produce(o);
	}

	public void produce(Object o)
	{
		currentBuffer.produce(o);
	}

	public Object consume()
	{
		return currentBuffer.consume();
	}

	public Object consume(SyncBuffer<Object> buff)
	{
		if (buff == null)
		{
			buff = currentBuffer;
		}
		return buff.consume();
	}

	public SyncBuffer<Object> wrap()
	{
		buffers.push(currentBuffer);
		currentBuffer = new SyncBuffer<Object>();
		return currentBuffer;
	}

	public void unwrap()
	{
		currentBuffer = buffers.pop();
	}

}
