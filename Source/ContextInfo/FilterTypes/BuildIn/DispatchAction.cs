using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes.BuildIn
{
    [FilterActionAnnotation( "DispatchAction", FilterActionAnnotation.FilterFlowBehaviour.Return,
        FilterActionAnnotation.MessageSubstitutionBehaviour.Original )]
    public class DispatchAction : FilterAction
    {
        [FilterActionSpecificationAnnotation("target.write(inner)") ]
        public override void execute(JoinPointContext context)
        {
        }
    }
}
