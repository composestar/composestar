using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes.BuildIn
{
    class SubstitutionAction : FilterAction
    {
        public override FilterAction.FilterFlowBehaviour FlowBehaviour
        {
            get
            {
                return FilterFlowBehaviour.Continue;
            }
        }

        public override FilterAction.MessageSubstitutionBehaviour SubstitutionBehaviour
        {
            get
            {
                return MessageSubstitutionBehaviour.Substituted;
            }
        }

        
        public override void execute( JoinPointContext context )
        {
        }
    }
}