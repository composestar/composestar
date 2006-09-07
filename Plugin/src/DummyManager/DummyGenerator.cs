using System;
using System.IO;
using System.CodeDom;
using System.CodeDom.Compiler;
using Microsoft.VJSharp;
using Microsoft.CSharp;
using System.Collections;
using System.Collections.Specialized;
using System.Globalization;

namespace Composestar.StarLight.VSAddin
{
	/// <summary>
	/// Summary description for DummyManager.
	/// </summary>
	public class DummyGenerator
	{
		private ICodeCompiler compiler;
		private ICodeGenerator generator;
		private ArrayList dummies;
		private string dummy_extension = ".cs";

		public DummyGenerator(string compiler_to_use)
		{
			dummies = new ArrayList();

			if (compiler_to_use.Equals("JS")) 
			{
				// Only generate dummies in J# if source if J#
				VJSharpCodeProvider jsProvider = new VJSharpCodeProvider();
				compiler = jsProvider.CreateCompiler();
				generator = jsProvider.CreateGenerator();
				dummy_extension = ".jsl";
			}
			else {
				// Generate dummies in C# by default
				CSharpCodeProvider csProvider = new CSharpCodeProvider();
				compiler = csProvider.CreateCompiler();
				generator = csProvider.CreateGenerator();
			}
		}

		public void createDummy(EnvDTE.CodeClass aClass) 
		{
			Dummy dummy = new Dummy();
			// register the dummy so it will be written
			this.dummies.Add(dummy);
			dummy.constructClass(aClass);
		}

		public void createDummy(EnvDTE.CodeInterface aInterface) 
		{
			Dummy dummy = new Dummy();

			// register the dummy so it will be written
			this.dummies.Add(dummy);
			dummy.constructInterface(aInterface);
		}

		public void createDummy(EnvDTE.CodeEnum aEnum) 
		{
			Dummy dummy = new Dummy();

			// register the dummy so it will be written
			this.dummies.Add(dummy);
			dummy.constructEnum(aEnum);
		}

		public string generateDummies(String solutionPath, String referencedAssemblies, out CompilerResults compilerResults) 
		{
			  
			compilerResults = null;
			if( dummies.Count == 0 )
				return null;

			String dummyPath = solutionPath + "obj\\";
			System.IO.Directory.CreateDirectory(dummyPath);

			CodeGeneratorOptions options = new CodeGeneratorOptions();
			CompilerParameters parameters = new CompilerParameters();

			
			string[] files = new string[dummies.Count];

			int index = 0;
			
			foreach(Dummy dummy in this.dummies ) 
			{
				CodeCompileUnit compileUnit = dummy.getCompileUnit();
				string srcPath = String.Concat(dummyPath, dummy.getName(), dummy_extension);
				string srcPathUser = String.Concat(dummyPath, dummy.getName(), ".user" , dummy_extension);

				// Delete dummy if it exists
				if (File.Exists(srcPath))
				{
					File.Delete(srcPath); 
				}

				// Copy user dummy when available
				if (File.Exists(srcPathUser))
				{
					// Copy file
					File.Copy( srcPathUser, srcPath, true);
					// Warn user
					Debug.Instance.Log(DebugModes.Warning, "Dummy Generator", 
						String.Format(CultureInfo.CurrentCulture, "The Dummy Manager is using an user defined file '{0}' as dummy. Make sure this file is up-to-date with your source file '{1}'.", srcPathUser,srcPath), srcPathUser, 0);    
				}
				else
				{
					// Generate a new dummy
					TextWriter tw = new StreamWriter(srcPath);
					generator.GenerateCodeFromCompileUnit(compileUnit, tw, options);
					tw.Close();
				}
			
				files[index] = srcPath;
				index ++;
			}

			string dllPath = Path.Combine(dummyPath, "dummies.dll");

			parameters.OutputAssembly = dllPath;
			parameters.CompilerOptions += referencedAssemblies;
			
			compilerResults  = compiler.CompileAssemblyFromFileBatch(parameters, files);
	
			dummies.Clear();
			return dllPath;
		
		}
	}
}
