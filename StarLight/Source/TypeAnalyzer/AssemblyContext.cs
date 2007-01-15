using System;
using System.Text;
using System.Collections.Generic;
using Mono.Cecil;

namespace Composestar.StarLight.TypeAnalyzer
{
	public class AssemblyContext
	{
		private IList<ModuleDefinition> _modules;
		private IDictionary<string, TypeReference> _cache;

		public AssemblyContext(List<string> assemblies)
		{
			_modules = new List<ModuleDefinition>();
			_cache = new Dictionary<string, TypeReference>();

			foreach (string fn in assemblies)
			{
				AssemblyDefinition ad = AssemblyFactory.GetAssembly(fn);
				_modules.Add(ad.MainModule);
			}
		}

		private TypeReference ResolveType(string name)
		{
			TypeReference tr = null;
			if (!_cache.TryGetValue(name, out tr))
			{
				tr = InnerResolveType(name);
				_cache[name] = tr;
			}

			return tr;
		}

		private TypeReference InnerResolveType(string name)
		{
			foreach (ModuleDefinition module in _modules)
			{
				TypeReference tr = module.Types[name];
				if (tr != null) return tr;
			}

			// not found
			return null;
		}

		public bool ContainsType(string fullName)
		{
			return (ResolveType(fullName) != null);
		}
	}
}
