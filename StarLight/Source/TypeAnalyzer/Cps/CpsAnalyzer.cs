using System;
using System.Text;
using System.Collections.Generic;
using System.Xml.Serialization;
using Composestar.StarLight.Entities.LanguageModel;

namespace Composestar.StarLight.TypeAnalyzer.Cps
{
	[XmlRoot("CpsSourceInfo", Namespace = "Composestar.StarLight.SourceAnalyzer")]
	public class CpsSourceInfo
	{
		private List<string> _referencedTypes = new List<string>();
		private List<string> _embeddedSources = new List<string>();
		private bool _hasOutputFilters;

		public CpsSourceInfo()
		{
		}

		[XmlArray]
		public List<string> ReferencedTypes
		{
			get { return _referencedTypes; }
			set { _referencedTypes = value; }
		}

		[XmlArray]
		public List<string> EmbeddedSources
		{
			get { return _embeddedSources; }
			set { _embeddedSources = value; }
		}

		[XmlAttribute]
		public bool HasOutputFilters
		{
			get { return _hasOutputFilters; }
			set { _hasOutputFilters = value; }
		}
	}
}
