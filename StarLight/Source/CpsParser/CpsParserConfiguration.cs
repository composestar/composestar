using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.StarLight.CpsParser
{

    /// <summary>
    /// Container to hold and manage the configuration for the Parser.
    /// </summary>
    public sealed class CpsParserConfiguration
    {

        private string _filename;

        /// <summary>
        /// Initializes a new instance of the <see cref="T:CpsParserConfiguration"/> class.
        /// </summary>
        /// <param name="filename">The filename.</param>
        public CpsParserConfiguration(string filename)
        {
            _filename = filename;
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
        /// <param name="filename">The filename.</param>
        /// <returns></returns>
        public static CpsParserConfiguration CreateDefaultConfiguration(string filename)
        {
            return new CpsParserConfiguration(filename);
        }

        internal void RuntimeValidate()
        {

        }
    }
}
