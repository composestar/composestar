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
using System.Threading;
using System.Diagnostics;

namespace Composestar.StarLight.ContextInfo.RuBCoDe
{
    /// <summary>
    /// A bookkeeper for a single resource
    /// </summary>
    [DebuggerNonUserCode()]
    public class SimpleBK : BookKeeper
    {
        private string name;
        private ResourceType type;
        private List<string> operations;

        /// <summary>
        /// The name of this resource (if any)
        /// </summary>
        public string Name
        {
            get { return name; }
        }

        /// <summary>
        /// The type of resource type.
        /// </summary>
        public ResourceType ResourceType
        {
            get { return type; }
        }

        /// <summary>
        /// Create a bookkeeper that keeps track of a single instance
        /// </summary>
        /// <param name="rtype"></param>
        /// <param name="rname"></param>
        public SimpleBK(ResourceType rtype, string rname)
        {
            init(rtype, rname);
            operations = new List<string>();
        }

        /// <summary>
        /// Reset this instance to its initial state
        /// </summary>
        public void reset()
        {
            type = ResourceType.Unknown;
            name = String.Empty;
            operations.Clear();
        }

        /// <summary>
        /// Initialize this bookkeeper
        /// </summary>
        /// <param name="rtype"></param>
        /// <param name="rname">if null a string representation of the type will be used</param>
        public void init(ResourceType rtype, string rname)
        {
            type = rtype;
            if (String.IsNullOrEmpty(rname))
            {
                name = resourceTypeAsString(type);
            }
            else
            {
                name = rname;
            }
        }

        /// <summary>
        /// Add an operation to the current list.
        /// </summary>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public void AddOperation(String op)
        {
#if DEBUG
            lock (Console.Error)
            {
                Console.Error.WriteLine("<SimpleBK> in thread #{0} : [{3}] {1}.{2}", Thread.CurrentThread.ManagedThreadId,
                    name, op, resourceTypeAsString(type));
            }
#endif
            operations.Add(op);
        }

#if DEBUG
        /// <summary>
        /// Print a report of the current resource values.
        /// </summary>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public override void report()
        {
            StringBuilder sb = new StringBuilder();
            sb.Append("@BookKeeping [");
            sb.Append(resourceTypeAsString(type));
            sb.Append("] ");
            sb.Append(name);
            sb.Append(" = ");
            foreach (String s in operations)
            {
                sb.Append(s);
                sb.Append(", ");
            }
            Console.Error.WriteLine(sb.ToString());
        }
#endif

        /// <summary>
        /// Validate the current operation sequence
        /// </summary>
        public override void validate()
        {
            ResourceValidator rv = ResourceValidator.instance();
            string altname = null;
            if (type == ResourceType.ArgumentEntry)
            {
                altname = resourceTypeAsString(type);
            }
            rv.validate(name, altname, operations, this);
        }
    }
}
