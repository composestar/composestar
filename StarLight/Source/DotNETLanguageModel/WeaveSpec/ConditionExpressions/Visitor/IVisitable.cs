namespace Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions.Visitor
{
    /// <summary>
    /// Indicates that the class is visitable.
    /// </summary>
    public interface IVisitable
    {
        /// <summary>
        /// Accepts the specified visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        void Accept(IVisitor visitor);
    } // IVisitable
}
