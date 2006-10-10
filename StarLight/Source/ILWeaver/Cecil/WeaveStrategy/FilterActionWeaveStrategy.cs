using System;
using System.Collections.Generic;
using System.Text;

using Mono.Cecil;
using Mono.Cecil.Cil;

using Composestar.Repository.LanguageModel.Inlining;

namespace Composestar.StarLight.ILWeaver
{
    public abstract class FilterActionWeaveStrategy
    {
        private static Dictionary<string, FilterActionWeaveStrategy> strategyMapping;

        /// <summary>
        /// Returns the name of the FilterAction for which this is the 
        /// weaving strategy.
        /// </summary>
        public abstract String FilterActionName
        {
            get;
        }


        public abstract void Weave(CecilInliningInstructionVisitor visitor, FilterAction filterAction,
            MethodDefinition originalCall);

        public static FilterActionWeaveStrategy GetFilterActionWeaveStrategy(string filterAction)
        {
            if(strategyMapping == null)
            {
                CreateStrategyMapping();
            }

            return strategyMapping[filterAction];
        }

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
