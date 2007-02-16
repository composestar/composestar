using System;
using System.Collections;
using System.Xml.XPath;

namespace Weavers.WeaveSpecifications
{
	public class ApplicationInformation
	{
		private string mName = "";
		private string mEntrypoint = "";

		public ApplicationInformation(string Name)
		{
			this.mName = Name;
		}

		public string Name 
		{
			get 
			{
				return this.mName;
			}
		}

		public string EntrypointMethod
		{
			get 
			{
				return this.mEntrypoint;
			}
			set 
			{
				this.mEntrypoint = value;
			}
		}
	}
}