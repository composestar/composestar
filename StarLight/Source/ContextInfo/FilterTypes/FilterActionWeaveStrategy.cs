using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes
{
    abstract class FilterActionWeaveStrategy
    {
        /// <summary>
        /// Returns the name of the FilterAction for which this is the 
        /// weaving strategy.
        /// </summary>
        public abstract String FilterAction
        {
            get;
        }


        public abstract String Weave();


    }
}
