#region Using directives
using System;
using System.Collections.Generic;
using System.Text;
#endregion

namespace Composestar.StarLight.Filters.FilterTypes
{
    /// <summary>
    /// The <see cref="T:FilterTypeAttribute"></see> can be applied to classes and describes a <see cref="T:FilterType"></see>.
    /// </summary>
    /// <example>
    /// Apply this custom attribute to a custom filter class as shown in the following example:
    /// <code>
    /// [FilterTypeAttribute("Tracing", "TracingInAction", "ContinueAction", "TracingOutAction", "ContinueAction")]
    /// public class TracingFilterType : FilterType
    /// {
    /// }
    /// </code>
    /// In this example, a custom <i>tracing</i> filter type is created. The accept actions are defined in the filteractions 
    /// <c>TracingInAction</c> and <c>TracingOutAction</c>. For the reject actions, the default <c>ContinueAction</c> is used.
    /// </example>     
    [AttributeUsage(AttributeTargets.Class, Inherited = false, AllowMultiple = false)]
    public sealed class FilterTypeAttribute : Attribute
    {

        #region Private Variables
        
        /// <summary>
        /// The name of the filtertype
        /// </summary>
        private string _name;

        /// <summary>
        /// The accept-call action
        /// </summary>
        private string _acceptCallAction;

        /// <summary>
        /// The reject-call action
        /// </summary>
        private string _rejectCallAction;

        /// <summary>
        /// The accept-return action
        /// </summary>
        private string _acceptReturnAction;

        /// <summary>
        /// The reject-return action
        /// </summary>
        private string _rejectReturnAction;

        #endregion

        #region Properties

        /// <summary>
        /// Gets or sets the name of the filter. This name can be used in the concern specification.
        /// </summary>
        /// <value>The unique name of the filter.</value>
        public string Name
        {
            get
            {
                return _name;
            }
          }

        /// <summary>
        /// Gets or sets the accept call action.
        /// </summary>
        /// <value>The accept call action.</value>
        public string AcceptCallAction
        {
            get
            {
                return _acceptCallAction;
            }
        }

        /// <summary>
        /// Gets or sets the reject call action.
        /// </summary>
        /// <value>The reject call action.</value>
        public string RejectCallAction
        {
            get
            {
                return _rejectCallAction;
            }
        }

        /// <summary>
        /// Gets or sets the accept return action.
        /// </summary>
        /// <value>The accept return action.</value>
        public string AcceptReturnAction
        {
            get
            {
                return _acceptReturnAction;
            }
        }

        /// <summary>
        /// Gets or sets the reject return action.
        /// </summary>
        /// <value>The reject return action.</value>
        public string RejectReturnAction
        {
            get
            {
                return _rejectReturnAction;
            }
        }

        #endregion
  
        #region ctors

        /// <summary>
        /// Initializes a new instance of the <see cref="T:FilterTypeAnnotation"/> class describing a custom filter type.
        /// </summary>
        /// <param name="name">The unique name of the filtertype.</param>
        /// <param name="acceptCallAction">The accept call filter action.</param>
        /// <param name="rejectCallAction">The reject call filter action.</param>
        /// <param name="acceptReturnAction">The accept return filter action.</param>
        /// <param name="rejectReturnAction">The reject return filter action.</param>
        /// <example>
        /// Apply this custom attribute to a custom filter class as shown in the following example:
        /// <code>
        /// [FilterTypeAttribute("Tracing", "TracingInAction", "ContinueAction", "TracingOutAction", "ContinueAction")]
        /// public class TracingFilterType : FilterType
        /// {
        /// }
        /// </code>
        /// In this example, a custom <i>tracing</i> filter type is created. The accept actions are defined in the filteractions 
        /// <c>TracingInAction</c> and <c>TracingOutAction</c>. For the reject actions, the default <c>ContinueAction</c> is used.
        /// </example> 
        /// <exception cref="ArgumentNullException">
        /// If the <paramref name="name"/> is <see langword="null"></see> or empty, this exception is thrown.
        /// </exception>
        public FilterTypeAttribute(string name, string acceptCallAction, string rejectCallAction,
                    string acceptReturnAction, string rejectReturnAction)
        {
            if (string.IsNullOrEmpty(name))
                throw new ArgumentNullException("name");
 
            _name = name;
            _acceptCallAction = acceptCallAction;
            _rejectCallAction = rejectCallAction;
            _acceptReturnAction = acceptReturnAction;
            _rejectReturnAction = rejectReturnAction;
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:FilterTypeAttribute"/> class describing a custom filter type using the types of the filteractions.
        /// </summary>
        /// <param name="name">The name.</param>
        /// <param name="acceptCallAction">The accept call filter action type.</param>
        /// <param name="rejectCallAction">The reject call filter action type.</param>
        /// <param name="acceptReturnAction">The accept return filter action type.</param>
        /// <param name="rejectReturnAction">The reject return filter action type.</param>
        /// <example>
        /// Apply this custom attribute to a custom filter class as shown in the following example:
        /// <code>
        /// [FilterTypeAttribute("After", typeof(ContinueAction), typeof(ContinueAction), typeof(AdviceAction), typeof(ContinueAction))]
        /// public class AfterFilterType : FilterType
        /// {
        /// }
        /// </code>
        /// In this example, an <i>AfterFilterType</i> filter type is created. The actions are defined in the filteractions 
        /// which are identief using their types.
        /// </example> 
        /// <exception cref="ArgumentNullException">
        /// If the <paramref name="name"/> is <see langword="null"></see> or empty, this exception is thrown.
        /// </exception>
        /// <exception cref="ArgumentException">
        /// If one of the actions does not have a <see cref="T:FilterActionAttribute"/>, then it is not possible to read the name of the filteraction.
        /// </exception>
        public FilterTypeAttribute(string name, Type acceptCallAction, Type rejectCallAction,
            Type acceptReturnAction, Type rejectReturnAction)
        {
            if (string.IsNullOrEmpty(name))
                throw new ArgumentNullException("name");
 
            _name = name;
            
            _acceptCallAction = FilterTypeAttribute.GetNameOfFilterAction(acceptCallAction);
            _rejectCallAction = FilterTypeAttribute.GetNameOfFilterAction(rejectCallAction);
            _acceptReturnAction = FilterTypeAttribute.GetNameOfFilterAction(acceptReturnAction);
            _rejectReturnAction = FilterTypeAttribute.GetNameOfFilterAction(rejectReturnAction);
        }
        #endregion

        /// <summary>
        /// Gets the name of filter action.
        /// </summary>
        /// <param name="filterAction">The filter action.</param>
        /// <returns></returns>
        private static string GetNameOfFilterAction(Type filterAction)
        {
            FilterActionAttribute[] fac = (FilterActionAttribute[]) filterAction.GetCustomAttributes(typeof(FilterActionAttribute), true);

            if (fac.Length == 1)
                return fac[0].ActionName;
            else
                throw new ArgumentException(Properties.Resources.FilterActionNameNotFound, filterAction.Name); 
  
        } // GetNameOfFilterAction(filterAction)

    }
}