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

#region using directives
using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Xml;
using System.Xml.Serialization;
using System.Threading;
using System.Diagnostics.CodeAnalysis;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.WeaveSpec;
using Composestar.StarLight.Entities.WeaveSpec.Instructions;
using Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions;
#endregion

namespace Composestar.Repository
{
	/// <summary>
	/// Functionality to read and store the entities.
	/// </summary>
	public sealed class EntitiesAccessor : IEntitiesAccessor
	{

		#region Singleton Instance
		private static readonly EntitiesAccessor m_Instance = new EntitiesAccessor();

		// Explicit static constructor to tell C# compiler
		// not to mark type as beforefieldinit
		[SuppressMessage("Microsoft.Performance", "CA1810:InitializeReferenceTypeStaticFieldsInline", Justification = "Needed for singleton.", Scope = "Member", Target = "Composestar.Repository.EntitiesAccessor..cctor()")]
		static EntitiesAccessor()
		{

		}

		/// <summary>
		/// Initializes a new instance of the <see cref="T:EntitiesAccessor"/> class.
		/// </summary>
		private EntitiesAccessor()
		{
		}

		/// <summary>
		/// Gets the instance.
		/// </summary>
		/// <value>The instance.</value>
		public static EntitiesAccessor Instance
		{
			get
			{
				return m_Instance;
			}
		}

		#endregion

		private Dictionary<string, AssemblyElement> _assemblyFileCache = new Dictionary<string, AssemblyElement>();
	
		#region Configuration

		/// <summary>
		/// Loads the configuration.
		/// </summary>
		/// <param name="fileName">The fileName.</param>
		/// <returns></returns>
		public ConfigurationContainer LoadConfiguration(string fileName)
		{
			if (string.IsNullOrEmpty(fileName))
				throw new ArgumentNullException("fileName");

			ConfigurationContainer configContainer;

			if (File.Exists(fileName))
			{
				configContainer = ObjectXMLSerializer<ConfigurationContainer>.Load(fileName, SerializedFormat.Document);
			} 
			else
			{
				configContainer = new ConfigurationContainer();
			} 


			return configContainer;
		}


		/// <summary>
		/// Saves the configuration.
		/// </summary>
		/// <param name="fileName">The fileName.</param>
		/// <param name="configContainer">The configuration container.</param>
		/// <returns></returns>
		public bool SaveConfiguration(string fileName, ConfigurationContainer configContainer)
		{
			if (configContainer == null)
				throw new ArgumentNullException("configContainer");

			if (string.IsNullOrEmpty(fileName))
				throw new ArgumentNullException("fileName");

			ObjectXMLSerializer<ConfigurationContainer>.Save(configContainer, fileName, SerializedFormat.Document);

			return true;
		}

		#endregion

		#region Assemblies

		/// <summary>
		/// Loads the assembly element.
		/// </summary>
		/// <param name="fileName">The fileName.</param>
		/// <returns></returns>
		public AssemblyElement LoadAssemblyElement(string fileName)
		{
			if (string.IsNullOrEmpty(fileName))
				throw new ArgumentNullException("fileName");

			AssemblyElement assemblyElement;
			if (!_assemblyFileCache.TryGetValue(fileName, out assemblyElement))
			{
				if (File.Exists(fileName))
				{
					assemblyElement = ObjectXMLSerializer<AssemblyElement>.Load(fileName, SerializedFormat.DocumentCompressed);
					_assemblyFileCache.Add(fileName, assemblyElement);
				} 
				else
				{
					throw new FileNotFoundException(fileName);
				} 
			} 

			return assemblyElement;
		}

		/// <summary>
		/// Saves the assembly element.
		/// </summary>
		/// <param name="fileName">The fileName.</param>
		/// <param name="assemblyElement">The assembly element.</param>
		/// <returns></returns>
		public bool SaveAssemblyElement(string fileName, AssemblyElement assemblyElement)
		{
			if (assemblyElement == null)
				throw new ArgumentNullException("assemblyElement");

			if (string.IsNullOrEmpty(fileName))
				throw new ArgumentNullException("fileName");

			ObjectXMLSerializer<AssemblyElement>.Save(assemblyElement, fileName, SerializedFormat.DocumentCompressed);

			// Update or add to cache
			_assemblyFileCache[fileName] = assemblyElement;

			return true;
		}

		#endregion

		#region Weave Specification

		/// <summary>
		/// Loads the weave specification.
		/// </summary>
		/// <param name="fileName">The fileName.</param>
		/// <returns>A <see cref="T:WeaveSpecification"></see> object or <see langword="null" /> if the file could not be found.</returns>
		public WeaveSpecification LoadWeaveSpecification(string fileName)
		{
			if (string.IsNullOrEmpty(fileName))
				throw new ArgumentNullException("fileName");

			WeaveSpecification weaveSpecification;

			if (File.Exists(fileName))
			{
				weaveSpecification = ObjectXMLSerializer<WeaveSpecification>.Load(fileName, SerializedFormat.DocumentCompressed, ExtraTypes);
			} // if
			else
			{
				return null;
			} // else

			return weaveSpecification;
		}

		private Type[] _extraTypes;

		/// <summary>
		/// Extra types
		/// </summary>
		private Type[] ExtraTypes
		{
			get
			{
				if (_extraTypes == null)
					_extraTypes = new Type[] { typeof(FilterAction), typeof(Block), typeof(Branch), typeof(CaseInstruction), typeof(JumpInstruction), typeof(SwitchInstruction), typeof(WhileInstruction), typeof(ContextInstruction),
                    typeof(AndCondition), typeof(ConditionExpression), typeof(ConditionLiteral), typeof(FalseCondition), typeof(NotCondition), typeof(OrCondition), typeof(TrueCondition) };
				return _extraTypes;
			}
		}

		/// <summary>
		/// Saves the weave specification.
		/// </summary>
		/// <param name="fileName">The fileName.</param>
		/// <param name="weaveSpecification">The weave specification.</param>
		/// <returns></returns>
		public bool SaveWeaveSpecification(string fileName, WeaveSpecification weaveSpecification)
		{
			if (weaveSpecification == null)
				throw new ArgumentNullException("weaveSpecification");

			if (string.IsNullOrEmpty(fileName))
				throw new ArgumentNullException("fileName");

			ObjectXMLSerializer<WeaveSpecification>.Save(weaveSpecification, fileName, SerializedFormat.DocumentCompressed);

			return true;
		}

		#endregion

	}
}
