#region License
/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * ComposeStar StarLight [http://janus.cs.utwente.nl:8000/twiki/bin/view/StarLight/WebHome]
 * Copyright (C) 2003, University of Twente.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification,are permitted provided that the following conditions
 * are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Twente nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
*/
#endregion

#region Using directives
using Composestar.StarLight.CoreServices.Logger;
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.Entities.LanguageModel;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Diagnostics.CodeAnalysis;
using System.Text;
#endregion

namespace Composestar.StarLight.CoreServices.Analyzer
{
	/// <summary>
	/// The analyzer results class is returned from the analyzer.
	/// </summary>
	[Serializable]
	public class GenericAnalyzerResults : IAnalyzerResults  
	{

		private IList<LogItem> _logItems;
		private IList<FilterTypeElement> _filterTypes;
		private IList<FilterActionElement> _filterActions;

		/// <summary>
		/// Initializes a new instance of the <see cref="T:GenericAnalyzerResults"/> class.
		/// </summary>
		public GenericAnalyzerResults()
		{
			_logItems = new List<LogItem>();
			_filterTypes = new List<FilterTypeElement>();
			_filterActions = new List<FilterActionElement>(); 
		}

		private AssemblyElement _assembly;


		/// <summary>
		/// Gets or sets the assembly.
		/// </summary>
		/// <value>The assembly.</value>
		public AssemblyElement Assembly
		{
			get
			{
				return _assembly;
			}
			set
			{
				_assembly = value;
			}
		}

		/// <summary>
		/// Gets the log items.
		/// </summary>
		/// <value>The log items.</value>
		public ReadOnlyCollection<LogItem> LogItems 
		{
			get
			{
				return new ReadOnlyCollection<LogItem>(_logItems); 
			}
		}

		/// <summary>
		/// Adds the log item.
		/// </summary>
		/// <param name="item">The item.</param>
		public void AddLogItem(LogItem item)
		{
			if (item == null)
				throw new ArgumentNullException("item");
 
			_logItems.Add(item);  
		}

		/// <summary>
		/// Gets the filter actions.
		/// </summary>
		/// <value>The filter actions.</value>
		public ReadOnlyCollection<FilterActionElement> FilterActions 
		{
			get
			{
				return new ReadOnlyCollection<FilterActionElement>(_filterActions); 
			}
		}

		/// <summary>
		/// Gets the filter types.
		/// </summary>
		/// <value>The filter types.</value>
		public ReadOnlyCollection<FilterTypeElement> FilterTypes
		{
			get
			{
				return new ReadOnlyCollection<FilterTypeElement>(_filterTypes);
			}
		}

		/// <summary>
		/// Add filter type
		/// </summary>
		/// <param name="filterType">Filter type</param>
		public void AddFilterType(FilterTypeElement filterType)
		{
			if (filterType == null)
				throw new ArgumentNullException("filterType");

			_filterTypes.Add(filterType); 

		}

		/// <summary>
		/// Adds the type of the filter.
		/// </summary>
		/// <param name="filterTypes">The filter types.</param>
		public void AddFilterType(IList<FilterTypeElement> filterTypes)
		{
			if (filterTypes == null)
				throw new ArgumentNullException("filterTypes");

			foreach (FilterTypeElement filterType in filterTypes)
			{
				AddFilterType(filterType); 
			}
			
		}

		/// <summary>
		/// Adds the filter action.
		/// </summary>
		/// <param name="filterAction">The filter action.</param>
		public void AddFilterAction(FilterActionElement filterAction)
		{
			if (filterAction == null)
				throw new ArgumentNullException("filterAction");

			_filterActions.Add(filterAction);

		}

		/// <summary>
		/// Adds the filter action.
		/// </summary>
		/// <param name="filterActions">The filter actions.</param>
		public void AddFilterAction(IList<FilterActionElement> filterActions)
		{
			if (filterActions == null)
				throw new ArgumentNullException("filterActions");

			foreach (FilterActionElement filterAction in filterActions)
			{
				AddFilterAction(filterAction);
			}

		}
	}
}
