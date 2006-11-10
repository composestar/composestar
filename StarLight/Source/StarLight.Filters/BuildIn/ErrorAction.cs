using System;
using System.Collections.Generic;
using System.Text;
using Composestar.StarLight.Filters.FilterTypes;
using Composestar.StarLight.ContextInfo;

namespace Composestar.StarLight.Filters.BuildIn
{
    /// <summary>
    /// Generates an exception.
    /// </summary>
    [FilterActionAttribute(FilterAction.ErrorAction, FilterActionAttribute.FilterFlowBehavior.Exit,
       FilterActionAttribute.MessageSubstitutionBehavior.Original)]
    public class ErrorAction : FilterAction
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
