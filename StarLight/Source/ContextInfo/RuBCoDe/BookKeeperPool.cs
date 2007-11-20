#region License
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
#endregion

using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.CompilerServices;
using System.Diagnostics;

namespace Composestar.StarLight.ContextInfo.RuBCoDe
{
    /// <summary>
    /// Manages a pool of free bookkeeper instances
    /// </summary>
    [DebuggerNonUserCode()]
    public class BookKeeperPool
    {
        private static Object locker = new Object();
        private static BookKeeperPool _instance;

        private Queue<SimpleBK> freeSimple;

        /// <summary>
        /// Returns a free LocalBookKeeper instance
        /// </summary>
        /// <returns></returns>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public static SimpleBK getSimpleBK(ResourceType rtype, string name)
        {
            SimpleBK bk;
            BookKeeperPool ins = instance();
            if (ins.freeSimple.Count > 0)
            {
                bk = ins.freeSimple.Dequeue();
                bk.init(rtype, name);
                return bk;
            }
            else
            {
                Console.Error.WriteLine("### Creating new SimpleBK, size = {0} ###", instance().freeSimple.Count);
                return new SimpleBK(rtype, name);
            }            
        }

        /// <summary>
        /// Adds the local bookkeeper to the pool
        /// </summary>
        /// <param name="bk"></param>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public static void release(ref SimpleBK bk)
        {
            bk.reset();
            instance().freeSimple.Enqueue(bk);
            bk = null;
            Console.Error.WriteLine("### Adding SimpleBK to queue, size = {0} ###", instance().freeSimple.Count);
        }

        private static BookKeeperPool instance()
        {
            if (_instance == null)
            {
                lock (locker)
                {
                    if (_instance == null)
                    {
                        _instance = new BookKeeperPool();
                    }
                }
            }
            return _instance;
        }

        private BookKeeperPool()
        {
            Console.Error.WriteLine("### Creating BookKeeperPool ###");
            freeSimple = new Queue<SimpleBK>();
        }
    }
}
