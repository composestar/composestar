using System;
using System.Collections;
using System.Xml.XPath;

namespace Weavers.WeaveSpecifications
{
	/// <summary>
	/// Class to store the information for an attribute definition from the weave specifications.
	/// </summary>
	public class AttributeInformation
	{
		// Define private variables
		private string mAssemblyName = "";
		private string mFullClassName = "";

		// Constructor
		public AttributeInformation(string AssemblyName, string FullClassName)
		{
			this.mAssemblyName = AssemblyName;
			this.mFullClassName = FullClassName;
		}

		// Readonly property AssemblyName
		public string AssemblyName
		{
			get 
			{
				return this.mAssemblyName;
			}
		}

		// Readonly property FullClassName
		public string FullClassName
		{
			get 
			{
				return this.mFullClassName;
			}
		}

	}
}
