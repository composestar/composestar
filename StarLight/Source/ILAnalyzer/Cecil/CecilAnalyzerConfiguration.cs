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

        public bool DoMethodCallAnalysis
        {
            get { return _doMethodCallAnalysis; }
            set { _doMethodCallAnalysis = value; }
        }

        public bool DoFieldAnalysis
        {
            get { return _doFieldAnalysis; }
            set { _doFieldAnalysis = value; }
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
