/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
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

package Composestar.Core.CORE2;

/**
 * CORE Conflict Types
 * 
 * @author Michiel Hendriks
 */
public enum ConflictType
{
	@Deprecated
	CONDITION_EXPRESSION_FALSE
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "Condition expression is the constant false";
		}
	},
	@Deprecated
	MATCHING_PART_ALWAYS_MATCHES
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "Matching part always matches";
		}
	},
	@Deprecated
	MATCHING_PART_NEVER_MATCHES
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "Matching part never matches";
		}
	},
	@Deprecated
	MATCHING_PATTERN_ALWAYS_REJECTS
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "Matching pattern always rejects";
		}
	},
	@Deprecated
	MATCHING_PATTERN_ALWAYS_ACCEPTS
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "Matching pattern always accepts";
		}
	},
	@Deprecated
	MATCHING_PATTERN_REDUNDANT
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "Redundant matching pattern";
		}
	},
	@Deprecated
	MATCHING_PATTERN_UNREACHABLE
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "Unreachable matching pattern";
		}
	},
	FILTER_ELEMENT_ALWAYS_REJECTS
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "Filter element always rejects";
		}
	},
	FILTER_ELEMENT_ALWAYS_ACCEPTS
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "Filter element always accepts";
		}
	},
	FILTER_ELEMENT_REDUNDANT
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "Redundant filter element";
		}
	},
	FILTER_ELEMENT_UNREACHABLE
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "Unreachable filter element";
		}
	},
	FILTER_ALWAYS_REJECTS
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "Filter always rejects";
		}
	},
	FILTER_ALWAYS_ACCEPTS
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "Filter always accepts";
		}
	},
	FILTER_REDUNDANT
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "Redundant filter";
		}
	},
	FILTER_UNREACHABLE
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "Unreachable filter";
		}
	},
	MATCHING_EXPRESSION_UNREACHABLE
	{
		/*
		 * (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString()
		{
			return "Unreachable matching expression segment";
		}
	}
}
