using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.FilterTypes
{
    /// <summary>
    /// The base class of every FilterType. Each subclass should be annotated with the FilterTypeAnnotation
    /// custom attribute, to provide information to the StarLight compiler about the actions in this filtertype.
    /// </summary>
    /// <example>
    /// The following code examples shows how a filtertype can be defined.
    /// <code>
    /// [FilterTypeAttribute("Tracing", "TracingInAction", "ContinueAction", "TracingOutAction", "ContinueAction")]
    /// public class TracingFilterType : FilterType
    /// {
    /// }
    /// </code>
    /// </example> 
    /// <remarks>
    /// This class does not need any implementation and currently provides only a way to declare filtertypes. 
    /// In future releases this class can be extended with more functionality.
    /// </remarks> 
    public abstract class FilterType
    {

        #region BuildIn FilterTypes

        /// <summary>
        /// Specifies an After filter type.
        /// </summary>
        public const string AfterFilter = "After";

        /// <summary>
        /// Specifies a Before filter type.
        /// </summary>
        public const string BeforeFilter = "Before";

        /// <summary>
        /// Specifies a Dispatch filter type.
        /// </summary>
        public const string DispatchFilter = "Dispatch";

        /// <summary>
        /// Specifies an Error filter type.
        /// </summary>
        public const string ErrorFilter = "Error";

        /// <summary>
        /// Specifies a Substitution filter type.
        /// </summary>
        public const string SubstitutionFilter = "Substitution";

        #endregion
    }
}
