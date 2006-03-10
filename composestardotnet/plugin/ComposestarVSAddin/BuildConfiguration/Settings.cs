using System;

namespace BuildConfiguration
{
	/// <summary>
	/// Summary description for Settings.
	/// </summary>
	public class Settings
	{
		public Settings()
		{
			
		}

		private System.Collections.Hashtable  _modules = new System.Collections.Hashtable ();
		public System.Collections.Hashtable  Modules
		{
			get
			{
				return _modules;
			}
			set
			{
				_modules = value;
			}
		}


		private System.Collections.Specialized.NameValueCollection  _paths = new System.Collections.Specialized.NameValueCollection ();
		public System.Collections.Specialized.NameValueCollection Paths
		{
			get
			{
				return _paths;
			}
			set
			{
				_paths = value;
			}
		}

		private string _composestarIni = "";

		public string ComposestarIni
		{
			get
			{
				return _composestarIni;
			}
			set
			{
				_composestarIni = value;
			}
		}
		private ComposestarVSAddin.DebugModes _buildDebugLevel = ComposestarVSAddin.DebugModes.NotSet   ;
		public ComposestarVSAddin.DebugModes BuildDebugLevel
		{
			get
			{
				return _buildDebugLevel;
			}
			set
			{
				_buildDebugLevel = value;
			}
		}

		private string _compilePhase = "one";
		public string CompilePhase
		{
			get
			{
				return _compilePhase;
			}
			set
			{
				_compilePhase = value;
			}
		}

		private string _platform = "dotNET";
		public string Platform
		{
			get
			{
				return _platform;
			}
			set
			{
				_platform = value;
			}
		}

		/// <summary>
		/// Update or add a module.
		/// </summary>
		/// <param name="ms"></param>
		public void SetModule(ModuleSetting ms)
		{
			if (ms == null)
				return;
			
			if (_modules.ContainsKey(ms.Name) )
				_modules[ms.Name] = ms;
			else
				_modules.Add(ms.Name, ms);
			
		}

		public ModuleSetting GetModule(string name)
		{
			if (_modules.ContainsKey(name) )
				return (ModuleSetting)_modules[name];
			else
				return null;
		}

	}
}
