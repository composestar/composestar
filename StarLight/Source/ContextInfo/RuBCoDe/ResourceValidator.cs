using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.Runtime.CompilerServices;
using System.Diagnostics;

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

        private Dictionary<ResourceType, List<string>> constraints;
        private Dictionary<ResourceType, List<string>> assertions;

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
        public void validate(ResourceType rt, List<string> operations)
        {
            // TODO
        }

        private ResourceValidator()
        {
            constraints = new Dictionary<ResourceType, List<string>>();
            assertions = new Dictionary<ResourceType, List<string>>();
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
                List<string> lst;
                if (cae.Constraint) // is constraint
                {
                    if (!constraints.TryGetValue(rt, out lst))
                    {
                        lst = new List<string>();
                        constraints.Add(rt,lst);
                    }
                }
                else
                {
                    if (!assertions.TryGetValue(rt, out lst))
                    {
                        lst = new List<string>();
                        assertions.Add(rt, lst);
                    }
                }
                lst.Add(cae.Expression);
                Console.Error.WriteLine("New conflict rule for {0}: {1}", cae.Resource, cae.Expression);
            }
        }
    }
}
