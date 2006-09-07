using System;
using System.Collections;
using System.Xml.XPath;

namespace Weavers.WeaveSpecifications
{
	/// <summary>
	/// Class to store the information for an attribute definition from the weave specifications.
	/// </summary>
	public class AttributeModificationInformation
	{
		// Define private variables
		private string mAttributeName = "";
		private ArrayList mArguments;

		// Constructor
		public AttributeModificationInformation()
		{
			this.mArguments = new ArrayList();
		}

		// Readonly property AssemblyName
		public string AttibuteNamae
		{
			get 
			{
				return mAttributeName;
			}
			set
			{
				mAttributeName = value;
			}
		}

		public void AddParameter(string type, object val)
		{
			this.mArguments.Add(new DictionaryEntry(type, val));
		}

		/// <summary>
		/// Returns the enumerator for the parameter list of this attribute.
		/// </summary>
		/// <returns>Enumerator for the arguments.</returns>
		public IEnumerator GetArgumentEnumerator()
		{
			return this.mArguments.GetEnumerator();
		}


	}
}
