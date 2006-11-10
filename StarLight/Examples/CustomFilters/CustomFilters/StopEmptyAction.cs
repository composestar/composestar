using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;

namespace CustomFilters
{
    [FilterActionAttribute("StopEmptyAction", FilterActionAttribute.FilterFlowBehavior.Continue,
       FilterActionAttribute.MessageSubstitutionBehavior.Original)]
    public class StopEmptyAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {
  
        }
    }
}
