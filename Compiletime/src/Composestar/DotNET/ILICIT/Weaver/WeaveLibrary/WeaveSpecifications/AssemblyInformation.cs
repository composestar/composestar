using System;
using System.Collections;
using System.Xml.XPath;

namespace Weavers.WeaveSpecifications
{
	/// <summary>
	/// Class for processing and storing the weave specifications file.
	/// </summary>
	public class AssemblyInformation
	{
		// Define private variables
		private string mAssemblyName = "";
		private string mVersion = "";
		private string mPublicKeytoken = "";
		private string mForceReferenceIn = "";
		private bool mRemove = false;

		// Constructor
		public AssemblyInformation(string Name)
		{
			this.mAssemblyName = Name;
		}

		// Constructor
		public AssemblyInformation(string Name, string Version)
		{
			this.mAssemblyName = Name;
			this.mVersion = Version;
		}

		// Constructor
		public AssemblyInformation(string Name, string Version, string PublicKeytoken)
		{
			this.mAssemblyName = Name;
			this.mVersion = Version;
			this.mPublicKeytoken = PublicKeytoken;
		}

		// Readonly property Name
		public string Name
		{
			get 
			{
				return this.mAssemblyName;
			}
		}

		// Readonly property Version
		public string Version
		{
			get 
			{
				return this.mVersion;
			}
		}

		// Readonly property PublicKeytoken
		public string PublicKeytoken
		{
			get 
			{
				return this.mPublicKeytoken;
			}
		}

		public string ForceReferenceIn
		{
			get 
			{
				return this.mForceReferenceIn;
			}
			set 
			{
				this.mForceReferenceIn = value;
			}
		}

		public bool Remove 
		{
			get 
			{
				return this.mRemove;
			}
			set 
			{
				this.mRemove = value;
			}
		}
	}
}