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
                    lst.Add(new Regex(cae.Expression));
                    Console.Error.WriteLine("New conflict rule for {0}: {1}", cae.Resource, cae.Expression);
                }
                catch (ArgumentException e)
                {
                    // TODO: how to handle?
                }

            }
        }
    }
}
