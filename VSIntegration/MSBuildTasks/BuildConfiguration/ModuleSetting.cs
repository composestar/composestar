using System;

namespace Composestar.StarLight.MSBuild.Tasks.BuildConfiguration
{
	/// <summary>
	/// Summary description for ModuleSetting.
	/// </summary>
	public class ModuleSetting
	{
		public ModuleSetting()
		{
			
		}

		private string _name = "";

		public string Name
		{
			get
			{
				return _name;
			}
			set
			{
				_name = value;
			}
		}

		private System.Collections.Specialized.NameValueCollection   _elements = new System.Collections.Specialized.NameValueCollection   ();

		public System.Collections.Specialized.NameValueCollection  Elements
		{
			get
			{
				return _elements;
			}
			set
			{
				_elements = value;
			}
		}

	}
}
