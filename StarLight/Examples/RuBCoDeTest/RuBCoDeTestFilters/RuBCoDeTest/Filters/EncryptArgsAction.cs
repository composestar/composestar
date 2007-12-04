using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.Collections;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;
using Composestar.StarLight.ContextInfo.RuBCoDe;

[assembly: ConflictRule("arg", "(encrypt)((decrypt)![encrypt]*(encrypt)|![decrypt])*(encrypt)", true, "Argument is already encrypted")]
[assembly: Resource("arg", "encrypt,decrypt")]
namespace RuBCoDeTestFilters.Filters
{
    [FilterActionAttribute("EncryptArgsAction", FilterActionAttribute.FilterFlowBehavior.Continue,
       FilterActionAttribute.MessageSubstitutionBehavior.Original)]
    [ResourceOperation("arg.read;arg.encrypt;arg.write", true)]
    public class EncryptArgsAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {
            foreach (ArgumentInfo ai in context.GetArguments.Values)
            {
                if (ai.Type == typeof(string))
                {
                    string val = (string) ai.Value;
                    ai.AddResourceOp("encrypt");
                    val = Convert.ToBase64String(Encoding.UTF8.GetBytes(val));
                    ai.Value = val;
                }
            }
        }
    }
}
