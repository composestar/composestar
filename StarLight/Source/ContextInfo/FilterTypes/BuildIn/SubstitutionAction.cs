using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes.BuildIn
{
    /// <summary>
    /// Substitution action.
    /// </summary>
    [FilterActionAttribute(FilterAction.SubstitutionAction, FilterActionAttribute.FilterFlowBehaviour.Continue,
       FilterActionAttribute.MessageSubstitutionBehaviour.Substituted)]
    public class SubstitutionAction : FilterAction
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