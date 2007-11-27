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
 * $Id: ResourceValidator.cs 3939 2007-11-20 13:23:29Z elmuerte $
 */
#endregion

using System;
using System.Collections.Generic;
using System.Text;
using System.Text.RegularExpressions;
using System.Diagnostics;
using Composestar.StarLight.ContextInfo.RuBCoDe.Pattern;

namespace Composestar.StarLight.ContextInfo.RuBCoDe
{
    /// <summary>
    /// Defines a conflict rule. It's constructed from ConflictRule attributes.
    /// </summary>
    [DebuggerNonUserCode()]
    public sealed class ConflictRule
    {
        private RegularPattern _pattern;
        private string _resource;
        private bool _constraint;
        private string _message;

        /// <summary>
        /// Create a new conflict rule
        /// </summary>
        /// <param name="pattern"></param>
        /// <param name="resource"></param>
        /// <param name="isconstraint"></param>
        /// <param name="message"></param>
        public ConflictRule(string pattern, string resource, bool isconstraint, string message)
        {
            _pattern = RegularPattern.compile(pattern); //new Regex("^" + pattern + "$");
            _resource = resource;
            _constraint = isconstraint;
            _message = message;
        }

        /// <summary>
        /// The expresion of this rule
        /// </summary>
        public string Pattern
        {
            get { return _pattern.ToString(); }
        }

        /// <summary>
        /// The resource this rule applies to
        /// </summary>
        public string Resource
        {
            get { return _resource; }
        }

        /// <summary>
        /// Optional message
        /// </summary>
        public string Message
        {
            get { return _message; }
        }

        /// <summary>
        /// True if this is a constraint, false if it's an assertion
        /// </summary>
        public bool Constraint
        {
            get { return _constraint; }
        }

        /// <summary>
        /// Returns true when the operation sequences violates this rule.
        /// </summary>
        /// <returns></returns>
        public bool violatesRule(IList<string> sequence)
        {
            return SimpleMatcher.matches(_pattern, sequence) == _constraint;
        }

        /// <summary>
        /// A string representation of this rule.
        /// </summary>
        /// <returns></returns>
        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();
            if (_constraint)
            {
                sb.Append("constraint ");
            }
            else
            {
                sb.Append("assertion ");
            }
            sb.Append("\"");
            sb.Append(_pattern);
            sb.Append("\"");
            sb.Append(" on ");
            sb.Append(_resource);
            if (!String.IsNullOrEmpty(_message))
            {
                sb.Append(": ");
                sb.Append(_message);
            }
            return sb.ToString();
        }
    }
}
