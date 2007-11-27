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

        private IList<string> loadedAsms;

        private IList<ConflictRule> rules;

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
        /// <param name="resource">the resource name</param>
        /// <param name="altres">alternative name for the resource (used for arg vs arg0)</param>
        /// <param name="operations"></param>
        /// <param name="bk"></param>
        /// <exception cref="ResourceOperationException">Thrown when a rule is violated.</exception>
        public void validate(string resource, string altres, IList<string> operations, BookKeeper bk)
        {
            if (operations.Count == 0) return;

            foreach (ConflictRule rule in rules)
            {
                if ("*".Equals(rule.Resource) || resource.Equals(rule.Resource) || 
                    (!String.IsNullOrEmpty(altres) && altres.Equals(rule.Resource)))
                {
                    if (rule.violatesRule(operations))
                    {
                        StringBuilder sb = new StringBuilder();
                        sb.Append("[");
                        foreach (String s in operations)
                        {
                            if (sb.Length > 1)
                            {
                                sb.Append(", ");
                            }
                            sb.Append(s);
                        }
                        sb.Append("]");
                        throw new ResourceOperationException(resource, sb.ToString(), rule, bk);
                    }
                }
            }
        }

        private ResourceValidator()
        {
            rules = new List<ConflictRule>();
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

                string resname;
                if ("*".Equals(cae.Resource))
                {
                    resname = "*";
                }
                else
                {
                    short ord;
                    ResourceType rt = BookKeeper.getResourceType(cae.Resource, out ord);
                    if (rt == ResourceType.Custom)
                    {
                        resname = cae.Resource.ToLower();
                    }
                    else if (rt == ResourceType.ArgumentEntry && ord > -1)
                    {
                        resname = BookKeeper.resourceTypeAsString(rt) + ord;
                    }
                    else
                    {
                        resname = BookKeeper.resourceTypeAsString(rt);
                    }
                }

                bool add = true;
                foreach (ConflictRule cr in rules)
                {
                    if (cr.Constraint == cae.Constraint && cr.Resource.Equals(resname) && cr.Pattern.Equals(cae.Pattern))
                    {
                        add = false;
                        break;
                    }
                }
                if (add)
                {
                    try
                    {
                        ConflictRule cr = new ConflictRule(cae.Pattern, resname, cae.Constraint, cae.Message);
                        rules.Add(cr);
                    }
                    catch (ArgumentException e)
                    {
                        // TODO: handle expression compile error
                    }
                }
            }
        }
    }
}
