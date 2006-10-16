using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo.FilterTypes;

namespace BasicTests
{
    [FilterActionAttribute("LoggingAction", FilterFlowBehaviour.Continue, MessageSubstitutionBehaviour.Original)]
    public class LoggingAction : FilterAction
    {
        public override void Execute(Composestar.StarLight.ContextInfo.JoinPointContext context)
        {
            Console.WriteLine("Log : " + context.StartSelector);
        }
    }
}
