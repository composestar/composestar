using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.ContextInfo.RuBCoDe
{
    /// <summary>
    /// This assembly attribute defines the conflict expressions that should be checked against the 
    /// resource operations. They are bound to the assembly by the weaver.
    /// </summary>
    [AttributeUsage(AttributeTargets.Assembly, Inherited = false, AllowMultiple = true)]
    public sealed class ConflictRuleAttribute: Attribute
    {
        private string _expression;

        private string _resource;

        private bool _constraint;

        /// <summary>
        /// The conflict regular expression
        /// </summary>
        public string Expression
        {
            get { return _expression; }
            set { _expression = value; }
        }

        /// <summary>
        /// The resource this expression applies to
        /// </summary>
        public string Resource
        {
            get { return _resource; }
            set { _resource = value; }
        }

        /// <summary>
        /// If true it's a constraint, otherwise it's an assertion
        /// </summary>
        public bool Constraint
        {
            get { return _constraint; }
            set { _constraint = value; }
        }

        /// <summary>
        /// Create a new conflict expression
        /// </summary>
        /// <param name="resc"></param>
        /// <param name="expr"></param>
        public ConflictRuleAttribute(String resc, String expr)
        {
            _resource = resc;
            _expression = expr;
            _constraint = true;
        }

        /// <summary>
        /// Create a new conflict expression
        /// </summary>
        /// <param name="resc"></param>
        /// <param name="expr"></param>
        /// <param name="cont"></param>
        public ConflictRuleAttribute(String resc, String expr, bool cont)
        {
            _resource = resc;
            _expression = expr;
            _constraint = cont;
        }
    }
}
