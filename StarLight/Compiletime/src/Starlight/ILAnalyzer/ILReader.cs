using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.Reflection.Emit;
using Composestar.StarLight.ILAnalyzer.ILInstructions;

namespace Composestar.StarLight.ILAnalyzer
{
    /// <summary>
    /// An IL Reader using the GetMethodBody function in the .NET reflection API.
    /// </summary>
    public class ILReader : IEnumerable<ILInstruction>
    {
        Byte[] m_byteArray;
        Int32 m_position;
        MethodBase m_enclosingMethod;

        static OpCode[] s_OneByteOpCodes = new OpCode[0x100];
        static OpCode[] s_TwoByteOpCodes = new OpCode[0x100];

        /// <summary>
        /// Initializes the <see cref="T:ILReader"/> class.
        /// </summary>
        static ILReader()
        {
            foreach (FieldInfo fi in typeof(OpCodes).GetFields(BindingFlags.Public | BindingFlags.Static))
            {
                OpCode opCode = (OpCode)fi.GetValue(null);
                UInt16 value = (UInt16)opCode.Value;
                if (value < 0x100)
                    s_OneByteOpCodes[value] = opCode;
                else if ((value & 0xff00) == 0xfe00)
                    s_TwoByteOpCodes[value & 0xff] = opCode;
            }
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ILReader"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        public ILReader(MethodBase enclosingMethod)
        {
            this.m_enclosingMethod = enclosingMethod;
            MethodBody methodBody = m_enclosingMethod.GetMethodBody();
            this.m_byteArray = (methodBody == null) ? new Byte[0] : methodBody.GetILAsByteArray();
            this.m_position = 0;
        }

        /// <summary>
        /// Returns an enumerator that iterates through the collection.
        /// </summary>
        /// <returns>
        /// A <see cref="T:System.Collections.Generic.IEnumerator`1"></see> that can be used to iterate through the collection.
        /// </returns>
        public IEnumerator<ILInstruction> GetEnumerator()
        {
            while (m_position < m_byteArray.Length)
                yield return Next();

            m_position = 0;
            yield break;
        }

        /// <summary>
        /// Returns an enumerator that iterates through a collection.
        /// </summary>
        /// <returns>
        /// An <see cref="T:System.Collections.IEnumerator"></see> object that can be used to iterate through the collection.
        /// </returns>
        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator() { return this.GetEnumerator(); }

        /// <summary>
        /// Return the next ILInstruction based on the contents of the byte buffer.
        /// </summary>
        /// <returns></returns>
        ILInstruction Next()
        {
            Int32 offset = m_position;
            OpCode opCode = OpCodes.Nop;
            Int32 token = 0;

            // read first 1 or 2 bytes as opCode
            Byte code = ReadByte();
            if (code != 0xFE)
                opCode = s_OneByteOpCodes[code];
            else
            {
                code = ReadByte();
                opCode = s_TwoByteOpCodes[code];
            }

            switch (opCode.OperandType)
            {
                case OperandType.InlineNone:
                    return new InlineNoneInstruction(m_enclosingMethod, offset, opCode);
                case OperandType.ShortInlineBrTarget:
                    SByte shortDelta = ReadSByte();
                    return new ShortInlineBrTargetInstruction(m_enclosingMethod, offset, opCode, shortDelta);
                case OperandType.InlineBrTarget:
                    Int32 delta = ReadInt32();
                    return new InlineInstruction(m_enclosingMethod, offset, opCode, delta);
                case OperandType.ShortInlineI:
                    Byte int8 = ReadByte();
                    return new InlineInstruction(m_enclosingMethod, offset, opCode, int8);
                case OperandType.InlineI:
                    Int32 int32 = ReadInt32();
                    return new InlineInstruction(m_enclosingMethod, offset, opCode, int32);
                case OperandType.InlineI8:
                    Int64 int64 = ReadInt64();
                    return new InlineInstruction(m_enclosingMethod, offset, opCode, int64);
                case OperandType.ShortInlineR:
                    Single float32 = ReadSingle();
                    return new InlineInstruction(m_enclosingMethod, offset, opCode, float32);
                case OperandType.InlineR:
                    Double float64 = ReadDouble();
                    return new InlineInstruction(m_enclosingMethod, offset, opCode, float64);
                case OperandType.ShortInlineVar:
                    Byte index8 = ReadByte();
                    return new InlineInstruction(m_enclosingMethod, offset, opCode, index8);
                case OperandType.InlineVar:
                    UInt16 index16 = ReadUInt16();
                    return new InlineInstruction(m_enclosingMethod, offset, opCode, index16);
                case OperandType.InlineString:
                    token = ReadInt32();
                    return new InlineInstruction(m_enclosingMethod, offset, opCode, token);
                case OperandType.InlineSig:
                    token = ReadInt32();
                    return new InlineInstruction(m_enclosingMethod, offset, opCode, token);
                case OperandType.InlineField:
                    token = ReadInt32();
                    return new InlineInstruction(m_enclosingMethod, offset, opCode, token);
                case OperandType.InlineType:
                    token = ReadInt32();
                    return new InlineInstruction(m_enclosingMethod, offset, opCode, token);
                case OperandType.InlineTok:
                    token = ReadInt32();
                    return new InlineInstruction(m_enclosingMethod, offset, opCode, token);
                case OperandType.InlineMethod:
                    token = ReadInt32();
                    return new InlineMethodInstruction(m_enclosingMethod, offset, opCode, token);
                case OperandType.InlineSwitch:
                    Int32 cases = ReadInt32();
                    Int32[] deltas = new Int32[cases];
                    for (Int32 i = 0; i < cases; i++) deltas[i] = ReadInt32();
                    return new InlineSwitchInstruction(m_enclosingMethod, offset, opCode, deltas);

                default:
                    throw new BadImageFormatException("unexpected OperandType " + opCode.OperandType);
            }
        }

        Byte ReadByte() { return (Byte)m_byteArray[m_position++]; }
        SByte ReadSByte() { return (SByte)ReadByte(); }

        UInt16 ReadUInt16() { m_position += 2; return BitConverter.ToUInt16(m_byteArray, m_position - 2); }
        UInt32 ReadUInt32() { m_position += 4; return BitConverter.ToUInt32(m_byteArray, m_position - 4); }
        UInt64 ReadUInt64() { m_position += 8; return BitConverter.ToUInt64(m_byteArray, m_position - 8); }

        Int32 ReadInt32() { m_position += 4; return BitConverter.ToInt32(m_byteArray, m_position - 4); }
        Int64 ReadInt64() { m_position += 8; return BitConverter.ToInt64(m_byteArray, m_position - 8); }

        Single ReadSingle() { m_position += 4; return BitConverter.ToSingle(m_byteArray, m_position - 4); }
        Double ReadDouble() { m_position += 8; return BitConverter.ToDouble(m_byteArray, m_position - 8); }

    }
}
