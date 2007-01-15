#region Using directives
using System;
using System.Xml.Serialization;
using System.Collections.Generic;

using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities;
using Composestar.StarLight.CoreServices;
#endregion

namespace Composestar.StarLight.TypeAnalyzer
{
	[XmlType("Type", Namespace = Constants.NS)]
	public class DefinedType
	{
		private string _name;
		private string _baseType;
		private List<string> _interfaces = new List<string>();
		private List<MethodElement> _methods = new List<MethodElement>();
		private List<FieldElement> _fields = new List<FieldElement>();
		private List<DefinedType> _definedTypes = new List<DefinedType>();
		private Set<string> _referencedTypes = new Set<string>();
		private bool _isClass;
		private bool _isInterface;
		private bool _isAbstract;
		private bool _isSealed;
		private bool _isPublic;
		private int _endPos;

		[XmlAttribute]
		public string Name
		{
			get { return _name; }
			set { _name = value; }
		}

		[XmlAttribute]
		public string BaseType
		{
			get { return _baseType; }
			set { _baseType = value; }
		}

		[XmlArray]
		public List<string> Interfaces
		{
			get { return _interfaces; }
			set { _interfaces = value; }
		}

		[XmlArray]
		public List<MethodElement> Methods
		{
			get { return _methods; }
			set { _methods = value; }
		}

		[XmlArray]
		public List<FieldElement> Fields
		{
			get { return _fields; }
			set { _fields = value; }
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
			get { return new List<string>(_referencedTypes); }
			set { _referencedTypes = new Set<string>(value); }
		}

		[XmlAttribute]
		public bool IsClass
		{
			get { return _isClass; }
			set { _isClass = value; }
		}

		[XmlAttribute]
		public bool IsInterface
		{
			get { return _isInterface; }
			set { _isInterface = value; }
		}

		[XmlAttribute]
		public bool IsSealed
		{
			get { return _isSealed; }
			set { _isSealed = value; }
		}

		[XmlAttribute]
		public bool IsAbstract
		{
			get { return _isAbstract; }
			set { _isAbstract = value; }
		}

		[XmlAttribute]
		public bool IsPublic
		{
			get { return _isPublic; }
			set { _isPublic = value; }
		}

		[XmlAttribute]
		public int EndPos
		{
			get { return _endPos; }
			set { _endPos = value; }
		}

		public void AddReferencedType(string name)
		{
			if (name != null)
			{
				name = name.TrimEnd('[', ']');
				_referencedTypes.Add(name);
			}
		}
	}
	
	[XmlRoot("AnalyzerResults", Namespace = Constants.NS)]
	public class AnalyzerResults
	{/*
		private List<SourceInfo> _sources = new List<SourceInfo>();

		public AnalyzerResults()
		{
		}

		[XmlArray("Sources")]
		[XmlArrayItem(typeof(CpsSourceInfo))]
		[XmlArrayItem(typeof(CSharpSourceInfo))]
		[XmlArrayItem(typeof(CompilationUnit))]
		public List<SourceInfo> Sources
		{
			get { return _sources; }
			set { _sources = value; }
		}*/
	}
	
	public abstract class SourceInfo
	{
		private string _source;
		private long _timestamp;

		public SourceInfo()
		{
		}

		[XmlAttribute]
		public string FileName
		{
			get { return _source; }
			set { _source = value; }
		}

		[XmlAttribute]
		public long Timestamp
		{
			get { return _timestamp; }
			set { _timestamp = value; }
		}
		
	//	public abstract List<TypeElement> DefinedTypes { get; set; }
	}
}
