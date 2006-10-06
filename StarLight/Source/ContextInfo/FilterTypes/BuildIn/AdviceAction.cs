using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes.BuildIn
{
    /// <summary>
    /// 
    /// </summary>
    [FilterActionAttribute("AdviceAction", FilterFlowBehaviour.Continue, MessageSubstitutionBehaviour.Original)]
    public class AdviceAction : FilterAction
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