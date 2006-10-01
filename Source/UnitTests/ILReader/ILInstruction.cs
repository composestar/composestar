using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.Reflection.Emit;

namespace Testing.CecilILReader
{
    /// <summary>
    /// A single IL instruction.
    /// </summary>
    public class ILInstruction
    {

        private Int32 _offset;
        private string _opCode ;
   
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
        public string OpCode
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

     

        public ILInstruction()
        {

        }

        private string _operand;

        public string Operand
        {
            get { return _operand; }
            set { _operand = value; }
        }
	

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ILInstruction"/> class.
        /// </summary>
        /// <param name="enclosingMethod">The enclosing method.</param>
        /// <param name="offset">The offset.</param>
        /// <param name="opCode">The operation code.</param>
        public ILInstruction(Int32 offset, string opCode, string operand)
        {
            _offset = offset;
            _opCode = opCode;
            _operand = operand;
        }

        public override string ToString()
        {
            return String.Format("{0} {1}", _opCode, _operand);
        }

    }
    

}
