using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.Reflection.Emit;

namespace Composestar.StarLight.ILAnalyzer.ILInstructions
{
    /// <summary>
    /// A single IL instruction.
    /// </summary>
    public class ILInstruction
    {

        private Int32 _offSet;
        private OpCode _opCode = OpCodes.Nop;
        private MethodBase _enclosingMethod;

        /// <summary>
        /// Gets or sets the off set.
        /// </summary>
        /// <value>The off set.</value>
        public Int32 OffSet
        {
            get
            {
                return _offSet;
            }
            set
            {
                _offSet = value;
            }
        }

        /// <summary>
        /// Gets or sets the op code.
        /// </summary>
        /// <value>The op code.</value>
        public OpCode OpCode
        {
            get
            {
                return _opCode;
            }
            set
            {
                _opCode = value;
            }
        }

        /// <summary>
        /// Gets or sets the enclosing method.
        /// </summary>
        /// <value>The enclosing method.</value>
        public MethodBase EnclosingMethod
        {
            get
            {
                return _enclosingMethod;
            }
            set
            {
                _enclosingMethod = value;
            }
        }

        public ILInstruction()
        {

        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ILInstruction"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        /// <param name="offset">The offset.</param>
        /// <param name="opCode">The operation code.</param>
        public ILInstruction(MethodBase enclosingMethod, Int32 offSet, OpCode opCode)
        {
            _offSet = offSet;
            _opCode = opCode;
            _enclosingMethod = enclosingMethod;
        }
    }

    public class InlineNoneInstruction : ILInstruction
    {

        /// <summary>
        /// Initializes a new instance of the <see cref="T:InlineNoneInstruction"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        /// <param name="offset">The offset.</param>
        /// <param name="opCode">The op code.</param>
        public InlineNoneInstruction(MethodBase enclosingMethod, Int32 offSet, OpCode opCode)
            : base(enclosingMethod, offSet, opCode)
        {
        }

    }

    public class ShortInlineBrTargetInstruction : ILInstruction
    {

        private short _delta;

        /// <summary>
        /// Gets or sets the delta.
        /// </summary>
        /// <value>The delta.</value>
        public short Delta
        {
            get
            {
                return _delta;
            }
            set
            {
                _delta = value;
            }
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ShortInlineBrTargetInstruction"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        /// <param name="offset">The offset.</param>
        /// <param name="opCode">The op code.</param>
        /// <param name="delta">The delta.</param>
        public ShortInlineBrTargetInstruction(MethodBase enclosingMethod, Int32 offSet, OpCode opCode, short delta)
            :
            base(enclosingMethod, offSet, opCode)
        {
            _delta = delta;
        }

    }

    public class InlineMethodInstruction : ILInstruction
    {

        private int _token;


        /// <summary>
        /// Gets or sets the token.
        /// </summary>
        /// <value>The token.</value>
        public int Token
        {
            get
            {
                return _token;
            }
            set
            {
                _token = value;
            }
        }


        /// <summary>
        /// Initializes a new instance of the <see cref="T:InlineMethodInstruction"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        /// <param name="offset">The offset.</param>
        /// <param name="opCode">The op code.</param>
        /// <param name="token">The token.</param>
        public InlineMethodInstruction(MethodBase enclosingMethod, Int32 offSet, OpCode opCode, int token)
            :
            base(enclosingMethod, offSet, opCode)
        {
            _token = token;
        }

    }

    public class InlineInstruction : ILInstruction
    {

        private object _token;


        /// <summary>
        /// Gets or sets the token.
        /// </summary>
        /// <value>The token.</value>
        public object Token
        {
            get
            {
                return _token;
            }
            set
            {
                _token = value;
            }
        }


        /// <summary>
        /// Initializes a new instance of the <see cref="T:InlineMethodInstruction"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        /// <param name="offset">The offset.</param>
        /// <param name="opCode">The op code.</param>
        /// <param name="token">The token.</param>
        public InlineInstruction(MethodBase enclosingMethod, Int32 offSet, OpCode opCode, object token)
            :
            base(enclosingMethod, offSet, opCode)
        {
            _token = token;
        }

    }

    public class InlineSwitchInstruction : ILInstruction
    {

        private int[] _deltas;

        /// <summary>
        /// Gets or sets the deltas.
        /// </summary>
        /// <value>The deltas.</value>
        public int[] Deltas
        {
            get
            {
                return _deltas;
            }
            set
            {
                _deltas = value;
            }
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:InlineSwitchInstruction"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        /// <param name="offset">The offset.</param>
        /// <param name="opCode">The op code.</param>
        /// <param name="deltas">The deltas.</param>
        public InlineSwitchInstruction(MethodBase enclosingMethod, Int32 offSet, OpCode opCode, int[] deltas)
            :
            base(enclosingMethod, offSet, opCode)
        {
            _deltas = deltas;
        }

    }

}
