using System;
using System.Text;
using System.Collections.Generic;
using System.Xml.Serialization;
using System.Diagnostics.CodeAnalysis;

namespace Composestar.StarLight.Entities.LanguageModel
{
	[Serializable]
	[XmlRoot("ExpandedAssembly", Namespace = Constants.NS)]
	public class ExpandedAssembly
	{
		private string _name;
		private List<ExpandedType> _types = new List<ExpandedType>();

		[XmlAttribute]
		public string Name
		{
			get { return _name; }
			set { _name = value; }
		}

		[XmlArray("Types")]
		[XmlArrayItem("Type")]
		[SuppressMessage("Microsoft.Design", "CA1002:DoNotExposeGenericLists")]
		public List<ExpandedType> Types
		{
			get { return _types; }
			set { _types = value; }
		}
	}
}
