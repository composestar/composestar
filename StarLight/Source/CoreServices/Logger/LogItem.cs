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

using System;
using System.Collections.Generic;
using System.Text;
using System.Diagnostics.CodeAnalysis;
using System.Globalization;
  
namespace Composestar.StarLight.CoreServices.Logger
{
	/// <summary>
	/// A single log item.
	/// </summary>
	public class LogItem
	{

		#region Constructors

		/// <summary>
		/// Initializes a new instance of the <see cref="T:StarLightItem"/> class.
		/// </summary>
		/// <param name="origin">The origin.</param>
		public LogItem(string origin)
		{
			_origin = origin;
		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:StarLightItem"/> class.
		/// </summary>
		/// <param name="origin">The origin.</param>
		/// <param name="text">The text.</param>
		public LogItem(string origin, string text)
		{
			_origin = origin;
			_text = text;
		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:StarLightItem"/> class.
		/// </summary>
		/// <param name="origin">The origin.</param>
		/// <param name="text">The text.</param>
		/// <param name="args">The args.</param>
		public LogItem(string origin, string text, params object[] args)
		{
			_origin = origin;
			_text = String.Format(CultureInfo.CurrentCulture,text, args);
		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:StarLightItem"/> class.
		/// </summary>
		/// <param name="origin">The origin.</param>
		/// <param name="text">The text.</param>
		/// <param name="category">The category.</param>
		public LogItem(string origin, string text, LogCategory category)
		{
			_origin = origin;
			_text = text;
			_category = category;
		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:StarLightItem"/> class.
		/// </summary>
		/// <param name="origin">The origin.</param>
		/// <param name="text">The text.</param>
		/// <param name="category">The category.</param>
		/// <param name="args">The args.</param>
		public LogItem(string origin, string text, LogCategory category, params object[] args)
		{
			_origin = origin;
			_text = String.Format(CultureInfo.CurrentCulture, text, args);
			_category = category;
		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:StarLightItem"/> class.
		/// </summary>
		/// <param name="origin">The origin.</param>
		/// <param name="text">The text.</param>
		/// <param name="category">The category.</param>
		/// <param name="subcategory">The subcategory.</param>
		public LogItem(string origin, string text, LogCategory category, string subcategory)
		{
			_origin = origin;
			_text = text;
			_category = category;
			_subcategory = subcategory;
		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:StarLightItem"/> class.
		/// </summary>
		/// <param name="origin">The origin.</param>
		/// <param name="text">The text.</param>
		/// <param name="category">The category.</param>
		/// <param name="subcategory">The subcategory.</param>
		/// <param name="args">The args.</param>
		public LogItem(string origin, string text, LogCategory category, string subcategory, params object[] args)
		{
			_origin = origin;
			_text = String.Format(CultureInfo.CurrentCulture, text, args);
			_category = category;
			_subcategory = subcategory;
		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:StarLightItem"/> class.
		/// </summary>
		/// <param name="origin">The origin.</param>
		/// <param name="text">The text.</param>
		/// <param name="category">The category.</param>
		/// <param name="subcategory">The subcategory.</param>
		/// <param name="code">The code.</param>
		public LogItem(string origin, string text, LogCategory category, string subcategory, string code)
		{
			_origin = origin;
			_text = text;
			_category = category;
			_subcategory = subcategory;
			_code = code;
		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:StarLightItem"/> class.
		/// </summary>
		/// <param name="origin">The origin.</param>
		/// <param name="text">The text.</param>
		/// <param name="category">The category.</param>
		/// <param name="subcategory">The subcategory.</param>
		/// <param name="code">The code.</param>
		/// <param name="args">The args.</param>
		public LogItem(string origin, string text, LogCategory category, string subcategory, string code, params object[] args)
		{
			_origin = origin;
			_text = String.Format(CultureInfo.CurrentCulture, text, args);
			_category = category;
			_subcategory = subcategory;
			_code = code;
		}
		#endregion

		private String _code;

		/// <summary>
		/// Code identifies an application specific error code / warning code. 
		/// Code must not be localized and it must not contain spaces.
		/// </summary>
		/// <value>The code.</value>
		public String Code
		{
			get { return _code; }
			set { _code = value; }
		}
	
		private string _origin;

		/// <summary>
		/// Origin can be blank. If present, the origin is usually a tool name, like 'cl' in one of the examples. 
		/// But it could also be a filename, like 'Main.cs' shown in another example. 
		/// If it is a filename, then it must be an absolute or a relative filename, 
		/// followed by an optional paranthesized line/column information in one of the following forms:
		/// (line) or (line-line) or (line-col) or (line,col-col) or (line,col,line,col)
		/// Lines and columns start at 1 in a file - i.e. the beginning of a file is 1, and the leftmost column is 1. 
		/// If the Origin is a tool name, then it must not change based on local - i.e. it needs to be locale neutral.
		/// </summary>
		/// <value>The origin.</value>
		public string Origin
		{
			get { return _origin; }
			set { _origin = value; }
		}
	
		private string _text;

		/// <summary>
		/// User friendly text that explains the error, and *must* be localized if you cater to multiple locales.
		/// </summary>
		public string Text
		{
			get { return _text; }
			set { _text = value; }
		}
	

		private LogCategory _category;

		/// <summary>
		/// Category must be either 'error' or 'warning'. Case does not matter. Like origin, category must not be localized.
		/// </summary>
		/// <value>The category.</value>
		public LogCategory Category
		{
			get { return _category; }
			set { _category = value; }
		}

		private string _subcategory;

		/// <summary>
		/// Subcategory is used to classify the category itself further, and should not be localized.
		/// </summary>
		/// <value>The sub category.</value>
		public string Subcategory
		{
			get { return _subcategory; }
			set { _subcategory = value; }
		}
	

		/// <summary>
		/// Log type
		/// </summary>
		public enum LogCategory
		{
			/// <summary>
			/// Information
			/// </summary>
			Information,
			/// <summary>
			/// Warning
			/// </summary>
			Warning,
			/// <summary>
			/// Error
			/// </summary>
			Error
		}

		/// <summary>
		/// Formatted string conforms the MSBuild output string
		/// </summary>
		/// <remarks>
		/// See http://blogs.msdn.com/msbuild/archive/2006/11/03/msbuild-visual-studio-aware-error-texts-and-text-formats.aspx
		/// </remarks> 
		/// <returns></returns>
		public override string ToString()
		{
			return String.Format(CultureInfo.CurrentCulture, "{0}:{1} {2} {3}:{4}", Origin, Subcategory, Category.ToString(), Code, Text);
		}
	}
}
