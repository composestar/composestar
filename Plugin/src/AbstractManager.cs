using EnvDTE;
using Ini;
using System;

namespace Composestar.StarLight.VSAddin
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

		public abstract void Run(_DTE applicationObject, vsBuildScope scope, vsBuildAction action);
	
		protected Object GetProperty(Properties properties, string propertyName)
		{
			if (properties.Item(propertyName) != null)
			{
				return properties.Item(propertyName).Value;
			}

			return null;
		}

		
	}
}
