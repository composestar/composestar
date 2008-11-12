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

package Composestar.Java.FLIRT;

import java.util.WeakHashMap;

import Composestar.Java.FLIRT.Env.ObjectManager;

/**
 * @author Michiel Hendriks
 */
public class ObjectManagerStorage
{
	private static WeakHashMap<Object, ObjectManager> managers = new WeakHashMap<Object, ObjectManager>(100);

	/**
	 * Get the object manager for a specific class
	 * 
	 * @param forObject
	 * @return
	 */
	public static synchronized ObjectManager get(Object forObject)
	{
		return managers.get(forObject);
	}

	/**
	 * Register an object manager for a specific class
	 * 
	 * @param forObject
	 * @param man
	 */
	public static synchronized void put(Object forObject, ObjectManager man)
	{
		if (forObject == null)
		{
			return;
		}
		if (forObject.getClass() != null
				&& (forObject.getClass().getName().startsWith("java.") || forObject.getClass().getName().startsWith(
						"javax.")))
		{
			// don't store object managers for framework classes
			// TODO why not?
			return;
		}
		managers.put(forObject, man);
	}

	/**
	 * Remove an object manager. Called from the object manager when it's
	 * monitoring object vanished.
	 * 
	 * @param objectManager
	 */
	public static synchronized void removeObjectManager(ObjectManager objectManager)
	{
		managers.values().remove(objectManager);
	}
}
