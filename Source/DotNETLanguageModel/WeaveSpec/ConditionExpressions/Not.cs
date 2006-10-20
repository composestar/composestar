#region Using directives
using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using Composestar.StarLight.WeaveSpec.ConditionExpressions.Visitor;
#endregion

namespace Composestar.StarLight.WeaveSpec.ConditionExpressions
{
    /// <summary>
    /// A not operator.
    /// </summary>
    [Serializable]
    [XmlRoot("Not", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class Not : ConditionExpression, IVisitable
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="T:Not"/> class.
        /// </summary>
        public Not()
        {
        } // Not()

        /// <summary>
        /// Gets or sets the operand.
        /// </summary>
        /// <value>The operand.</value>
        public ConditionExpression Operand
        {
            get
            {
                return _operand;
            } // get
            set
            {
                _operand = value;
            } // set
        } // Operand

        private ConditionExpression _operand;


        /// <summary>
        /// Accepts the specified visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        public void Accept(IVisitor visitor)
        {
            if (_operand != null)
            {
                ((IVisitable)_operand).Accept(visitor);
            } // if
            visitor.VisitNot(this);
        } // Accept(visitor)

    } // class Not
}
