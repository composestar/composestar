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
	/// Contains information about a single method.
	/// </summary>
	[Serializable]
	[XmlRoot("Method", Namespace = "Entities.TYM.DotNET.Composestar")]
	public sealed class MethodElement : ICustomAttributes 
	{
		private string _name;
		private string _returnType;
		private string _signature;
		private bool _isAbstract;
		private bool _isConstructor;
		private bool _isPrivate;
		private bool _isPublic;
		private bool _isStatic;
		private bool _isVirtual;
		private List<ParameterElement> _parameters = new List<ParameterElement>();
		private MethodBody _methodBody;

		/// <summary>
		/// Gets or sets the name.
		/// </summary>
		/// <value>The name.</value>
		/// <returns>String</returns>
		[XmlAttribute]
		public string Name
		{
			get { return _name; }
			set { _name = value; }
		}

		/// <summary>
		/// Gets or sets the type of the return.
		/// </summary>
		/// <value>The type of the return.</value>
		[XmlAttribute]
		public string ReturnType
		{
			get { return _returnType; }
			set { _returnType = value; }
		}

		/// <summary>
		/// Gets or sets the signature.
		/// </summary>
		/// <value>The signature.</value>
		[XmlAttribute]
		public string Signature
		{
			get { return _signature; }
			set { _signature = value; }
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
		/// Gets or sets a value indicating whether this instance is constructor.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this instance is constructor; otherwise, <c>false</c>.
		/// </value>
		[XmlAttribute]
		public bool IsConstructor
		{
			get { return _isConstructor; }
			set { _isConstructor = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether this instance is private.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this instance is private; otherwise, <c>false</c>.
		/// </value>
		[XmlAttribute]
		public bool IsPrivate
		{
			get { return _isPrivate; }
			set { _isPrivate = value; }
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
		/// Gets or sets a value indicating whether this instance is static.
		/// </summary>
		/// <value><c>true</c> if this instance is static; otherwise, <c>false</c>.</value>
		[XmlAttribute]
		public bool IsStatic
		{
			get { return _isStatic; }
			set { _isStatic = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether this instance is virtual.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this instance is virtual; otherwise, <c>false</c>.
		/// </value>
		[XmlAttribute]
		public bool IsVirtual
		{
			get { return _isVirtual; }
			set { _isVirtual = value; }
		}

		/// <summary>
		/// Gets or sets the parameters.
		/// </summary>
		/// <value>The parameters.</value>
		[XmlArray("Parameters")]
		[XmlArrayItem("Parameter")]
		[System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
		public List<ParameterElement> Parameters
		{
			get { return _parameters; }
			set { _parameters = value; }
		}

		/// <summary>
		/// Gets or sets the body.
		/// </summary>
		/// <value>The body.</value>
		public MethodBody Body
		{
			get { return _methodBody; }
			set { _methodBody = value; }
		}

		/// <summary>
		/// Gets a value indicating whether this instance has a method body.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this instance has a method body; otherwise, <c>false</c>.
		/// </value>
		[XmlIgnore]
		public bool HasMethodBody
		{
			get { return (_methodBody != null); }
		}

		#region ICustomAttributes

		private List<AttributeElement> _attributes = new List<AttributeElement>();

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
