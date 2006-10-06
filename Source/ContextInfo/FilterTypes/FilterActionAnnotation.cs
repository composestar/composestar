using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes
{
    /// <summary>
    /// 
    /// </summary>
    [AttributeUsage(AttributeTargets.Class, Inherited = false, AllowMultiple = false)]
    public class FilterActionAnnotation : Attribute
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
            set
            {
                _flowBehaviour = value;
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
            set
            {
                _substitutionBehaviour = value;
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
            set
            {
                _actionName = value;
            }
        }

        #endregion
        
               
        /// <summary>
        /// Initializes a new instance of the <see cref="T:FilterActionAnnotation"/> class.
        /// </summary>
        /// <param name="name">The name.</param>
        /// <param name="flowBehaviour">The flow behaviour.</param>
        /// <param name="substitutionBehaviour">The substitution behaviour.</param>
        public FilterActionAnnotation(string name, FilterFlowBehaviour flowBehaviour, MessageSubstitutionBehaviour substitutionBehaviour)
        {
            _actionName = name;
            _flowBehaviour = flowBehaviour;
            _substitutionBehaviour = substitutionBehaviour;
        }

    }

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
    class FilterActionSpecificationAnnotation : Attribute
    {
        #region Private variables
        private string _spec;
        #endregion

        #region Properties
        /// <summary>
        /// Gets or sets the spec.
        /// </summary>
        /// <value>The spec.</value>
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
        public FilterActionSpecificationAnnotation(string spec)
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
