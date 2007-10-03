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
using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.LanguageModel;
using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
#endregion

namespace Composestar.StarLight.Entities.Configuration
{
	/// <summary>
	/// Contains all the settings for the current starlight project that is currently building.
	/// </summary>
	[Serializable]
    [XmlRoot("ConfigurationContainer", Namespace = Constants.NS)]
	public class ConfigurationContainer
	{
		/// <summary>
		/// List of concerns.
		/// </summary>
		private List<ConcernElement> _concerns = new List<ConcernElement>();
		/// <summary>
		/// List of assembly configurations.
		/// </summary>
		private List<AssemblyConfig> _assemblies = new List<AssemblyConfig>();
		/// <summary>
		/// List of settings.
		/// </summary>
		private List<KeyValueSetting> _settings = new List<KeyValueSetting>();
		/// <summary>
		/// List of filter types.
		/// </summary>
		private List<FilterTypeElement> _filterTypes = new List<FilterTypeElement>();
		/// <summary>
		/// List of filter actions.
		/// </summary>
		private List<FilterActionElement> _filterActions = new List<FilterActionElement>();

		/// <summary>
		/// Gets the version.
		/// </summary>
		/// <value>The version.</value>
		[XmlAttribute]
		public static string Version
		{
			get { return System.Reflection.Assembly.GetExecutingAssembly().GetName().Version.ToString(); }
		}

		/// <summary>
		/// Gets or sets the concerns.
		/// </summary>
		/// <value>The concerns.</value>
		[XmlArray("Concerns")]
		[XmlArrayItem("Concern")]
		[SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
		public List<ConcernElement> Concerns
		{
			get { return _concerns; }
			set { _concerns = value; }
		}

		/// <summary>
		/// Gets or sets the assemblies.
		/// </summary>
		/// <value>The assemblies.</value>
		[XmlArray("Assemblies")]
		[XmlArrayItem("AssemblyConfig")]
		[SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
		public List<AssemblyConfig> Assemblies
		{
			get { return _assemblies; }
			set { _assemblies = value; }
		}

		/// <summary>
		/// Gets or sets the settings.
		/// </summary>
		/// <value>The settings.</value>
		[XmlArray("Settings")]
		[XmlArrayItem("Setting")]
		[SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
		public List<KeyValueSetting> Settings
		{
			get { return _settings; }
			set { _settings = value; }
		}

		/// <summary>
		/// Gets or sets the filter types.
		/// </summary>
		/// <value>The filter types.</value>
		[XmlArray("FilterTypes")]
		[XmlArrayItem("FilterType")]
		[SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
		public List<FilterTypeElement> FilterTypes
		{
			get { return _filterTypes; }
			set { _filterTypes = value; }
		}

		/// <summary>
		/// Gets or sets the filter actions.
		/// </summary>
		/// <value>The filter actions.</value>
		[XmlArray("FilterActions")]
		[XmlArrayItem("FilterAction")]
		[SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
		public List<FilterActionElement> FilterActions
		{
			get { return _filterActions; }
			set { _filterActions = value; }
		}

		/// <summary>
		/// Adds a setting to the list.
		/// </summary>
		/// <param name="setting">The setting.</param>
		public void AddSetting(KeyValueSetting setting)
		{
			if (setting == null)
				throw new ArgumentNullException("setting");

			_settings.Add(setting); 
		}

		/// <summary>
		/// Create a new setting and add it to the list.
		/// </summary>
		/// <param name="key">The key.</param>
		/// <param name="value">The value.</param>
		public void AddSetting(string key, string value)
		{
			if (string.IsNullOrEmpty(key))
				throw new ArgumentNullException("key");

			AddSetting(new KeyValueSetting(key, value)); 
		}
	}
}
