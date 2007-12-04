using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.Collections;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;
using Composestar.StarLight.ContextInfo.RuBCoDe;

[assembly: Resource("arg", "encrypt,decrypt,verify,expectPlain")]
[assembly: ConflictRule("arg", "(encrypt)(![decrypt]*(decrypt)![encrypt]*(encrypt))*(expectPlain)", true, "Data must NOT be encrypted")]
namespace RuBCoDeTestFilters.Filters
{
    [FilterActionAttribute("VerifyArgsAction", FilterActionAttribute.FilterFlowBehavior.Continue,
       FilterActionAttribute.MessageSubstitutionBehavior.Original)]
    [ResourceOperation("arg.expectPlain;arg.read;arg.verify", true)]
    public class VerifyArgsAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {
            foreach (ArgumentInfo ai in context.GetArguments.Values)
            {
                if (ai.Type == typeof(string))
                {
                    ai.AddResourceOp("expectPlain");
                    ai.AddResourceOp("verify");
                }
            }
        }
    }
}
