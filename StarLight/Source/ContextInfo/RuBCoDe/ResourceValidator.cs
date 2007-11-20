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
using System.Reflection;
using System.Runtime.CompilerServices;
using System.Diagnostics;
using System.Text.RegularExpressions;

namespace Composestar.StarLight.ContextInfo.RuBCoDe
{
    /// <summary>
    /// Performs validation of resources according to the conflict rules
    /// </summary>
    [DebuggerNonUserCode()]
    public sealed class ResourceValidator
    {
        private static Object locker = new Object();
        private static ResourceValidator _instance;

        private List<string> loadedAsms;

        private Dictionary<ResourceType, List<Regex>> constraints;
        private Dictionary<ResourceType, List<Regex>> assertions;

        /// <summary>
        /// Get the instance of the resource validator
        /// </summary>
        /// <returns></returns>
        public static ResourceValidator instance()
        {
            if (_instance == null)
            {
                lock (locker)
                {
                    if (_instance == null)
                    {
                        _instance = new ResourceValidator();
                    }
                }
            }
            return _instance;
        }

        /// <summary>
        /// Validate the resource operation
        /// </summary>
        /// <param name="rt"></param>
        /// <param name="operations"></param>
        /// <param name="bk"></param>
        public void validate(ResourceType rt, List<string> operations, BookKeeper bk)
        {
            if (operations.Count == 0) return;

            StringBuilder sb = new StringBuilder();
            foreach (String s in operations)
            {
                sb.Append(s);
            }
            string ops = sb.ToString();
            sb = null;

            List<Regex> lst;
            if (constraints.TryGetValue(rt, out lst))
            {
                foreach (Regex rgx in lst)
                {
                    if (rgx.IsMatch(ops))
                    {
                        throw new ResourceOperationException(rt, ops, rgx.ToString(), true, bk);
                    }
                }
            }
            if (assertions.TryGetValue(rt, out lst))
            {
                foreach (Regex rgx in lst)
                {
                    if (!rgx.IsMatch(ops))
                    {
                        throw new ResourceOperationException(rt, ops, rgx.ToString(), false, bk);
                    }
                }
            }
        }

        private ResourceValidator()
        {
            constraints = new Dictionary<ResourceType, List<Regex>>();
            assertions = new Dictionary<ResourceType, List<Regex>>();
            loadedAsms = new List<string>();
            loadRules();
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        private void loadRules()
        {
            foreach (Assembly asm in AppDomain.CurrentDomain.GetAssemblies())
            {
                if (loadedAsms.Contains(asm.FullName))
                {
                    continue;
                }
                loadedAsms.Add(asm.FullName);
                procAssembly(asm);
            }
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        private void procAssembly(Assembly asm)
        {
            foreach (Object ca in asm.GetCustomAttributes(typeof(ConflictRuleAttribute), true))
            {
                ConflictRuleAttribute cae = (ConflictRuleAttribute)ca;
                ResourceType rt = BookKeeper.getResourceType(cae.Resource);
                List<Regex> lst;
                if (cae.Constraint)
                {
                    if (!constraints.TryGetValue(rt, out lst))
                    {
                        lst = new List<Regex>();
                        constraints.Add(rt,lst);
                    }
                }
                else
                {
                    if (!assertions.TryGetValue(rt, out lst))
                    {
                        lst = new List<Regex>();
                        assertions.Add(rt, lst);
                    }
                }
                try
                {
                    lst.Add(new Regex(cae.Pattern));
                    Console.Error.WriteLine("New conflict rule for {0}: {1}", cae.Resource, cae.Pattern);
                }
                catch (ArgumentException e)
                {
                    // TODO: how to handle?
                }

            }
        }
    }
}
