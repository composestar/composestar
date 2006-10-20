#region Using directives
using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using Composestar.StarLight.WeaveSpec.Instructions.Visitor;
using Composestar.StarLight.WeaveSpec.ConditionExpressions; 
#endregion

/// <summary>
/// Composestar. star light. weave spec. instructions
/// </summary>
namespace Composestar.StarLight.WeaveSpec.Instructions
{

    /// <summary>
    /// A branch contains two blocks; a true and a false flow. The condition determines which path is taken at runtime.
    /// </summary>
    [Serializable]
    [XmlRoot("Branch", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class Branch : InlineInstruction, IVisitable
    {
        private ConditionExpression _conditionExpression;

        private Block _trueBlock;
        private Block _falseBlock;

        /// <summary>
        /// Initializes a new instance of the <see cref="T:Branch"/> class.
        /// </summary>
        public Branch()
        {

        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:Branch"/> class.
        /// </summary>
        /// <param name="conditionExpression">The condition expression.</param>
        public Branch(ConditionExpression conditionExpression)
        {
            _conditionExpression = conditionExpression;

        } // Branch(conditionExpression)

        /// <summary>
        /// Gets or sets the true block.
        /// </summary>
        /// <value>The true block.</value>
        public Block TrueBlock
        {
            get
            {
                return _trueBlock;
            } // get
            set
            {
                _trueBlock = value;
            } // set
        } // TrueBlock

        /// <summary>
        /// Gets or sets the false block.
        /// </summary>
        /// <value>The false block.</value>
        public Block FalseBlock
        {
            get
            {
                return _falseBlock;
            } // get
            set
            {
                _falseBlock = value;
            } // set
        } // FalseBlock

        /// <summary>
        /// Gets or sets the condition expression.
        /// </summary>
        /// <value>The condition expression.</value>
        [XmlElement("Condition")]
        public ConditionExpression ConditionExpression
        {
            get
            {
                return _conditionExpression;
            } // get
            set
            {
                _conditionExpression = value;
            } // set
        } // ConditionExpression


        /// <summary>
        /// Accepts the specified visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        public void Accept(IVisitor visitor)
        {
            base.Accept(visitor);

            visitor.VisitBranch(this);
            if (_trueBlock != null)
                _trueBlock.Accept(visitor);
            visitor.VisitBranchFalse(this);
            if (_falseBlock != null)
                _falseBlock.Accept(visitor);
            visitor.VisitBranchEnd(this);

        } // Accept(visitor)


    } // class Branch
} // namespace Composestar.StarLight.WeaveSpec.Instructions
