using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes.BuildIn
{

    /// <summary>
    /// The continue action simply performs no operation.
    /// </summary>
    [FilterActionAttribute(FilterAction.ContinueAction, FilterActionAttribute.FilterFlowBehaviour.Continue,
       FilterActionAttribute.MessageSubstitutionBehaviour.Original)]
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
