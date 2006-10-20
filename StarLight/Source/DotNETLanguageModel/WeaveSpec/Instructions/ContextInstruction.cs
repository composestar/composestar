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
    /// A ContextInstructions controls the context of the instructions.
    /// </summary>
    [Serializable]
    [XmlRoot("ContextInstruction", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class ContextInstruction : InlineInstruction, IVisitable
    {

        private ContextType _type;
        private int _code;
        private Block _innerBlock;

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ContextInstruction"/> class.
        /// </summary>
        public ContextInstruction()
        {

        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ContextInstruction"/> class.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <param name="code">The code.</param>
        /// <param name="innerBlock">The inner block.</param>
        public ContextInstruction(ContextType type, int code, Block innerBlock)
        {
            _type = type;
            _code = code;
            _innerBlock = innerBlock;
        } // ContextInstruction(type, code, innerBlock)

        /// <summary>
        /// Gets or sets the inner block.
        /// </summary>
        /// <value>The inner block.</value>
        public Block InnerBlock
        {
            get
            {
                return _innerBlock;
            } // get
            set
            {
                _innerBlock = value;
            } // set
        } // InnerBlock

        /// <summary>
        /// Gets or sets the code.
        /// </summary>
        /// <value>The code.</value>
        [XmlAttribute]
        public int Code
        {
            get
            {
                return _code;
            } // get
            set
            {
                _code = value;
            } // set
        } // Code

        /// <summary>
        /// Gets or sets the type.
        /// </summary>
        /// <value>The type.</value>
        [XmlAttribute]
        public ContextType Type
        {
            get
            {
                return _type;
            } // get
            set
            {
                _type = value;
            } // set
        } // Type


        /// <summary>
        /// Accepts the specified visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        public void Accept(IVisitor visitor)
        {
            base.Accept(visitor);

            switch (_type)
            {
                case ContextType.SetInnerCall:
                    visitor.VisitSetInnerCall(this);
                    break;
                case ContextType.CheckInnerCall:
                    visitor.VisitCheckInnerCall(this);
                    break;
                case ContextType.ResetInnerCall:
                    visitor.VisitResetInnerCall(this);
                    break;
                case ContextType.CreateActionStore:
                    visitor.VisitCreateActionStore(this);
                    break;
                case ContextType.StoreAction:
                    visitor.VisitStoreAction(this);
                    break;
                case ContextType.CreateJPC:
                    visitor.VisitCreateJoinPointContext(this);
                    break;
                case ContextType.RestoreJPC:
                    visitor.VisitRestoreJoinPointContext(this);
                    break;
                case ContextType.ReturnAction:
                    visitor.VisitReturnAction(this);
                    break;
                case ContextType.Removed:
                    return;
                default:
                    break;
            } // switch

            if (_innerBlock != null)
                _innerBlock.Accept(visitor);
        } // Accept(visitor)

    } // class ContextInstruction
}
