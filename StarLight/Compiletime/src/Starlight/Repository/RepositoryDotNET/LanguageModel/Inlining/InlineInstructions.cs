using System;
using System.Collections.Generic;
using System.Text;
using Composestar.Repository.LanguageModel.ConditionExpressions;

namespace Composestar.Repository.LanguageModel.InlineInstructions
{
    /// <summary>
    /// 
    /// </summary>
    public abstract class Instruction
    {

    }

    /// <summary>
    /// 
    /// </summary>
    public class Block : Instruction
    {
        private List<Instruction> _instructions;

        /// <summary>
        /// Gets or sets the instructions.
        /// </summary>
        /// <value>The instructions.</value>
        public List<Instruction> Instructions
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
        /// Initializes a new instance of the <see cref="T:Block"/> class.
        /// </summary>
        public Block()
        {
            _instructions = new List<Instruction>();
        }

        /// <summary>
        /// Adds the instruction.
        /// </summary>
        /// <param name="instruction">The instruction.</param>
        public void AddInstruction(Instruction instruction)
        {
            if (instruction == null) throw new ArgumentNullException("instruction");
            _instructions.Add(instruction);
        }

    }

    /// <summary>
    /// 
    /// </summary>
    public class Branch : Instruction
    {
        private ConditionExpression _conditionExpression;

        private Block _trueBlock;
        private Block _falseBlock;

        /// <summary>
        /// Gets or sets the condition expression.
        /// </summary>
        /// <value>The condition expression.</value>
        public ConditionExpression ConditionExpression
        {
            get
            {
                return _conditionExpression;
            }
            set
            {
                _conditionExpression = value;
            }
        }

        /// <summary>
        /// Gets or sets the true block.
        /// </summary>
        /// <value>The true block.</value>
        public Block TrueBlock
        {
            get
            {
                return _trueBlock;
            }
            set
            {
                _trueBlock = value;
            }
        }

        /// <summary>
        /// Gets or sets the false block.
        /// </summary>
        /// <value>The false block.</value>
        public Block FalseBlock
        {
            get
            {
                return _falseBlock;
            }
            set
            {
                _falseBlock = value;
            }
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:Branch"/> class.
        /// </summary>
        /// <param name="conditionExpression">The condition expression.</param>
        public Branch(ConditionExpression conditionExpression)
        {
            _conditionExpression = conditionExpression;
        }

    }

    /// <summary>
    /// 
    /// </summary>
    public class Jump : Instruction
    {
        private Int32 _target;

        /// <summary>
        /// Gets or sets the target.
        /// </summary>
        /// <value>The target.</value>
        public Int32 Target
        {
            get
            {
                return _target;
            }
            set
            {
                _target = value;
            }
        }

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
        public Jump(Int32 target)
        {
            _target = target;
        }

    }

    /// <summary>
    /// 
    /// </summary>
    public class FilterBlock : Block
    {
        private Label _label;
        private String _filterblockType;

        /// <summary>
        /// Gets or sets the type of the filterblock.
        /// </summary>
        /// <value>The type of the filterblock.</value>
        public String FilterblockType
        {
            get
            {
                return _filterblockType;
            }
            set
            {
                _filterblockType = value;
            }
        }

        /// <summary>
        /// Gets or sets the label.
        /// </summary>
        /// <value>The label.</value>
        public Label Label
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

        public FilterBlock(Label label, String type)
        {
            _label = label;
            _filterblockType = type;
        }

    }

    /// <summary>
    /// 
    /// </summary>
    public class Label : Instruction
    {

        private Int32 _value;

        /// <summary>
        /// Gets or sets the value.
        /// </summary>
        /// <value>The value.</value>
        public Int32 Value
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
        /// Initializes a new instance of the <see cref="T:Label"/> class.
        /// </summary>
        /// <param name="value">The value.</param>
        public Label(Int32 value)
        {
            _value = value;
        }

    }


}
