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

package Composestar.Core.CpsRepository2.TypeSystem;

import java.util.Collection;

/**
 * The message object passed around. Messages can be compared to eachother.
 * 
 * @author Michiel Hendriks
 */
public interface CpsMessage extends Cloneable
{
	/**
	 * Technically inner is not a message property (it is also not available
	 * through the other property methods). It's just available in the message
	 * because the message provides a proper context.
	 * 
	 * @return The inner object
	 */
	CpsObject getInner();

	/**
	 * Set inner object
	 * 
	 * @param value The new inner value
	 * @throws NullPointerException Thrown when the target is null
	 */
	void setInner(CpsObject value) throws NullPointerException;

	/**
	 * @return The current target
	 */
	CpsObject getTarget();

	/**
	 * Set the new target
	 * 
	 * @param value The new target value
	 * @throws NullPointerException Thrown when the target is null
	 */
	void setTarget(CpsObject value) throws NullPointerException;

	/**
	 * @return The current selector
	 */
	CpsSelector getSelector();

	/**
	 * Set a new selector
	 * 
	 * @param value The new selector value
	 * @throws NullPointerException Thrown when the selector is null
	 */
	void setSelector(CpsSelector value) throws NullPointerException;

	/**
	 * @return The sender of this message
	 */
	CpsObject getSender();

	/**
	 * Send this message to a cloned message with a new sender. The sender
	 * property is read-only and can only be changed by sending a new message.
	 * 
	 * @param sender The sender to use for the new message
	 * @return The new message
	 * @throws NullPointerException Thrown when the sender is null
	 */
	CpsMessage send(CpsObject sender) throws NullPointerException;

	/**
	 * @return The server
	 */
	CpsObject getServer();

	/**
	 * Set a new server
	 * 
	 * @param value The new server
	 * @throws NullPointerException Thrown when the server is null
	 */
	void setServer(CpsObject value) throws NullPointerException;

	/**
	 * @return The self object
	 */
	CpsObject getSelf();

	/**
	 * Set a new self
	 * 
	 * @param value The new self
	 * @throws NullPointerException Thrown when the value is null
	 */
	void setSelf(CpsObject value) throws NullPointerException;

	/**
	 * Get an arbitrary message property. This can be either a custom property
	 * or one of the predefined properties which also have direct getters.
	 * 
	 * @param name
	 * @return
	 */
	CpsVariable getProperty(String name);

	/**
	 * Set a new value for a given property. This can be used to set both custom
	 * properties as standard message properties.
	 * 
	 * @param name The name of the property to set.
	 * @param value The new value
	 * @throws NullPointerException Thrown when a standard property is set and
	 *             the new value is null
	 * @throws IllegalArgumentException Thrown when a standard property is set
	 *             and the new value does not match.
	 */
	void setProperty(String name, CpsVariable value) throws NullPointerException, IllegalArgumentException;

	/**
	 * @return The custom property names
	 */
	Collection<String> getCustomProperties();

	/**
	 * @return The list of all <b>set</b> properties. Message in the execution
	 *         model do not always have all standard properties set.
	 */
	Collection<String> getAllProperties();

	/**
	 * @see Cloneable
	 */
	CpsMessage clone() throws CloneNotSupportedException;
}
