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
using System.IO;
using System.Xml;
using System.Xml.Serialization;
using System.Globalization;

using Composestar.StarLight.Entities.LanguageModel;
#endregion

namespace Composestar.StarLight.Entities.Configuration
{
	/// <summary>
	/// Contains configuration about the assembly.
	/// </summary>
	[Serializable]
	[XmlType("AssemblyConfig", Namespace = Constants.NS)]
	public class AssemblyConfig
	{
		private AssemblyElement _assembly;
		private string _id;
		private string _name;
		private string _fileName;
		private long _timestamp;
		private bool _isReference;
		private string _typeSpec;
		private string _weaveSpec;
		private string _expandSpec;
		private PdbMode _debugFileMode = PdbMode.HideFilters;

		/// <summary>
		/// Gets or sets the assembly.
		/// </summary>
		/// <value>The assembly.</value>
		[XmlIgnore]
		public AssemblyElement Assembly
		{
			get { return _assembly; }
			set { _assembly = value; }
		}

		/// <summary>
		/// Gets or sets the unique identifier for this assembly.
		/// </summary>
		/// <value>The identifier.</value>
		[XmlAttribute]
		public string Id
		{
			get
			{
				if (_id == null)
				{
					string name = _name;
					name = name.Replace(", ", "_");
					name = name.Replace(".", "_");
					name = name.Replace("Version=", "");
					name = name.Replace("Culture=", "");
					name = name.Replace("PublicKeyToken=", "");
					_id = name;
				}
				return _id;
			}
			set { _id = value; }
		}

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
		/// Gets or sets the filename.
		/// </summary>
		/// <value>The filename.</value>
		[XmlAttribute]
		public string FileName
		{
			get { return _fileName; }
			set { _fileName = value; }
		}

		/// <summary>
		/// Gets or sets the timestamp.
		/// </summary>
		/// <value>The timestamp.</value>
		[XmlAttribute]
		public long Timestamp
		{
			get { return _timestamp; }
			set { _timestamp = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether this assembly is a reference.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this instance is reference; otherwise, <c>false</c>.
		/// </value>
		[XmlAttribute]
		public bool IsReference
		{
			get { return _isReference; }
			set { _isReference = value; }
		}

		/// <summary>
		/// Gets or sets the type specification file.
		/// </summary>
		/// <value>The filename of the type specification.</value>
		[XmlAttribute]
		public string TypeSpecificationFile
		{
			get { return _typeSpec; }
			set { _typeSpec = value; }
		}

		/// <summary>
		/// Gets or sets the weave specification file.
		/// </summary>
		/// <value>The filename of the weave specification.</value>
		[XmlAttribute]
		public string WeaveSpecificationFile
		{
			get { return _weaveSpec; }
			set { _weaveSpec = value; }
		}

		/// <summary>
		/// Gets or sets the expansion specification file.
		/// </summary>
		/// <value>The filename of the expansion specification.</value>
		[XmlAttribute]
		public string ExpansionSpecificationFile
		{
			get { return _expandSpec; }
			set { _expandSpec = value; }
		}

		/// <summary>
		/// Gets or sets the debug file mode.
		/// </summary>
		/// <value>The debug file mode.</value>
		[XmlAttribute]
		public PdbMode DebugFileMode
		{
			get { return _debugFileMode; }
			set { _debugFileMode = value; }
		}

		/// <summary>
		/// Generate a serialized filename to be used to store the file.
		/// </summary>
		public void GenerateTypeSpecificationFileName(string baseDir)
		{
			_typeSpec = Path.Combine(baseDir, string.Concat(Id, ".xml.gzip"));
		}

		/// <summary>
		/// Pdb debug file mode
		/// </summary>
		public enum PdbMode
		{
			/// <summary>
			/// Perform no actions on the PDB file.
			/// </summary>
			None,
			/// <summary>
			/// Hide the injected filter code for the developer.
			/// </summary>
			HideFilters,
		}
	}
}
