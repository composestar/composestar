#region Using directives
using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using Composestar.StarLight.Entities.WeaveSpec.Instructions.Visitor;
#endregion

/// <summary>
/// Composestar. star light. weave spec. instructions
/// </summary>
namespace Composestar.StarLight.Entities.WeaveSpec.Instructions
{

    /// <summary>
    /// Base class for the inline weaving instructions.
    /// </summary>
    [Serializable]
    [XmlRoot("Instruction", Namespace = "Entities.TYM.DotNET.Composestar")]
    public abstract class InlineInstruction : IVisitable
    {

        private int _label;

        /// <summary>
        /// Create inline instruction
        /// </summary>
        public InlineInstruction()
        {

        } // InlineInstruction()

        /// <summary>
        /// Create inline instruction
        /// </summary>
        /// <param name="label">Label</param>
        public InlineInstruction(int label)
        {
            _label = label;
        } // InlineInstruction(label)


        /// <summary>
        /// Gets or sets the label.
        /// </summary>
        /// <value>The label.</value>
        [XmlAttribute]
        public int Label
        {
            get
            {
                return _label;
            }
            set
            {
                _label = value;
            }
        }

        /// <summary>
        /// Accepts the specified visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        public void Accept(IVisitor visitor)
        {
            visitor.VisitInlineInstruction(this);
        } // Accept(visitor)

        public override string ToString()
        {
            return string.Format("Label {0}:\n", _label.ToString());
        }

    } // class InlineInstruction
} // namespace Composestar.StarLight.Entities.WeaveSpec.Instructions
