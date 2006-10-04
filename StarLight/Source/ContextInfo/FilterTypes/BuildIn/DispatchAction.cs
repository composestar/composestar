using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes.BuildIn
{
    class DispatchAction : FilterAction
    {
        public override FilterAction.FilterFlowBehaviour FlowBehaviour
        {
            get
            {
                return FilterFlowBehaviour.Return;            }
        }

        public override FilterAction.MessageSubstitutionBehaviour SubstitutionBehaviour
        {
            get
            {
                return MessageSubstitutionBehaviour.Original;
            }
        }

        
        [FilterActionSpecificationAnnotation("target.write(inner)") ]
        public override void execute(JoinPointContext context)
        {
        }
    }
}
