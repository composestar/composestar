using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using Composestar.StarLight.Entities.WeaveSpec.Instructions.Visitor;

namespace Composestar.StarLight.Entities.WeaveSpec.Instructions
{
    /// <summary>
    /// A block containg a list of <see cref="T:InlineInstruction"></see> objects.
    /// </summary>
    [Serializable]
    [XmlRoot("Block", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class Block : InlineInstruction, IVisitable
    {

        /// <summary>
        /// Initializes a new instance of the <see cref="T:Block"/> class.
        /// </summary>
        public Block()
        {

        } // Block()


        private List<InlineInstruction> _instructions = new List<InlineInstruction>();

        /// <summary>
        /// Gets or sets the instructions.
        /// </summary>
        /// <value>The instructions.</value>
        [XmlArray("Instructions")]
        [XmlArrayItem("Instruction")]
        public List<InlineInstruction> Instructions
        {
            get
            {
                return _instructions;
            }
            set
            {
                _instructions = value;
            }
        }

        /// <summary>
        /// Accepts the specified visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        public void Accept(IVisitor visitor)
        {
            base.Accept(visitor);

            foreach (IVisitable instr in _instructions)
            {
                instr.Accept(visitor);
            }

        } // Accept(visitor)

    }

} // namespace Composestar.StarLight.Entities.WeaveSpec.Instructions
