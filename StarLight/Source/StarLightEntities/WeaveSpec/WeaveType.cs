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

namespace Composestar.StarLight.Entities.WeaveSpec
{
	/// <summary>
	/// 
	/// </summary>
	[Serializable]
	[XmlType("WeaveType", Namespace = Constants.NS)]
	public class WeaveType
	{
		private string _name;
		private List<Internal> _internals = new List<Internal>();
		private List<External> _externals = new List<External>();
		private List<Condition> _conditions = new List<Condition>();
		private List<WeaveMethod> _methods = new List<WeaveMethod>();

		/// <summary>
		/// Gets or sets the name of the type. Needed for the lookup.
		/// </summary>
		/// <value>The name.</value>
		[XmlAttribute]
		public string Name
		{
			get { return _name; }
			set { _name = value; }
		}

		/// <summary>
		/// Gets or sets the internals.
		/// </summary>
		/// <value>The internals.</value>
		/// <returns>List</returns>
		[XmlArray("Internals")]
		[XmlArrayItem("Internal")]
		[SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
		public List<Internal> Internals
		{
			get { return _internals; }
			set { _internals = value; }
		}

		/// <summary>
		/// Gets or sets the externals.
		/// </summary>
		/// <value>The externals.</value>
		[XmlArray("Externals")]
		[XmlArrayItem("External")]
		[SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
		public List<External> Externals
		{
			get { return _externals; }
			set { _externals = value; }
		}

		/// <summary>
		/// Gets or sets the conditions.
		/// </summary>
		/// <value>The conditions.</value>
		[XmlArray("Conditions")]
		[XmlArrayItem("Condition")]
		[SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
		public List<Condition> Conditions
		{
			get { return _conditions; }
			set { _conditions = value; }
		}

		/// <summary>
		/// Gets or sets the methods with instructions to weave.
		/// </summary>
		/// <value>The methods.</value>
		[SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
		public List<WeaveMethod> Methods
		{
			get { return _methods; }
		}

		/// <summary>
		/// Gets the method with the specified signature.
		/// </summary>
		/// <param name="signature">The signature.</param>
		/// <returns>The method or null if not found.</returns>
		public WeaveMethod GetMethod(String signature)
		{
			foreach (WeaveMethod weaveMethod in Methods)
			{
				if (weaveMethod.Signature.Equals(signature))
				{
					return weaveMethod;
				}
			}

			return null;
		}

		/// <summary>
		/// Gets a value indicating whether this instance has internals.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this instance has internals; otherwise, <c>false</c>.
		/// </value>
		[XmlIgnore]
		public bool HasInternals
		{
			/// <summary>
			/// _internals
			/// </summary>
			get { return _internals.Count > 0; }
		}

		/// <summary>
		/// Gets a value indicating whether this instance has exterals.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this instance has exterals; otherwise, <c>false</c>.
		/// </value>
		/// <returns>Bool</returns>
		[XmlIgnore]
		public bool HasExternals
		{
			/// <summary>
			/// Externals
			/// </summary>
			/// <returns>List</returns>
			get { return Externals.Count > 0; }
		}

		/// <summary>
		/// Gets a value indicating whether this instance has conditions.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this instance has conditions; otherwise, <c>false</c>.
		/// </value>
		[XmlIgnore]
		public bool HasConditions
		{
			get { return Conditions.Count > 0; }
		}

		/// <summary>
		/// Gets a value indicating whether this instance has methods with input filters.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this instance has input filters; otherwise, <c>false</c>.
		/// </value>
		[XmlIgnore]
		public bool HasInputFilters
		{
			get
			{
				foreach (WeaveMethod method in _methods)
				{
					if (method.HasInputFilters)
						return true;
				}

				return false;
			}
		}

		/// <summary>
		/// Gets a value indicating whether this instance has methods with output filters.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this instance has output filters; otherwise, <c>false</c>.
		/// </value>
		[XmlIgnore]
		public bool HasOutputFilters
		{
			get
			{
				foreach (WeaveMethod method in _methods)
				{
					if (method.HasOutputFilters)
						return true;
				}

				return false;
			}
		}
	}
}
