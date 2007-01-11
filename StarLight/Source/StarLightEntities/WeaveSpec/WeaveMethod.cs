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
using System.Xml;
using System.Xml.Serialization;
using System.Diagnostics.CodeAnalysis;

using Composestar.StarLight;
using Composestar.StarLight.Entities.WeaveSpec.Instructions;
#endregion

namespace Composestar.StarLight.Entities.WeaveSpec
{
	/// <summary>
	/// The method in a type with certain input filters and output filters.
	/// </summary>
	[Serializable]
	[XmlType("WeaveMethod", Namespace = Constants.NS)]
	public class WeaveMethod
	{
		private int _id;
		private string _signature;
		private InlineInstruction _inputFilter;
		private List<WeaveCall> _weaveCalls = new List<WeaveCall>();

		/// <summary>
		/// Gets or sets the id of the method. This id is unique within the assembly.
		/// </summary>
		[XmlAttribute]
		public int Id
		{
			get { return _id; }
			set { _id = value; }
		}

		/// <summary>
		/// Gets or sets the signature of the method.
		/// </summary>
		/// <value>The signature.</value>
		[XmlAttribute]
		public string Signature
		{
			get { return _signature; }
			set { _signature = value; }
		}

		/// <summary>
		/// Gets or sets the input filter.
		/// </summary>
		/// <value>The input filter.</value>
		public InlineInstruction InputFilter
		{
			get { return _inputFilter; }
			set { _inputFilter = value; }
		}

		/// <summary>
		/// Gets or sets the calls to weave. 
		/// Specified as a list with WeaveCall elements.
		/// </summary>
		/// <value>The calls to weave.</value>
		[XmlArray("WeaveCalls")]
		[XmlArrayItem("WeaveCall")]
		[SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
		public List<WeaveCall> Calls
		{
			get { return _weaveCalls; }
			set { _weaveCalls = value; }
		}

		/// <summary>
		/// Gets a value indicating whether this instance has input filters.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this instance has input filters; otherwise, <c>false</c>.
		/// </value>
		[XmlIgnore]
		public bool HasInputFilters
		{
			get { return InputFilter != null; }
		}

		/// <summary>
		/// Gets a value indicating whether this instance has output filters.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this instance has output filters; otherwise, <c>false</c>.
		/// </value>
		[XmlIgnore]
		public bool HasOutputFilters
		{
			get { return Calls.Count > 0; }
		}
	}
}
