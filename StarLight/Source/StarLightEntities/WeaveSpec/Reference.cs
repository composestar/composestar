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
using Composestar.StarLight.Entities.LanguageModel;
using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Globalization;
using System.IO;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
#endregion

namespace Composestar.StarLight.Entities.WeaveSpec
{
	/// <summary>
	/// A reference.
	/// </summary>
	[Serializable]
	[XmlType("Reference", Namespace = Constants.NS)]
	public class Reference
	{
		public const string InnerTarget = "inner";
		public const string SelfTarget = "self";

		private string _namespace;
		private string _target;
        private string _type;
		private string _assembly;
		private string _selector;
		private int _innerCallContext;

		/// <summary>
		/// Gets or sets the name space.
		/// </summary>
		/// <value>The name space.</value>
		[XmlAttribute]
		public string Namespace
		{
			get { return _namespace; }
			set { _namespace = value; }
		}

		/// <summary>
		/// Gets or sets the target. The target is the name of a local variable to use. 
        /// If it is empty or null then the reference is a static reference. "inner" and "self" are
        /// special target names.
		/// </summary>
		/// <value>The target.</value>
		[XmlAttribute]
		public string Target
		{
			get { return _target; }
			set { _target = value; }
		}

		/// <summary>
		/// Gets the fullname.
		/// </summary>
		/// <value>The fullname.</value>
		[XmlIgnore]
		public string FullName
		{
            get { return string.Concat(_namespace, ".", _type); }
		}

        /// <summary>
        /// Gets or sets the type.
        /// </summary>
        /// <value>The type.</value>
        [XmlAttribute]
        public string Type
        {
            get { return _type; }
            set { _type = value; }
        }

		/// <summary>
		/// Gets or sets the assembly containing the type of the reference.
		/// </summary>
		/// <value>The assembly.</value>
		[XmlAttribute]
		public string Assembly
		{
			get { return _assembly; }
			set { _assembly = value; }
		}
		
		/// <summary>
		/// Gets or sets the selector.
		/// </summary>
		/// <value>The selector.</value>
		[XmlAttribute]
		public string Selector
		{
			get { return _selector; }
			set { _selector = value; }
		}

		/// <summary>
		/// Gets or sets the inner call context.
		/// </summary>
		/// <value>The inner call context.</value>
		[XmlAttribute]
		public int InnerCallContext
		{
			get { return _innerCallContext; }
			set { _innerCallContext = value; }
		}
	}
}
