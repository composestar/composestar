using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes.BuildIn
{
    class AdviceAction : FilterAction
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
                return MessageSubstitutionBehaviour.Original;
            }
        }

        

        public override void execute( JoinPointContext context )
        {
        }
    }
}