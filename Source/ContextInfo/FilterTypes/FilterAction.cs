using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes
{
    /// <summary>
    /// The base class of every FilterAction.
    /// </summary>
    abstract class FilterAction
    {
        /// <summary>
        /// Enumeration to indicate how a certain FilterAction influences the flow through the 
        /// filterset.
        /// <para>There are three options possible:
        /// <list type="Bullet">
        /// <item><description>Continue: To indicate that flow continues to the next filter</description></item>
        /// <item><description>Exit: To indicate that flow exits the filterset without a return,
        /// for example with an Error action</description></item>
        /// <item><description>Return: To indicate that flow changes from call to return, 
        /// for example with a Dispatch action</description></item>
        /// </list>
        /// </para>
        /// </summary>
        public enum FilterFlowBehaviour
        {
            Continue, Exit, Return
        }

        /// <summary>
        /// Enumeration to indicate how the action changes the message
        /// <para>There are three options possible:
        /// <list type="Bullet">
        /// <item><description>Original: The message is not changed</description></item>
        /// <item><description>Substituted: The message is changed according to the substitutionpart</description></item>
        /// <item><description>Any: The message can change into any other message. 
        /// Be carefull when using this option, as it introduces more uncertainty in the static reasoning algorithms. 
        /// Use only this option when you cannot use a following substitution filter</description></item>
        /// </list>
        /// </para>
        /// </summary>
        public enum MessageSubstitutionBehaviour
        {
            Original, Substituted, Any
        }

        /// <summary>
        /// Returns how this FilterAction influences the flow through the filterset.
        /// </summary>
        public abstract FilterFlowBehaviour FlowBehaviour
        {
            get;
        }

        /// <summary>
        /// Returns how this FilterAction changes the message.
        /// </summary>
        public abstract MessageSubstitutionBehaviour SubstitutionBehaviour
        {
            get;
        }

        
        /// <summary>
        /// Implements the behaviour of the FilterAction.
        /// </summary>
        /// <param name="context">Context information</param>
        public abstract void execute( JoinPointContext context );
    }
}
