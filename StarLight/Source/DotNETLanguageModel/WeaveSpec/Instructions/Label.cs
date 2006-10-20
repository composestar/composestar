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
    /// A label is used to specify points in the control flow.
    /// </summary>
    [Serializable]
    [XmlRoot("Label", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class Label : IVisitable
    {
        private int _id;

        /// <summary>
        /// Initializes a new instance of the <see cref="T:Label"/> class.
        /// </summary>
        public Label()
        {
            _id = -1;
        } // Label()

        /// <summary>
        /// Initializes a new instance of the <see cref="T:Label"/> class.
        /// </summary>
        /// <param name="id">The id.</param>
        public Label(int id)
        {
            _id = id;
        } // Label()

        /// <summary>
        /// Gets or sets the id.
        /// </summary>
        /// <value>The id.</value>
        [XmlAttribute]
        public int Id
        {
            get { return _id; } // get
            set { _id = value; } // set
        } // Id

        /// <summary>
        /// Accepts the specified visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        public void Accept(IVisitor visitor)
        {

        } // Accept(visitor)

    } // class Label
}
