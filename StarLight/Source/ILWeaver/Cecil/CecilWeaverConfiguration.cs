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

        readonly bool _delaySignOutput;
        readonly string _outputImageSNK;
        readonly bool _shouldSignOutput;
        readonly string _outputImagePath;
        readonly string _inputImagePath;
        private string _binfolder;
        private AssemblyConfig _assemblyConfig;
        private ConfigurationContainer _weaveConfiguration;

        /// <summary>
        /// Initializes a new instance of the <see cref="T:WeaverConfiguration"/> class.
        /// </summary>
        /// <param name="outputImagePath">The output image path.</param>
        /// <param name="shouldSignOutput">if set to <c>true</c> [should sign output].</param>
        /// <param name="outputImageSNK">The output image SNK.</param>
        /// <param name="inputImagePath">The input image path.</param>
        /// <param name="delaySignOutput">if set to <c>true</c> [delay sign output].</param>
        /// <param name="assemblyConfig">The assembly config.</param>
        /// <param name="weaveConfiguration">The weave configuration.</param>
        public CecilWeaverConfiguration(string outputImagePath, bool shouldSignOutput, string outputImageSNK, string inputImagePath, bool delaySignOutput, AssemblyConfig assemblyConfig, ConfigurationContainer weaveConfiguration)
        {
            if (assemblyConfig == null)
                throw new ArgumentNullException("AssemblyConfig"); 

            if (shouldSignOutput && string.IsNullOrEmpty(outputImageSNK)) 
                throw new ArgumentException(Resources.NoSNKSpecified, "outputImageSNK");

            if (delaySignOutput && !shouldSignOutput)
                throw new ArgumentException(Resources.CannotDelaySignWithoutSigning, "delaySignOutput");

            if (string.IsNullOrEmpty(inputImagePath))
                throw new ArgumentNullException("inputImagePath");

            _binfolder = System.IO.Path.GetDirectoryName(inputImagePath); 
            _outputImageSNK = outputImageSNK;
            _assemblyConfig = assemblyConfig; 
            _shouldSignOutput = shouldSignOutput;
            _outputImagePath = outputImagePath;
            _inputImagePath = inputImagePath;
            _delaySignOutput = delaySignOutput;
            _weaveConfiguration = weaveConfiguration;
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:CecilWeaverConfiguration"/> class.
        /// </summary>
        /// <param name="assemblyConfig">The assembly config.</param>
        /// <param name="weaveConfiguration">The weave configuration.</param>
        public CecilWeaverConfiguration(AssemblyConfig assemblyConfig, ConfigurationContainer weaveConfiguration)
        {
            if (assemblyConfig == null)
                throw new ArgumentNullException("AssemblyConfig");

            if (weaveConfiguration == null)
                throw new ArgumentNullException("weaveConfiguration");

            _weaveConfiguration = weaveConfiguration;
            _binfolder = System.IO.Path.GetDirectoryName(assemblyConfig.Filename);
            _outputImageSNK = string.Empty;
            _assemblyConfig = assemblyConfig;
            _shouldSignOutput = false;
            _outputImagePath = assemblyConfig.Filename;
            _inputImagePath = assemblyConfig.Filename;
            _delaySignOutput = false;
        }

        /// <summary>
        /// Gets the output image SNK.
        /// </summary>
        /// <value>The output image SNK.</value>
        public string OutputImageSNK
        {
            get { return _outputImageSNK; }
        }

        /// <summary>
        /// Gets the weave configuration.
        /// </summary>
        /// <value>The weave configuration.</value>
        /// <returns>Configuration container</returns>
        public ConfigurationContainer WeaveConfiguration
        {
            get { return _weaveConfiguration; } // get
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
        /// Gets a value indicating whether the output should be signed.
        /// </summary>
        /// <value><c>true</c> if should sign output; otherwise, <c>false</c>.</value>
        public bool ShouldSignOutput
        {
            get { return _shouldSignOutput; }
        }

        /// <summary>
        /// Gets the output image path.
        /// </summary>
        /// <value>The output image path.</value>
        public string OutputImagePath
        {
            get { return _outputImagePath; }
        }

        /// <summary>
        /// Gets the input image path.
        /// </summary>
        /// <value>The input image path.</value>
        public string InputImagePath
        {
            get { return _inputImagePath; }
        }

        /// <summary>
        /// Gets a value indicating whether to delay signing the output.
        /// </summary>
        /// <value><c>true</c> if [delay sign output]; otherwise, <c>false</c>.</value>
        public bool DelaySignOutput
        {
            get { return _delaySignOutput; }
        }

        /// <summary>
        /// Creates the default configuration.
        /// </summary>
        /// <param name="inputImagePath">The input image path.</param>
        /// <returns></returns>
        public static CecilWeaverConfiguration CreateDefaultConfiguration(string inputImagePath)
        {
            return new CecilWeaverConfiguration(inputImagePath, false, string.Empty, inputImagePath, false, null, null);
        }

        /// <summary>
        /// Creates the default configuration.
        /// </summary>
        /// <param name="inputImagePath">The input image path.</param>
        /// <param name="outputImagePath">The output image path.</param>
        /// <returns></returns>
        public static CecilWeaverConfiguration CreateDefaultConfiguration(string inputImagePath, string outputImagePath)
        {
            return new CecilWeaverConfiguration(outputImagePath, false, string.Empty, inputImagePath, false, null, null);
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

        internal void RuntimeValidate()
        {

        }
    }
}
