using System;

namespace BuildConfiguration
{
	/// <summary>
	/// Summary description for Project.
	/// </summary>
	public class Project
	{
		public Project()
		{

		}

		private string _name="";

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
		private string _language="";

		public string Language
		{
			get
			{
				return _language;
			}
			set
			{
				_language = value;
			}
		}
		private string _buildPath="";

		public string BuildPath
		{
			get
			{
				return _buildPath;
			}
			set
			{
				_buildPath = value;
			}
		}
	
		private string _outputPath="";

		public string OutputPath
		{
			get
			{
				return _outputPath;
			}
			set
			{
				_outputPath = value;
			}
		}
		private string _basePath = "";

		public string BasePath
		{
			get
			{
				return _basePath;
			}
			set
			{
				_basePath = value;
			}
		}
	

		private System.Collections.ArrayList _sources = new System.Collections.ArrayList ();
		public System.Collections.ArrayList Sources
		{
			get
			{
				return _sources;
			}
			set
			{
				_sources = value;
			}
		}

		private System.Collections.ArrayList _dependencies = new System.Collections.ArrayList ();
		public System.Collections.ArrayList Dependencies
		{
			get
			{
				return _dependencies;
			}
			set
			{
				_dependencies = value;
			}
		}

		private System.Collections.ArrayList _typeSources = new System.Collections.ArrayList ();
		public System.Collections.ArrayList TypeSources
		{
			get
			{
				return _typeSources;
			}
			set
			{
				_typeSources = value;
			}
		}

 
	}
}
