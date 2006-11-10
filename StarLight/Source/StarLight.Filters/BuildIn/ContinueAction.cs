using System;
using System.Collections.Generic;
using System.Text;
using Composestar.StarLight.Filters.FilterTypes;
using Composestar.StarLight.ContextInfo;

namespace Composestar.StarLight.Filters.BuildIn
{

    /// <summary>
    /// The continue action simply performs no operation.
    /// </summary>
    [FilterActionAttribute(FilterAction.ContinueAction, FilterActionAttribute.FilterFlowBehavior.Continue,
       FilterActionAttribute.MessageSubstitutionBehavior.Original)]
    public class ContinueAction : FilterAction
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
