using System;

namespace ComposeStar.MSBuild.Tasks.BuildConfiguration
{
	/// <summary>
	/// Summary description for Settings.
	/// </summary>
	public class Settings
	{
		public Settings()
		{
			
		}

        private static System.Collections.Hashtable _modules = new System.Collections.Hashtable();
		public static System.Collections.Hashtable  Modules
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

        private static System.Collections.ArrayList _customFilters = new System.Collections.ArrayList();
        public static System.Collections.ArrayList CustomFilters
        {
            get
            {
                return _customFilters;
            }
            set
            {
                _customFilters = value;
            }
        }

        private static System.Collections.Specialized.NameValueCollection _paths = new System.Collections.Specialized.NameValueCollection();
        public static System.Collections.Specialized.NameValueCollection Paths
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

        private static string _composestarIni = "";

        public static string ComposestarIni
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
        private static DebugModes _buildDebugLevel = DebugModes.NotSet;
        public static DebugModes BuildDebugLevel
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

        private static DebugModes _runDebugLevel = DebugModes.NotSet;
        public static DebugModes RunDebugLevel
        {
            get
            {
                return _runDebugLevel;
            }
            set
            {
                _runDebugLevel = value;
            }
        }

        private static string _platform = "dotNET";
        public static string Platform
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
        public static void SetModule(ModuleSetting ms)
		{
			if (ms == null)
				return;
			
			if (_modules.ContainsKey(ms.Name) )
			{
				ModuleSetting msExisting = (ModuleSetting)_modules[ms.Name];
				// Update or add the elements
				foreach (String s in ms.Elements.AllKeys  )
				{
					if (msExisting.Elements[s] == null )
						msExisting.Elements.Add(s, ms.Elements[s] );
					else
						msExisting.Elements[s] = ms.Elements[s];
					
				}
			}
			else
				_modules.Add(ms.Name, ms);
			
		}

        public static ModuleSetting GetModule(string name)
		{
			if (_modules.ContainsKey(name) )
				return (ModuleSetting)_modules[name];
			else
				return null;
		}

	}
}
