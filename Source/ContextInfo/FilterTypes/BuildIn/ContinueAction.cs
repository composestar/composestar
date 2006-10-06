using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes.BuildIn
{
    [FilterActionAnnotation( "ContinueAction", FilterActionAnnotation.FilterFlowBehaviour.Continue,
        FilterActionAnnotation.MessageSubstitutionBehaviour.Original )]
    public class ContinueAction : FilterAction
    {
        public override void execute(JoinPointContext context)
        {
        }
    }
}
