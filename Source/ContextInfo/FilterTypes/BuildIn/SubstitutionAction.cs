using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes.BuildIn
{
    [FilterActionAnnotation( "SubstitutionAction", FilterActionAnnotation.FilterFlowBehaviour.Continue,
        FilterActionAnnotation.MessageSubstitutionBehaviour.Substituted )]
    public class SubstitutionAction : FilterAction
    {
        public override void execute( JoinPointContext context )
        {
        }
    }
}