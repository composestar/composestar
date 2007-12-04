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
using System.Collections.ObjectModel;

namespace Composestar.StarLight.ContextInfo.RuBCoDe.Pattern
{
    /// <summary>
    /// A transition from one state to the other. Transitions have labels that much be matched
    /// </summary>
    public class RegularTransition
    {
        /// <summary>
        /// The wildcard label
        /// </summary>
        public static string WILDCARD = "<ÿÿÿ>";

        private RegularState startState;

        private RegularState endState;

        private Set<string> labels;

        private bool negation;

        /// <summary>
        /// Create a new transition with a given start and end
        /// </summary>
        /// <param name="mystart"></param>
        /// <param name="myend"></param>
        public RegularTransition(RegularState mystart, RegularState myend)
        {
            setStartState(mystart);
            setEndState(myend);
            labels = new Set<string>();
        }

        /// <summary>
        /// Set a new start state
        /// </summary>
        /// <param name="newStart"></param>
        public void setStartState(RegularState newStart)
        {
            if (startState != null)
            {
                startState.removeOutTransition(this);
            }
            startState = newStart;
            startState.addOutTransition(this);
        }

        /// <summary>
        /// Set a new end state
        /// </summary>
        /// <param name="newEnd"></param>
        public void setEndState(RegularState newEnd)
        {
            endState = newEnd;
        }

        /// <summary>
        /// Set the negation value fo this transition
        /// </summary>
        /// <param name="value"></param>
        public void setNegation(bool value)
        {
            negation = value;
        }

        /// <summary>
        /// True if this is a negating transition
        /// </summary>
        /// <returns></returns>
        public bool isNegation()
        {
            return negation;
        }

        /// <summary>
        /// Add a single label
        /// </summary>
        /// <param name="label"></param>
        public void addLabel(string label)
        {
            labels.Add(label);
        }

        /// <summary>
        /// Add a set of labels
        /// </summary>
        /// <param name="label"></param>
        public void addLabels(IList<string> label)
        {
            labels.AddRange(label);
        }

        /// <summary>
        /// Get a list of all labels
        /// </summary>
        /// <returns></returns>
        public IList<string> getLabels()
        {
            return new ReadOnlyCollection<string>(labels);
        }

        /// <summary>
        /// Return true if this transition matches the given label
        /// </summary>
        /// <param name="word"></param>
        /// <returns></returns>
        public bool match(string word)
        {
            if (labels.Contains(WILDCARD))
            {
                return !negation;
            }
            else if (negation)
            {
                return !labels.Contains(word);
            }
            else
            {
                return labels.Contains(word);
            }
        }

        /// <summary>
        /// True if this is a lambda transition
        /// </summary>
        /// <returns></returns>
        public bool isEmpty()
        {
            return labels.Count == 0;
        }

        /// <summary>
        /// True if it's a wildcard transition
        /// </summary>
        /// <returns></returns>
        public bool isWildcard()
        {
            return labels.Contains(WILDCARD);
        }

        /// <summary>
        /// Get the end state
        /// </summary>
        /// <returns></returns>
        public RegularState getEndState()
        {
            return endState;
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
        /// Return a string representation of this transition
        /// </summary>
        /// <returns></returns>
        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();
            if (negation)
            {
                sb.Append("!");
            }
            sb.Append(labels.ToString());
            return sb.ToString();
        }
    }
}
