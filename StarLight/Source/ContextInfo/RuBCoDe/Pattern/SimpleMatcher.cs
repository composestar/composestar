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
using System.Text.RegularExpressions;

namespace Composestar.StarLight.ContextInfo.RuBCoDe.Pattern
{
    /// <summary>
    ///  A simple pattern matcher for the FIRE2 RegularAutomatons.
    /// </summary>
    public sealed class SimpleMatcher
    {
        private Pattern pattern;

        private Queue<string> words;

        private Set<RegularState> states;

        protected bool hasGreedyEnd;

        /// <summary>
        /// True if the word sequence matches the pattern
        /// </summary>
        /// <param name="pattern"></param>
        /// <param name="words"></param>
        /// <returns></returns>
        public static bool matches(Pattern pattern, Queue<string> words)
        {
            SimpleMatcher sm = new SimpleMatcher(pattern, words);
            return sm.matches();
        }

        /// <summary>
        /// True if the word sequence matches the pattern
        /// </summary>
        /// <param name="pattern"></param>
        /// <param name="words"></param>
        /// <returns></returns>
        public static bool matches(Pattern pattern, IList<string> words)
        {
            Queue<string> queue = new Queue<string>(words);
            return matches(pattern, queue);
        }

        /// <summary>
        /// True if the word sequence matches the pattern
        /// </summary>
        /// <param name="pattern"></param>
        /// <param name="words"></param>
        /// <returns></returns>
        public static bool matches(Pattern pattern, string[] words)
        {
            Queue<string> queue = new Queue<string>();
            foreach (string s in words)
            {
                queue.Enqueue(s);
            }
            return matches(pattern, queue);
        }

        /// <summary>
        /// True if the word sequence matches the pattern. The sentence is broken on whitespace
        /// </summary>
        /// <param name="pattern"></param>
        /// <param name="sentence"></param>
        /// <returns></returns>
        public static bool matches(Pattern pattern, string sentence)
        {
            return matches(pattern, Regex.Split(sentence, "\\s"));
        }

        private SimpleMatcher(Pattern inPattern, Queue<string> inWords)
        {
            pattern = inPattern;
            words = inWords;
            states = new Set<RegularState>();
            addState(pattern.getStartState());
        }

        private void addState(RegularState state)
        {
            if (states.Contains(state))
            {
                return;
            }
            states.Add(state);

            // add lambda transitions
            foreach (RegularTransition rt in state.getOutTransitions())
            {
                if (rt.isEmpty())
                {
                    addState(rt.getEndState());
                }
            }
        }

        private bool matches()
        {
            while (words.Count > 0 && !hasGreedyEnd)
            {
                string word = words.Dequeue();
                if (String.IsNullOrEmpty(word))
                {
                    continue;
                }
                if (traverseStates(word) == 0)
                {
                    break;
                }
            }
            return hasGreedyEnd || ((words.Count == 0) && (states.Contains(pattern.getEndState())));
        }

        private int traverseStates(string word)
        {
            Queue<RegularState> queue = new Queue<RegularState>(states);
            Set<RegularState> visited = new Set<RegularState>();
            states.Clear();
            while (queue.Count > 0)
            {
                RegularState state = queue.Dequeue();
                hasGreedyEnd = state.isGreedyEnd();
                if (hasGreedyEnd)
                {
                    return 0;
                }
                visited.Add(state);
                foreach (RegularTransition rt in state.getOutTransitions())
                {
                    if (rt.isEmpty())
                    {
                        RegularState rst = rt.getEndState();
                        if (!queue.Contains(rst) && !visited.Contains(rst))
                        {
                            queue.Enqueue(rst);
                        }
                    }
                    else if (rt.match(word))
                    {
                        addState(rt.getEndState());
                    }
                }
            }
            return states.Count;
        }
    }
}
