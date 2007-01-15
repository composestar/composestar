using System;
using System.Text;
using System.Collections.Generic;
using Composestar.StarLight.CoreServices;

namespace Composestar.StarLight.TypeAnalyzer
{
	public class NamespaceContext
	{
		private AssemblyContext _ac;
		private string _filename;
		private string _namespace;
		private List<string> _namespaceImports = new List<string>();
		private Dictionary<string, string> _specificImports = new Dictionary<string, string>();
		private Set<string> _sourceTypes = new Set<string>();
		private Dictionary<string, string> _cache = new Dictionary<string, string>();

		public NamespaceContext(AssemblyContext rc, string filename, string ns, Set<string> types)
		{
			_ac = rc;
			_filename = filename;
			_namespace = ns;
			_sourceTypes = types;
		}

		public NamespaceContext(NamespaceContext parent, string ns)
		{
			_ac = parent._ac;
			_filename = parent._filename;
			_namespace = parent._namespace + "." + ns;
			_namespaceImports = new List<string>(parent._namespaceImports);
			_specificImports = new Dictionary<string, string>(parent._specificImports);
			_sourceTypes = parent._sourceTypes;
		}

		public string FileName
		{
			get { return _filename; }
		}

		public string Namespace
		{
			get { return _namespace; }
		}

		public void AddNamespaceImport(string qname)
		{
		//	Console.WriteLine("AddNamespaceImport: {0}", qname);
		
			_namespaceImports.Add(qname);
		}

		public void AddSpecificImport(string alias, string qname)
		{
		//	Console.WriteLine("AddSpecificImport:  {0} -> {1}", alias, qname);

			_specificImports[alias] = qname;
		}

		public void AddSpecificImport(string qname)
		{
			int dot = qname.LastIndexOf('.');
			if (dot == -1) throw new ArgumentException(qname);

			string alias = qname.Substring(dot + 1);
			AddSpecificImport(alias, qname);
		}

		public string ResolveFullName(string partialName)
		{
			string fullName;
			if (_cache.TryGetValue(partialName, out fullName))
				return fullName;
			else
			{
				fullName = InternalResolveFullName(partialName);
				_cache[partialName] = fullName;
				return fullName;
			}
		}

		private string InternalResolveFullName(string partialName)
		{
			// try specific imports
			{
				string fullName;
				if (_specificImports.TryGetValue(partialName, out fullName))
					return fullName;
			}

			// try as full name
			if (IsExistingType(partialName))
				return partialName;

			// try this namespace
			{
				string fullName = _namespace + "." + partialName;
				if (IsExistingType(fullName))
					return fullName;
			}

			// try imported namespaces
			foreach (string ns in _namespaceImports)
			{
				string fullName = ns + "." + partialName;
				if (IsExistingType(fullName))
					return fullName;
			}

			Console.WriteLine("! Unable to resolve {0} !", partialName);
			return "@" + partialName;
		}

		private bool IsExistingType(string fullName)
		{
			if (_sourceTypes.Contains(fullName))
				return true;

			if (_ac.ContainsType(fullName))
				return true;

			return false;
		}
	}
}
