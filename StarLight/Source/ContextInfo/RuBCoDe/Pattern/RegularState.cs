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
 * $Id: Pattern.java 3953 2007-11-27 11:26:28Z elmuerte $
 */
#endregion

using System;
using System.Collections.Generic;
using System.Text;
using System.Collections.ObjectModel;

namespace Composestar.StarLight.ContextInfo.RuBCoDe.Pattern
{
    /// <summary>
    /// A state in the regular automaton
    /// </summary>
    public class RegularState
    {
        private static int currentId;

        /**
         * Used in ToString() for state names
         */
        private string stateId;

        private IList<RegularTransition> outTransitions;

        /// <summary>
        /// Create a new state
        /// </summary>
        public RegularState()
        {
            stateId = getNewStateId();
            outTransitions = new List<RegularTransition>();
        }

        private static string getNewStateId()
        {
            return "s" + (currentId++);
        }

        /// <summary>
        /// Get the identifier of the state (state name)
        /// </summary>
        /// <returns></returns>
        public string getStateId()
        {
            return stateId;
        }

        /// <summary>
        /// Add a new transition
        /// </summary>
        /// <param name="transition"></param>
        public virtual void addOutTransition(RegularTransition transition)
        {
            outTransitions.Add(transition);
        }

        /// <summary>
        /// Remove a transition
        /// </summary>
        /// <param name="transition"></param>
        public virtual void removeOutTransition(RegularTransition transition)
        {
            outTransitions.Remove(transition);
        }

        /// <summary>
        /// Get a list of all transitions
        /// </summary>
        /// <returns></returns>
        public IList<RegularTransition> getOutTransitions()
        {
            return new ReadOnlyCollection<RegularTransition>(outTransitions);
        }

        /// <summary>
        /// Return a string representation of this state+transitions
        /// </summary>
        /// <returns></returns>
        public override string ToString()
        {
            Set<RegularState> states = new Set<RegularState>();
            getAllStats(states);
            StringBuilder sb = new StringBuilder();
            foreach (RegularState state in states)
            {
                if (state.outTransitions.Count == 0)
                {
                    sb.Append(state.stateId);
                    sb.Append(" -> ");
                    sb.Append("<END>");
                    sb.Append("\n");
                }
                else
                {
                    foreach (RegularTransition rt in state.outTransitions)
                    {
                        sb.Append(state.stateId);
                        sb.Append(" -> ");
                        sb.Append(rt.ToString());
                        sb.Append(" -> ");
                        sb.Append(rt.getEndState().stateId);
                        sb.Append("\n");
                    }
                }
            }
            return sb.ToString();
        }

        private void getAllStats(Set<RegularState> states)
        {
            states.Add(this);
            foreach (RegularTransition transition in outTransitions)
            {
                if (!states.Contains(transition.getEndState()))
                {
                    transition.getEndState().getAllStats(states);
                }
            }
        }
    }
}
