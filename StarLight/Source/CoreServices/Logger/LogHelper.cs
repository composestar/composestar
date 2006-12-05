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
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;
#endregion

namespace Composestar.StarLight.CoreServices.Logger
{
	/// <summary>
	/// Logger class to add log items
	/// </summary>
	public class LogHelper
	{

		private IList<LogItem> _logItems = new List<LogItem>();

		/// <summary>
		/// Logs the error.
		/// </summary>
		/// <param name="origin">The origin.</param>
		/// <param name="text">The text.</param>
		/// <param name="subcategory">The subcategory.</param>
		/// <param name="code">The code.</param>
		/// <param name="args">The args.</param>
		public void LogError(string origin, string text, string subcategory, string code, params object[] args)
		{
			LogItem item = new LogItem(origin, text, LogItem.LogCategory.Error, subcategory, code, args); 
			_logItems.Add(item);  
		}

		/// <summary>
		/// Logs the warning.
		/// </summary>
		/// <param name="origin">The origin.</param>
		/// <param name="text">The text.</param>
		/// <param name="subcategory">The subcategory.</param>
		/// <param name="code">The code.</param>
		/// <param name="args">The args.</param>
		public void LogWarning(string origin, string text, string subcategory, string code, params object[] args)
		{
			LogItem item = new LogItem(origin, text, LogItem.LogCategory.Warning, subcategory, code, args);
			_logItems.Add(item);
		}

		/// <summary>
		/// Logs the info.
		/// </summary>
		/// <param name="origin">The origin.</param>
		/// <param name="text">The text.</param>
		/// <param name="subcategory">The subcategory.</param>
		/// <param name="code">The code.</param>
		/// <param name="args">The args.</param>
		public void LogInfo(string origin, string text, string subcategory, string code, params object[] args)
		{
			LogItem item = new LogItem(origin, text, LogItem.LogCategory.Information, subcategory, code, args);
			_logItems.Add(item);
		}

		/// <summary>
		/// Logs the fatal.
		/// </summary>
		/// <param name="origin">The origin.</param>
		/// <param name="text">The text.</param>
		/// <param name="subcategory">The subcategory.</param>
		/// <param name="code">The code.</param>
		/// <param name="args">The args.</param>
		public void LogFatal(string origin, string text, string subcategory, string code, params object[] args)
		{
			LogItem item = new LogItem(origin, text, LogItem.LogCategory.Error, subcategory, code, args);
			_logItems.Add(item);
		}

		/// <summary>
		/// Get the log items.
		/// </summary>
		/// <returns>Read only collection</returns>
		public ReadOnlyCollection<LogItem> LogItems
		{
			get
			{
				return new ReadOnlyCollection<LogItem>(_logItems);
			}
		}

		/// <summary>
		/// Clear the log items list.
		/// </summary>
		public void ClearLogItems()
		{
			_logItems.Clear(); 
		}

	}
}
