using System;

namespace Weavers.WeaveSpecifications
{
	public class ReplacementInformation
	{
		private String mAssemblyName = "";
		private String mClassName = "";
		private String mReplacementAssemblyName = "";
		private String mReplacementClassName = "";

		public ReplacementInformation (String assemblyName, String className, String replacementAssemblyName, String replacementClassName)
		{
			this.mAssemblyName = assemblyName;
			this.mClassName = className;
			this.mReplacementAssemblyName = replacementAssemblyName;
			this.mReplacementClassName = replacementClassName;
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

		public String ReplacementAssemblyName 
		{
			get 
			{
				return this.mReplacementAssemblyName;
			}
		}

		public String ReplacementClassName 
		{
			get 
			{
				return this.mReplacementClassName;
			}
		}
	}

}
