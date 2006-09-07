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
		
		protected _DTE mApplicationObject = null;

		public AbstractManager() 
		{
			
		}

		public abstract void run(_DTE applicationObject, vsBuildScope scope, vsBuildAction action);
	
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
