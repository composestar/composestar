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
    /// True value.
    /// </summary>
    [Serializable]
    [XmlRoot("True", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class True : ConditionExpression, IVisitable
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="T:True"/> class.
        /// </summary>
        public True()
        {

        } // True()

        /// <summary>
        /// Accepts the specified visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        public void Accept(IVisitor visitor)
        {
            visitor.VisitTrue(this);
        } // Accept(visitor)

    } // class True

}
