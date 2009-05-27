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

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.CORE2.ConflictType#getSeverity()
		 */
		@Override
		public ConflictSeverity getSeverity()
		{
			return ConflictSeverity.NOTICE;
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

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.CORE2.ConflictType#getSeverity()
		 */
		@Override
		public ConflictSeverity getSeverity()
		{
			return ConflictSeverity.NOTICE;
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

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.CORE2.ConflictType#getSeverity()
		 */
		@Override
		public ConflictSeverity getSeverity()
		{
			return ConflictSeverity.WARNING;
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

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.CORE2.ConflictType#getSeverity()
		 */
		@Override
		public ConflictSeverity getSeverity()
		{
			return ConflictSeverity.WARNING;
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

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.CORE2.ConflictType#getSeverity()
		 */
		@Override
		public ConflictSeverity getSeverity()
		{
			return ConflictSeverity.NOTICE;
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

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.CORE2.ConflictType#getSeverity()
		 */
		@Override
		public ConflictSeverity getSeverity()
		{
			return ConflictSeverity.NOTICE;
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

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.CORE2.ConflictType#getSeverity()
		 */
		@Override
		public ConflictSeverity getSeverity()
		{
			return ConflictSeverity.WARNING;
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

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.CORE2.ConflictType#getSeverity()
		 */
		@Override
		public ConflictSeverity getSeverity()
		{
			return ConflictSeverity.WARNING;
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

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.CORE2.ConflictType#getSeverity()
		 */
		@Override
		public ConflictSeverity getSeverity()
		{
			return ConflictSeverity.WARNING;
		}
	};

	/**
	 * @return The severity of the conflict
	 */
	public abstract ConflictSeverity getSeverity();
}
