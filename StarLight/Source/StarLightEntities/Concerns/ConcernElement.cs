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
using System.IO;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
#endregion

namespace Composestar.StarLight.Entities.Concerns
{
	/// <summary>
	/// A concern specification.
	/// </summary>
	[Serializable]
	[XmlRoot("Concern", Namespace = "Entities.TYM.DotNET.Composestar")]
	public class ConcernElement
	{
		/// <summary>
		/// _file name
		/// </summary>
		private string _fileName;
		/// <summary>
		/// _concern name
		/// </summary>
		private string _concernName;
		/// <summary>
		/// _timestamp
		/// </summary>
		private long _timestamp;
		private string _pathName;
		private bool _hasOutputFilters;
		private List<String> _types = new List<string>();

		/// <summary>
		/// Gets or sets the types used in the internals or externals.
		/// </summary>
		/// <value>The types.</value>
		[XmlArray("Types")]
		[XmlArrayItem("Type")]
		public List<String> ReferencedTypes
		{
			get { return _types; }
			set { _types = value; }
		}

		/// <summary>
		/// Gets or sets the name of the file.
		/// </summary>
		/// <value>The name of the file.</value>
		[XmlAttribute]
		public string FileName
		{
			get { return _fileName; }
			set { _fileName = value; }
		}

		/// <summary>
		/// Gets or sets the name of the concern.
		/// </summary>
		/// <value>The name of the concern.</value>
		[XmlAttribute]
		public string ConcernName
		{
			get { return _concernName; }
			set { _concernName = value; }
		}

		/// <summary>
		/// Gets or sets the time stamp.
		/// </summary>
		/// <value>The time stamp.</value>
		[XmlAttribute]
		public long Timestamp
		{
			get { return _timestamp; }
			set { _timestamp = value; }
		}

		/// <summary>
		/// Gets or sets the name of the path.
		/// </summary>
		/// <value>The name of the path.</value>
		[XmlAttribute]
		public string PathName
		{
			get { return _pathName; }
			set { _pathName = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether this concern has output filters.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this concern has output filters; otherwise, <c>false</c>.
		/// </value>
		[XmlAttribute]
		public bool HasOutputFilters
		{
			get { return _hasOutputFilters; }
			set { _hasOutputFilters = value; }
		}

		/// <summary>
		/// Gets the full path of this concern.
		/// </summary>
		/// <value>The full path.</value>
		[XmlIgnore]
		public string FullPath
		{
			get { return Path.Combine(_pathName, _fileName); }
		}
	}
}
