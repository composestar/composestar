using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes.BuildIn
{
    [FilterActionAnnotation("AdviceAction", FilterActionAnnotation.FilterFlowBehaviour.Continue, 
        FilterActionAnnotation.MessageSubstitutionBehaviour.Original)]
    public class AdviceAction : FilterAction
    {
        public override void execute( JoinPointContext context )
        {
        }
    }
}