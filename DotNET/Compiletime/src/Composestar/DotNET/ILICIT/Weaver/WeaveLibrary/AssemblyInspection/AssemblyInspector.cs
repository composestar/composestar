using System;
using System.Collections;

using Weavers.WeaveSpecifications;

namespace Weavers.AssemblyInspection
{
	/// <summary>
	/// Summary description for AssemblyInspector.
	/// </summary>
	public class AssemblyInspector
	{
		private static AssemblyInspector ai = null;
        private bool mQuiet = false;
		private bool mDebug = false;
		private string assemblyName = "";
		private Hashtable ClassCache = null;
		private ArrayList MissingClassList = null;
		private ArrayList MissingAssemblyList = null;
		
		private AssemblyInspector(bool quiet, bool debug)
		{
			this.mQuiet = quiet;
			this.mDebug = debug;

			this.ClassCache = new Hashtable();
			this.MissingClassList = new ArrayList();
			this.MissingAssemblyList = new ArrayList();
		}

		public static AssemblyInspector GetInstance(bool quiet, bool debug)
		{
			if (ai == null) 
			{
				ai = new AssemblyInspector(quiet, debug);
			}
            
			return ai;
		}


		public System.Reflection.Assembly MyResolveEventHandler(object sender, ResolveEventArgs args)
		{
			//This handler is called only when the common language runtime tries to bind to the assembly and fails.

			String searchPath = (String)System.AppDomain.CurrentDomain.GetData("AssemblySearchPath");

			if ( this.MissingAssemblyList.Contains(args.Name) ) return null;

			//Load the assembly from the specified path. 			
			try 
			{
				//Console.WriteLine(s.);
				Console.WriteLine("\nHandler takes over in search for '"+ args.Name + "' in folder '" + searchPath + "'.");
				//String pathName = s.ToString();
				//String name = args.Name.ToString();

				//String baseName =  pathName + "/ " + assName;
				System.Reflection.Assembly a = null;
//Console.WriteLine("Handler checking file: " + searchPath + args.Name.Substring(0, args.Name.IndexOf(",")) + ".dll");
				if ( System.IO.File.Exists(searchPath + args.Name.Substring(0, args.Name.IndexOf(",")) + ".dll") )
				{
					a = System.Reflection.Assembly.LoadFile(searchPath + args.Name.Substring(0, args.Name.IndexOf(",")) + ".dll");
				}
				else if ( System.IO.File.Exists(searchPath + args.Name.Substring(0, args.Name.IndexOf(","))+ ".exe") )
				{
					a = System.Reflection.Assembly.LoadFile(searchPath + args.Name.Substring(0, args.Name.IndexOf(",")) + ".exe");
				}
				else 
				{
					this.MissingAssemblyList.Add(args.Name);
					Console.WriteLine("WARNING: Unable to locate assembly '" + args.Name + "'.");
				}


				if (a != null) Console.WriteLine("Handler returns with assembly: " + a.FullName);
				//Return the loaded assembly.
				return a;			
			}
			catch (Exception e) 
			{
				Console.WriteLine("Error in ResolveEventHandler: "+e.Message);
			}

//Console.WriteLine("Handler returns.");
			return null;
		}


