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

package Composestar.Utils.Regex;

/**
 * Provides a token buffer to be used for matching
 * 
 * @author Michiel Hendriks
 */
public interface MatchingBuffer
{
	/**
	 * The current token in the buffer. If there are no more tokens it should
	 * return null.
	 * 
	 * @return
	 */
	String current();

	/**
	 * The next token in the buffer, will become the current one after
	 * consume().
	 * 
	 * @return
	 */
	String next();

	/**
	 * Returns true when the end of the buffer has been reached. Current should
	 * return null in this case.
	 * 
	 * @return
	 */
	boolean atEnd();

	/**
	 * Consume the current token and advances to the next token.
	 */
	void consume();

	/**
	 * Fork the current buffer. Will be used for lookaround matching of
	 * subexpressions.
	 * 
	 * @return
	 */
	MatchingBuffer fork();
}