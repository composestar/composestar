using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;   

namespace BasicTests
{
    [FilterActionAttribute("LoggingAction", FilterActionAttribute.FilterFlowBehaviour.Continue, FilterActionAttribute.MessageSubstitutionBehaviour.Original)]
    public class LoggingAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {
            Console.WriteLine("Log : " + context.StartSelector);
        }
    }
}
