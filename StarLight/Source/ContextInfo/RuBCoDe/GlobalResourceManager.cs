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
using System.Diagnostics;
using System.Runtime.Serialization;
using System.Runtime.CompilerServices;

namespace Composestar.StarLight.ContextInfo.RuBCoDe
{
    /// <summary>
    /// Keeps record of global message resources
    /// </summary>
    [DebuggerNonUserCode()]
    [Obsolete("No longer used")]
    public sealed class GlobalResourceManager
    {
        private static Object locker = new Object();
        private static GlobalResourceManager _instance;

        private Dictionary<long, SimpleBK> objmapping;
        private ObjectIDGenerator idgen;

        /// <summary>
        /// Get a bookkeeper for the specified opbject
        /// </summary>
        /// <returns></returns>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public static SimpleBK getBookKeeper(Object obj)
        {
            if (obj == null)
            {
                throw new ArgumentNullException("obj");
            }
            return instance()._getBookKeeper(obj);
        }

        /// <summary>
        /// Get the instance of the resource validator
        /// </summary>
        /// <returns></returns>
        public static GlobalResourceManager instance()
        {
            if (_instance == null)
            {
                lock (locker)
                {
                    if (_instance == null)
                    {
                        _instance = new GlobalResourceManager();
                    }
                }
            }
            return _instance;
        }

        private GlobalResourceManager()
        {
            objmapping = new Dictionary<long, SimpleBK>();
            idgen = new ObjectIDGenerator();
        }

        private SimpleBK _getBookKeeper(Object obj)
        {
            bool isNew;
            long objid = idgen.GetId(obj, out isNew);
            SimpleBK result;
            if (!objmapping.TryGetValue(objid, out result))
            {
                result = new SimpleBK(ResourceType.ArgumentEntry, String.Format("{0}#{1}", obj.GetType().Name, objid));
                Console.Error.WriteLine("Created bookkeeper {0} for {1}", result.Name, obj.ToString());
                objmapping.Add(objid, result);
            }
            return result;
        }
    }
}
