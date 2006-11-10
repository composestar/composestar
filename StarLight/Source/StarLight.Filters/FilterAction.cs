#region Using directives
using System;
using System.Collections.Generic;
using System.Text;
using Composestar.StarLight.ContextInfo;
#endregion

namespace Composestar.StarLight.Filters.FilterTypes
{
    /// <summary>
    /// The base class of every FilterAction. Each subclass should be annotated with the FilterActionAnnotation
    /// custom attribute, to provide information to the StarLight compiler about the behavior of this action concerning
    /// the message in the filterset.
    /// </summary>
    /// <example>
    /// <para>
    /// To create a custom filter action, create a class inheriting <see cref="T:Composestar.StarLight.Filters.FilterTypes.FilterAction"></see>. Override the <c>Execute</c> method and supply your own implementation.
    /// Next, place a <see cref="T:Composestar.StarLight.Filters.FilterTypes.FilterActionAttribute"></see> describing the filter action to the class.</para>
    /// <para>
    /// The following code shows a <i>StartTimerAction</i>, which will start a timer and place the timer values in the context properties of the <see cref="T:JoinPointContext"></see> parameter.</para>
    /// <code>
    /// [FilterActionAttribute("StartTimerAction", FilterFlowBehavior.Continue, MessageSubstitutionBehavior.Original)]
    /// public class StartTimerAction : FilterAction
    /// {
    ///     [DllImport("Kernel32.dll")]
    ///     private static extern bool QueryPerformanceCounter(
    ///         out long lpPerformanceCount);
    ///
    ///     [DllImport("Kernel32.dll")]
    ///     private static extern bool QueryPerformanceFrequency(
    ///         out long lpFrequency);
    ///        
    ///     public override void Execute(JoinPointContext context)
    ///     {
    ///         long starttime = 0;
    ///         long freq = 0;
    ///         
    ///         if (QueryPerformanceFrequency(out freq) == false) 
    ///         {
    ///             starttime = DateTime.Now.Ticks;
    ///         }
    ///         else 
    ///         {
    ///             QueryPerformanceCounter(out starttime);
    ///         }
    ///
    ///         context.AddProperty("frequency", freq);
    ///         context.AddProperty("starttime", starttime);
    ///     }
    /// }
    /// </code>
    /// <para>
    /// To use this filter action, implement a <see cref="T:Composestar.StarLight.Filters.FilterTypes.FilterType"/> and specify, using the <see cref="T:Composestar.StarLight.Filters.FilterTypes.FilterTypeAttribute"/>, the name of this filter action. 
    /// Now you can use the filter type in your concern specification.   
    /// </para> 
    /// <note>
    /// To make a properly working filter, implement also a <i>StopTimerAction</i>, which retrieves the properties from the context and calculates the end time and thus the duration.
    /// </note> 
    /// <note>
    /// The above example uses kernel calls to a high performance counter. It is also possible to use the <see cref="T:System.Diagnostics.Stopwatch"></see> class.
    /// </note> 
    /// </example> 
    /// <seealso cref="T:Composestar.StarLight.Filters.FilterTypes.FilterType"/>
    /// <seealso cref="T:Composestar.StarLight.Filters.FilterTypes.FilterActionAttribute"/>
    public abstract class FilterAction
    {
        /// <summary>
        /// Implements the behavior of the FilterAction. You must override this method and supply your own filteraction implementation.
        /// </summary>
        /// <param name="context">Join Point Context information.</param>
        /// <remarks>If the developer has set the CreateJoinPointContext to <see langword="false"/> 
        /// in the <see cref="T:Composestar.StarLight.Filters.FilterTypes.FilterActionAttribute"/>, then the weaver injects <see langword="null"/> instead of a 
        /// <see cref="Composestar.StarLight.ContextInfo.JoinPointContext"/></remarks>
        public abstract void Execute(JoinPointContext context);

        #region BuildIn FilterActions

        /// <summary>
        /// Advice action constant value.
        /// </summary>
        public const string AdviceAction = "AdviceAction";

        /// <summary>
        /// Continue action constant value.
        /// </summary>
        public const string ContinueAction = "ContinueAction";

        /// <summary>
        /// Dispatch action constant value.
        /// </summary>
        public const string DispatchAction = "DispatchAction";

        /// <summary>
        /// Error action constant value.
        /// </summary>
        public const string ErrorAction = "ErrorAction";

        /// <summary>
        /// Substitution action constant value.
        /// </summary>
        public const string SubstitutionAction = "SubstitutionAction";
        
        #endregion

    }
}
