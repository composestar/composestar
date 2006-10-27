#region Using directives
using System;
using System.Collections.Generic;
using System.Text;
using System.Security.Permissions;
using System.IO;
using System.Reflection;
using Microsoft.Win32;

using Mono.Cecil;
using Mono.Cecil.Cil;

using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.WeaveSpec;
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions;
using Composestar.StarLight.Entities.WeaveSpec.Instructions;
using Composestar.StarLight.Utilities.Interfaces;

using Composestar.StarLight.Weaving.Strategies;
using Composestar.StarLight.CoreServices.Exceptions;
#endregion

namespace Composestar.StarLight.ILWeaver.WeaveStrategy
{
    /// <summary>
    /// This class is responsible for calling the correct strategy.
    /// </summary>
    public sealed class FilterActionStrategyDispatcher
    {
        private static Dictionary<string, FilterActionWeaveStrategy> strategyMapping;
        private static FilterActionWeaveStrategy defaultStrategy = new DefaultWeaveStrategy();
        private static object lockObject = new Object();

        /// <summary>
        /// Gets the filter action weave strategy.
        /// </summary>
        /// <param name="filterAction">The filter action.</param>
        /// <returns></returns>
        public static FilterActionWeaveStrategy GetFilterActionWeaveStrategy(string filterAction)
        {
            if (string.IsNullOrEmpty(filterAction))
                throw new ArgumentNullException("filterAction");

            if (strategyMapping == null)
            {
                lock (lockObject)
                {
                    if (strategyMapping == null)
                        CreateStrategyMapping();
                } // lock
            }

            FilterActionWeaveStrategy strategy;

            if (strategyMapping.TryGetValue(filterAction, out strategy))
                return strategy;
            else
                return defaultStrategy;
        }

        /// <summary>
        /// Creates the strategy mapping.
        /// </summary>
        /// <remarks>Uses reflection to look for weaving strategies.</remarks> 
        private static void CreateStrategyMapping()
        {
            strategyMapping = new Dictionary<string, FilterActionWeaveStrategy>();

            string strategiesPath = GetWeaveStrategiesLocation();

            if (string.IsNullOrEmpty(strategiesPath))
                throw new ILWeaverException(Properties.Resources.StrategiesFolderNotFound);

            string[] dir = Directory.GetFiles(strategiesPath, "*.dll");
            Assembly assembly;

            foreach (string filename in dir)
            {
                string dll = Path.GetFileNameWithoutExtension(filename);
                assembly = Assembly.LoadFrom(filename);
                  
                Type[] types = assembly.GetTypes();
                foreach (Type t in types)
                {
                    if (t.BaseType != null && t.BaseType.Equals(typeof(FilterActionWeaveStrategy)))
                    {
                        WeaveStrategyAttribute[] wsas = (WeaveStrategyAttribute[])t.GetCustomAttributes(typeof(WeaveStrategyAttribute), true);
                        if (wsas.Length > 0)
                        {
                            // dynamically load thisclass
                            FilterActionWeaveStrategy strategy = (FilterActionWeaveStrategy)Activator.CreateInstance(t);
                            foreach (WeaveStrategyAttribute wsa in wsas)
                            {
                                // Check if we already have this strategy
                                if (strategyMapping.ContainsKey(wsa.WeavingStrategyName))
                                    throw new ILWeaverException(String.Format(Properties.Resources.WeaveStrategyNotUnique, wsa.WeavingStrategyName));
                                else
                                    strategyMapping.Add(wsa.WeavingStrategyName, strategy);
                            } // foreach  (wsa)
                        } // if
                        else
                        {
                            throw new ILWeaverException(string.Format(Properties.Resources.WeaveStrategyAttributeNotFound, t.FullName, typeof(FilterActionWeaveStrategy).Name, typeof(WeaveStrategyAttribute).FullName));
                        } // else
                    } // if
                } // foreach 
            } // foreach 
            
        }

        /// <summary>
        /// Get weave strategies location
        /// </summary>
        /// <returns>String</returns>
        private static string GetWeaveStrategiesLocation()
        {
            // Retrieve the settings from the registry
            RegistryPermission keyPermissions = new RegistryPermission(
               RegistryPermissionAccess.Read, @"HKEY_LOCAL_MACHINE\Software\Composestar\StarLight");

            RegistryKey regKey = Registry.LocalMachine.OpenSubKey(@"Software\Composestar\StarLight");

            if (regKey != null)
            {
                return (string)regKey.GetValue("WeaveStrategiesFolder", "");
            }
            else
            {
                return string.Empty;
            }

        } // GetWeaveStrategiesLocation()

    }
}
