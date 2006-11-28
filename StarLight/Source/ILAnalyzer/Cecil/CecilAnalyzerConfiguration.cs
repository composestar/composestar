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
using Composestar.StarLight.ILAnalyzer.Properties;
using System;
using System.Collections.Generic;
using System.Text;
#endregion

namespace Composestar.StarLight.ILAnalyzer
{

	/// <summary>
	/// Container to hold and manage the configuration for the Cecil IL Analyzer.
	/// </summary>
	/// <remarks>
	/// Currently we are not using the CecilAnalyzerConfiguration, 
	/// but it is still here for future implementation of options for the analyzer.
	/// </remarks> 
	public sealed class CecilAnalyzerConfiguration
	{

		/// <summary>
		/// _repository file name
		/// </summary>
		private readonly string _repositoryFileName;
		/// <summary>
		/// _do field analysis
		/// </summary>
		private bool _doFieldAnalysis = true;
		private bool _doMethodCallAnalysis = true;
		private bool _extractUnresolvedOnly;
		private string _binFolder;

		/// <summary>
		/// Initializes a new instance of the <see cref="T:WeaverConfiguration"/> class.
		/// </summary>
		/// <param name="repositoryFileName">Name of the repository file.</param>
		public CecilAnalyzerConfiguration(string repositoryFileName)
		{

			_repositoryFileName = repositoryFileName;
		}

		/// <summary>
		/// Gets the repository filename.
		/// </summary>
		/// <value>The repository filename.</value>
		public string RepositoryFileName
		{
			get { return _repositoryFileName; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether to extract only unresolved types.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if [extract unresolved only]; otherwise, <c>false</c>.
		/// </value>
		public bool ExtractUnresolvedOnly
		{
			get { return _extractUnresolvedOnly; }
			set { _extractUnresolvedOnly = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether to do method call analysis.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if the analyzer has to do method call analysis; otherwise, <c>false</c>.
		/// </value>
		public bool DoMethodCallAnalysis
		{
			get { return _doMethodCallAnalysis; }
			set { _doMethodCallAnalysis = value; }
		}

		/// <summary>
		/// Gets or sets a value indicating whether to do field analysis.
		/// </summary>
		/// <value><c>true</c> if the analyzer has to do field analysis; otherwise, <c>false</c>.</value>
		public bool DoFieldAnalysis
		{
			get { return _doFieldAnalysis; }
			set { _doFieldAnalysis = value; }
		}

		/// <summary>
		/// Gets or sets the bin folder.
		/// </summary>
		/// <value>The bin folder.</value>
		public string BinFolder
		{
			get
			{
				return _binFolder;
			}
			set
			{
				_binFolder = value;
			}
		}

		/// <summary>
		/// Creates the default configuration.
		/// </summary>
		/// <param name="repositoryFilename">The repository filename.</param>
		/// <returns></returns>
		public static CecilAnalyzerConfiguration CreateDefaultConfiguration(string repositoryFileName)
		{
			return new CecilAnalyzerConfiguration(repositoryFileName);
		}
	}
}
