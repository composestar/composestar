using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;

namespace CustomFilters
{
    [FilterActionAttribute("StartEmptyAction", FilterActionAttribute.FilterFlowBehavior.Continue,
       FilterActionAttribute.MessageSubstitutionBehavior.Original)]
    public class StartEmptyAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {

        }
    }
}
