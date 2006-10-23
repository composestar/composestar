using System;
using System.Collections.Generic;
using System.Text;
using Composestar.StarLight.ILAnalyzer.Properties;

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
              
        private readonly string _repositoryFilename;
        private bool _doFieldAnalysis = true;
        private bool _doMethodCallAnalysis = true;
        private bool _extractUnresolvedOnly;
        private string _binFolder;

        /// <summary>
        /// Initializes a new instance of the <see cref="T:WeaverConfiguration"/> class.
        /// </summary>
        /// <param name="filename">The filename.</param>
        /// <param name="repositoryFilename">The repository filename.</param>
        public CecilAnalyzerConfiguration( string repositoryFilename)
        {
            
             _repositoryFilename = repositoryFilename;
        }

        /// <summary>
        /// Gets the repository filename.
        /// </summary>
        /// <value>The repository filename.</value>
        public string RepositoryFilename
        {
            get { return _repositoryFilename; }
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
        public static CecilAnalyzerConfiguration CreateDefaultConfiguration(string repositoryFilename)
        {
            return new CecilAnalyzerConfiguration(repositoryFilename);
        }

        internal void RuntimeValidate()
        {

        }
    }
}
