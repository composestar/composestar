using System;
using System.Collections.Generic;
using System.Text;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.ContextInfo.FilterTypes;

namespace FilterTypes
{
    [FilterActionAttribute("StopEmptyAction", FilterFlowBehaviour.Continue,
        MessageSubstitutionBehaviour.Original)]
    public class StopEmptyAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {
  
        }
    }
}
