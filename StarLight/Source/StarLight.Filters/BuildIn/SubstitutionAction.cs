using System;
using System.Collections.Generic;
using System.Text;
using Composestar.StarLight.Filters.FilterTypes;
using Composestar.StarLight.ContextInfo;

namespace Composestar.StarLight.Filters.BuildIn
{
    /// <summary>
    /// Substitution action.
    /// </summary>
    [FilterActionAttribute(FilterAction.SubstitutionAction, FilterActionAttribute.FilterFlowBehavior.Continue,
       FilterActionAttribute.MessageSubstitutionBehavior.Substituted)]
    public class SubstitutionAction : FilterAction
    {
        /// <summary>
        /// Implements the behavior of the FilterAction.
        /// </summary>
        /// <param name="context">Context information</param>
        public override void Execute(JoinPointContext context)
        {
        }
    }
}