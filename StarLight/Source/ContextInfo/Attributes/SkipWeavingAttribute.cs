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

namespace Composestar.StarLight
{
	/// <summary>
	/// Place this custom attribute before elements you want to exclude from the weaving process. 
	/// Put it at assembly level to exclude the complete assembly from being weaved on.
	/// </summary>
	/// <remarks>
	/// The analyzer will skip elements with this attribute applied to them.
	/// If you have defined filters in the assembly, then they will not be collected.
	/// </remarks>
	/// <example>
	/// To exclude a complete assembly for weaving, add the following to your AssemblyInfo file:
	/// <code>
	/// [assembly: Composestar.StarLight.SkipWeaving()]
	/// </code>
	/// Or to exclude a class or method:
	/// <code>
	/// [Composestar.StarLight.SkipWeaving()]
	/// public class SkippedClass
	/// {
	///    [Composestar.StarLight.SkipWeaving()]
	///    public void Method()
	///    {
	/// 
	///    }
	/// } 
	/// </code>
	/// </example> 
	[AttributeUsage(AttributeTargets.Assembly | AttributeTargets.Property | AttributeTargets.Method | AttributeTargets.Class | AttributeTargets.Constructor | AttributeTargets.Field , AllowMultiple=false)]
	public sealed class SkipWeavingAttribute : Attribute 
	{
		private bool _enabled = true;

		/// <summary>
		/// Gets or sets a value indicating whether this <see cref="T:SkipWeavingAttribute"/> is enabled. Default value is <see langword="true"/>.
		/// </summary>
		/// <value><c>true</c> if enabled; otherwise, <c>false</c>. Default value is <see langword="true"/>.</value>
		public bool Enabled
		{
			get { return _enabled; }
			set { _enabled = value; }
		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:SkipWeavingAttribute"/> class. Weaving is then skipped.
		/// </summary>
		public SkipWeavingAttribute()
		{
			_enabled = true; 
		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:SkipWeavingAttribute"/> class.
		/// </summary>
		/// <param name="enabled">if set to <c>true</c> then the element where this attribute is applied on is skipped.</param>
		public SkipWeavingAttribute(Boolean enabled)
		{
			_enabled = enabled;
		}

	}
}
