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
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Diagnostics.CodeAnalysis;
using System.Text;
#endregion

namespace Composestar.StarLight.CoreServices.Weaver
{
	/// <summary>
	/// The weave results class is returned from the weaver.
	/// </summary>
	[Serializable]
	public class GenericWeaveResults : IWeaveResults 
	{

		private WeaveStatistics _weaveStatistics;
		private IList<LogItem> _logItems; 

		/// <summary>
		/// Initializes a new instance of the <see cref="T:GenericWeaveResults"/> class.
		/// </summary>
		public GenericWeaveResults()
		{
			_weaveStatistics = new WeaveStatistics();
			_logItems = new List<LogItem>(); 
		}

		/// <summary>
		/// Gets or sets the weave statistics.
		/// </summary>
		/// <value>The weave statistics.</value>
		public WeaveStatistics WeaveStatistics
		{
			get { return _weaveStatistics; }
			set { _weaveStatistics = value; }
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

	}
}
