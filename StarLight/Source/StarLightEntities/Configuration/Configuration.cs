using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using System.Diagnostics.CodeAnalysis;
using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.LanguageModel;

namespace Composestar.StarLight.Entities.Configuration
{
	/// <summary>
	/// Contains all the settings for the current starlight project that is currenlty building.
	/// </summary>
	[Serializable]
	[XmlRoot("ConfigurationContainer", Namespace = "Entities.TYM.DotNET.Composestar")]
	public class ConfigurationContainer
	{
		private List<ConcernElement> _concerns = new List<ConcernElement>();
		private List<AssemblyConfig> _assemblies = new List<AssemblyConfig>();
		private List<KeyValueSetting> _settings = new List<KeyValueSetting>();
		private List<FilterTypeElement> _filterTypes = new List<FilterTypeElement>();
		private List<FilterActionElement> _filterActions = new List<FilterActionElement>();

		/// <summary>
		/// Gets the version.
		/// </summary>
		/// <value>The version.</value>
		[XmlAttribute]
		public string Version
		{
			get
			{
				return System.Reflection.Assembly.GetExecutingAssembly().GetName().Version.ToString();      
			}
			set { }
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