		private System.Type GetType(String assembly, String type)
		{
			Type result = null;
			System.Reflection.Assembly a = null;

			System.AppDomain.CurrentDomain.SetData("AssemblySearchPath", this.assemblyName);
			System.AppDomain.CurrentDomain.AssemblyResolve += new ResolveEventHandler(MyResolveEventHandler);

//			try 
//			{
//				result = Type.GetType(type);
//				if (result != null) 
//				{
//					Console.WriteLine("Assembly for type '" + type + "' already loaded.");
//					return result;
//				}
//			}
//			catch (Exception)
//			{
//				//Console.WriteLine(e.Message);
//			}


			IEnumerator loadedAssemblies = System.AppDomain.CurrentDomain.GetAssemblies().GetEnumerator();
			while (loadedAssemblies.MoveNext())
			{
				string name = ((System.Reflection.Assembly)loadedAssemblies.Current).FullName;
				name = name.Substring(0, name.IndexOf(","));
				if ( name.Equals(assembly) )
				{
					// Assembly already loaded
					a = (System.Reflection.Assembly)loadedAssemblies.Current;
					break;
				}
			}

			if ( a == null )
			{
				try 
				{
					//Console.WriteLine("Looking for assembly '" + this.assemblyName + assembly + ".dll' from disk...");

					if ( System.IO.File.Exists(this.assemblyName + assembly + ".dll") )
					{
						//System.AppDomain.CurrentDomain.GetAssemblies()
						//Console.WriteLine("Loading assembly '" + this.assemblyName + assembly + ".dll' from disk...");
						a = System.Reflection.Assembly.LoadFile(this.assemblyName + assembly + ".dll");
					}
					else if ( System.IO.File.Exists(this.assemblyName + assembly + ".exe") )
					{
						a = System.Reflection.Assembly.LoadFile(this.assemblyName + assembly + ".exe");
					}
				}
				catch (Exception)
				{ 
					//Console.WriteLine(e.Message);
				}
			}

			if ( a == null )
			{
				//String searchPath = (String)System.AppDomain.CurrentDomain.GetData("AssemblySearchPath");
				try 
				{
					//Console.WriteLine("Loading assembly '" + System.IO.Directory.GetCurrentDirectory() + assembly + ".dll' from disk...");
					if ( System.IO.File.Exists(System.IO.Directory.GetCurrentDirectory() + "\\" + assembly + ".dll") )
					{

						//System.AppDomain.CurrentDomain.GetAssemblies()
						//Console.WriteLine("Loading assembly '" + System.IO.Directory.GetCurrentDirectory() + "\\" + assembly + ".dll' from disk...");
						a = System.Reflection.Assembly.LoadFile(System.IO.Directory.GetCurrentDirectory() + "\\" + assembly + ".dll");
					}
					else if ( System.IO.File.Exists(System.IO.Directory.GetCurrentDirectory() + "\\" + assembly + ".exe") )
					{
						a = System.Reflection.Assembly.LoadFile(System.IO.Directory.GetCurrentDirectory() + "\\" + assembly + ".exe");
					}
				}
				catch (Exception e)
				{ 
					Console.WriteLine("Error during loading: " + e.Message);
				}		
				
			}

			if ( a != null ) 
			{
				//Console.WriteLine("Loaded assembly: " + a.FullName );
				try 
				{
					System.AppDomain.CurrentDomain.AssemblyResolve += new ResolveEventHandler(MyResolveEventHandler);
					
					result = a.GetType(type, true);
					//if (result != null) Console.WriteLine("Found type: " + result.Name );
				}
				catch (Exception)
				{
					//Console.WriteLine(e.Message);
				}
			}

			return result;
		}

		public bool IsMethod(String currentAssembly, IEnumerator enumAssemblies, String className, String methodName)
		{
			System.Type t = null;

			if ( this.MissingClassList.Contains(className) ) return false;

			if ( this.ClassCache.ContainsKey(className) ) 
			{
				// Already looked up this class and stored the assembly
				//Console.WriteLine("Cache hit for '" + className + "', assembly is '" + this.ClassCache[className].ToString() + "'.");
				t = GetType(this.ClassCache[className].ToString(), className);
			}

			if ( t == null )
			{
				this.assemblyName = currentAssembly.Substring(0, currentAssembly.LastIndexOf("/")+1);
				if ( this.assemblyName.Equals("") ) this.assemblyName = currentAssembly.Substring(0, currentAssembly.LastIndexOf("\\")+1);

				while (enumAssemblies.MoveNext())
				{
					AssemblyInformation assembly = (AssemblyInformation)((System.Collections.DictionaryEntry)enumAssemblies.Current).Value;
					if (!assembly.Remove) 
					{
						if ( this.MissingAssemblyList.Contains(assembly.Name) ) continue;

						//Console.WriteLine("IsMethod is looking in: " + assembly.Name + " for " + className);
						t = GetType( assembly.Name , className);

						if (t != null) 
						{
							//Console.WriteLine("Caching '" + className + "' with value '" + assembly.Name + "'.");
							this.ClassCache.Add(className, assembly.Name);
							break;
						}
					}
				}
			}
			

			if (t != null) 
			{
				//Console.WriteLine("Found type: " + t.ToString() + " | " + methodName);
				//methodName = "*";
				System.Reflection.MemberInfo[] mi = t.GetMember( methodName );
				
				//for(int index=0; index < mi.Length; index++)
				//	Console.WriteLine("Member {0}: {1}", index + 1, mi[index].ToString());

				if ( mi.Length > 0 )
				{
					//Console.WriteLine("IsMethod says: " + methodName + " is member of " + className);
					return true;
				}

				//Console.WriteLine("IsMethod says: " + methodName + " is not a member of " + className);
				return false;
			}

			// Maybe we should throw an error or notification, in fact we were unable to find a class
			// and cannot determine inheritance trees. This may lead to unexpected weave results.
			this.MissingClassList.Add(className);
			Console.WriteLine("WARNING: Unable to find class '" + className + "', inherited methods (if any) are not intercepted.");
			return false;
		}
	}
}
