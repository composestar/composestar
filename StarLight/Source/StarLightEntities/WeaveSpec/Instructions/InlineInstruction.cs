#region Using directives
using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using System.Globalization; 
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
        protected InlineInstruction()
        {

        } // InlineInstruction()

        /// <summary>
        /// Create inline instruction
        /// </summary>
        /// <param name="label">Label</param>
        protected InlineInstruction(int label)
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
            if (visitor == null)
                throw new ArgumentNullException("visitor");

            visitor.VisitInlineInstruction(this);
        }

        /// <summary>
        /// Returns a <see cref="T:System.String"></see> that represents the current <see cref="T:System.Object"></see>.
        /// </summary>
        /// <returns>
        /// A <see cref="T:System.String"></see> that represents the current <see cref="T:System.Object"></see>.
        /// </returns>
        public override string ToString()
        {
            return string.Format(CultureInfo.CurrentCulture, "Label {0}:\n", _label.ToString(CultureInfo.CurrentCulture));
        }

    } 
} 
