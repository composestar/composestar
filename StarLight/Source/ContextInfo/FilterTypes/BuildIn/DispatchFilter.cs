using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes.BuildIn
{
    class DispatchFilter : FilterType
    {
        public override string Name
        {
            get { return "Dispatch"; }
        }

        public override FilterAction AcceptCallAction
        {
            get
            {
                return new DispatchAction();
            }
        }

        public override FilterAction RejectCallAction
        {
            get
            {
                return new ContinueAction();
            }
        }

        public override FilterAction AcceptReturnAction
        {
            get
            {
                return new DispatchAction();
            }
        }

        public override FilterAction RejectReturnAction
        {
            get
            {
                return new DispatchAction();
            }
        }
    }
}
