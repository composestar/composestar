using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo
{
    public abstract class CustomFilterType
    {
        public abstract void onCallAccept( JoinPointContext context );
        public abstract void onCallReject( JoinPointContext context );
        public abstract void onReturnAccept( JoinPointContext context );
        public abstract void onReturnReject( JoinPointContext context );
    }
}
