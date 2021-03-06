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
using System.Xml;
using System.Xml.Serialization;
using System.Collections.Generic;
using System.Reflection;
using System.Diagnostics.CodeAnalysis;

using Composestar.StarLight.Entities.WeaveSpec.Instructions;
using Composestar.StarLight.Entities.Configuration;
#endregion

namespace Composestar.StarLight.Entities.WeaveSpec
{
	/// <summary>
	/// A weave specification file.
	/// </summary>
	[Serializable]
	[XmlRoot("WeaveSpecification", Namespace = Constants.NS)]
	public class WeaveSpecification
	{
		private string _assemblyName;
		private List<WeaveType> _weaveTypes;
		private List<FilterCode> _generalizedFilterCodes;
        private List<ConflictRuleElement> _conflictRules;

		/// <summary>
		/// Gets or sets the name of the assembly this weave file applies to.
		/// </summary>
		/// <value>The name of the assembly.</value>
		/// <returns>string</returns>
		[XmlAttribute]
		public string AssemblyName
		{
			get { return _assemblyName; }
			set { _assemblyName = value; }
		}

		/// <summary>
		/// Gets or sets the types to weave on.
		/// </summary>
		/// <value>The weave types.</value>
		[XmlArray("WeaveTypes")]
		[XmlArrayItem("WeaveType")]
		[SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
		public List<WeaveType> WeaveTypes
		{
			get { return _weaveTypes; }
			set { _weaveTypes = value; }
		}

		/// <summary>
		/// Gets the version.
		/// </summary>
		/// <value>The version.</value>
		[XmlAttribute]
		public static string Version
		{
			get { return Assembly.GetExecutingAssembly().GetName().Version.ToString(); }
		}

		/// <summary>
		/// Gets or sets the generalized abstract instruction models, referenced by WeaveMethod and WeaveCall
		/// </summary>
		/// <value>The generalized abstract instruction models</value>
		[XmlArray("GeneralizedFilterCodes")]
		[XmlArrayItem("GeneralizedFilterCode")]
		[SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
		public List<FilterCode> GeneralizedFilterCodes
		{
			get { return _generalizedFilterCodes; }
			set { _generalizedFilterCodes = value; }
		}

        /// <summary>
        /// Gets or sets the conflict rules
        /// </summary>
        /// <value>The generalized abstract instruction models</value>
        [XmlArray("ConflictRules")]
        [XmlArrayItem("ConflictRule")]
        [SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
        public List<ConflictRuleElement> ConflictRules
        {
            get { return _conflictRules; }
            set { _conflictRules = value; }
        }
	}
}
