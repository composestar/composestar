using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using System.Diagnostics.CodeAnalysis;

namespace Composestar.StarLight.Entities.Configuration
{
	/// <summary>
	/// Contains a key and value to store settings
	/// </summary>
	[Serializable]
	[XmlRoot("Setting", Namespace = "Entities.TYM.DotNET.Composestar")]
	public class KeyValueSetting
	{

		/// <summary>
		/// Initializes a new instance of the <see cref="T:KeyValueSetting"/> class.
		/// </summary>
		public KeyValueSetting()
		{

		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:KeyValueSetting"/> class.
		/// </summary>
		/// <param name="key">The key.</param>
		/// <param name="value">The value.</param>
		public KeyValueSetting(string key, string value)
		{
			_key = key;
			_value = value;
		}

		private string _key;
		private string _value;

		/// <summary>
		/// Gets or sets the key.
		/// </summary>
		/// <value>The key.</value>
		[XmlAttribute]
		public string Key
		{
			get
			{
				return _key;
			}
			set
			{
				_key = value;
			}
		}

		/// <summary>
		/// Gets or sets the value.
		/// </summary>
		/// <value>The value.</value>
		[XmlAttribute]
		public string Value
		{
			get
			{
				return _value;
			}
			set
			{
				_value = value;
			}
		}
		
	}
}
