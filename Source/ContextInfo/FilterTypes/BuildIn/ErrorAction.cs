using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes.BuildIn
{
    [FilterActionAnnotation( "ErrorAction", FilterFlowBehaviour.Exit,
        MessageSubstitutionBehaviour.Original )]
    public class ErrorAction : FilterAction
    {
        /// <summary>
        /// Implements the behaviour of the FilterAction.
        /// </summary>
        /// <param name="context">Context information</param>
        public override void Execute( JoinPointContext context )
        {
        }
    }
}
