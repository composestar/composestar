using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes.BuildIn
{
    [FilterActionAttribute( "ContinueAction", FilterFlowBehaviour.Continue,
        MessageSubstitutionBehaviour.Original )]
    public class ContinueAction : FilterAction
    {
        /// <summary>
        /// Implements the behaviour of the FilterAction.
        /// </summary>
        /// <param name="context">Context information</param>
        public override void Execute(JoinPointContext context)
        {
        }
    }
}
