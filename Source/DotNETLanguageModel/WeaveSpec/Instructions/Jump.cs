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
    /// A jump instruction to a specific label specified by the target.
    /// </summary>
    [Serializable]
    [XmlRoot("Jump", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class Jump : InlineInstruction, IVisitable
    {
        private int _target;

        /// <summary>
        /// Initializes a new instance of the <see cref="T:Jump"/> class.
        /// </summary>
        public Jump()
        {

        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:Jump"/> class.
        /// </summary>
        /// <param name="target">The target.</param>
        public Jump(int target)
        {
            _target = target;
        } // Jump(target)

        /// <summary>
        /// Gets or sets the target.
        /// </summary>
        /// <value>The target.</value>
        [XmlAttribute]
        public int Target
        {
            get { return _target; } // get
            set { _target = value; } // set
        } // Target

        /// <summary>
        /// Accepts the specified visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        public void Accept(IVisitor visitor)
        {
            base.Accept(visitor);
            visitor.VisitJumpInstruction(this);
        } // Accept(visitor)

    } // class Jump
}
