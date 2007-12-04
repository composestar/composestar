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
    /// The automaton for a pattern
    /// </summary>
    public class RegularAutomaton
    {
        private RegularState startState;

        private RegularState endState;

        /// <summary>
        /// Create a new automaton without a start and end state
        /// </summary>
        public RegularAutomaton()
        { }

        /// <summary>
        /// Create an automaton with a given start state and a newly created end state
        /// </summary>
        /// <param name="inStart"></param>
        public RegularAutomaton(RegularState inStart)
        {
            startState = inStart;
            endState = new RegularState();
        }

        /// <summary>
        /// Creates an automaton with a given start and end state
        /// </summary>
        /// <param name="inStart"></param>
        /// <param name="inEnd"></param>
        public RegularAutomaton(RegularState inStart, RegularState inEnd)
        {
            startState = inStart;
            endState = inEnd;
        }

        /// <summary>
        /// Set the new start state
        /// </summary>
        /// <param name="newStartState"></param>
        public void setStartState(RegularState newStartState)
        {
            startState = newStartState;
        }

        /// <summary>
        /// Get the start state
        /// </summary>
        /// <returns></returns>
        public RegularState getStartState()
        {
            return startState;
        }

        /// <summary>
        /// set the new end state
        /// </summary>
        /// <param name="newEndState"></param>
        public void setEndState(RegularState newEndState)
        {
            endState = newEndState;
        }

        /// <summary>
        /// Get the end state
        /// </summary>
        /// <returns></returns>
        public RegularState getEndState()
        {
            return endState;
        }
    }
}
