using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes.BuildIn
{
    [FilterActionAnnotation( "DispatchAction", FilterFlowBehaviour.Return,
        MessageSubstitutionBehaviour.Original )]
    public class DispatchAction : FilterAction
    {
        /// <summary>
        /// Implements the behaviour of the FilterAction.
        /// </summary>
        /// <param name="context">Context information</param>
        [FilterActionSpecificationAnnotation("target.write(inner)") ]
        public override void Execute(JoinPointContext context)
        {
        }
    }
}
