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
    /// A single case in a select statement.
    /// </summary>
    [Serializable]
    [XmlRoot("Case", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class CaseInstruction : InlineInstruction, IVisitable
    {

        private int _checkConstant;
        private Block _instructions;

        /// <summary>
        /// Initializes a new instance of the <see cref="T:Case"/> class.
        /// </summary>
        public CaseInstruction()
        {

        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:Case"/> class.
        /// </summary>
        /// <param name="checkConstant">The check constant.</param>
        /// <param name="instructions">The instructions.</param>
        public CaseInstruction(int checkConstant, Block instructions)
        {
            _checkConstant = checkConstant;
            _instructions = instructions;
        } // Case(checkConstant, instructions)

        /// <summary>
        /// Gets or sets the check constant.
        /// </summary>
        /// <value>The check constant.</value>
        [XmlAttribute]
        public int CheckConstant
        {
            get
            {
                return _checkConstant;
            } // get
            set
            {
                _checkConstant = value;
            } // set
        } // CheckConstant
        /// <summary>
        /// Instructions
        /// </summary>
        /// <returns>Block</returns>
        public Block Instructions
        {
            get
            {
                return _instructions;
            } // get
            set
            {
                _instructions = value;
            } // set
        } // Instructions


        /// <summary>
        /// Accepts the specified visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        public new void Accept(IVisitor visitor)
        {
            if (visitor == null)
                throw new ArgumentNullException("visitor");

            base.Accept(visitor);
            visitor.VisitCase(this);
            if (_instructions != null)
                ((IVisitable)_instructions).Accept(visitor);
        } // Accept(visitor)


    } // class Case
} // namespace Composestar.StarLight.Entities.WeaveSpec.Instructions
