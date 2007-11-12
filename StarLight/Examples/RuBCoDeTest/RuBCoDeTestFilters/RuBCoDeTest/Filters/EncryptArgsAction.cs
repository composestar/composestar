using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.Collections;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;

namespace RuBCoDeTestFilters.Filters
{
    [FilterActionAttribute("EncryptArgsAction", FilterActionAttribute.FilterFlowBehavior.Continue,
       FilterActionAttribute.MessageSubstitutionBehavior.Original)]
    public class EncryptArgsAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {
            foreach (ArgumentInfo ai in context.GetArguments.Values)
            {
                Object val = ai.Value;
                ai.ArgumentBK.AddOperation("encrypt");
                ai.Value = val;
            }
        }
    }
}
