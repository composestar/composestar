using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes.BuildIn
{
    /// <summary>
    /// Generates an exception.
    /// </summary>
    [FilterActionAttribute(FilterAction.ErrorAction, FilterActionAttribute.FilterFlowBehaviour.Exit,
       FilterActionAttribute.MessageSubstitutionBehaviour.Original)]
    public class ErrorAction : FilterAction
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
