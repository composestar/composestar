using System;
using System.Collections.Generic;
using System.Text;
using Composestar.StarLight.ILAnalyzer.Properties;

namespace Composestar.StarLight.ILAnalyzer
{

    /// <summary>
    /// Container to hold and manage the configuration for the Cecil IL Analyzer.
    /// </summary>
    public sealed class CecilAnalyzerConfiguration
    {

        private readonly string _filename;
        private readonly string _repositoryFilename;

        /// <summary>
        /// Initializes a new instance of the <see cref="T:WeaverConfiguration"/> class.
        /// </summary>
        /// <param name="filename">The filename.</param>
        /// <param name="repositoryFilename">The repository filename.</param>
        public CecilAnalyzerConfiguration(string filename, string repositoryFilename)
        {
            _filename = filename;
            _repositoryFilename = repositoryFilename;
        }

        /// <summary>
        /// Gets the filename.
        /// </summary>
        /// <value>The filename.</value>
        public string Filename
        {
            get { return _filename; }
        }

        /// <summary>
        /// Creates the default configuration.
        /// </summary>
        /// <param name="inputImagePath">The input image path.</param>
        /// <returns></returns>
        public static CecilAnalyzerConfiguration CreateDefaultConfiguration(string inputImagePath)
        {
            return new CecilAnalyzerConfiguration(inputImagePath, "");
        }

        internal void RuntimeValidate()
        {

        }
    }
}
