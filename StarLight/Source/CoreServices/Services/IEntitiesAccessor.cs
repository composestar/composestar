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
using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.WeaveSpec;
using System;
using System.Collections.Generic;
#endregion

namespace Composestar.StarLight.CoreServices
{

	/// <summary>
	/// Combines the interfaces for the entities.
	/// </summary>
	public interface IEntitiesAccessor 
		: IConfigurationAccessors, IAssemblyAccessors, IWeaveSpecAccessors, ISignatureSpecAccessors
	{
	}

	/// <summary>
	/// Contains the functions to load and save the configuration.
	/// </summary>
	public interface IConfigurationAccessors
	{
		/// <summary>
		/// Loads the configuration.
		/// </summary>
		/// <param name="fileName">The fileName.</param>
		/// <returns></returns>
		ConfigurationContainer LoadConfiguration(string fileName);

		/// <summary>
		/// Saves the configuration.
		/// </summary>
		/// <param name="fileName">The fileName.</param>
		/// <param name="configContainer">The config container.</param>
		/// <returns></returns>
		bool SaveConfiguration(string fileName, ConfigurationContainer configContainer);
	}

	/// <summary>
	/// Functions to load and save assemblies.
	/// </summary>
	public interface IAssemblyAccessors
	{
		/// <summary>
		/// Loads the assembly element.
		/// </summary>
		/// <param name="fileName">The fileName.</param>
		/// <returns></returns>
		AssemblyElement LoadAssemblyElement(string fileName);

		/// <summary>
		/// Saves the assembly element.
		/// </summary>
		/// <param name="fileName">The fileName.</param>
		/// <param name="assemblyElement">The assembly element.</param>
		/// <returns></returns>
		bool SaveAssemblyElement(string fileName, AssemblyElement assemblyElement);
	}

	/// <summary>
	/// Interface for the weave specification load and save functions.
	/// </summary>
	public interface IWeaveSpecAccessors
	{
		/// <summary>
		/// Loads the weave specification.
		/// </summary>
		/// <param name="fileName">The fileName.</param>
		/// <returns></returns>
		WeaveSpecification LoadWeaveSpecification(string fileName);

		/// <summary>
		/// Saves the weave specification.
		/// </summary>
		/// <param name="fileName">The fileName.</param>
		/// <param name="weaveSpecification">The weave specification.</param>
		/// <returns></returns>
		bool SaveWeaveSpecification(string fileName, WeaveSpecification weaveSpecification);
	}

	public interface ISignatureSpecAccessors
	{
		Signatures LoadSignatureSpecification(string fileName);
	}
}
