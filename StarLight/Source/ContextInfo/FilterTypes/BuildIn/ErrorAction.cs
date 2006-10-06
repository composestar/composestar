using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes.BuildIn
{
    [FilterActionAnnotation( "ErrorAction", FilterActionAnnotation.FilterFlowBehaviour.Exit,
        FilterActionAnnotation.MessageSubstitutionBehaviour.Original )]
    public class ErrorAction : FilterAction
    {
        public override void execute( JoinPointContext context )
        {
        }
    }
}
