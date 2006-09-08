using System;

namespace BuildConfiguration
{
	/// <summary>
	/// Summary description for Platform.
	/// </summary>
	public class Platform
	{
		public Platform()
		{
			
		}

		private string _name = "dotNET";

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
		private string _mainClass = "";

		public string MainClass
		{
			get
			{
				return _mainClass;
			}
			set
			{
				_mainClass = value;
			}
		}
		private string _classPath = "";

		public string ClassPath
		{
			get
			{
				return _classPath;
			}
			set
			{
				_classPath = value;
			}
		}
		private string _options = "";
		public string Options
		{
			get
			{
				return _options;
			}
			set
			{
				_options = value;
			}
		}

		private System.Collections.ArrayList _requiredFiles = new System.Collections.ArrayList ();

		public System.Collections.ArrayList RequiredFiles
		{
			get
			{
				return _requiredFiles;
			}
			set
			{
				_requiredFiles = value;
			}
		}
 
	}
}
