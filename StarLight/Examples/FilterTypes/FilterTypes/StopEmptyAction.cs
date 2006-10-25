using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;

namespace FilterTypes
{
    [FilterActionAttribute("StopEmptyAction", FilterActionAttribute.FilterFlowBehaviour.Continue,
       FilterActionAttribute.MessageSubstitutionBehaviour.Original)]
    public class StopEmptyAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {
  
        }
    }
}
