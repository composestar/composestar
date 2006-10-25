using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;

namespace FilterTypes
{
    [FilterActionAttribute("StartEmptyAction", FilterActionAttribute.FilterFlowBehaviour.Continue,
       FilterActionAttribute.MessageSubstitutionBehaviour.Original)]
    public class StartEmptyAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {

        }
    }
}
