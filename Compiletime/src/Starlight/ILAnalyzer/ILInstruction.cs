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
    public abstract class ILInstruction
    {

        private Int32 _offset;
        private OpCode _opCode = OpCodes.Nop;
        private MethodBase _enclosingMethod;

        /// <summary>
        /// Gets or sets the off set.
        /// </summary>
        /// <value>The off set.</value>
        public Int32 Offset
        {
            get
            {
                return _offset;
            }
            set
            {
                _offset = value;
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

        public virtual string ProcessedOperand { get { return String.Empty; } }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ILInstruction"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        /// <param name="offset">The offset.</param>
        /// <param name="opCode">The operation code.</param>
        public ILInstruction(MethodBase enclosingMethod, Int32 offset, OpCode opCode)
        {
            _offset = offset;
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
        public InlineNoneInstruction(MethodBase enclosingMethod, Int32 offset, OpCode opCode)
            : base(enclosingMethod, offset, opCode)
        {
        }

    }

    public class InlineBrTargetInstruction : ILInstruction
    {

        private Int32 _delta;

        /// <summary>
        /// Gets or sets the delta.
        /// </summary>
        /// <value>The delta.</value>
        public Int32 Delta
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
        /// Initializes a new instance of the <see cref="T:InlineBrTargetInstruction"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        /// <param name="offset">The off set.</param>
        /// <param name="opCode">The op code.</param>
        /// <param name="delta">The delta.</param>
        public InlineBrTargetInstruction(MethodBase enclosingMethod, Int32 offset, OpCode opCode, Int32 delta)
            :
            base(enclosingMethod, offset, opCode)
        {
            _delta = delta;
        }

        public Int32 TargetOffset { get { return Offset + _delta + 1 + 4; } }
    }

    public class ShortInlineBrTargetInstruction : ILInstruction
    {

        private SByte _delta;

        /// <summary>
        /// Gets or sets the delta.
        /// </summary>
        /// <value>The delta.</value>
        public SByte Delta
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
        public ShortInlineBrTargetInstruction(MethodBase enclosingMethod, Int32 offset, OpCode opCode, SByte delta)
            :
            base(enclosingMethod, offset, opCode)
        {
            _delta = delta;
        }

        public Int32 TargetOffset { get { return Offset + _delta + 1 + 1; } }

    }

    public class InlineIInstruction : ILInstruction
    {

        private Int32 _int32;

        /// <summary>
        /// Gets or sets the int32.
        /// </summary>
        /// <value>The int32.</value>
        public Int32 Int32
        {
            get
            {
                return _int32;
            }
            set
            {
                _int32 = value;
            }
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:InlineIInstruction"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        /// <param name="offset">The offset.</param>
        /// <param name="opCode">The op code.</param>
        /// <param name="value">The value.</param>
        public InlineIInstruction(MethodBase enclosingMethod, Int32 offset, OpCode opCode, Int32 value)
            :
                    base(enclosingMethod, offset, opCode)
        {
            _int32 = value;
        }

        /// <summary>
        /// Gets the processed operand.
        /// </summary>
        /// <value>The processed operand.</value>
        public override string ProcessedOperand { get { return _int32.ToString(); } }

    }

    public class ShortInlineIInstruction : ILInstruction
    {

        private Byte _int8;


        /// <summary>
        /// Gets or sets the _int8.
        /// </summary>
        /// <value>The _int8.</value>
        public Byte Int64
        {
            get
            {
                return _int8;
            }
            set
            {
                _int8 = value;
            }
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ShortInlineIInstruction"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        /// <param name="offset">The offset.</param>
        /// <param name="opCode">The op code.</param>
        /// <param name="value">The value.</param>
        public ShortInlineIInstruction(MethodBase enclosingMethod, Int32 offset, OpCode opCode, Byte value)
            : base(enclosingMethod, offset, opCode)
        {
            _int8 = value;
        }

        /// <summary>
        /// Gets the processed operand.
        /// </summary>
        /// <value>The processed operand.</value>
        public override string ProcessedOperand { get { return _int8.ToString(); } }

    }


    public class InlineRInstruction : ILInstruction
    {

        private Double _value;


        /// <summary>
        /// Gets or sets the Double.
        /// </summary>
        /// <value>The Double.</value>
        public Double Double
        {
            get
            {
                return _value;
            }
            set
            {
                _value = value;
            }
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:InlineRInstruction"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        /// <param name="offset">The offset.</param>
        /// <param name="opCode">The op code.</param>
        /// <param name="value">The value.</param>
        public InlineRInstruction(MethodBase enclosingMethod, Int32 offset, OpCode opCode, Double value)
            : base(enclosingMethod, offset, opCode)
        {
            _value = value;
        }

        /// <summary>
        /// Gets the processed operand.
        /// </summary>
        /// <value>The processed operand.</value>
        public override string ProcessedOperand { get { return _value.ToString(); } }

    }

    public class ShortInlineRInstruction : ILInstruction
    {

        private Single _value;


        /// <summary>
        /// Gets or sets the Single.
        /// </summary>
        /// <value>The Single.</value>
        public Single Single
        {
            get
            {
                return _value;
            }
            set
            {
                _value = value;
            }
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ShortInlineRInstruction"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        /// <param name="offset">The offset.</param>
        /// <param name="opCode">The op code.</param>
        /// <param name="value">The value.</param>
        public ShortInlineRInstruction(MethodBase enclosingMethod, Int32 offset, OpCode opCode, Single value)
            :             base(enclosingMethod, offset, opCode)
        {
            _value = value;
        }

        /// <summary>
        /// Gets the processed operand.
        /// </summary>
        /// <value>The processed operand.</value>
        public override string ProcessedOperand { get { return _value.ToString(); } }

    }

    public class InlineFieldInstruction : ILInstruction
    {

        private Int32 _token;
        FieldInfo _field;

        /// <summary>
        /// Gets the field.
        /// </summary>
        /// <value>The field.</value>
        public FieldInfo Field
        {
            get
            {
                throw new NotImplementedException("This functionality is not yet implemented");
                //if (m_field == null) m_field = m_resolver.AsField(m_token);
                //return m_field;
            }
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:InlineFieldInstruction"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        /// <param name="offset">The offset.</param>
        /// <param name="opCode">The op code.</param>
        /// <param name="token">The token.</param>
        public InlineFieldInstruction(MethodBase enclosingMethod, Int32 offset, OpCode opCode, Int32 token)
            :
                    base(enclosingMethod, offset, opCode)
        {
            _token = token;
        }

        /// <summary>
        /// Gets the token.
        /// </summary>
        /// <value>The token.</value>
        public Int32 Token { get { return _token; } }
        /// <summary>
        /// Gets the processed operand.
        /// </summary>
        /// <value>The processed operand.</value>
        public override string ProcessedOperand { get { return Field + "/" + Field.DeclaringType; } }

    }

    public class InlineI8Instruction : ILInstruction
    {

        private Int64 _int64;

        /// <summary>
        /// Gets or sets the Int64.
        /// </summary>
        /// <value>The Int64.</value>
        public Int64 Int64
        {
            get
            {
                return _int64;
            }
            set
            {
                _int64 = value;
            }
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:InlineI8Instruction"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        /// <param name="offset">The offset.</param>
        /// <param name="opCode">The op code.</param>
        /// <param name="value">The value.</param>
        public InlineI8Instruction(MethodBase enclosingMethod, Int32 offset, OpCode opCode, Int64 value)
            :
                    base(enclosingMethod, offset, opCode)
        {
            _int64 = value;
        }

        /// <summary>
        /// Gets the processed operand.
        /// </summary>
        /// <value>The processed operand.</value>
        public override string ProcessedOperand { get { return _int64.ToString(); } }

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

        Int32[] _targetOffsets;

        /// <summary>
        /// Gets the target offsets.
        /// </summary>
        /// <value>The target offsets.</value>
        public Int32[] TargetOffsets
        {
            get
            {
                if (_targetOffsets == null)
                {
                    int cases = _deltas.Length;
                    int itself = 1 + 4 + 4 * cases;
                    _targetOffsets = new Int32[cases];
                    for (Int32 i = 0; i < cases; i++)
                        _targetOffsets[i] = Offset + _deltas[i] + itself;
                }
                return _targetOffsets;
            }
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:InlineSwitchInstruction"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        /// <param name="offset">The offset.</param>
        /// <param name="opCode">The op code.</param>
        /// <param name="deltas">The deltas.</param>
        public InlineSwitchInstruction(MethodBase enclosingMethod, Int32 offset, OpCode opCode, int[] deltas)
            :
            base(enclosingMethod, offset, opCode)
        {
            _deltas = deltas;
        }

    }

    public class InlineMethodInstruction : ILInstruction
    {
        public InlineMethodInstruction(MethodBase enclosingMethod, Int32 offset, OpCode opCode, Int32 token)
            : base(enclosingMethod, offset, opCode)
        {
            this.m_token = token;
        }

        /// <summary>
        /// Gets the method.
        /// </summary>
        /// <value>The method.</value>
        public MethodBase Method
        {
            get
            {
                throw new NotImplementedException("This functionality is not yet implemented");
                //if (m_method == null) m_method = m_resolver.AsMethod(m_token);
                return m_method;
            }
        }
        /// <summary>
        /// Gets the token.
        /// </summary>
        /// <value>The token.</value>
        public Int32 Token { get { return m_token; } }
        /// <summary>
        /// Gets the processed operand.
        /// </summary>
        /// <value>The processed operand.</value>
        public override string ProcessedOperand { get { return Method + "/" + Method.DeclaringType; } }

        Int32 m_token;
        MethodBase m_method;
    }
    public class InlineTypeInstruction : ILInstruction
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="T:InlineTypeInstruction"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        /// <param name="offset">The offset.</param>
        /// <param name="opCode">The op code.</param>
        /// <param name="token">The token.</param>
        public InlineTypeInstruction(MethodBase enclosingMethod, Int32 offset, OpCode opCode, Int32 token)
            : base(enclosingMethod, offset, opCode)
        {
            this.m_token = token;
        }

        /// <summary>
        /// Gets the type.
        /// </summary>
        /// <value>The type.</value>
        public Type Type
        {
            get
            {
                throw new NotImplementedException("This functionality is not yet implemented");
                //if (m_type == null) m_type = m_resolver.AsType(m_token);
                return m_type;
            }
        }
        /// <summary>
        /// Gets the token.
        /// </summary>
        /// <value>The token.</value>
        public Int32 Token { get { return m_token; } }
        /// <summary>
        /// Gets the processed operand.
        /// </summary>
        /// <value>The processed operand.</value>
        public override string ProcessedOperand { get { return Type.ToString(); } }

        Int32 m_token;
        Type m_type;
    }
    public class InlineSigInstruction : ILInstruction
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="T:InlineSigInstruction"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        /// <param name="offset">The offset.</param>
        /// <param name="opCode">The op code.</param>
        /// <param name="token">The token.</param>
        public InlineSigInstruction(MethodBase enclosingMethod, Int32 offset, OpCode opCode, Int32 token)
            : base(enclosingMethod, offset, opCode)
        {
            this.m_token = token;
        }

        /// <summary>
        /// Gets the signature.
        /// </summary>
        /// <value>The signature.</value>
        public byte[] Signature
        {
            get
            {
                throw new NotImplementedException("This functionality is not yet implemented");
                // if (m_signature == null) m_signature = m_resolver.AsSignature(m_token);
                return m_signature;
            }
        }
        /// <summary>
        /// Gets the token.
        /// </summary>
        /// <value>The token.</value>
        public Int32 Token { get { return m_token; } }

        /// <summary>
        /// Gets the processed operand.
        /// </summary>
        /// <value>The processed operand.</value>
        public override string ProcessedOperand { get { return Signature.ToString() ; } }

        Int32 m_token;
        byte[] m_signature;
    }
    public class InlineTokInstruction : ILInstruction
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="T:InlineTokInstruction"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        /// <param name="offset">The offset.</param>
        /// <param name="opCode">The op code.</param>
        /// <param name="token">The token.</param>
        public InlineTokInstruction(MethodBase enclosingMethod, Int32 offset, OpCode opCode, Int32 token)
            : base(enclosingMethod, offset, opCode)
        {
            this.m_token = token;
        }

        /// <summary>
        /// Gets the member.
        /// </summary>
        /// <value>The member.</value>
        public MemberInfo Member
        {
            get
            {
                throw new NotImplementedException("This functionality is not yet implemented");
                //if (m_member == null) { m_member = m_resolver.AsMember(Token); }
                return m_member;
            }
        }
        /// <summary>
        /// Gets the token.
        /// </summary>
        /// <value>The token.</value>
        public Int32 Token { get { return m_token; } }
        /// <summary>
        /// Gets the processed operand.
        /// </summary>
        /// <value>The processed operand.</value>
        public override string ProcessedOperand { get { return Member + "/" + Member.DeclaringType; } }

        Int32 m_token;
        MemberInfo m_member;
    }

    public class InlineStringInstruction : ILInstruction
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="T:InlineStringInstruction"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        /// <param name="offset">The offset.</param>
        /// <param name="opCode">The op code.</param>
        /// <param name="token">The token.</param>
        public InlineStringInstruction(MethodBase enclosingMethod, Int32 offset, OpCode opCode, Int32 token)
            : base(enclosingMethod, offset, opCode)
        {
            this.m_token = token;
        }

        /// <summary>
        /// Gets the string.
        /// </summary>
        /// <value>The string.</value>
        public String String
        {
            get
            {
                throw new NotImplementedException("This functionality is not yet implemented");
                // if (m_string == null) m_string = m_resolver.AsString(Token);
                return m_string;
            }
        }
        /// <summary>
        /// Gets the token.
        /// </summary>
        /// <value>The token.</value>
        public Int32 Token { get { return m_token; } }
        /// <summary>
        /// Gets the processed operand.
        /// </summary>
        /// <value>The processed operand.</value>
        public override string ProcessedOperand { get { return String; } }

        Int32 m_token;
        String m_string;
    }

    public class InlineVarInstruction : ILInstruction
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="T:InlineVarInstruction"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        /// <param name="offset">The offset.</param>
        /// <param name="opCode">The op code.</param>
        /// <param name="ordinal">The ordinal.</param>
        public InlineVarInstruction(MethodBase enclosingMethod, Int32 offset, OpCode opCode, UInt16 ordinal)
            : base(enclosingMethod, offset, opCode)
        {
            this.m_ordinal = ordinal;
        }

        /// <summary>
        /// Gets the ordinal.
        /// </summary>
        /// <value>The ordinal.</value>
        public UInt16 Ordinal { get { return m_ordinal; } }
        /// <summary>
        /// Gets the processed operand.
        /// </summary>
        /// <value>The processed operand.</value>
        public override string ProcessedOperand { get { return m_ordinal.ToString(); } }
    
        UInt16 m_ordinal;
    }

    public class ShortInlineVarInstruction : ILInstruction
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="T:ShortInlineVarInstruction"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        /// <param name="offset">The offset.</param>
        /// <param name="opCode">The op code.</param>
        /// <param name="ordinal">The ordinal.</param>
        public ShortInlineVarInstruction(MethodBase enclosingMethod, Int32 offset, OpCode opCode, Byte ordinal)
            : base(enclosingMethod, offset, opCode)
        {
            this.m_ordinal = ordinal;
        }

        /// <summary>
        /// Gets the ordinal.
        /// </summary>
        /// <value>The ordinal.</value>
        public Byte Ordinal { get { return m_ordinal; } }
        /// <summary>
        /// Gets the processed operand.
        /// </summary>
        /// <value>The processed operand.</value>
        public override string ProcessedOperand { get { return m_ordinal.ToString(); } }
     
        Byte m_ordinal;
    }

}
