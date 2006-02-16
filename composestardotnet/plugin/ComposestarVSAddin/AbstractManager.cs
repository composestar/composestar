using EnvDTE;
using Ini;
using System;

namespace ComposestarVSAddin
{
	/// <summary>
	/// Summary description for AbstractManager.
	/// </summary>
	public abstract class AbstractManager
	{
		
		private IniFile configFile = null;
		protected _DTE mApplicationObject = null;

		public AbstractManager(IniFile inifile) 
		{
			configFile = inifile;
		}

		public abstract void run(_DTE applicationObject, vsBuildScope scope, vsBuildAction action);

		protected void writeIniValue(string Section, string Key, string Value)
		{
			configFile.IniWriteValue(Section, Key, Value);
		}

		protected string readIniValue(string Section, string Key)
		{
			return configFile.IniReadValue(Section, Key);
		}

		protected string[] readIniSection(string Section)
		{
			return configFile.IniReadSection(Section);
		}

		protected Object getProperty(Properties properties, string propertyName)
		{
			if (properties.Item(propertyName) != null)
			{
				return properties.Item(propertyName).Value;
			}

			return null;
		}

		
	}
}
