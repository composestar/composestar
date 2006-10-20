#region Using directives
using Composestar.StarLight.WeaveSpec.ConditionExpressions;
#endregion

/// <summary>
/// Composestar. star light. weave spec. condition expressions. visitor
/// </summary>
namespace Composestar.StarLight.WeaveSpec.ConditionExpressions.Visitor
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
        void VisitAndLeft(And and);
        
        /// <summary>
        /// Visits the AND right part.
        /// </summary>
        /// <param name="and">The and.</param>
        void VisitAndRight(And and);

        /// <summary>
        /// Visits the condition literal.
        /// </summary>
        /// <param name="conditionLiteral">The condition literal.</param>
        void VisitConditionLiteral(ConditionLiteral conditionLiteral);
        
        /// <summary>
        /// Visits the false.
        /// </summary>
        /// <param name="falseObject">The false object.</param>
        void VisitFalse(False falseObject);
        
        /// <summary>
        /// Visits the true.
        /// </summary>
        /// <param name="trueObject">The true object.</param>
        void VisitTrue(True trueObject);
        
        /// <summary>
        /// Visits the not.
        /// </summary>
        /// <param name="not">The not.</param>
        void VisitNot(Not not);
        
        /// <summary>
        /// Visits the OR left part.
        /// </summary>
        /// <param name="or">The or.</param>
        void VisitOrLeft(Or or);

        /// <summary>
        /// Visits the OR right part.
        /// </summary>
        /// <param name="or">The or.</param>
        void VisitOrRight(Or or);

    } // IVisitor
} 
