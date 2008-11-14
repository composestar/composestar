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

	// Note: the following code shouldn't be needed, the ObjectManagers would
	// automatically
	// become garbage collected when their associated object is no longer in the
	// WeakHashMap

	// private static Map<Reference<? extends Object>, ObjectManager> refToMan =
	// new HashMap<Reference<? extends Object>, ObjectManager>();
	//
	// private static ReferenceQueue<Object> managerQueue = new
	// ReferenceQueue<Object>();
	//	
	// /**
	// * Get a managed weak reference for an Object Manager. These references
	// are
	// * internally monitor so that ObjectManager release their resources when
	// * their monitoring object is garbage collected.
	// *
	// * @param forObject
	// * @param objectManager
	// * @return
	// */
	// public static WeakReference<Object> getWeakReference(Object forObject,
	// ObjectManager objectManager)
	// {
	// WeakReference<Object> ref = new WeakReference<Object>(forObject,
	// managerQueue);
	// refToMan.put(ref, objectManager);
	// runCleanupThread();
	// return ref;
	// }
	//
	// private static boolean cleanupRunning = false;
	//
	// private static void runCleanupThread()
	// {
	// if (cleanupRunning)
	// {
	// return;
	// }
	// synchronized (refToMan)
	// {
	// cleanupRunning = true;
	// new Thread(new CleanupThread(), "ObjectManagerGC");
	// }
	// }
	//
	// /**
	// * The clean up thread that checks the reference queue and calls cleanup
	// on
	// * the associated manager objects.
	// *
	// * @author Michiel Hendriks
	// */
	// private static class CleanupThread implements Runnable
	// {
	// public void run()
	// {
	// while (!managers.isEmpty())
	// {
	// try
	// {
	// Reference<? extends Object> ref = managerQueue.remove();
	// ObjectManager om = refToMan.get(ref);
	// if (om != null)
	// {
	// om.cleanup();
	// }
	// refToMan.remove(ref);
	// }
	// catch (InterruptedException e)
	// {
	// }
	// }
	// synchronized (refToMan)
	// {
	// cleanupRunning = false;
	// }
	// }
	// }
}
