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
    public class BookKeeper
    {
        /// <summary>
        /// If true print debug output on StdErr
        /// </summary>
        private static readonly bool DEBUG = true;

        /// <summary>
        /// Read operation
        /// </summary>
        public static readonly string READ = "read";
        /// <summary>
        /// Write operation
        /// </summary>
        public static readonly string WRITE = "write";

        /// <summary>
        /// The type of resource this BookKeeper keeps track off
        /// </summary>
        protected ResourceType resType;

        /// <summary>
        /// List of performed operations
        /// </summary>
        protected List<String> operations;

        /// <summary>
        /// Cerate a new resource book keeper for a given type
        /// </summary>
        /// <param name="type">the type this bookkeeper tracks</param>
        public BookKeeper(ResourceType type)
        {
            resType = type;
            operations = new List<String>();
        }

        /// <summary>
        /// Add an operation to the current list.
        /// </summary>
        /// <param name="op"></param>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public void AddOperation(String op)
        {
            if (DEBUG)
            {
                lock (Console.Error)
                {
                    Console.Error.WriteLine("[RescOp] in thread #{0} : {1}.{2}", Thread.CurrentThread.ManagedThreadId,
                        Enum.GetName(typeof(ResourceType), resType), op);
                }
            }
            operations.Add(op);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();
            sb.Append(Enum.GetName(typeof(ResourceType), resType));
            sb.Append(" = ");
            foreach (String s in operations)
            {
                sb.Append(s);
                sb.Append(", ");
            }
            return sb.ToString();
        }
    }

    /// <summary>
    /// The different resource types the system knows about
    /// </summary>
    public enum ResourceType
    {
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
