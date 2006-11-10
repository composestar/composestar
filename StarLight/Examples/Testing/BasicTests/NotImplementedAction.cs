using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;   

namespace BasicTests
{
    [FilterActionAttribute("NotImplementedAction", FilterActionAttribute.FilterFlowBehavior.Exit, FilterActionAttribute.MessageSubstitutionBehavior.Original)]
    public class NotImplementedAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {
            
        }
    }
}
