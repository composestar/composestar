using System;

namespace Weavers.WeaveSpecifications
{
	public class CastInterceptionInformation
	{
		private String mAssemblyName = "";
		private String mClassName = "";
		private String mExecuteMethodBefore = "";

		public CastInterceptionInformation (String assemblyName, String className, String executeMethodBefore)
		{
			this.mAssemblyName = assemblyName;
			this.mClassName = className;
			this.mExecuteMethodBefore = executeMethodBefore;
		}

		public String AssemblyName 
		{
			get 
			{
				return this.mAssemblyName;
			}
		}

		public String ClassName
		{
			get 
			{
				return this.mClassName;
			}
		}

		public String ExecuteMethodBefore
		{
			get 
			{
				return this.mExecuteMethodBefore;
			}
		}
	}

}
