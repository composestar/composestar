using System;
using System.ComponentModel;
using System.Windows.Forms.Design;
using System.Drawing.Design;

namespace ComposestarVSAddin
{
	/// <summary>
	/// SettingsContainer to be displayed in the property grid. 
	/// Use custom attributes to indicate how to save the settings to the ini files.
	/// </summary>
	[DefaultPropertyAttribute("BuildDebugLevel")]
	public class SettingsContainer : ISettingsContainer
	{
		private string _composestarPath;
		private string _classPath;
		private string _dotnetPath;
		private string _dotnetSDKPath;
		private string _mainClass;
		private string _embeddedSourcesFolder;
		private string _requiredDlls;
		private string _JSCompiler;
		private string _JSCompilerOptions;
		private string _VBCompiler;
		private string _VBCompilerOptions;
		private string _CSCompiler;
		private string _CSCompilerOptions;
		private string _JVMOptions;
		private DebuggerType _debuggerType = DebuggerType.NotSet;
		private string _FILTHInput;
		private SecretModes _secretMode = SecretModes.NotSet;
		private TriStateBooleanModes _verifyAssemblies = TriStateBooleanModes.NotSet;
		private DebugModes _buildDebugLevel = DebugModes.NotSet;
		private DebugModes _runDebugLevel = DebugModes.NotSet;
		private TriStateBooleanModes _incremental = TriStateBooleanModes.NotSet;


		[CategoryAttribute("User defined settings"),  
		DescriptionAttribute("FilterModule Order specification file"),
		EditorAttribute(typeof(FileNameEditor), typeof(UITypeEditor)),
		IniSettingFieldAttribute("FILTH_INPUT", "Global Composestar configuration")]
		public string FilterModuleOrder
		{
			get
			{
				return this._FILTHInput;
			}
			set 
			{
				this._FILTHInput = value;
				SetIsDirty(true);
			}
		}

		[CategoryAttribute("User defined settings"),  
		DescriptionAttribute("SECRET mode"),
		IniSettingFieldAttribute("SECRETMode", "Global Composestar configuration")]
		public SecretModes SecretMode
		{
			get
			{
				return this._secretMode;
			}
			set 
			{
				this._secretMode = value;
				SetIsDirty(true);
			}
		}

		[CategoryAttribute("User defined settings"),  
		DescriptionAttribute("DebuggerType"),
		IniSettingFieldAttribute("DebuggerType", "Global Composestar configuration")]
		public DebuggerType DebuggerType
		{
			get
			{
				return _debuggerType;
			}
			set 
			{
				this._debuggerType = value;
				SetIsDirty(true);
			}
		}

		[CategoryAttribute("User defined settings"),  
		DescriptionAttribute("Build debug level."),
		IniSettingFieldAttribute("BuildDebugLevel", "Common")]
		public DebugModes BuildDebugLevel
		{
			get
			{
				return this._buildDebugLevel;
			}
			set 
			{
				this._buildDebugLevel = value;
				SetIsDirty(true);
			}
		}

		[CategoryAttribute("User defined settings"),  
		DescriptionAttribute("Run debug level."),
		IniSettingFieldAttribute("RunDebugLevel", "Common")]
		public DebugModes RunDebugLevel
		{
			get
			{
				return this._runDebugLevel;
			}
			set 
			{
				this._runDebugLevel = value;
				SetIsDirty(true);
			}
		}

		[CategoryAttribute("User defined settings"),  
		DescriptionAttribute("Verify generated assemblies after compilation."),
		IniSettingFieldAttribute("VerifyAssemblies", "Common")]
		public TriStateBooleanModes VerifyAssemblies
		{
			get
			{
				return this._verifyAssemblies;
			}
			set 
			{
				this._verifyAssemblies = value;
				SetIsDirty(true);
			}
		}

		[CategoryAttribute("User defined settings"),  
		DescriptionAttribute("Incremental compilation."),
		IniSettingFieldAttribute("INCRE_ENABLED", "Common")]
		public TriStateBooleanModes Incremental
		{
			get
			{
				return this._incremental;
			}
			set 
			{
				this._incremental = value;
				SetIsDirty(true);
			}
		}

		[CategoryAttribute("Compose* installation settings"),  
		DescriptionAttribute("JVM Options"),
		IniSettingFieldAttribute("JVMOptions", "JVM")]
		public string JVMOptions
		{
			get
			{
				return this._JVMOptions;
			}
			set 
			{
				this._JVMOptions = value;
				SetIsDirty(true);
			}
		}

		[CategoryAttribute("Compose* installation settings"),  
		DescriptionAttribute("Path of the Composestar installation."),
		EditorAttribute(typeof(FolderNameEditor), typeof(UITypeEditor)),
		IniSettingFieldAttribute("ComposestarPath", "Global Composestar configuration")]
		public string ComposestarPath
		{
			get
			{
				return this._composestarPath;
			}
			set 
			{
				this._composestarPath = value;
				SetIsDirty(true);
			}
		}

		[CategoryAttribute("Compose* installation settings"),  
		DescriptionAttribute("Classpath of the Java binaries of Composestar."),
		IniSettingFieldAttribute("ClassPath", "Global Composestar configuration")]
		public string Classpath
		{
			get
			{
				return this._classPath;
			}
			set 
			{
				this._classPath = value;
				SetIsDirty(true);
			}
		}

