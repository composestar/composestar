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
    /// A switch statement.
    /// </summary>
    [Serializable]
    [XmlRoot("Switch", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class SwitchInstruction : InlineInstruction, IVisitable
    {

        private ContextExpression _expression;
        private List<CaseInstruction> _cases = new List<CaseInstruction>();

        /// <summary>
        /// Initializes a new instance of the <see cref="T:Switch"/> class.
        /// </summary>
        public SwitchInstruction()
        {

        } // Switch()

        /// <summary>
        /// Initializes a new instance of the <see cref="T:Switch"/> class.
        /// </summary>
        /// <param name="expression">The expression.</param>
        public SwitchInstruction(ContextExpression expression)
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
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
        public List<CaseInstruction> Cases
        {
            get { return _cases; } // get    
        } // Cases

        /// <summary>
        /// Accepts the specified visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        public new void Accept(IVisitor visitor)
        {
            if (visitor == null)
                throw new ArgumentNullException("visitor");

            base.Accept(visitor);
            visitor.VisitSwitch(this);

            foreach (CaseInstruction caseItem in _cases)
            {
                caseItem.Accept(visitor);
                visitor.VisitCaseEnd(this);
            } // foreach  (caseItem)

            visitor.VisitSwitchEnd(this);

        } // Accept(visitor)

    } // class Switch
  
}
