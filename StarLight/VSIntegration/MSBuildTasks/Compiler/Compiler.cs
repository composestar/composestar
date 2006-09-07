
using System;
using System.Collections.Generic;
using System.Text;

namespace ComposeStar.MSBuild.Tasks.Compiler
{
	/// <summary>
	/// The main purpose of this class is to associate the PythonCompiler
	/// class with the ICompiler interface.
	/// </summary>
	public class Compiler : ICompiler
	{
		public Compiler(IList<string> sourcesFiles, string OutputAssembly)
		{

        }


        #region ICompiler Members

        private IList<string> _sourceFiles = new List<string>(); 

        public IList<string> SourceFiles
        {
            get
            {
                return _sourceFiles;
            }
            set
            {
                _sourceFiles = value;
            }
        }

        private string _outputAssembly;
        public string OutputAssembly
        {
            get
            {
                return _outputAssembly;
            }
            set
            {
                _outputAssembly = value;
            }
        }
        private IList<string> _referencedAssemblies = new List<string>(); 

        public IList<string> ReferencedAssemblies
        {
            get
            {
                return _referencedAssemblies;
            }
            set
            {
                _referencedAssemblies = value;
            }
        }
        private IList<string> _resourceFiles = new List<string>(); 

        public IList<string> ResourceFiles
        {
            get
            {
                return _resourceFiles;
            }
            set
            {
                _resourceFiles = value;
            }
        }
        private string _mainFile;
        public string MainFile
        {
            get
            {
                return _mainFile;
            }
            set
            {
                _mainFile = value;
            }
        }
        private System.Reflection.Emit.PEFileKinds _targetKind;
        public System.Reflection.Emit.PEFileKinds TargetKind
        {
            get
            {
                return _targetKind;
            }
            set
            {
                _targetKind = value;
            }
        }

        private bool _includeDebugInformation;

        public bool IncludeDebugInformation
        {
            get
            {
                return _includeDebugInformation;
            }
            set
            {
                _includeDebugInformation = value;
            }
        }

        public void Compile()
        {
            
        }

        #endregion
    }
}
