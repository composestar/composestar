using System;
using System.Collections.Generic;
using System.Text;

using Mono.Cecil;

namespace Composestar.StarLight.TypeAnalyzer
{
	public class SourceTypeResolver
	{
		private AssemblyContext _rc;
		private NamespaceContext _nc;

		public SourceTypeResolver(AssemblyContext rc)
		{
			_rc = rc;
			_nc = null;
		}

		public TypeReference Resolve(string qname)
		{
			return null;
		}

		public string GetFullName(string qname)
		{
			return qname;
		}
	}
}
