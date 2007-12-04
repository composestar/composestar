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

namespace Composestar.StarLight.ContextInfo.RuBCoDe.Pattern
{
    /// <summary>
    /// Base class for patterns
    /// </summary>
    public abstract class Pattern
    {
        /// <summary>
        /// The string representation of this pattern (the input string)
        /// </summary>
        protected string patternString;

        /// <summary>
        /// Get the end state of the pattern
        /// </summary>
        /// <returns></returns>
        public abstract RegularState getEndState();

        /// <summary>
        /// Get the starting state of the pattern's automaton
        /// </summary>
        /// <returns></returns>
        public abstract RegularState getStartState();

        /// <summary>
        /// Create a pattern
        /// </summary>
        /// <param name="pattern"></param>
        public Pattern(string pattern)
        {
            patternString = pattern;
        }

        /// <summary>
        /// Returns the string that resembles the current pattern
        /// </summary>
        /// <returns></returns>
        public override string ToString()
        {
            return patternString;
        }
    }
}
