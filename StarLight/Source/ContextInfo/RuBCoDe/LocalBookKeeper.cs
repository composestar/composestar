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
    /// BookKeeper that manages all standard local resources of a message: target, selector, message, return, argslist
    /// </summary>
    [DebuggerNonUserCode()]
    [Obsolete("No longer used")]
    public sealed class LocalBookKeeper : BookKeeper
    {
        /// <summary>
        /// List of operations per resource type
        /// </summary>
        private Dictionary<ResourceType, List<string>> operations;

        /// <summary>
        /// Create a new instance
        /// </summary>
        public LocalBookKeeper()
        {
            operations = new Dictionary<ResourceType, List<string>>();
        }

        /// <summary>
        /// Reset the bookkeeper to it's initial state
        /// </summary>
        public void reset()
        {
            foreach (ResourceType resType in operations.Keys)
            {
                List<String> ops;
                if (!operations.TryGetValue(resType, out ops))
                {
                    continue;
                }
                ops.Clear();
            }
        }

        /// <summary>
        /// Add an operation to the current list.
        /// </summary>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public void AddOperation(ResourceType resource, String op)
        {
            if (DEBUG)
            {
                lock (Console.Error)
                {
                    Console.Error.WriteLine("[RescOp] in thread #{0} : {1}.{2}", Thread.CurrentThread.ManagedThreadId,
                        Enum.GetName(typeof(ResourceType), resource), op);
                }
            }
            List<string> lst;
            if (!operations.TryGetValue(resource, out lst))
            {
                lst = new List<string>();
                operations.Add(resource, lst);
            }
            lst.Add(op);
        }

        /// <summary>
        /// Print a report of the current resource values.
        /// </summary>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public override void report()
        {
            foreach (ResourceType resType in operations.Keys)
            {
                List<String> ops;
                if (!operations.TryGetValue(resType, out ops))
                {
                    continue;
                }
                StringBuilder sb = new StringBuilder();
                sb.Append(Enum.GetName(typeof(ResourceType), resType));
                sb.Append(" = ");
                foreach (String s in ops)
                {
                    sb.Append(s);
                    sb.Append(", ");
                }
                Console.Error.WriteLine(sb.ToString());
            }
        }

        /// <summary>
        /// Validate the current operation sequence
        /// </summary>
        public override void validate()
        {
            ResourceValidator rv = ResourceValidator.instance();
            foreach (ResourceType resType in operations.Keys)
            {
                List<String> ops;
                if (!operations.TryGetValue(resType, out ops))
                {
                    continue;
                }
                if (ops.Count == 0)
                {
                    continue;
                }
                rv.validate(resType, ops, this);
            }
        }
    }
}
