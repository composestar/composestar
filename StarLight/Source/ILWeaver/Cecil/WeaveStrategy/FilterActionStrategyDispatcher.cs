using System;
using System.Collections.Generic;
using System.Text;

using Mono.Cecil;
using Mono.Cecil.Cil;

using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.WeaveSpec;
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions;
using Composestar.StarLight.Entities.WeaveSpec.Instructions;
using Composestar.StarLight.Utilities.Interfaces;

using Composestar.StarLight.Weaving.Strategies;  

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

            if (strategyMapping.ContainsKey(filterAction))
            {
                return strategyMapping[filterAction];
            }
            else
            {
                return defaultStrategy;
            }

        }

        /// <summary>
        /// Creates the strategy mapping.
        /// </summary>
        private static void CreateStrategyMapping()
        {
            strategyMapping = new Dictionary<string, FilterActionWeaveStrategy>();

            // TODO create strategies based on file contents.

            FilterActionWeaveStrategy strategy;
            
            strategy = new AdviceActionWeaveStrategy();
            strategyMapping.Add(strategy.FilterActionName, strategy);
            strategyMapping.Add("BeforeAction", strategy);
            strategyMapping.Add("AfterAction", strategy);

            strategy = new ContinueActionWeaveStrategy();
            strategyMapping.Add(strategy.FilterActionName, strategy);

            strategy = new DispatchActionWeaveStrategy();
            strategyMapping.Add(strategy.FilterActionName, strategy);

            strategy = new ErrorActionWeaveStrategy();
            strategyMapping.Add(strategy.FilterActionName, strategy);

            strategy = new SubstitutionActionWeaveStrategy();
            strategyMapping.Add(strategy.FilterActionName, strategy);
        }

    }
}
