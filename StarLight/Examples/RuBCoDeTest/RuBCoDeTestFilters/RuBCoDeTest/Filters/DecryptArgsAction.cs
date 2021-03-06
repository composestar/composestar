using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.Collections;

using Composestar.StarLight.ContextInfo;
using Composestar.StarLight.Filters.FilterTypes;
using Composestar.StarLight.ContextInfo.RuBCoDe;

[assembly: ConflictRule("arg", "(decrypt)((encrypt)![decrypt]*(decrypt)|![encrypt])*(decrypt)", true, "Argument is already decrypted")]
[assembly: Resource("arg", "decrypt,encrypt")]
namespace RuBCoDeTestFilters.Filters
{
    [FilterActionAttribute("DecryptArgsAction", FilterActionAttribute.FilterFlowBehavior.Continue,
       FilterActionAttribute.MessageSubstitutionBehavior.Original)]
    [ResourceOperation("arg.read;arg.decrypt;arg.write", true)]
    public class DecryptArgsAction : FilterAction
    {
        public override void Execute(JoinPointContext context)
        {
            foreach (ArgumentInfo ai in context.GetArguments.Values)
            {
                if (ai.Type == typeof(string))
                {
                    string val = (string)ai.Value;
                    ai.AddResourceOp("decrypt");
                    val = Encoding.UTF8.GetString(Convert.FromBase64String(val));
                    ai.Value = val;
                }
            }
        }
    }
}
