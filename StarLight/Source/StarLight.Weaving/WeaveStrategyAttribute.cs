#region Using directives
using System;
using System.Collections.Generic;
using System.Text;
#endregion

namespace Composestar.StarLight.Weaving.Strategies
{
    /// <summary>
    /// This custom attribute must be applied to weaving strategies to enable the naming of those strategies.      
    /// </summary>
    /// <example>
    /// To use a weaving strategy, create a class which inherits from <see cref="T:FilterActionWeaveStrategy"/> and place this custom attribute to the top.
    /// It is possible to add multiple attributes to a weaving strategy, so you can specify multiple names for a strategy.
    /// <code>
    /// [WeaveStrategyAttribute("AdviceAction")]
    /// [WeaveStrategyAttribute("BeforeAction")]
    /// [WeaveStrategyAttribute("AfterAction")]
    /// public class AdviceActionWeaveStrategy : FilterActionWeaveStrategy
    /// {
    ///                
    ///    public override void Weave(ICecilInliningInstructionVisitor visitor, FilterAction filterAction,
    ///        MethodDefinition originalCall)
    ///    {
    ///
    ///    } 
    /// 
    /// }
    /// </code>
    /// </example> 
    [AttributeUsage(AttributeTargets.Class, Inherited = false, AllowMultiple = true)]
    public sealed class WeaveStrategyAttribute : Attribute
    {

        #region Private Variables

        /// <summary>
        /// The name of the weaving strategy
        /// </summary>
        private string _name;

        #endregion

        #region Properties

        /// <summary>
        /// Gets the name of the weaving strategy.
        /// </summary>
        /// <value>The name of the weaving strategy.</value>
        public string WeavingStrategyName
        {
            get
            {
                return _name;
            }
        }

        #endregion

        #region ctors

        /// <summary>
        /// Initializes a new instance of the <see cref="T:WeaveStrategyAttribute"/> class. 
        /// This custom attribute must be applied to weaving strategies to enable the naming of those strategies.        
        /// </summary>
        /// <param name="weaveStrategyName">Name of the weave strategy.</param>
        /// <example>
        /// To use a weaving strategy, create a class which inherits from <see cref="T:FilterActionWeaveStrategy"/> and place this custom attribute to the top.     
        /// <code>
        /// [WeaveStrategyAttribute("AdviceAction")]
        /// public class AdviceActionWeaveStrategy : FilterActionWeaveStrategy
        /// {
        ///                
        ///    public override void Weave(ICecilInliningInstructionVisitor visitor, 
        ///        FilterAction filterAction,
        ///        MethodDefinition originalCall)
        ///    {
        ///
        ///    } 
        /// 
        /// }
        /// </code>
        /// </example> 
        /// <exception cref="weaveStrategyName">Thrown when the <paramref name="weaveStrategyName"/> is <see langword="null"/> or empty.</exception>
        public WeaveStrategyAttribute(string weaveStrategyName)
        {
            if (string.IsNullOrEmpty(weaveStrategyName))
                throw new ArgumentNullException("weaveStrategyName");

            _name = weaveStrategyName;

        }

        #endregion

    }
}