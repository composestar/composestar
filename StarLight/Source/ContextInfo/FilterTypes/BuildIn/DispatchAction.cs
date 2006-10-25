using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes.BuildIn
{
    /// <summary>
    ///  Dispatches to another method.
    /// </summary>
    [FilterActionAttribute(FilterAction.DispatchAction, FilterActionAttribute.FilterFlowBehaviour.Return,
       FilterActionAttribute.MessageSubstitutionBehaviour.Original)]
    public class DispatchAction : FilterAction
    {
        /// <summary>
        /// Implements the behaviour of the FilterAction.
        /// </summary>
        /// <param name="context">Context information</param>
        [FilterActionSpecificationAttribute("target.write(inner)")]
        public override void Execute(JoinPointContext context)
        {
        }
    }
}
