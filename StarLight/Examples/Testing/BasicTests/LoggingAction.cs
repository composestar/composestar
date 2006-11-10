using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;   

namespace BasicTests
{
    [FilterActionAttribute("LoggingAction", FilterActionAttribute.FilterFlowBehavior.Continue, FilterActionAttribute.MessageSubstitutionBehavior.Original)]
    public class LoggingAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {
            Console.WriteLine("Log : " + context.StartSelector);
        }
    }
}
