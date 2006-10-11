using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.ContextInfo.FilterTypes;

namespace FilterTypes
{
    [FilterActionAttribute("StartTimerAction", FilterFlowBehaviour.Continue, 
        MessageSubstitutionBehaviour.Original)]
    public class StartTimerAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {
            long starttime = DateTime.Now.Ticks;
            context.AddProperty("starttime", starttime);
        }
    }
}
