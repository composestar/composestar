using System;
using System.Collections.Generic;
using System.Xml.Serialization;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.Entities;
using Composestar.StarLight.Entities.LanguageModel;

namespace Composestar.StarLight.TypeAnalyzer.JSharp
{
	[XmlRoot("CompilationUnit", Namespace = Constants.NS)]
	public class CompilationUnit : SourceInfo
	{
		private string _package;
		private List<string> _imports = new List<string>();
		private List<DefinedType> _definedTypes = new List<DefinedType>();

		public CompilationUnit()
		{
		}

		public CompilationUnit(string package, List<string> imports, List<DefinedType> types)
		{
			_package = package;
			_imports = imports;
			_definedTypes = types;
		}

		[XmlElement]
		public string Package
		{
			get { return _package; }
			set { _package = value; }
		}

		[XmlArray]
		public List<string> Imports
		{
			get { return _imports; }
			set { _imports = value; }
		}

		[XmlArray]
		public List<DefinedType> DefinedTypes
		{
			get { return _definedTypes; }
			set { _definedTypes = value; }
		}
	}
}
