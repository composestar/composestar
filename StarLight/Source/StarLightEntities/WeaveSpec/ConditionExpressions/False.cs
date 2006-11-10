#region Using directives
using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions.Visitor;
#endregion

namespace Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions
{
    /// <summary>
    /// A false literal.
    /// </summary>
    [Serializable]
    [XmlRoot("False", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class FalseCondition : ConditionExpression, IVisitable
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="T:False"/> class.
        /// </summary>
        public FalseCondition()
        {

        } // False()

        /// <summary>
        /// Accepts the specified visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        public void Accept(IVisitor visitor)
        {
            if (visitor == null)
                throw new ArgumentNullException("visitor");

            visitor.VisitFalse(this);
        } // Accept(visitor)

    } // class False
}
