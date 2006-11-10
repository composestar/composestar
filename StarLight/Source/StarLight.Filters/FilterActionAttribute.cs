using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.Filters.FilterTypes
{
    /// <summary>
    /// This custom attribute defines a FilterAction used by one ore more <see cref="T:FilterType">FilterTypes</see>.
    /// </summary>
    /// <remarks>The <see cref="T:FilterActionAttribute"></see> can be applied to classes of the <see cref="T:FilterAction"></see> type.</remarks> 
    /// <example>
    /// Place this custom attribute on classes inheriting <see cref="T:FilterAction"></see> like in the following example.
    /// <code>
    /// [FilterActionAttribute("TracingInAction", FilterFlowBehavior.Continue, MessageSubstitutionBehavior.Original)]
    /// public class TracingInAction : FilterAction
    /// {
    ///    public override void Execute(JoinPointContext context)
    ///    {
    ///    }
    /// }
    /// </code>
    /// In this example, the name of the filter action is <c>TracingInAction</c>, the flow behavior is <c>continue</c> and the message substitution behavior is <c>original</c>. 
    /// The <c>TracingInAction</c> class implements the <c>Execute</c> function with a custom implementation of the filter action. In this case, it will perform some sort of tracing operation (not shown here).
    /// </example> 
    [AttributeUsage(AttributeTargets.Class, Inherited = false, AllowMultiple = false)]
    public sealed class FilterActionAttribute : Attribute
    {

        #region Private Variables

        private string _actionName;
        private FilterFlowBehavior _flowBehavior;
        private MessageSubstitutionBehavior _substitutionBehavior;
        private bool _createJPC = true;

        #endregion

        #region Properties

        /// <summary>
        /// Gets or sets a value indicating whether to create a <see cref="T:Composestar.StarLight.ContextInfo.JoinPointContext"></see> object.
        /// Default, this is enabled and the <c>Execute</c> function has access to this object which is passed as it's argument.
        /// However, creating the Join Point Context object does imply some overhead. If you do not use the Join Point Context in your code, 
        /// then disable this option.
        /// </summary>
        /// <value>
        /// 	<see langword="true"/> if the weaver has to create a Join Point Context; otherwise, <see langword="false"/>.
        /// </value>
        public bool CreateJoinPointContext
        {
            get
            {
                return _createJPC;
            }
        }

        /// <summary>
        /// Gets or sets the flow behavior.
        /// </summary>
        /// <value>The flow behavior.</value>
        public FilterFlowBehavior FlowBehavior
        {
            get
            {
                return _flowBehavior;
            }          
        }

        /// <summary>
        /// Gets or sets the substitution behavior.
        /// </summary>
        /// <value>The substitution behavior.</value>
        public MessageSubstitutionBehavior SubstitutionBehavior
        {
            get
            {
                return _substitutionBehavior;
            }          
        }

        /// <summary>
        /// Gets or sets the name of the action. make sure this value is unique as it will be used in the FilterTypes.
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

        #region ctor

        /// <summary>
        /// Initializes a new instance of the <see cref="T:FilterActionAnnotation"/> class describing a FilterAction.
        /// </summary>
        /// <param name="actionName">Name of the action.</param>
        /// <param name="flowBehavior">The flow behavior of the filter action indicating how a certain FilterAction influences the flow through the filterset.</param>
        /// <param name="substitutionBehavior">The substitution behavior of the filter action.</param>
        /// <remarks>Place this attribute only at classes inheriting the <see cref="T:FilterType"></see> base class since the filter action must implement the <c>Execute</c> method.</remarks>
        /// <example>
        /// Place this custom attribute on classes inheriting <see cref="T:FilterAction"></see> like in the following example.
        /// <code>
        /// [FilterActionAttribute("TracingInAction", FilterFlowBehavior.Continue, MessageSubstitutionBehavior.Original)]
        /// public class TracingInAction : FilterAction
        /// {
        /// public override void Execute(JoinPointContext context)
        /// {
        /// }
        /// }
        /// </code>
        /// In this example, the name of the filter action is <c>TracingInAction</c>, the flow behavior is <c>continue</c> and the message substitution behavior is <c>original</c>.
        /// The <c>TracingInAction</c> class implements the <c>Execute</c> function with a custom implementation of the filter action. In this case, it will perform some sort of tracing operation.
        /// </example>
        /// <exception cref="ArgumentNullException">
        /// If the <paramref name="name"/> is <see langword="null"></see> or empty, this exception is thrown.
        /// </exception>
        public FilterActionAttribute(string actionName, FilterFlowBehavior flowBehavior, MessageSubstitutionBehavior substitutionBehavior)
        {
            if (string.IsNullOrEmpty(actionName))
                throw new ArgumentNullException("actionName");

            _actionName = actionName;
            _flowBehavior = flowBehavior;
            _substitutionBehavior = substitutionBehavior;

        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:FilterActionAnnotation"/> class describing a FilterAction.
        /// </summary>
        /// <param name="actionName">The unique name of the filter action.</param>
        /// <param name="flowBehavior">The flow behavior of the filter action indicating how a certain FilterAction influences the flow through the filterset.</param>
        /// <param name="substitutionBehavior">The substitution behavior of the filter action.</param>
        /// <param name="createJoinPointContext">if set to <c>true</c>, the weaver injects a join point context object to be used in the <c>Execute</c> method. 
        /// When <c>false</c>, this step will be skipped and a null reference is placed instead of a Join Point Context.</param>
        /// <remarks>Place this attribute only at classes inheriting the <see cref="T:FilterType"></see> base class since the filter action must implement the <c>Execute</c> method.</remarks>
        /// <example>
        /// Place this custom attribute on classes inheriting <see cref="T:FilterAction"></see> like in the following example.
        /// <code>
        /// [FilterActionAttribute("TracingInAction", FilterFlowBehavior.Continue, MessageSubstitutionBehavior.Original, false)]
        /// public class TracingInAction : FilterAction
        /// {
        ///    public override void Execute(JoinPointContext context)
        ///    {
        ///       // Because the createJoinPointContext parameter is false, the context will be null!
        ///    }
        /// }
        /// </code>
        /// In this example, the name of the filter action is <c>TracingInAction</c>, the flow behavior is <c>continue</c> and the message substitution behavior is <c>original</c>.
        /// The <c>TracingInAction</c> class implements the <c>Execute</c> function with a custom implementation of the filter action. 
        /// In this case, it can not use the JoinPointContext because the Attribute indicates that a JoinPointContext is not needed for this filter action.
        /// </example>
        /// <exception cref="ArgumentNullException">
        /// If the <paramref name="name"/> is <see langword="null"></see> or empty, this exception is thrown.
        /// </exception>
        public FilterActionAttribute(string actionName, FilterFlowBehavior flowBehavior, MessageSubstitutionBehavior substitutionBehavior, bool createJoinPointContext)
        {
            if (string.IsNullOrEmpty(actionName))
                throw new ArgumentNullException("actionName");

            _actionName = actionName;
            _flowBehavior = flowBehavior;
            _substitutionBehavior = substitutionBehavior;
            _createJPC = createJoinPointContext;

        }

        #endregion

        #region FilterFlowBehavior Enum

        /// <summary>
        /// Enumeration to indicate how a certain FilterAction influences the flow through the 
        /// filterset.
        /// </summary>
        /// <remarks>
        /// <para>There are three options possible:
        /// <list type="definition">
        /// <item><term>Continue</term><description>To indicate that flow continues to the next filter.</description></item>
        /// <item><term>Exit</term><description>To indicate that flow exits the filterset without a return,
        /// for example with an <c>Error</c> action.</description></item>
        /// <item><term>Return</term><description>To indicate that flow changes from call to return, 
        /// for example with a <c>Dispatch</c> action.</description></item>
        /// </list>
        /// </para>
        /// </remarks>         
        public enum FilterFlowBehavior
        {
            /// <summary>
            /// To indicate that flow continues to the next filter.
            /// </summary>
            Continue,
            /// <summary>
            /// To indicate that flow exits the filterset without a return,
            /// for example with an <c>Error</c> action.
            /// </summary>
            Exit,
            /// <summary>
            /// To indicate that flow changes from call to return, 
            /// for example with a <c>Dispatch</c> action.
            /// </summary>
            Return
        }

        #endregion

        #region MessageSubstitutionBehavior Enum

        /// <summary>
        /// Enumeration to indicate how the action changes the message.        
        /// </summary>
        /// <remarks>
        /// <para>There are three options possible:
        /// <list type="definition">
        /// <item><term>Original</term><description>The message is not changed.</description></item>
        /// <item><term>Substituted</term><description>The message is changed according to the substitutionpart.</description></item>
        /// <item><term>Any</term><description>The message can change into any other message. 
        /// Be careful when using this option, as it introduces more uncertainty in the static reasoning algorithms. 
        /// Use only this option when you cannot use a following substitution filter</description></item>
        /// </list>
        /// </para>  
        /// </remarks> 
        public enum MessageSubstitutionBehavior
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
            /// Be careful when using this option, as it introduces more uncertainty in the static reasoning algorithms. 
            /// Only use this option when you cannot use a following substitution filter.
            /// </remarks> 
            Any
        }

        #endregion
    }

    #region FilterActionSpecificationAttribute

    /// <summary>
    /// A custom attribute to specify the usage of the <see cref="T:JoinPointContext"></see> in the <c>Execute</c> method.
    /// </summary>
    /// <remarks>Currently, this functionality is not yet used.</remarks> 
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
        public string Specification
        {
            get
            {
                return _spec;
            }
        }

        #endregion

        #region ctor

        /// <summary>
        /// Initializes a new instance of the <see cref="T:FilterActionSpecificationAnnotation"/> class.
        /// </summary>
        /// <param name="specification">The specification.</param>
        public FilterActionSpecificationAttribute(string specification)
        {
            _spec = specification;
        }

        #endregion

        /*
        /// <summary>
        /// Parses the specification.
        /// </summary>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Performance", "CA1811:AvoidUncalledPrivateCode")]
        private void ParseSpecification()
        {
            //// Input: target.write(foo)& selector.write(foo)
            //string[] rawops = _spec.Split("&".ToCharArray());
            //for (int index = 0; index <= rawops.Length; index++)
            //{
            //    Resource rsrc = null;
            //    string[] parts = rawops[index].Split(".".ToCharArray()); // [target][write(foo,bar)]
            //    if (parts.Length == 2)
            //    {
            //        rsrc = new Resource();
            //        rsrc.Name = parts[0];
            //        string[] opparts = parts[1].Split("(".ToCharArray()); // [write][foo,bar)]
            //        if (opparts.Length == 2)
            //        {
            //            Operation op = new Operation();
            //            op.Name = parts[0];
            //            string[] argparts = opparts[1].Split(",".ToCharArray()); // [foo][bar)]
            //            for (int j = 0; j < argparts.Length; j++)
            //            {
            //                string argname = argparts[j];
            //                if (argname.EndsWith(")"))
            //                    argname = argname.Substring(0, argname.Length - 1);
            //                Argument arg = new Argument();
            //                arg.Name = argname;
            //                op.args.Add(arg);
            //            }
            //            rsrc.operations.Add(op);
            //        }
            //    }
            //}
        }
        */
    }

    #endregion
}
