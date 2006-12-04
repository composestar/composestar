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
using System.Text;
#endregion

namespace Composestar.StarLight
{
	/// <summary>
	/// Indicates if the element has properties which should be analyzed and weaved upon by StarLight.
	/// Default action is to skip properties. By applying this attribute at assembly or class level, it is possible 
	/// to enable processing of properties. To exclude a specific property, apply the SkipWeaveAttribute to that property.
	/// </summary>
	/// <example>
	/// In the following example, the class is marked with the ProcessProperties attribute. So the Enabled property will be processed, 
	/// the Skipped property will be skipped because of the SkipWeaving attribute applied to it.
	/// <code>
	/// [Composestar.StarLight.ProcessProperties]
	/// public class ClassWithProperties
	/// {
	/// <returns>Bool</returns>
	///    private bool _enabled = true;
	/// 
	///    public bool Enabled
	///	   {
	///		  get { return _enabled; }
	///		  set { _enabled = value; }
	///	   }
	/// 
	///    private bool _skipped = true;
	/// 
	///    [Composestar.StarLight.SkipWeaving()]
	///    public bool Skipped
	///	   {
	///		  get { return _skipped; }
	///		  set { _skipped = value; }
	///	   }
	/// } 
	/// </code>
	/// </example>
	[AttributeUsage(AttributeTargets.Assembly | AttributeTargets.Class )]
	public sealed class ProcessPropertiesAttribute : Attribute
	{
		private bool _enabled = true;

		/// <summary>
		/// Gets or sets a value indicating whether this <see cref="T:ProcessPropertiesAttribute"/> is enabled.  Default value is <see langword="true"/>.
		/// </summary>
		/// <value><c>true</c> if enabled; otherwise, <c>false</c>. Default value is <see langword="true"/>.</value>
		public bool Enabled
		{
			get { return _enabled; }
			set { _enabled = value; }
		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:ProcessPropertiesAttribute"/> class.
		/// </summary>
		public ProcessPropertiesAttribute()
		{
			_enabled = true; 
		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:ProcessPropertiesAttribute"/> class.
		/// </summary>
		/// <param name="enabled">if set to <c>true</c> [enabled].</param>
		public ProcessPropertiesAttribute(Boolean enabled)
		{
			_enabled = enabled;
		}
	}
}
