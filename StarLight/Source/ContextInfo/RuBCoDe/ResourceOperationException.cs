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
    /// Exception thrown when there's a conflict in the resource operations.
    /// </summary>
    public class ResourceOperationException: Exception
    {
        private ResourceType _resource;
        private string _operations;

        private string _rule;
        private bool _ruleIsConstraint;

        private BookKeeper _bookkeeper;

        /// <summary>
        /// The resource on which the operations created a conflict
        /// </summary>
        public ResourceType Resource
        {
            get { return _resource; }
        }

        /// <summary>
        /// The conflicting operation sequence
        /// </summary>
        public string Operations
        {
            get { return _operations; }
        }

        /// <summary>
        /// The rule that was violated
        /// </summary>
        public string Rule
        {
            get { return _rule; }
        }

        /// <summary>
        /// True if the rule was a conflict rule rather than an assertion
        /// </summary>
        public bool RuleIsContraint
        {
            get { return _ruleIsConstraint; }
        }

        /// <summary>
        /// Get the bookkeeper that contained the resource operations. Can be null.
        /// </summary>
        public BookKeeper BookKeeper
        {
            get { return _bookkeeper; }
        }

        /// <summary>
        /// Creates a new Resource Operation exception
        /// </summary>
        /// <param name="resource"></param>
        /// <param name="operations"></param>
        /// <param name="rule"></param>
        /// <param name="isConstraint"></param>
        /// <param name="bk"></param>
        public ResourceOperationException(ResourceType resource, string operations, string rule, bool isConstraint, BookKeeper bk)
            : base(String.Format("The operation sequence \"{1}\" on the resource \"{0}\" violates the rule \"{2}\" (constraint: {3})",
                Enum.GetName(typeof(ResourceType), resource), operations, rule, isConstraint.ToString()))
        {
            _resource = resource;
            _operations = operations;
            _rule = rule;
            _ruleIsConstraint = isConstraint;
            _bookkeeper = bk;
        }
    }
}
