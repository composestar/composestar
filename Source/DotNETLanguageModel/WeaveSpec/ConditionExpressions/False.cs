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
    /// A false literal.
    /// </summary>
    [Serializable]
    [XmlRoot("False", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class False : ConditionExpression, IVisitable
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="T:False"/> class.
        /// </summary>
        public False()
        {

        } // False()

        /// <summary>
        /// Accepts the specified visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        public void Accept(IVisitor visitor)
        {
            visitor.VisitFalse(this);
        } // Accept(visitor)

    } // class False
}
