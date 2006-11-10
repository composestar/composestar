#region Using directives
using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions.Visitor;
#endregion

namespace Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions
{
    /// <summary>
    /// An AND operator operating on two parts.
    /// </summary>
    [Serializable]
    [XmlRoot("And", Namespace = "Entities.TYM.DotNET.Composestar")]
    public class AndCondition : ConditionExpression, IVisitable
    {

        /// <summary>
        /// Initializes a new instance of the <see cref="T:And"/> class.
        /// </summary>
        public AndCondition()
        {

        } // And()

        private int _branchId;

        /// <summary>
        /// Gets or sets the branch id.
        /// </summary>
        /// <value>The branch id.</value>
        [XmlAttribute]
        public int BranchId
        {
            get { return _branchId; } // get
            set { _branchId = value; } // set
        } // BranchId


        /// <summary>
        /// Gets or sets the left.
        /// </summary>
        /// <value>The left.</value>
        public ConditionExpression Left
        {
            get
            {
                return _left;
            } // get
            set
            {
                _left = value;
            } // set
        } // Left

        private ConditionExpression _left;

        /// <summary>
        /// Gets or sets the right.
        /// </summary>
        /// <value>The right.</value>
        public ConditionExpression Right
        {
            get
            {
                return _right;
            } // get
            set
            {
                _right = value;
            } // set
        } // Right

        private ConditionExpression _right;

        /// <summary>
        /// Accepts the specified visitor.
        /// </summary>
        /// <param name="visitor">The visitor.</param>
        public void Accept(IVisitor visitor)
        {
            if (visitor == null)
                throw new ArgumentNullException("visitor"); 

            ((IVisitable)_left).Accept(visitor);
            visitor.VisitAndLeft(this);
            ((IVisitable)_right).Accept(visitor);
            visitor.VisitAndRight(this);
        } // Accept(visitor)

    } // class And
}
