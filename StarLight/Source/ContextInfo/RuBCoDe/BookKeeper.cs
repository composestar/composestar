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
            return ResourceType.Unknown;
        }

        /// <summary>
        /// Validate the resource operations. When not validate a runtime exception is thrown.
        /// </summary>
        public abstract void validate();
    }

    /// <summary>
    /// The different resource types the system knows about
    /// </summary>
    public enum ResourceType
    {
        /// <summary>
        /// Unknown resource type
        /// </summary>
        Unknown = -1,
        /// <summary>
        /// The message. This resource does not actually exist in memory?
        /// </summary>
        Message = 0,
        /// <summary>
        /// The target of a message
        /// </summary>
        Target = 1,
        /// <summary>
        /// The selector of a message
        /// </summary>
        Selector = 2,
        /// <summary>
        /// The return value
        /// </summary>
        Return = 3,
        /// <summary>
        /// The method call argument list itself
        /// </summary>
        ArgumentList = 4,
        /// <summary>
        /// A method call argument entry
        /// </summary>
        ArgumentEntry = 5,
    }
}
