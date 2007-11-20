#region License
/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */
#endregion

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
        private string _pattern;

        private string _resource;

        private bool _constraint;

        private string _message;

        /// <summary>
        /// The conflict regular expression
        /// </summary>
        public string Pattern
        {
            get { return _pattern; }
            set { _pattern = value; }
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
        /// A "friendly" message explaining the conflict rule.
        /// </summary>
        public string Message
        {
            get { return _message; }
            set { _message = value; }
        }

        /// <summary>
        /// Create a new conflict rule (a constraint)
        /// </summary>
        /// <param name="resc">The resource this rule applies to</param>
        /// <param name="expr">The pattern to match</param>
        public ConflictRuleAttribute(String resc, String expr)
        {
            _resource = resc;
            _pattern = expr;
            _constraint = true;
        }

        /// <summary>
        /// Create a new conflict rule
        /// </summary>
        /// <param name="resc">The resource this rule applies to</param>
        /// <param name="expr">The pattern to match</param>
        /// <param name="cont">If true it's a constraint, otherwise an assertion</param>
        public ConflictRuleAttribute(String resc, String expr, bool cont)
        {
            _resource = resc;
            _pattern = expr;
            _constraint = cont;
        }

        /// <summary>
        /// Create a new conflict rule
        /// </summary>
        /// <param name="resc">The resource this rule applies to</param>
        /// <param name="expr">The pattern to match</param>
        /// <param name="cont">If true it's a constraint, otherwise an assertion</param>
        /// <param name="msg">A friendly message</param>
        public ConflictRuleAttribute(String resc, String expr, bool cont, string msg)
        {
            _resource = resc;
            _pattern = expr;
            _constraint = cont;
            _message = msg;
        }
    }
}
