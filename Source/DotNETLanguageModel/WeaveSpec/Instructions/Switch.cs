#region Using directives
using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using Composestar.StarLight.WeaveSpec.Instructions.Visitor;    
#endregion

namespace Composestar.StarLight.WeaveSpec.Instructions
{
    /// <summary>
    /// A switch statement.
    /// </summary>
    [Serializable]
    [XmlRoot("Switch", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class Switch : InlineInstruction, IVisitable
    {

        private ContextExpression _expression;
        private List<Case> _cases = new List<Case>();

        /// <summary>
        /// Initializes a new instance of the <see cref="T:Switch"/> class.
        /// </summary>
        public Switch()
        {

        } // Switch()

        /// <summary>
        /// Initializes a new instance of the <see cref="T:Switch"/> class.
        /// </summary>
        /// <param name="expression">The expression.</param>
        public Switch(ContextExpression expression)
        {
            _expression = expression;
        } // Switch(expression)


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
        /// Gets or sets the cases.
        /// </summary>
        /// <value>The cases.</value>
        public List<Case> Cases
        {
            get { return _cases; } // get
            set { _cases = value; } // set
        } // Cases

        /// <summary>
        /// Accepts the specified visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        public void Accept(IVisitor visitor)
        {
            base.Accept(visitor);
            visitor.VisitSwitch(this);

            foreach (Case caseItem in _cases)
            {
                caseItem.Accept(visitor);
                visitor.VisitCaseEnd(this);
            } // foreach  (caseItem)

            visitor.VisitSwitchEnd(this);

        } // Accept(visitor)

    } // class Switch
  
}
