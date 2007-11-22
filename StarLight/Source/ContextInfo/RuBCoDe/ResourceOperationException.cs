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
        private string _resource;
        private string _operations;

        private ConflictRule _rule;

        private BookKeeper _bookkeeper;

        /// <summary>
        /// The resource on which the operations created a conflict
        /// </summary>
        public string Resource
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
        public ConflictRule Rule
        {
            get { return _rule; }
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
        /// <param name="bk"></param>
        public ResourceOperationException(string resource, string operations, ConflictRule rule, BookKeeper bk)
            : base(String.Format("The operation sequence {1} on the resource \"{0}\" violates the {2}",
                resource, operations, rule.ToString()))
        {
            _resource = resource;
            _operations = operations;
            _rule = rule;
            _bookkeeper = bk;
        }
    }
}
