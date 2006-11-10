#region Using directives
using System;
using System.Collections.Generic;
using System.Text;
using System.Security.Permissions;
using System.IO;
using System.Reflection;
using System.Globalization;
using System.Diagnostics.CodeAnalysis; 
   
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
        private FilterActionStrategyDispatcher()
        {

        }

        private static Dictionary<string, FilterActionWeaveStrategy> _strategyMapping;
        private static FilterActionWeaveStrategy _defaultStrategy = new DefaultWeaveStrategy();
        private static object _lockObject = new Object();

        /// <summary>
        /// Gets the filter action weave strategy.
        /// </summary>
        /// <param name="filterAction">The filter action.</param>
        /// <returns></returns>
        public static FilterActionWeaveStrategy GetFilterActionWeaveStrategy(string filterAction)
        {
            if (string.IsNullOrEmpty(filterAction))
                throw new ArgumentNullException("filterAction");

            if (_strategyMapping == null)
            {
                lock (_lockObject)
                {
                    if (_strategyMapping == null)
                        CreateStrategyMapping();
                } 
            }

            FilterActionWeaveStrategy strategy;

            if (_strategyMapping.TryGetValue(filterAction, out strategy))
                return strategy;
            else
                return _defaultStrategy;
        }

        /// <summary>
        /// Creates the strategy mapping.
        /// </summary>
        /// <remarks>Uses reflection to look for weaving strategies.</remarks> 
        private static void CreateStrategyMapping()
        {
            _strategyMapping = new Dictionary<string, FilterActionWeaveStrategy>();

            string strategiesPath = GetWeaveStrategiesLocation();

            if (string.IsNullOrEmpty(strategiesPath))
                throw new ILWeaverException(Properties.Resources.StrategiesFolderNotFound);

            string[] dir = Directory.GetFiles(strategiesPath, "*.dll");
            Assembly assembly;

            foreach (string filename in dir)
            {
                assembly = Assembly.LoadFrom(filename);
                  
                Type[] types = assembly.GetTypes();
                foreach (Type t in types)
                {
                    if (t.BaseType != null && t.BaseType.Equals(typeof(FilterActionWeaveStrategy)))
                    {
                        WeaveStrategyAttribute[] wsas = (WeaveStrategyAttribute[])t.GetCustomAttributes(typeof(WeaveStrategyAttribute), true);
                        if (wsas.Length > 0)
                        {
                            // dynamically load this class
                            FilterActionWeaveStrategy strategy = (FilterActionWeaveStrategy)Activator.CreateInstance(t);
                            foreach (WeaveStrategyAttribute wsa in wsas)
                            {
                                // Check if we already have this strategy
                                if (_strategyMapping.ContainsKey(wsa.WeavingStrategyName))
                                    throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
                                        Properties.Resources.WeaveStrategyNotUnique, wsa.WeavingStrategyName));
                                else
                                    _strategyMapping.Add(wsa.WeavingStrategyName, strategy);
                            } // foreach  (wsa)
                        } // if
                        else
                        {
                            throw new ILWeaverException(string.Format(CultureInfo.CurrentCulture,
                                Properties.Resources.WeaveStrategyAttributeNotFound, t.FullName, typeof(FilterActionWeaveStrategy).Name, typeof(WeaveStrategyAttribute).FullName));
                        } // else
                    } // if
                } // foreach 
            } // foreach 

            if (_strategyMapping.Count == 0)
            {
                // We did not find any strategies. Although this is possible (only default strategy), we still require the dispatch, 
                // advice, error and so on strategies.
                throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
                    Properties.Resources.NoWeavingStrategiesFound, strategiesPath)); 
            }

        }

        /// <summary>
        /// Get weave strategies location
        /// </summary>
        /// <returns>String</returns>
        [RegistryPermissionAttribute(System.Security.Permissions.SecurityAction.Demand,
            Read = "HKEY_LOCAL_MACHINE\\Software\\Composestar\\StarLight")]
        private static string GetWeaveStrategiesLocation()
        {
            
            RegistryKey regKey = Registry.LocalMachine.OpenSubKey(@"Software\Composestar\StarLight");

            if (regKey != null)
            {
                return (string)regKey.GetValue("WeaveStrategiesFolder", "");
            }
            else
            {
                return string.Empty;
            }

        } 

    }
}
