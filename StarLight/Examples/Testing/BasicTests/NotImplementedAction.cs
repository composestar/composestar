using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;   

namespace BasicTests
{
    [FilterActionAttribute("NotImplementedAction", FilterActionAttribute.FilterFlowBehaviour.Exit, FilterActionAttribute.MessageSubstitutionBehaviour.Original)]
    public class NotImplementedAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {
            
        }
    }
}
