using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;

namespace BasicTests
{
	[FilterActionAttribute("TimingStartAction", FilterActionAttribute.FilterFlowBehavior.Continue,
	   FilterActionAttribute.MessageSubstitutionBehavior.Original)]
    [ResourceOperation("starttime.write")]
	public class TimingStartAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {
            long starttime = DateTime.Now.Ticks;
            context.AddProperty("starttime", starttime);
        }
	}
}
