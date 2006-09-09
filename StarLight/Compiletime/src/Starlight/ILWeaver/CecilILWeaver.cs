using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Diagnostics;
using System.IO;
using System.Reflection;
using System.Text;

using Mono.Cecil;
using Mono.Cecil.Binary;
using Mono.Cecil.Cil;
using Mono.Cecil.Metadata;
using Mono.Cecil.Signatures;

namespace Composestar.StarLight.ILWeaver
{
    /// <summary>
    /// Cecil implementation of the IL Weaver
    /// </summary>
    public class CecilILWeaver : IILWeaver 
    {
        private AssemblyDefinition _targetAssemblyDefinition;
        private WeaverConfiguration _configuration;

        private bool _isInitialized = false;
        private TimeSpan _lastDuration=TimeSpan.MinValue;

        /// <summary>
        /// Initializes the analyzer with the specified assembly name.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        /// <param name="config">The config.</param>
        public void Initialize(string inputImage, NameValueCollection config)
        {
            #region fileName
            if (String.IsNullOrEmpty(inputImage))
                throw new ArgumentNullException("inputImage", Properties.Resources.FileNameNullOrEmpty);

            if (!File.Exists(inputImage))
                throw new ArgumentException(String.Format(Properties.Resources.FileNotFound, inputImage), "inputImage");

            try
            {
                _targetAssemblyDefinition = AssemblyFactory.GetAssembly(inputImage);
            }
            catch (EndOfStreamException)
            {
                throw new BadImageFormatException(String.Format(Properties.Resources.ImageIsBad, inputImage));
            }
            #endregion

            #region config

            if (null == config)
            {
                _configuration = WeaverConfiguration.CreateDefaultConfiguration(inputImage);
            }
            else
            {
                string outputImagePath = config.Get("OutputImagePath");
                string shouldSignAssembly = config.Get("ShouldSignAssembly");
                string outputImageSNK = config.Get("OutputImageSNK");

                if (string.IsNullOrEmpty(outputImagePath))
                {
                    outputImagePath = inputImage;
                }

                if (!string.IsNullOrEmpty(shouldSignAssembly))
                {
                    bool shouldSignAssemblyB = false;
                    Boolean.TryParse(shouldSignAssembly, out shouldSignAssemblyB);

                    if (shouldSignAssemblyB)
                    {
                        if (string.IsNullOrEmpty(outputImageSNK))
                        {
                            throw new ArgumentException(Properties.Resources.NoSNKSpecified, "config");
                        }

                        if (File.Exists(outputImageSNK))
                        {
                            throw new ArgumentException(string.Format(Properties.Resources.SNKFileNotFound, outputImageSNK), "config");
                        }
                        _configuration = new WeaverConfiguration(outputImagePath, shouldSignAssemblyB, outputImageSNK);
                    }
                    else
                    {
                        _configuration = new WeaverConfiguration(outputImagePath, false, string.Empty);
                    }
                }
            }

            #endregion

            _isInitialized = true;

        }              

        /// <summary>
        /// Checks for initialization. Throw exception when not inited.
        /// </summary>
        private void CheckForInit()
        {
            if (!_isInitialized)
                throw new ApplicationException(Properties.Resources.NotYetInitialized);
            
        }

        /// <summary>
        /// Gets the duration of the last executed method.
        /// </summary>
        /// <value>The last duration.</value>
        public TimeSpan LastDuration
        {
            get
            {
                return _lastDuration;
            }
        }

        /// <summary>
        /// Returns a <see cref="T:System.String"></see> that represents the current <see cref="T:System.Object"></see>.
        /// </summary>
        /// <returns>
        /// A <see cref="T:System.String"></see> that represents the current <see cref="T:System.Object"></see>.
        /// </returns>
        public override string ToString()
        {
            return "Cecil IL Weaver";
        }


        public void DoWeave()
        {
            CheckForInit();
        }

        #region nested class WeaverConfiguration
        private sealed class WeaverConfiguration
        {
            private string _outputImageSNK;
            private bool _shouldSignOutput;
            private string _outputImagePath;

            public WeaverConfiguration(string outputImagePath, bool shouldSignOutput, string outputImageSNK)
            {
                _outputImageSNK = outputImageSNK;
                _shouldSignOutput = shouldSignOutput;
                _outputImagePath = outputImagePath;
            }

            public string OutputImageSNK
            {
                get { return _outputImageSNK; }
            }

            public bool ShouldSignOutput
            {
                get { return _shouldSignOutput; }
            }

            public string OutputImagePath
            {
                get { return _outputImagePath; }
            }

            public static WeaverConfiguration CreateDefaultConfiguration(string inputImagePath)
            {
                return new WeaverConfiguration(inputImagePath, false, string.Empty);
            }
        }
        #endregion
    }
}
