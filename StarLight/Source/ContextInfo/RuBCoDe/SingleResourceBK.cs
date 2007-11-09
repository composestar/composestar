using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.CompilerServices;
using System.Threading;

namespace Composestar.StarLight.ContextInfo.RuBCoDe
{
    /// <summary>
    /// A bookkeeper for a single resource
    /// </summary>
    public class SingleResourceBK : BookKeeper
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
        /// The type of resource. This will usually be ResourceType.ArgumentEntry because other 
        /// resources are managed by the LocalBookKeeper.
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
        public SingleResourceBK(ResourceType rtype, string rname)
        {
            type = rtype;
            name = rname;
            operations = new List<string>();
        }

        /// <summary>
        /// Add an operation to the current list.
        /// </summary>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public void AddOperation(String op)
        {
            if (DEBUG)
            {
                lock (Console.Error)
                {
                    Console.Error.WriteLine("[RescOp] in thread #{0} : [{3}] {1}.{2}", Thread.CurrentThread.ManagedThreadId,
                        Enum.GetName(typeof(ResourceType), type), op, name);
                }
            }
            operations.Add(op);
        }

        /// <summary>
        /// Print a report of the current resource values.
        /// </summary>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public override void report()
        {
            StringBuilder sb = new StringBuilder();
            sb.Append("[");
            sb.Append(name);
            sb.Append("] ");
            sb.Append(Enum.GetName(typeof(ResourceType), type));
            sb.Append(" = ");
            foreach (String s in operations)
            {
                sb.Append(s);
                sb.Append(", ");
            }
            Console.Error.WriteLine(sb.ToString());

        }

        /// <summary>
        /// Validate the current operation sequence
        /// </summary>
        public override void validate()
        {
            ResourceValidator rv = ResourceValidator.instance();
            rv.validate(type, operations, this);
        }
    }
}
