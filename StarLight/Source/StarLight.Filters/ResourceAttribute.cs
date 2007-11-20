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
 * $Id: RegexPattern.java 3917 2007-11-06 15:45:31Z elmuerte $
 */
#endregion

using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.Filters.FilterTypes
{
    /// <summary>
    /// This attributes provides one of the means to declare a custom resource type used
    /// in the resource operation conflict detection. It is not required to declare
    /// custom resources, but it is strongly adviced. Declaring the resources provides
    /// the means to perform checking on the correctness of the used resource operations and
    /// rules (read: check for typos).
    /// </summary>
    [AttributeUsage(AttributeTargets.Assembly, Inherited = false, AllowMultiple = true)]
    public class ResourceAttribute : Attribute
    {
        private string _name;
        private string _vocab;

        /// <summary>
        /// Define a resource and its vocabulary.
        /// </summary>
        /// <param name="name">The name of the resource. The following names are
        /// protected and can not be used: msg, message, target, selector, arg, return.
        /// A resource name is an identifier and case insensitive.</param>
        /// <param name="vocab">A comma separated list of possible operations this
        /// resource accepts.</param>
        public ResourceAttribute(string name, string vocab)
        {
            _name = name;
            _vocab = vocab;
        }

        /// <summary>
        /// The resource name
        /// </summary>
        public string Name
        {
            get { return _name; }
        }

        /// <summary>
        /// Comma separated list of operations.
        /// </summary>
        public string Vocabulary
        {
            get { return _vocab; }
        }
    }
}
