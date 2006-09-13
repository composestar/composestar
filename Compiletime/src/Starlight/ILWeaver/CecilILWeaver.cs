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
        private TimeSpan _lastDuration = TimeSpan.MinValue;

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
                string outputFilename = config.Get("OutputFilename");

                if (string.IsNullOrEmpty(outputImagePath))
                {
                    outputImagePath = Path.GetFullPath(inputImage);
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

                if (String.IsNullOrEmpty(outputFilename) )
                    _configuration.OutputFilename = Path.GetFileName(inputImage);
                else
                    _configuration.OutputFilename = outputFilename;
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

        /// <summary>
        /// Perform the weaving actions.
        /// </summary>
        public void DoWeave()
        {
            // See if we are initialized.
            CheckForInit();

            // Check if the _targetAssemblyDefinition is still available
            if (_targetAssemblyDefinition == null)
                throw new ArgumentNullException(Properties.Resources.AssemblyNotOpen);

            // Lets walk over all the modules in the assembly
            foreach (ModuleDefinition module in _targetAssemblyDefinition.Modules)
	        {
                // Walk over each type in the module
                foreach (TypeDefinition type in module.Types)
	            {
                    // Add the externals and internals
                    WeaveExternals(type);
                    WeaveInternals(type);

                    foreach (MethodDefinition method in type.Methods)
                    {
                        WeaveMethod( method);
                    }

                    //Import the modifying type into the AssemblyDefinition
                    module.Import(type);
	            }
		        
	        }
            
            //Save the modified assembly
            AssemblyFactory.SaveAssembly(_targetAssemblyDefinition, _configuration.OutputFile);
        }

        /// <summary>
        /// Weaves the internals.
        /// </summary>
        /// <param name="type">The type.</param>
        public void WeaveInternals(TypeDefinition type)
        {
            // Declarations
            FieldDefinition internalDef;
            String name;
            TypeReference fieldType;
            Mono.Cecil.FieldAttributes attrs;

            // Prepare the data
            name = "test";            
            fieldType = _targetAssemblyDefinition.MainModule.Import(typeof(String));
            attrs = Mono.Cecil.FieldAttributes.Public;  
            
            // Create the field
            internalDef = new FieldDefinition(name, fieldType, attrs);

            // Add the field
            type.Fields.Add(internalDef);  
        }

        public void WeaveExternals(TypeDefinition type)
        {

        }

        /// <summary>
        /// Weaves the code into the method.
        /// </summary>
        /// <param name="method">The method definition.</param>
        public void WeaveMethod(MethodDefinition method)
        {
            if (method == null)
                return;
 
            // Add the inputfilters
            WeaveInputFilters(method);

            // Add the outputfilters
            WeaveOutputFilters(method);
              
        }

        public void WeaveInputFilters(MethodDefinition method)
        {
                 //Gets the MethodInfo of Console.WriteLine() method
            MethodInfo writeLineMethod = typeof(Console).GetMethod("WriteLine", new Type[]{typeof(string)});

      //Gets the CilWorker of the method for working with CIL instructions
			CilWorker worker = method.Body.CilWorker;

 
			//Creating a sentence according to the current method
			string sentence;
			sentence = String.Concat("Code added in ", method.Name);
 
			//Import the Console.WriteLine() method
			MethodReference writeLine;
			writeLine = _targetAssemblyDefinition.MainModule.Import(writeLineMethod);
 
			//Creates the MSIL instruction for inserting the sentence
			Instruction insertSentence;
			insertSentence = worker.Create(OpCodes.Ldstr, sentence);
 
			//Creates the CIL instruction for calling the 
			//Console.WriteLine(string value) method
			Instruction callWriteLine;
			callWriteLine = worker.Create(OpCodes.Call, writeLine);
 			
			//Getting the first instruction of the current method
			Instruction ins = method.Body.Instructions[0];
 
			//Inserts the insertSentence instruction before the first instruction
			method.Body.CilWorker.InsertBefore(ins, insertSentence);
 
			//Inserts the callWriteLineMethod after the //insertSentence instruction
			worker.InsertAfter(insertSentence, callWriteLine);
        }

        public void WeaveOutputFilters(MethodDefinition method)
        {

        }

        #region nested class WeaverConfiguration
        /// <summary>
        /// Contains the configuration for the weaver.
        /// </summary>
        private sealed class WeaverConfiguration
        {
            private string _outputImageSNK;
            private bool _shouldSignOutput;
            private string _outputImagePath;
            private string _outputFilename;

            /// <summary>
            /// Initializes a new instance of the <see cref="T:WeaverConfiguration"/> class.
            /// </summary>
            /// <param name="outputImagePath">The output image path.</param>
            /// <param name="shouldSignOutput">if set to <c>true</c> [should sign output].</param>
            /// <param name="outputImageSNK">The output image SNK.</param>
            public WeaverConfiguration(string outputImagePath, bool shouldSignOutput, string outputImageSNK)
            {
                _outputImageSNK = outputImageSNK;
                _shouldSignOutput = shouldSignOutput;
                _outputImagePath = outputImagePath;
            }

            /// <summary>
            /// Initializes a new instance of the <see cref="T:WeaverConfiguration"/> class.
            /// </summary>
            /// <param name="outputImagePath">The output image path.</param>
            /// <param name="outputFilename">The output filename.</param>
            /// <param name="shouldSignOutput">if set to <c>true</c> [should sign output].</param>
            /// <param name="outputImageSNK">The output image SNK.</param>
            public WeaverConfiguration(string outputImagePath, string outputFilename, bool shouldSignOutput, string outputImageSNK)
            {
                _outputImageSNK = outputImageSNK;
                _outputFilename = outputFilename;
                _shouldSignOutput = shouldSignOutput;
                _outputImagePath = outputImagePath;
            }

            /// <summary>
            /// Gets or sets the output filename.
            /// </summary>
            /// <value>The output filename.</value>
            public string OutputFilename
            {
                get
                {
                    return _outputFilename;
                }
                set
                {
                    _outputFilename = value;
                }
            }

            /// <summary>
            /// Gets the output file.
            /// </summary>
            /// <value>The output file.</value>
            public string OutputFile
            {
                get { return Path.Combine(_outputImagePath, _outputFilename); } 
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
            /// Creates the default configuration.
            /// </summary>
            /// <param name="inputImagePath">The input image path.</param>
            /// <returns></returns>
            public static WeaverConfiguration CreateDefaultConfiguration(string inputImagePath)
            {
                return new WeaverConfiguration(inputImagePath, false, string.Empty);
            }
        }
        #endregion
    }
}
