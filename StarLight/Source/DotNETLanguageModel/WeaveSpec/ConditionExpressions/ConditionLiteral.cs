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
    /// A literal representation.
    /// </summary>
    [Serializable]
    [XmlRoot("ConditionLiteral", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class ConditionLiteral : ConditionExpression, IVisitable
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="T:ConditionLiteral"/> class.
        /// </summary>
        public ConditionLiteral()
        {

        } // ConditionLiteral()

        /// <summary>
        /// Gets or sets the name.
        /// </summary>
        /// <value>The name.</value>
        [XmlAttribute]
        public String Name
        {
            get
            {
                return _name;
            } // get
            set
            {
                _name = value;
            } // set
        } // Name

        private String _name;


        /// <summary>
        /// Accepts the specified visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        public void Accept(IVisitor visitor)
        {
            visitor.VisitConditionLiteral(this);
        } // Accept(visitor)

    } // class ConditionLiteral
}
