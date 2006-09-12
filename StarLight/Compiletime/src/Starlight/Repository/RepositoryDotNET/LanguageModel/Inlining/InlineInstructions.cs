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

      /// <summary>
    /// A filter action.
    /// </summary>
    public class FilterAction : Instruction
    {
        private String _filterType;
        private String _target;
        private String _selector;

        /// <summary>
        /// Gets or sets the selector.
        /// </summary>
        /// <value>The selector.</value>
        public String Selector
        {
            get
            {
                return _selector;
            }
            set
            {
                _selector = value;
            }
        }

        /// <summary>
        /// Gets or sets the target.
        /// </summary>
        /// <value>The target.</value>
        public String Target
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
        /// Gets or sets the type of the filter.
        /// </summary>
        /// <value>The type of the filter.</value>
        public String FilterType
        {
            get
            {
                return _filterType;
            }
            set
            {
                _filterType = value;
            }
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:FilterAction"/> class.
        /// </summary>
        /// <param name="filterType">Type of the filter.</param>
        /// <param name="target">The target.</param>
        /// <param name="selector">The selector.</param>
        public FilterAction(String filterType, string target, string selector)
        {
            _filterType = filterType;
            _target = target;
            _selector = selector;
        }

    }

    /// <summary>
    /// A context action.
    /// </summary>
    public class ContextInstruction : Instruction
    {

        private String _contextType;
        private MethodInfo _method;
        private bool _isEnabled;

        /// <summary>
        /// Gets or sets a value indicating whether this instance is enabled.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance is enabled; otherwise, <c>false</c>.
        /// </value>
        public bool IsEnabled
        {
            get
            {
                return _isEnabled;
            }
            set
            {
                _isEnabled = value;
            }
        }

        /// <summary>
        /// Gets or sets the method.
        /// </summary>
        /// <value>The method.</value>
        public MethodInfo Method
        {
            get
            {
                return _method;
            }
            set
            {
                _method = value;
            }
        }

        /// <summary>
        /// Gets or sets the type of the context.
        /// </summary>
        /// <value>The type of the context.</value>
        public String ContextType
        {
            get
            {
                return _contextType;
            }
            set
            {
                _contextType = value;
            }
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ContextAction"/> class.
        /// </summary>
        /// <param name="_contextType">Type of the _context.</param>
        /// <param name="method">The method.</param>
        public ContextInstruction(String contextType, MethodInfo method)
        {
            this._contextType = contextType;
            this._method = method;
        }

    }

}
