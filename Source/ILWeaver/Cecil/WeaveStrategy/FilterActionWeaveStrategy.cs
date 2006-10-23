using System;
using System.Collections.Generic;
using System.Text;

using Mono.Cecil;
using Mono.Cecil.Cil;

using Composestar.StarLight.Concerns;
using Composestar.StarLight.LanguageModel;
using Composestar.StarLight.WeaveSpec;
using Composestar.StarLight.WeaveSpec.ConditionExpressions;
using Composestar.StarLight.WeaveSpec.Instructions;

namespace Composestar.StarLight.ILWeaver
{

    /// <summary>
    /// TODO generate comment
    /// </summary>
    public abstract class FilterActionWeaveStrategy
    {
        private static Dictionary<string, FilterActionWeaveStrategy> strategyMapping;
        private static FilterActionWeaveStrategy defaultStrategy = new DefaultWeaveStrategy();
        private static object lockObject = new Object();

        /// <summary>
        /// Returns the name of the FilterAction for which this is the 
        /// weaving strategy.
        /// </summary>
        public abstract String FilterActionName
        {
            get;
        }


        /// <summary>
        /// Generate the code which has to be inserted at the place of the filter specified by the visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        /// <param name="filterAction">The filter action.</param>
        /// <param name="originalCall">The original call.</param>
        public abstract void Weave(CecilInliningInstructionVisitor visitor, FilterAction filterAction,
            MethodDefinition originalCall);

        /// <summary>
        /// Gets the filter action weave strategy.
        /// </summary>
        /// <param name="filterAction">The filter action.</param>
        /// <returns></returns>
        public static FilterActionWeaveStrategy GetFilterActionWeaveStrategy(string filterAction)
        {
            if (strategyMapping == null)
            {
                lock (lockObject)
                {
                    if (strategyMapping == null) CreateStrategyMapping();
                } // lock
            }

            if(strategyMapping.ContainsKey(filterAction))
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
