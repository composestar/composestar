using System;
using System.Collections.Generic;
using System.Configuration;
using System.Text;

namespace BasicTests
{
	public enum MyEnumeration
	{
		None = 0,
		Kilobytes = 1024,
		Megabytes = 1048576,
		Gigabytes = 1073741824,
	}

	public class CustomAttributes : ConfigurationSection
	{
		[ConfigurationProperty("myProperty", IsRequired = false, DefaultValue = MyEnumeration.Gigabytes)]
		public MyEnumeration MyProperty
		{
			get { return (MyEnumeration) this["myProperty"]; }
			set { this["myProperty"] = value; }
		}
	
	}
}
