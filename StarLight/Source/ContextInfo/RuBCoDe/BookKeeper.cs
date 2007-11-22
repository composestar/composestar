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
    /// This class will keep track of resource operations of a single resource
    /// </summary>
    [DebuggerNonUserCode()]
    public abstract class BookKeeper
    {
        /// <summary>
        /// If true print debug output on StdErr
        /// </summary>
        protected static readonly bool DEBUG = true;

        /// <summary>
        /// Read operation
        /// </summary>
        public static readonly string READ = "read";
        /// <summary>
        /// Write operation
        /// </summary>
        public static readonly string WRITE = "write";

        /// <summary>
        /// Print the current details to the standard output (for debugging)
        /// </summary>
        public abstract void report();

        /// <summary>
        /// Validate the resource operations. When not validate a runtime exception is thrown.
        /// </summary>
        public abstract void validate();

        /// <summary>
        /// Convert a string to the proper resource type
        /// </summary>
        /// <param name="type"></param>
        /// <returns></returns>
        public static ResourceType getResourceType(string type)
        {
            switch (type.ToLower())
            {
                case "msg":
                case "message":
                    return ResourceType.Message;
                case "target":
                    return ResourceType.Target;
                case "selector":
                    return ResourceType.Selector;
                case "return":
                    return ResourceType.Return;
                case "args":
                case "arglist":
                    return ResourceType.ArgumentList;
            }
            // arg0, arg1, arg2, ...
            if (type.ToLower().StartsWith("arg"))
            {
                return ResourceType.ArgumentEntry;
            }
            return ResourceType.Custom;
        }

        /// <summary>
        /// Convert a string to the proper resource type.
        /// </summary>
        /// <param name="type"></param>
        /// <param name="ordinal">contains the value of the argument entry ordinal if present, -1 otherwise</param>
        /// <returns></returns>
        public static ResourceType getResourceType(string type, out short ordinal)
        {
            ResourceType res = getResourceType(type);
            if (res == ResourceType.ArgumentEntry && type.Length > 3)
            {
                if (!short.TryParse(type.Substring(3), out ordinal))
                {
                    ordinal = -1;
                }
            }
            else
            {
                ordinal = -1;
            }
            return res;
        }

        /// <summary>
        /// Convert a resource type to a string representation. Don't use this method for the 
        /// types Unknown and Custom. They don't have a valid string representation.
        /// </summary>
        /// <param name="type"></param>
        /// <returns></returns>
        public static string resourceTypeAsString(ResourceType type)
        {
            switch (type)
            {
                case ResourceType.ArgumentEntry:
                    return "arg";
                case ResourceType.ArgumentList:
                    return "args";
                case ResourceType.Message:
                    return "message";
                case ResourceType.Return:
                    return "return";
                case ResourceType.Selector:
                    return "selector";
                case ResourceType.Target:
                    return "target";
                case ResourceType.Custom:
                    return "?CustomResource";
            }
            return "?UnknownResource";
        }
    }

    /// <summary>
    /// The different resource types the system knows about
    /// </summary>
    public enum ResourceType
    {
        /// <summary>
        /// Unknown resource type
        /// </summary>
        Unknown,
        /// <summary>
        /// The message. This resource does not actually exist in memory?
        /// </summary>
        Message,
        /// <summary>
        /// The target of a message
        /// </summary>
        Target,
        /// <summary>
        /// The selector of a message
        /// </summary>
        Selector,
        /// <summary>
        /// The return value
        /// </summary>
        Return,
        /// <summary>
        /// The method call argument list itself
        /// </summary>
        ArgumentList,
        /// <summary>
        /// A method call argument entry
        /// </summary>
        ArgumentEntry,
        /// <summary>
        /// A custom resource
        /// </summary>
        Custom,
    }
}
