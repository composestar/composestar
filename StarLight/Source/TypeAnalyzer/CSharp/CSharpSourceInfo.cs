using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using Composestar.StarLight.Entities;

namespace Composestar.StarLight.TypeAnalyzer.CSharp
{
	[XmlRoot("CompilationUnit", Namespace = Constants.NS)]
	public class CompilationUnit : SourceInfo
	{
		private Namespace _root;

		public CompilationUnit()
		{
		}

		[XmlElement]
		public Namespace Root
		{
			get { return _root; }
			set { _root = value; }
		}
	}

	[XmlType("Namespace", Namespace = Constants.NS)]
	public class Namespace
	{
		private string _name;
		private List<string> _imports = new List<string>();
		private List<Namespace> _namespaces = new List<Namespace>();
		private List<DefinedType> _definedTypes = new List<DefinedType>();
		private List<string> _referencedTypes = new List<string>();

		public Namespace()
		{
		}

		public Namespace(string name)
		{
			_name = name;
		}

		[XmlAttribute]
		public string Name
		{
			get { return _name; }
			set { _name = value; }
		}

		[XmlArray]
		public List<string> Imports
		{
			get { return _imports; }
			set { _imports = value; }
		}

		[XmlArray]
		public List<Namespace> Namespaces
		{
			get { return _namespaces; }
			set { _namespaces = value; }
		}

		[XmlArray]
		public List<DefinedType> DefinedTypes
		{
			get { return _definedTypes; }
			set { _definedTypes = value; }
		}

		[XmlArray]
		public List<string> ReferencedTypes
		{
			get { return _referencedTypes; }
			set { _referencedTypes = value; }
		}

		public Namespace AddNamespace(string name)
		{
			Namespace ns = new Namespace(name);
			_namespaces.Add(ns);
			return ns;
		}
	}
}
