using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes
{
    /// <summary>
    /// The base class of every FilterAction. Each subclass should be annotated with the FilterActionAnnotation
    /// custom attribute, to provide information to the compiler about the behaviour of this action concerning
    /// the message in the filterset.
    /// </summary>
    public abstract class FilterAction
    {
        /// <summary>
        /// Implements the behaviour of the FilterAction.
        /// </summary>
        /// <param name="context">Context information</param>
        public abstract void execute( JoinPointContext context );
    }
}
