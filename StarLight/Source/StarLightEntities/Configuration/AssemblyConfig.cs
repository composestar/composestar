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
	[XmlRoot("AssemblyConfig", Namespace = "Entities.TYM.DotNET.Composestar")]
	public class AssemblyConfig
	{
		private string _name;

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

		private long _timestamp;

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

		private string _fileName;

		/// <summary>
		/// Gets or sets the filename.
		/// </summary>
		/// <value>The filename.</value>
		[XmlAttribute("Filename")]
		public string FileName
		{
			get { return _fileName; }
			set { _fileName = value; }
		}

		private string _serializedFileName = string.Empty;

		/// <summary>
		/// Gets or sets the serialized filename.
		/// </summary>
		/// <value>The serialized filename.</value>
		[XmlAttribute("SerializedFilename")]
		public string SerializedFileName
		{
			get { return _serializedFileName; }
			set { _serializedFileName = value; }
		}

		private AssemblyElement _assembly;

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
		/// Generate a serialized filename to be used to store the file.
		/// </summary>
		public void GenerateSerializedFileName(string objectFolder)
		{
			if (_assembly != null)
			{
				string name = _assembly.Name;
				name = name.Replace(", ", "_");
				name = name.Replace(".", "_");
				name = name.Replace("Version=", "");
				name = name.Replace("Culture=", "");
				name = name.Replace("PublicKeyToken=", "");
				_serializedFileName = Path.Combine(objectFolder, string.Format(CultureInfo.CurrentCulture, "{0}.xml.gzip", name));
			}
		}

		private String _weaveSpec;

		/// <summary>
		/// Gets or sets the weave specification file.
		/// </summary>
		/// <value>The weave specification file.</value>
		[XmlAttribute]
		public String WeaveSpecificationFile
		{
			get { return _weaveSpec; }
			set { _weaveSpec = value; }
		}

		private bool _isReference;

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

		private bool _isDummy;

		/// <summary>
		/// Gets or sets a value indicating whether this is a dummy assembly.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this is a dummy assembly; otherwise, <c>false</c>.
		/// </value>
		[XmlAttribute]
		public bool IsDummy
		{
			get { return _isDummy; }
			set { _isDummy = value; }
		}

		private  PdbMode _debugFileMode = PdbMode.HideFilters;

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
