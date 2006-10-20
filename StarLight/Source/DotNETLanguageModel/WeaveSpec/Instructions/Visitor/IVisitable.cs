namespace Composestar.StarLight.WeaveSpec.Instructions.Visitor
{
    /// <summary>
    /// Interface for instructions which can be visited.
    /// </summary>
    public interface IVisitable
    {
        /// <summary>
        /// Accepts the specified visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        void Accept(IVisitor visitor);

    } // IVisitable
} // namespace Composestar.StarLight.WeaveSpec.Instructions.Visitor
