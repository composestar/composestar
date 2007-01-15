#region License
/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * ComposeStar StarLight [http://janus.cs.utwente.nl:8000/twiki/bin/view/StarLight/WebHome]
 * Copyright (C) 2003, University of Twente.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification,are permitted provided that the following conditions
 * are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Twente nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
*/
#endregion

#region Using directives
using System;
using System.Text;
using System.Collections.Generic;
using System.Xml.Serialization;

using Composestar.StarLight.TypeAnalyzer.Cps;
using Composestar.StarLight.TypeAnalyzer.CSharp;
using Composestar.StarLight.TypeAnalyzer.JSharp;
using antlr.collections;
using Composestar.StarLight.Entities.LanguageModel;
#endregion

namespace Composestar.StarLight.TypeAnalyzer
{
	public abstract class SourceAnalyzer
	{
		public abstract List<TypeElement> Analyze(AssemblyContext ac, List<string> sources);

		protected List<TypeElement> CreateTypeElements(NamespaceContext context, List<DefinedType> types)
		{
			List<TypeElement> result = new List<TypeElement>();
			foreach (DefinedType dt in types)
			{
				TypeElement te = new TypeElement();
				te.FromSource = context.FileName;
				te.Name = dt.Name;
				te.Namespace = context.Namespace;
				te.BaseType = ResolveBaseType(context, dt.BaseType);
				te.Interfaces = ResolveFullNames(context, dt.Interfaces);
				te.Methods = ProcessMethods(context, dt.Methods);
				te.Fields = ProcessFields(context, dt.Fields);
				te.IsClass = dt.IsClass;
				te.IsInterface = dt.IsInterface;
				te.IsAbstract = dt.IsAbstract;
				te.IsSealed = dt.IsSealed;
				te.IsPublic = dt.IsPublic;
				result.Add(te);

				// set method signatures
				foreach (MethodElement method in te.Methods)
					method.Signature = MethodElement.GenerateSignature(te.FullName, method);

				ResolveReferencedTypes(context, dt.ReferencedTypes);
				CreateTypeElements(context, dt.DefinedTypes);
			}
			return result;
		}

		private List<FieldElement> ProcessFields(NamespaceContext context, List<FieldElement> fields)
		{
			foreach (FieldElement field in fields)
				field.Type = ResolveFullName(context, field.Type);

			return fields;
		}

		private List<MethodElement> ProcessMethods(NamespaceContext context, List<MethodElement> methods)
		{
			foreach (MethodElement method in methods)
			{
				method.ReturnType = ResolveFullName(context, method.ReturnType);
				method.Parameters = ProcessParameters(context, method.Parameters);
			}
			return methods;
		}

		private List<ParameterElement> ProcessParameters(NamespaceContext context, List<ParameterElement> parameters)
		{
			foreach (ParameterElement param in parameters)
				param.Type = ResolveFullName(context, param.Type);

			return parameters;
		}

		private void ResolveReferencedTypes(NamespaceContext context, List<string> partialNames)
		{
			foreach (string partialName in partialNames)
			{
				string fullName = context.ResolveFullName(partialName);
				// ignore result for now
			}
		}

		private string ResolveBaseType(NamespaceContext context, string partialName)
		{
			if (partialName == null)
				return "System.Object";
			else
				return ResolveFullName(context, partialName);
		}

		private List<string> ResolveFullNames(NamespaceContext context, List<string> partialNames)
		{
			if (partialNames == null)
				throw new ArgumentNullException("partialNames");

			for (int i = 0; i < partialNames.Count; i++)
				partialNames[i] = ResolveFullName(context, partialNames[i]);

			return partialNames;
		}

		private string ResolveFullName(NamespaceContext context, string partialName)
		{
			if (context == null)
				throw new ArgumentNullException("context");

			if (partialName == null)
			{
				return "(null)";
				//throw new ArgumentNullException("partialName");
			}

			int dimension = 0;
			while (partialName.EndsWith("[]"))
			{
				partialName = partialName.Substring(0, partialName.Length - 2);
				dimension++;
			}

			string fullName = InnerResolveFullName(context, partialName);
			//	Console.WriteLine("InnerResolveFullName: {0} -> {1}", partialName, fullName);

			for (int i = 0; i < dimension; i++)
				fullName += "[]";

			return fullName;
		}

		private string InnerResolveFullName(NamespaceContext context, string partialName)
		{
			string fullName = GetBuiltInTypeName(partialName);
			if (fullName != null) return fullName;

			return context.ResolveFullName(partialName);
		}

		protected abstract string GetBuiltInTypeName(string partialName);
	}
}
