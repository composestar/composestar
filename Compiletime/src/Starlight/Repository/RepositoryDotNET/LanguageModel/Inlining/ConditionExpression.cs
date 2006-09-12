using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.Repository.LanguageModel.ConditionExpressions
{
    /// <summary>
    /// 
    /// </summary>
    public abstract class ConditionExpression
    {

        /// <summary>
        /// Gets a value indicating whether this instance is binary.
        /// </summary>
        /// <value><c>true</c> if this instance is binary; otherwise, <c>false</c>.</value>
        public virtual bool IsBinary { get { return false; } }

        /// <summary>
        /// Gets a value indicating whether this instance is literal.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance is literal; otherwise, <c>false</c>.
        /// </value>
        public virtual bool IsLiteral { get { return false; } }

        /// <summary>
        /// Gets a value indicating whether this instance is unary.
        /// </summary>
        /// <value><c>true</c> if this instance is unary; otherwise, <c>false</c>.</value>
        public virtual bool IsUnary { get { return false; } }

    }

    /// <summary>
    /// An AND condition expression.
    /// </summary>
    public class AndExpression : ConditionExpression
    {
        private ConditionExpression _left;
        private ConditionExpression _right;

        /// <summary>
        /// Gets or sets the right.
        /// </summary>
        /// <value>The right.</value>
        public ConditionExpression Right
        {
            get
            {
                return _right;
            }
            set
            {
                _right = value;
            }
        }

        /// <summary>
        /// Gets or sets the left.
        /// </summary>
        /// <value>The left.</value>
        public ConditionExpression Left
        {
            get
            {
                return _left;
            }
            set
            {
                _left = value;
            }
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:AndExpression"/> class.
        /// </summary>
        public AndExpression()
        {

        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:AndExpression"/> class.
        /// </summary>
        /// <param name="left">The left.</param>
        /// <param name="right">The right.</param>
        public AndExpression(ConditionExpression left, ConditionExpression right)
        {
            _left = left;
            _right = right;
        }

        /// <summary>
        /// Gets a value indicating whether this instance is binary.
        /// </summary>
        /// <value><c>true</c> if this instance is binary; otherwise, <c>false</c>.</value>
        public override bool IsBinary
        {
            get
            {
                return true;
            }
        }
    }

    /// <summary>
    /// A NOT condition expression.
    /// </summary>
    public class NotExpression : ConditionExpression
    {
        private ConditionExpression _operand;


        /// <summary>
        /// Gets or sets the operand.
        /// </summary>
        /// <value>The operand.</value>
        public ConditionExpression Operand
        {
            get
            {
                return _operand;
            }
            set
            {
                _operand = value;
            }
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:NotExpression"/> class.
        /// </summary>
        public NotExpression()
        {

        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:NotExpression"/> class.
        /// </summary>
        /// <param name="operand">The operand.</param>
        public NotExpression(ConditionExpression operand)
        {
            _operand = operand;
        }

        /// <summary>
        /// Gets a value indicating whether this instance is binary.
        /// </summary>
        /// <value><c>true</c> if this instance is binary; otherwise, <c>false</c>.</value>
        public override bool IsBinary
        {
            get
            {
                return false;
            }
        }

        /// <summary>
        /// Gets a value indicating whether this instance is unary.
        /// </summary>
        /// <value><c>true</c> if this instance is unary; otherwise, <c>false</c>.</value>
        public override bool IsUnary
        {
            get
            {
                return true;
            }
        }
    }

    /// <summary>
    /// An OR condition expression.
    /// </summary>
    public class OrExpression : ConditionExpression
    {
        private ConditionExpression _left;
        private ConditionExpression _right;

        /// <summary>
        /// Gets or sets the right.
        /// </summary>
        /// <value>The right.</value>
        public ConditionExpression Right
        {
            get
            {
                return _right;
            }
            set
            {
                _right = value;
            }
        }

        /// <summary>
        /// Gets or sets the left.
        /// </summary>
        /// <value>The left.</value>
        public ConditionExpression Left
        {
            get
            {
                return _left;
            }
            set
            {
                _left = value;
            }
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:OrExpression"/> class.
        /// </summary>
        public OrExpression()
        {

        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:OrExpression"/> class.
        /// </summary>
        /// <param name="left">The left.</param>
        /// <param name="right">The right.</param>
        public OrExpression(ConditionExpression left, ConditionExpression right)
        {
            _left = left;
            _right = right;
        }

        /// <summary>
        /// Gets a value indicating whether this instance is binary.
        /// </summary>
        /// <value><c>true</c> if this instance is binary; otherwise, <c>false</c>.</value>
        public override bool IsBinary
        {
            get
            {
                return true;
            }
        }
    }

    /// <summary>
    /// A true expression.
    /// </summary>
    public class TrueExpression : ConditionExpression
    {

        /// <summary>
        /// Initializes a new instance of the <see cref="T:TrueExpression"/> class.
        /// </summary>
        public TrueExpression()
        {

        }

    }

    /// <summary>
    /// A false expression.
    /// </summary>
    public class FalseExpression : ConditionExpression
    {

        /// <summary>
        /// Initializes a new instance of the <see cref="T:FalseExpression"/> class.
        /// </summary>
        public FalseExpression()
        {

        }

    }

    /// <summary>
    /// A literal
    /// </summary>
    public class ConditionLiteral : ConditionExpression
    {

        private string _name;

        /// <summary>
        /// Gets or sets the name.
        /// </summary>
        /// <value>The name.</value>
        public string Name
        {
            get
            {
                return _name;
            }
            set
            {
                _name = value;
            }
        }

        /// <summary>
        /// Gets a value indicating whether this instance is literal.
        /// </summary>
        /// <value>
        /// 	<c>true</c> if this instance is literal; otherwise, <c>false</c>.
        /// </value>
        public override bool IsLiteral
        {
            get
            {
                return true;
            }
        }
    }
}
