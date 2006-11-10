#region Using directives
using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using Composestar.StarLight.Entities.WeaveSpec.Instructions.Visitor;    
#endregion

namespace Composestar.StarLight.Entities.WeaveSpec.Instructions
{
    /// <summary>
    /// A while statement.
    /// </summary>
    [Serializable]
    [XmlRoot("While", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class WhileInstruction : InlineInstruction, IVisitable 
    {

        private ContextExpression _expression;
        private Block _instructions;

        /// <summary>
        /// Initializes a new instance of the <see cref="T:While"/> class.
        /// </summary>
        public WhileInstruction()
        {

        } // While()

        /// <summary>
        /// Initializes a new instance of the <see cref="T:While"/> class.
        /// </summary>
        /// <param name="expression">The expression.</param>
        /// <param name="instructions">The instructions.</param>
        public WhileInstruction(ContextExpression expression, Block instructions)
        {
            _expression = expression;
            _instructions = instructions;
        } // While(expression, instructions)

        /// <summary>
        /// Gets or sets the expression.
        /// </summary>
        /// <value>The expression.</value>
        public ContextExpression Expression
        {
            get { return _expression; } // get
            set { _expression = value; } // set
        } // Expression

        /// <summary>
        /// Gets or sets the instructions.
        /// </summary>
        /// <value>The instructions.</value>
        public Block Instructions
        {
            get { return _instructions; } // get
            set { _instructions = value; } // set
        } // Instructions

        /// <summary>
        /// Accepts the specified visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        public new void Accept(IVisitor visitor)
        {
            if (visitor == null)
                throw new ArgumentNullException("visitor");

            base.Accept(visitor);
            int label = this.Label; // FIXME nice way to restore label after visitor has changed it
            visitor.VisitWhile(this);
            if (_instructions != null)
                ((IVisitable)_instructions).Accept(visitor);
            visitor.VisitWhileEnd(this);
            this.Label = label; // FIXME
        } // Accept(visitor)

    } // class While
}