using System;

namespace ComposestarVSAddin
{
	/// <remarks>
	/// Attribute used to indicate if a property should be configured
	/// by the Configurator object.  It is valid only on Properties and
	/// only one attribute is allowed per property.
	/// </remarks>
	[AttributeUsage(AttributeTargets.Property, AllowMultiple = false)]
	public class IniSettingFieldAttribute : Attribute
	{

		// Internal key value
		private string _key = "";

		/// <value>Name of the key</value>
		public string Key
		{
			get { return this._key; }
			set { this._key = value; }
		}

		// Internal section value
		private string _section = "";

		/// <value>Name of the section field</value>
		public string Section
		{
			get { return this._section; }
			set { this._section = value; }
		}

		// Internal InConfiguration value
		private bool _InConfiguration = false;
		/// <value>Indicator if the data field is in
		///        configuration file</value>
		public bool InConfiguration
		{
			get { return this._InConfiguration; }
			set { this._InConfiguration = value; }
		}

		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="key">Name of the data field</param>
		/// <param name="section">Name of the section</param>
		/// <param name="inConfiguration">Indicator if data field in config</param>
		public IniSettingFieldAttribute(string key, string section, bool inConfiguration)
		{
			this._key = key;
			this._section = section;
			this._InConfiguration = inConfiguration;
		}

		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="key">Name of the data field</param>
		/// <param name="section">Name of the section</param>
		public IniSettingFieldAttribute(string key, string section)
		{
			this._key = key;
			this._section = section;
			this._InConfiguration = true;
		}

	}
}
