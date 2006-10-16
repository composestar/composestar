using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes
{
    /// <summary>
    /// Generate documentation here
    /// </summary>
    [AttributeUsage(AttributeTargets.Class, Inherited = false, AllowMultiple = false)]
    public sealed class FilterActionAttribute : Attribute
    {

        #region Private Variables

        private string _actionName;
        private FilterFlowBehaviour _flowBehaviour;
        private MessageSubstitutionBehaviour _substitutionBehaviour;

        #endregion

        #region Properties

        /// <summary>
        /// Gets or sets the flow behaviour.
        /// </summary>
        /// <value>The flow behaviour.</value>
        public FilterFlowBehaviour FlowBehaviour
        {
            get
            {
                return _flowBehaviour;
            }          
        }

        /// <summary>
        /// Gets or sets the substitution behaviour.
        /// </summary>
        /// <value>The substitution behaviour.</value>
        public MessageSubstitutionBehaviour SubstitutionBehaviour
        {
            get
            {
                return _substitutionBehaviour;
            }          
        }

        /// <summary>
        /// Gets or sets the name of the action.
        /// </summary>
        /// <value>The name of the action.</value>
        public string ActionName
        {
            get
            {
                return _actionName;
            }         
        }

        #endregion


        /// <summary>
        /// Initializes a new instance of the <see cref="T:FilterActionAnnotation"/> class.
        /// </summary>
        /// <param name="name">The name.</param>
        /// <param name="flowBehaviour">The flow behaviour.</param>
        /// <param name="substitutionBehaviour">The substitution behaviour.</param>
        public FilterActionAttribute(string name, FilterFlowBehaviour flowBehaviour, MessageSubstitutionBehaviour substitutionBehaviour)
        {
            _actionName = name;
            _flowBehaviour = flowBehaviour;
            _substitutionBehaviour = substitutionBehaviour;
        }

    }

    /// <summary>
    /// Enumeration to indicate how a certain FilterAction influences the flow through the 
    /// filterset.
    /// </summary>
    /// <remarks>
    /// <para>There are three options possible:
    /// <list type="bullet">
    /// <item><term>Continue:</term><description>To indicate that flow continues to the next filter</description></item>
    /// <item><term>Exit:</term><description>To indicate that flow exits the filterset without a return,
    /// for example with an Error action</description></item>
    /// <item><term>Return:</term><description>To indicate that flow changes from call to return, 
    /// for example with a Dispatch action</description></item>
    /// </list>
    /// </para>
    /// </remarks>         
    public enum FilterFlowBehaviour
    {
        /// <summary>
        /// To indicate that flow continues to the next filter.
        /// </summary>
        Continue,
        /// <summary>
        /// To indicate that flow exits the filterset without a return,
        /// for example with an Error action.
        /// </summary>
        Exit,
        /// <summary>
        /// To indicate that flow changes from call to return, 
        /// for example with a Dispatch action.
        /// </summary>
        Return
    }

    /// <summary>
    /// Enumeration to indicate how the action changes the message.        
    /// </summary>
    /// <remarks>
    /// <para>There are three options possible:
    /// <list type="bullet">
    /// <item><term>Original</term><description>The message is not changed</description></item>
    /// <item><term>Substituted</term><description>The message is changed according to the substitutionpart</description></item>
    /// <item><term>Any</term><description>The message can change into any other message. 
    /// Be carefull when using this option, as it introduces more uncertainty in the static reasoning algorithms. 
    /// Use only this option when you cannot use a following substitution filter</description></item>
    /// </list>
    /// </para>  
    /// </remarks> 
    public enum MessageSubstitutionBehaviour
    {
        /// <summary>
        /// The message is not changed.
        /// </summary>
        Original,
        /// <summary>
        /// The message is changed according to the substitutionpart.
        /// </summary>
        Substituted,
        /// <summary>
        /// The message can change into any other message. 
        /// </summary>
        /// <remarks>
        /// Be carefull when using this option, as it introduces more uncertainty in the static reasoning algorithms. 
        /// Only use this option when you cannot use a following substitution filter.
        /// </remarks> 
        Any
    }

    /// <summary>
    /// 
    /// </summary>
    [AttributeUsage(AttributeTargets.Method, Inherited = false, AllowMultiple = false)]
    public sealed class FilterActionSpecificationAttribute : Attribute
    {

        #region Private variables
        private string _spec;
        #endregion

        #region Properties
        /// <summary>
        /// Gets or sets the specification.
        /// </summary>
        /// <value>The specification.</value>
        public string Spec
        {
            get
            {
                return _spec;
            }
            set
            {
                _spec = value;
            }
        }
        #endregion

        #region ctor

        /// <summary>
        /// Initializes a new instance of the <see cref="T:FilterActionSpecificationAnnotation"/> class.
        /// </summary>
        /// <param name="spec">The spec.</param>
        public FilterActionSpecificationAttribute(string spec)
        {
            _spec = spec;
        }

        #endregion

        /// <summary>
        /// Parses the specification.
        /// </summary>
        private void ParseSpecification()
        {
            // Input: target.write(foo)& selector.write(foo)
            string[] rawops = _spec.Split("&".ToCharArray());
            for (int index = 0; index <= rawops.Length; index++)
            {
                Resource rsrc = null;
                string[] parts = rawops[index].Split(".".ToCharArray()); // [target][write(foo,bar)]
                if (parts.Length == 2)
                {
                    rsrc = new Resource();
                    rsrc.Name = parts[0];
                    string[] opparts = parts[1].Split("(".ToCharArray()); // [write][foo,bar)]
                    if (opparts.Length == 2)
                    {
                        Operation op = new Operation();
                        op.Name = parts[0];
                        string[] argparts = opparts[1].Split(",".ToCharArray()); // [foo][bar)]
                        for (int j = 0; j < argparts.Length; j++)
                        {
                            string argname = argparts[j];
                            if (argname.EndsWith(")"))
                                argname = argname.Substring(0, argname.Length - 1);
                            Argument arg = new Argument();
                            arg.Name = argname;
                            op.args.Add(arg);
                        }
                        rsrc.operations.Add(op);
                    }
                }
            }
        }

    }
}
