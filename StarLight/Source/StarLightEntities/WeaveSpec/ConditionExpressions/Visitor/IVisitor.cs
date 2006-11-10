#region Using directives
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions;
#endregion

/// <summary>
/// Composestar. star light. weave spec. condition expressions. visitor
/// </summary>
namespace Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions.Visitor
{

    /// <summary>
    /// Interface for the Condition Expression visitor.
    /// </summary>
    public interface IVisitor
    {
        /// <summary>
        /// Visits the AND left part.
        /// </summary>
        /// <param name="and">The and.</param>
        void VisitAndLeft(AndCondition and);
        
        /// <summary>
        /// Visits the AND right part.
        /// </summary>
        /// <param name="and">The and.</param>
        void VisitAndRight(AndCondition and);

        /// <summary>
        /// Visits the condition literal.
        /// </summary>
        /// <param name="conditionLiteral">The condition literal.</param>
        void VisitConditionLiteral(ConditionLiteral conditionLiteral);
        
        /// <summary>
        /// Visits the false.
        /// </summary>
        /// <param name="falseObject">The false object.</param>
        void VisitFalse(FalseCondition falseObject);
        
        /// <summary>
        /// Visits the true.
        /// </summary>
        /// <param name="trueObject">The true object.</param>
        void VisitTrue(TrueCondition trueObject);
        
        /// <summary>
        /// Visits the not.
        /// </summary>
        /// <param name="not">The not.</param>
        void VisitNot(NotCondition not);
        
        /// <summary>
        /// Visits the OR left part.
        /// </summary>
        /// <param name="or">The or.</param>
        void VisitOrLeft(OrCondition or);

        /// <summary>
        /// Visits the OR right part.
        /// </summary>
        /// <param name="or">The or.</param>
        void VisitOrRight(OrCondition or);

    } // IVisitor
} 
