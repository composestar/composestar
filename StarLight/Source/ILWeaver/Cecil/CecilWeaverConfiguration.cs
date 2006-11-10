using System;
using System.Collections.Generic;
using System.Text;
using Composestar.StarLight.ILWeaver.Properties;
using Composestar.StarLight.Entities.Configuration;
 
namespace Composestar.StarLight.ILWeaver
{

    /// <summary>
    /// Container to hold and manage the configuration for the Cecil IL weaver.
    /// </summary>
    public sealed class CecilWeaverConfiguration
    {

        /// <summary>
        /// Indicated the level of debug information collected by the weaver.
        /// </summary>
        public enum WeaveDebug
        {
            /// <summary>
            /// No debugging is collected.
            /// </summary>
            None,
            /// <summary>
            /// Weave statistics are collected.
            /// </summary>
            Statistics,
            /// <summary>
            /// Weave statistics and detailed information is collected.
            /// </summary>
            Detailed
        }

        readonly string _outputImagePath;
        readonly string _inputImagePath;
        private string _binfolder;
        private AssemblyConfig _assemblyConfig;
        private ConfigurationContainer _weaveConfiguration;

        private WeaveDebug _weaveDebugLevel;

        /// <summary>
        /// Initializes a new instance of the <see cref="T:WeaverConfiguration"/> class.
        /// </summary>
        /// <param name="outputImagePath">The output image path.</param>
        /// <param name="inputImagePath">The input image path.</param>
        /// <param name="assemblyConfig">The assembly config.</param>
        /// <param name="weaveConfiguration">The weave configuration.</param>
        public CecilWeaverConfiguration(string outputImagePath, string inputImagePath, AssemblyConfig assemblyConfig, ConfigurationContainer weaveConfiguration)
        {
            if (assemblyConfig == null)
                throw new ArgumentNullException("assemblyConfig"); 
                     
            if (string.IsNullOrEmpty(inputImagePath))
                throw new ArgumentNullException("inputImagePath");

            _binfolder = System.IO.Path.GetDirectoryName(inputImagePath); 
              _assemblyConfig = assemblyConfig; 
             _outputImagePath = outputImagePath;
            _inputImagePath = inputImagePath;
               _weaveConfiguration = weaveConfiguration;
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:CecilWeaverConfiguration"/> class.
        /// </summary>
        /// <param name="assemblyConfig">The assembly config.</param>
        /// <param name="weaveConfiguration">The weave configuration.</param>
        public CecilWeaverConfiguration(AssemblyConfig assemblyConfig, ConfigurationContainer weaveConfiguration, WeaveDebug weaveDebug)
        {
            if (assemblyConfig == null)
                throw new ArgumentNullException("assemblyConfig");

            if (weaveConfiguration == null)
                throw new ArgumentNullException("weaveConfiguration");

            _weaveConfiguration = weaveConfiguration;
            _binfolder = System.IO.Path.GetDirectoryName(assemblyConfig.Filename);
            _assemblyConfig = assemblyConfig;
            _outputImagePath = assemblyConfig.Filename;
            _inputImagePath = assemblyConfig.Filename;
            _weaveDebugLevel = weaveDebug;
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:CecilWeaverConfiguration"/> class.
        /// </summary>
        /// <param name="assemblyConfig">The assembly config.</param>
        /// <param name="weaveConfiguration">The weave configuration.</param>
        public CecilWeaverConfiguration(AssemblyConfig assemblyConfig, ConfigurationContainer weaveConfiguration)
            : this(assemblyConfig, weaveConfiguration, WeaveDebug.Statistics)
        {
           
        }

      
        /// <summary>
        /// Gets or sets the weave debug level.
        /// </summary>
        /// <value>The weave debug level.</value>
        public WeaveDebug WeaveDebugLevel
        {
            get
            {
                return _weaveDebugLevel;
            }
            set
            {
                _weaveDebugLevel = value;
            }
        }

        /// <summary>
        /// Gets the weave configuration.
        /// </summary>
        /// <value>The weave configuration.</value>
        /// <returns>Configuration container</returns>
        public ConfigurationContainer WeaveConfiguration
        {
            get 
            { 
                return _weaveConfiguration; 
            }
        } // WeaveConfiguration

        /// <summary>
        /// Assembly configuration
        /// </summary>
        /// <returns>Assembly config</returns>
        public AssemblyConfig AssemblyConfiguration
        {
            get
            {
                return _assemblyConfig;
            } 
        } // AssemblyConfiguration

        /// <summary>
        /// Gets or sets the binfolder.
        /// </summary>
        /// <value>The binfolder.</value>
        public string BinFolder
        {
            get
            {
                return _binfolder;
            }
            set
            {
                _binfolder = value;
            }
        }

               /// <summary>
        /// Gets the output image path.
        /// </summary>
        /// <value>The output image path.</value>
        public string OutputImagePath
        {
            get 
            { 
                return _outputImagePath;
            }
        }

        /// <summary>
        /// Gets the input image path.
        /// </summary>
        /// <value>The input image path.</value>
        public string InputImagePath
        {
            get
            { 
                return _inputImagePath;
            }
        }

        /// <summary>
        /// Gets the debug image path.
        /// </summary>
        /// <value>The debug image path.</value>
        public string DebugImagePath
        {
            get { 
                if (string.IsNullOrEmpty(_inputImagePath))
                    return "";
                else 
                    return string.Concat(_inputImagePath.Substring(0, _inputImagePath.LastIndexOf(".")), ".pdb");
            }
        }
               

        /// <summary>
        /// Creates the default configuration.
        /// </summary>
        /// <param name="inputImagePath">The input image path.</param>
        /// <returns></returns>
        public static CecilWeaverConfiguration CreateDefaultConfiguration(string inputImagePath)
        {
            return new CecilWeaverConfiguration(inputImagePath,  inputImagePath, null, null);
        }

        /// <summary>
        /// Creates the default configuration.
        /// </summary>
        /// <param name="inputImagePath">The input image path.</param>
        /// <param name="outputImagePath">The output image path.</param>
        /// <returns></returns>
        public static CecilWeaverConfiguration CreateDefaultConfiguration(string inputImagePath, string outputImagePath)
        {
            return new CecilWeaverConfiguration(outputImagePath, inputImagePath, null, null);
        }

        /// <summary>
        /// Creates the default configuration.
        /// </summary>
        /// <param name="assemblyConfig">The assembly config.</param>
        /// <param name="weaveConfiguration">The weave configuration.</param>
        /// <returns></returns>
        public static CecilWeaverConfiguration CreateDefaultConfiguration(AssemblyConfig assemblyConfig, ConfigurationContainer weaveConfiguration)
        {
            return new CecilWeaverConfiguration(assemblyConfig, weaveConfiguration);
        }
           
    }
}
