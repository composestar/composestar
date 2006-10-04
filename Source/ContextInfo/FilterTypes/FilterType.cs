using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes
{
    /// <summary>
    /// The base class of every FilterType.
    /// </summary>
    abstract class FilterType
    {   
        /// <summary>
        /// Gets the name of the FilterType
        /// </summary>
        public abstract string Name
        {
            get;
        }

        /// <summary>
        /// To get the FilterAction on call when the filter accepts
        /// </summary>
        public abstract FilterAction AcceptCallAction
        {
            get;
        }

        /// <summary>
        /// To get the FilterAction on call when the filter rejects
        /// </summary>
        public abstract FilterAction RejectCallAction
        {
            get;
        }

        /// <summary>
        /// To get the FilterAction on return when the filter accepts
        /// </summary>
        public abstract FilterAction AcceptReturnAction
        {
            get;
        }

        /// <summary>
        /// To get the FilterAction on return when the filter rejects
        /// </summary>
        public abstract FilterAction RejectReturnAction
        {
            get;
        }
    }
}
