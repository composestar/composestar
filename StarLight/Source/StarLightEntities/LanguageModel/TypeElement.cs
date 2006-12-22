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
using System.Globalization;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using System.Diagnostics.CodeAnalysis;
#endregion

namespace Composestar.StarLight.Entities.LanguageModel
{
	/// <summary>
	/// Contains a single type with properties and fields/methods.
	/// </summary>
	/// <returns>ICustom attributes</returns>
	[Serializable]
	[XmlRoot("Type", Namespace = "Entities.TYM.DotNET.Composestar")]
	public sealed class TypeElement : ICustomAttributes
	{
		private string _name = string.Empty;
		private string _namespace = string.Empty;
		private string _baseType = string.Empty;
		private List<string> _interfaces = new List<string>();
		private bool _isAbstract;
		private bool _isInterface;
		private bool _isSealed;
		private bool _isValueType;
		private bool _isEnum;
		private bool _isClass;
		private bool _isNotPublic;
		private bool _isPrimitive;
		private bool _isPublic;
		private bool _isSerializable;
		private List<MethodElement> _methods = new List<MethodElement>();
		private List<FieldElement> _fields = new List<FieldElement>();
		private List<AttributeElement> _attributes = new List<AttributeElement>();

		/// <summary>
		/// <returns>String</returns>
		/// Name of this type.
		/// </summary>
		[XmlAttribute]
		public string Name
		{
			get { return _name; }
			set { _name = value; }
		}

		/// <summary>
		/// Full namespace of this type.
		/// </summary>
		/// <value>The namespace.</value>
		[XmlAttribute]
		public string Namespace
		{
			get { return _namespace; }
			set { _namespace = value; }
		}

		/// <summary>
		/// Gets the fullname.
		/// </summary>
		/// <value>The fullname.</value>
		[XmlIgnore]
		public string FullName
		{
			get { return string.Concat(_namespace, ".", _name); }
		}

		/// <summary>
		/// Base type of this type.
		/// </summary>
		[XmlAttribute]
		public string BaseType
		{
			get { return _baseType; }
			set { _baseType = value; }
		}

		/// <summary>
		/// Gets or sets the list of implemented interfaces.
		/// </summary>
		[XmlArray("Interfaces")]
		[XmlArrayItem("Interface")]
		public List<string> Interfaces
		{
			get { return _interfaces; }
			set { _interfaces = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether this instance is abstract.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this instance is abstract; otherwise, <c>false</c>.
		/// </value>
		[XmlAttribute]
		public bool IsAbstract
		{
			get { return _isAbstract; }
			set { _isAbstract = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether this instance is interface.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this instance is interface; otherwise, <c>false</c>.
		/// </value>
		[XmlAttribute]
		public bool IsInterface
		{
			get { return _isInterface; }
			set { _isInterface = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether this instance is sealed.
		/// </summary>
		/// <value><c>true</c> if this instance is sealed; otherwise, <c>false</c>.</value>
		[XmlAttribute]
		public bool IsSealed
		{
			get { return _isSealed; }
			set { _isSealed = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether this instance is value type.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this instance is value type; otherwise, <c>false</c>.
		/// </value>
		[XmlAttribute]
		public bool IsValueType
		{
			get { return _isValueType; }
			set { _isValueType = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether this instance is enum.
		/// </summary>
		/// <value><c>true</c> if this instance is enum; otherwise, <c>false</c>.</value>
		[XmlAttribute]
		public bool IsEnum
		{
			get { return _isEnum; }
			set { _isEnum = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether this instance is class.
		/// </summary>
		/// <value><c>true</c> if this instance is class; otherwise, <c>false</c>.</value>
		[XmlAttribute]
		public bool IsClass
		{
			get { return _isClass; }
			set { _isClass = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether this instance is not public.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this instance is not public; otherwise, <c>false</c>.
		/// </value>
		[XmlAttribute]
		public bool IsNotPublic
		{
			get { return _isNotPublic; }
			set { _isNotPublic = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether this instance is primitive.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this instance is primitive; otherwise, <c>false</c>.
		/// </value>
		[XmlAttribute]
		public bool IsPrimitive
		{
			get { return _isPrimitive; }
			set { _isPrimitive = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether this instance is public.
		/// </summary>
		/// <value><c>true</c> if this instance is public; otherwise, <c>false</c>.</value>
		[XmlAttribute]
		public bool IsPublic
		{
			get { return _isPublic; }
			set { _isPublic = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether this instance is serializable.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this instance is serializable; otherwise, <c>false</c>.
		/// </value>
		[XmlAttribute]
		public bool IsSerializable
		{
			get { return _isSerializable; }
			set { _isSerializable = value; }
		}

		/// <summary>
		/// Gets or sets the methods.
		/// </summary>
		/// <value>The methods.</value>
		[XmlArray("Methods")]
		[XmlArrayItem("Method")]
		[SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
		public List<MethodElement> Methods
		{
			get { return _methods; }
			set { _methods = value; }
		}

		/// <summary>
		/// Gets or sets the fields.
		/// </summary>
		/// <value>The fields.</value>
		[XmlArray("Fields")]
		[XmlArrayItem("Field")]
		[SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
		public List<FieldElement> Fields
		{
			get { return _fields; }
			set { _fields = value; }
		}

		#region ICustomAttributes

		/// <summary>
		/// Gets a value indicating whether this instance has attributes.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this instance has attributes; otherwise, <c>false</c>.
		/// </value>
		public bool HasAttributes
		{
			get { return (_attributes.Count > 0); }
		}

		/// <summary>
		/// Gets or sets the attributes.
		/// </summary>
		/// <value>The attributes.</value>
		[XmlArray("Attributes")]
		[XmlArrayItem("Attribute")]
		[SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
		public List<AttributeElement> Attributes
		{
			get { return _attributes; }
			set { _attributes = value; }
		}

		#endregion
	}
}
