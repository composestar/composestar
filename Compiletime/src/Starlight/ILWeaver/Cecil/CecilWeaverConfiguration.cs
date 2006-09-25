using System;
using System.Collections.Generic;
using System.Text;
using Composestar.Repository.Properties;
using Composestar.StarLight.ILWeaver.Properties;

namespace Composestar.StarLight.ILWeaver
{
    public sealed class CecilWeaverConfiguration
    {
        readonly bool _delaySignOutput;
        readonly string _outputImageSNK;
        readonly bool _shouldSignOutput;
        readonly string _outputImagePath;
        readonly string _inputImagePath;

        /// <summary>
        /// Initializes a new instance of the <see cref="T:WeaverConfiguration"/> class.
        /// </summary>
        /// <param name="outputImagePath">The output image path.</param>
        /// <param name="shouldSignOutput">if set to <c>true</c> [should sign output].</param>
        /// <param name="outputImageSNK">The output image SNK.</param>
        public CecilWeaverConfiguration(string outputImagePath, bool shouldSignOutput, string outputImageSNK, string inputImagePath, bool delaySignOutput)
        {
            if (shouldSignOutput && string.IsNullOrEmpty(outputImageSNK)) 
                throw new ArgumentException(Resources.NoSNKSpecified, "outputImageSNK");

            if (delaySignOutput && !shouldSignOutput)
                throw new ArgumentException(Resources.CannotDelaySignWithoutSigning, "delaySignOutput");

            if (string.IsNullOrEmpty(inputImagePath))
                throw new ArgumentNullException("inputImagePath");

            _outputImageSNK = outputImageSNK;
            _shouldSignOutput = shouldSignOutput;
            _outputImagePath = outputImagePath;
            _inputImagePath = inputImagePath;
            _delaySignOutput = delaySignOutput;
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
        /// 
        /// </summary>
        public string InputImagePath
        {
            get { return _inputImagePath; }
        }

        /// <summary>
        /// 
        /// </summary>
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
            return new CecilWeaverConfiguration(inputImagePath, false, string.Empty, inputImagePath, false);
        }

        public static CecilWeaverConfiguration CreateDefaultConfiguration(string inputImagePath, string outputImagePath)
        {
            return new CecilWeaverConfiguration(outputImagePath, false, string.Empty, inputImagePath, false);
        }

        internal void RuntimeValidate()
        {

        }
    }
}
