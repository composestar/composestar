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

namespace Composestar.StarLight.Filters.FilterTypes
{
    /// <summary>
    /// Annotation for FilterActions to define their resource operation sequence. It is
    /// required for static resource operation conflict analysis.
    /// </summary>
    [AttributeUsage(AttributeTargets.Class, Inherited = false, AllowMultiple = false)]
    public class ResourceOperationAttribute: Attribute
    {
        private string _sequence;
        private bool _manualbk;
        private bool _autorw;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="sequence"><see cref="Sequence"/></param>
        public ResourceOperationAttribute(string sequence)
        {
            _sequence = sequence;
            _manualbk = false;
            _autorw = false;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="sequence"></param>
        /// <param name="manual"></param>
        public ResourceOperationAttribute(string sequence, bool manual)
        {
            _sequence = sequence;
            _manualbk = manual;
            _autorw = manual;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="sequence"></param>
        /// <param name="manual"></param>
        /// <param name="autorw"></param>
        public ResourceOperationAttribute(string sequence, bool manual, bool autorw)
        {
            _sequence = sequence;
            _manualbk = manual;
            _autorw = autorw;
        }

        /// <summary>
        /// The resource operation sequence this filter action produces. This is a sequence of
        /// resource.operation fairs separated by semicolons. For example: <code>return.read;return.encrypt;return.write</code>.
        /// This sequence is always used for static conflict detection at compile time. At runtime
        /// it will only be used when there is no manual book keeping code (<code>ManualBookKeeping=false</code>).
        /// </summary>
        public string Sequence
        {
            get { return _sequence; }
        }

        /// <summary>
        /// Defines if this filter actions contains explicit resource operation book keeping in the
        /// implementation. When this is false the runtime will simply use the sequence. Manual book
        /// keeping allows more specific tracking of resource operations.
        /// </summary>
        public bool ManualBookKeeping
        {
            get { return _manualbk; }
        }

        /// <summary>
        /// If true automatically perform tracking of read and write operations (on resources where it
        /// is possible). This only has effect when manual book keeping is used. It's adviced to leave
        /// auto read write bookkeeping enabled.
        /// </summary>
        public bool AutoReadWriteBK
        {
            get { return _autorw; }
        }
    }
}
