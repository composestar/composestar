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
using System.Xml;
using System.Xml.Serialization;
using System.Diagnostics.CodeAnalysis;
#endregion

namespace Composestar.StarLight.Entities.LanguageModel
{
	/// <summary>
	/// Contains a single annotation definition.
	/// </summary>
	[Serializable]
	[XmlType("AttributeElement", Namespace = Constants.NS)]
	public sealed class AttributeElement
	{
		private String _attributeType;
		private List<AttributeValueElement> _values = new List<AttributeValueElement>();

		/// <summary>
		/// Gets or sets the type of the attribute.
		/// </summary>
		/// <value>The type of the attribute.</value>
		/// <returns>String</returns>
		[XmlAttribute]
		public String AttributeType
		{
			get { return _attributeType; }
			set { _attributeType = value; }
		}

		/// <summary>
		/// Gets or sets the values.
		/// </summary>
		/// <value>The values.</value>
		[XmlArray("Values")]
		[XmlArrayItem("Value")]
		[SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
		public List<AttributeValueElement> Values
		{
			get { return _values; }
			set { _values = value; }
		}
	}

	/// <summary>
	/// Contains a single attribute value.
	/// </summary>
	[Serializable]
	[XmlRoot("AttributeValue", Namespace = "Entities.TYM.DotNET.Composestar")]
	public sealed class AttributeValueElement
	{
		private string _name;
		private string _value;

		/// <summary>
		/// Gets or sets the name.
		/// </summary>
		/// <value>The name.</value>
		[XmlAttribute]
		public string Name
		{
			get { return _name; }
			set { _name = value; }
		}

		/// <summary>
		/// Gets or sets the value.
		/// </summary>
		/// <value>The value.</value>
		[XmlAttribute]
		public string Value
		{
			get { return _value; }
			set { _value = value; }
		}
	}
}
