using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.Collections;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;
using Composestar.StarLight.ContextInfo.RuBCoDe;

[assembly: Resource("arg", "encrypt,decrypt,verify,expectEncrypted")]
//[assembly: ConflictRule("arg", "...", true, "Data must be encrypted")]
namespace RuBCoDeTestFilters.Filters
{
    [FilterActionAttribute("VerifyEncArgsAction", FilterActionAttribute.FilterFlowBehavior.Continue,
       FilterActionAttribute.MessageSubstitutionBehavior.Original)]
    [ResourceOperation("arg.expectEncrypted;arg.read;arg.verify", true)]
    public class VerifyEncArgsAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {
            foreach (ArgumentInfo ai in context.GetArguments.Values)
            {
                ai.AddResourceOp("expectEncrypted");
                Object val = ai.Value;
                ai.AddResourceOp("verify");
            }
        }
    }
}
