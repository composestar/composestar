using System;
using System.Collections;

namespace Weavers.IlStructures
{
	/// <summary>
	/// Summary description for ExternalAssemblyBlock.
	/// </summary>
	public class ExternalAssemblyBlock : IlStructure
	{
		private string mAssemblyName;
		private string mPublicKeyToken = "";
		private string mVersion = "";

		public ExternalAssemblyBlock(string name)
		{
			mAssemblyName = name;
		}

		public string Name
		{
			get
			{
				return mAssemblyName;
			}
		}

		public string PublicKeyToken
		{
			get
			{
				return mPublicKeyToken;
			}
			set
			{
				mPublicKeyToken = value;
			}
		}

		public string Version
		{
			get
			{
				return mVersion;
			}
			set
			{
				mVersion = value;
			}
		}

		public override ArrayList ToStringList()
		{
			ArrayList result = new ArrayList(5);
			
			result.Add(".assembly extern " + this.Name);
			result.Add("{");
			if (this.PublicKeyToken != "") result.Add("  .publickeytoken = " + this.PublicKeyToken);
			if (this.Version != "")result.Add("  .ver " + this.Version);
			result.AddRange(this.GetAdditionalCodeLines());
			result.Add("}");

			return result;
		}

	}

}