		[CategoryAttribute("Compose* installation settings"),  
		DescriptionAttribute("Path to the .NET framework."),
		EditorAttribute(typeof(FolderNameEditor), typeof(UITypeEditor)),
		IniSettingFieldAttribute(".NETPath", "Global Composestar configuration")]
		public string dotNETPath
		{
			get
			{
				return this._dotnetPath;
			}
			set 
			{
				this._dotnetPath = value;
				SetIsDirty(true);
			}
		}

		[CategoryAttribute("Compose* installation settings"),  
		DescriptionAttribute("Path to the .NET SDK."),
		EditorAttribute(typeof(FolderNameEditor), typeof(UITypeEditor)),
		IniSettingFieldAttribute(".NETSDKPath", "Global Composestar configuration")]
		public string dotNETSDKPath
		{
			get
			{
				return this._dotnetSDKPath;
			}
			set 
			{
				this._dotnetSDKPath = value;
				SetIsDirty(true);
			}
		}

		[CategoryAttribute("Compose* installation settings"),  
		DescriptionAttribute("Main class."),
		IniSettingFieldAttribute("MainClass", "Global Composestar configuration")]
		public string MainClass
		{
			get
			{
				return this._mainClass;
			}
			set 
			{
				this._mainClass = value;
				SetIsDirty(true);
			}
		}

		[CategoryAttribute("Compose* installation settings"),  
		DescriptionAttribute("Path to the embedded sources folder."),
		EditorAttribute(typeof(FolderNameEditor), typeof(UITypeEditor)),
		IniSettingFieldAttribute("EmbeddedSourcesFolder", "Global Composestar configuration")]
		public string EmbeddedSourcesFolder
		{
			get
			{
				return this._embeddedSourcesFolder;
			}
			set 
			{
				this._embeddedSourcesFolder = value;
				SetIsDirty(true);
			}
		}

		[CategoryAttribute("Compose* installation settings"),  
		DescriptionAttribute("DLL names required by the compiler."),
		IniSettingFieldAttribute("RequiredDlls", "Global Composestar configuration")]
		public string RequiredDlls
		{
			get
			{
				return this._requiredDlls;
			}
			set 
			{
				this._requiredDlls = value;
				SetIsDirty(true);
			}
		}

		[CategoryAttribute("Native Compiler settings"),  
		DescriptionAttribute("J# compiler location."),
		EditorAttribute(typeof(FolderNameEditor), typeof(UITypeEditor)),
		IniSettingFieldAttribute("JSCompiler", "Global Composestar configuration")]
		public string JSCompiler
		{
			get
			{
				return this._JSCompiler;
			}
			set 
			{
				this._JSCompiler = value;
				SetIsDirty(true);
			}
		}

		[CategoryAttribute("Native Compiler settings"),  
		DescriptionAttribute("J# compiler options."),
		IniSettingFieldAttribute("JSCompilerOptions", "Global Composestar configuration")]
		public string JSCompilerOptions
		{
			get
			{
				return this._JSCompilerOptions;
			}
			set 
			{
				this._JSCompilerOptions = value;
				SetIsDirty(true);
			}
		}
		
		[CategoryAttribute("Native Compiler settings"),  
		DescriptionAttribute("VB compiler location."),
		EditorAttribute(typeof(FolderNameEditor), typeof(UITypeEditor)),
		IniSettingFieldAttribute("VBCompiler", "Global Composestar configuration")]
		public string VBCompiler
		{
			get
			{
				return this._VBCompiler;
			}
			set 
			{
				this._VBCompiler = value;
				SetIsDirty(true);
			}
		}

		[CategoryAttribute("Native Compiler settings"),  
		DescriptionAttribute("VB.NET compiler options."),
		IniSettingFieldAttribute("VBCompilerOptions", "Global Composestar configuration")]
		public string VBCompilerOptions
		{
			get
			{
				return this._VBCompilerOptions;
			}
			set 
			{
				this._VBCompilerOptions = value;
				SetIsDirty(true);
			}
		}
		[CategoryAttribute("Native Compiler settings"),  
		DescriptionAttribute("C# compiler location."),
		EditorAttribute(typeof(FolderNameEditor), typeof(UITypeEditor)),
		IniSettingFieldAttribute("CSCompiler", "Global Composestar configuration")]
		public string CSCompiler
		{
			get
			{
				return this._CSCompiler;
			}
			set 
			{
				this._CSCompiler = value;
				SetIsDirty(true);
			}
		}

		[CategoryAttribute("Native Compiler settings"),  
		DescriptionAttribute("C# compiler options."),
		IniSettingFieldAttribute("CSCompilerOptions", "Global Composestar configuration")]
		public string CSCompilerOptions
		{
			get
			{
				return this._CSCompilerOptions;
			}
			set 
			{
				this._CSCompilerOptions = value;
				SetIsDirty(true);
			}
		}

		public SettingsContainer()
		{
		}
 
		#region ISettingsContainer Members
		private bool _isDirty;

		public void SetIsDirty(bool IsDirty)
		{
			_isDirty = IsDirty;
		}

		[Browsable(false)]
		public bool IsDirty
		{
			get
			{
				return _isDirty;
			}
		}

		#endregion
	} 

}
