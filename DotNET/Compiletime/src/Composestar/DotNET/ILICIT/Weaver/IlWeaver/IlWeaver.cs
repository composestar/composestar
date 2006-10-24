using System;
using System.Collections;
using System.Collections.Specialized;
using System.Runtime.InteropServices;

using Weavers.ExitCodes;
using Weavers.IlWeaving;
using Weavers.IO;
using Weavers.WeaveSpecifications;

namespace Weavers.IlWeaver
{
	/// <summary>
	/// Summary description for Class1.
	/// </summary>
	class IlWeaver
	{
		static IlWeaverExitCodes exitcode = IlWeaverExitCodes.Success;
		static bool quiet = false;
		static bool debug = false;

		[DllImport("kernel32.dll")]
		extern static short QueryPerformanceCounter(ref long x);
		[DllImport("kernel32.dll")]
		extern static short QueryPerformanceFrequency(ref long x);

		private static void PrintLogo() 
		{
			Version IlWeaverVersion = System.Reflection.Assembly.GetExecutingAssembly().GetName().Version;
			Console.WriteLine("Compose* IL Weaver  Version " + IlWeaverVersion.ToString());
			Console.WriteLine("Copyright (C) 2003,2004 University of Twente.");
			Console.WriteLine();
		}

		private static void PrintHelp()
		{
			Console.WriteLine("Usage: ilweaver [Options] <sourcefiles>");
			Console.WriteLine("");
			Console.WriteLine("Options:");
			Console.WriteLine("/NOLOGO");
			Console.WriteLine("/QUIET");
			Console.WriteLine("/DEBUG");
			Console.WriteLine("/BACKUP");
			Console.WriteLine("/WS=<weave specification>");
			Console.WriteLine("/?");
		}

		/// <summary>
		/// The main entry point for the application.
		/// </summary>
		[STAThread]
		static void Main(string[] args)
		{
			bool nologo = false;
			bool showhelp = false;
			bool backup = false;
			string weaveSpecificationFile = "";
			string fileList = "";
			StringCollection files = new StringCollection();
			bool inspectingOptions = true;

			// Inspect arguments
			for (int i = 0; i < args.Length; i++)
			{
				if (inspectingOptions) 
				{
					if (args[i].ToUpper().Equals("-NOLOGO") || args[i].ToUpper().Equals("/NOLOGO")) 
					{
						nologo = true;
					}
					else if (args[i].ToUpper().Equals("-QUIET") || args[i].ToUpper().Equals("/QUIET"))
					{
						quiet = true;
					}
					else if (args[i].ToUpper().Equals("-DEBUG") || args[i].ToUpper().Equals("/DEBUG"))
					{
						debug = true;
					}
					else if (args[i].ToUpper().Equals("-BACKUP") || args[i].ToUpper().Equals("/BACKUP"))
					{
						backup = true;
					}
					else if (args[i].ToUpper().Equals("-HELP") || args[i].ToUpper().Equals("/HELP") || args[i].Equals("-?") || args[i].Equals("/?"))
					{
						showhelp = true;
					}
					else if (args[i].ToUpper().StartsWith("-WS=") || args[i].ToUpper().StartsWith("/WS="))
					{
						weaveSpecificationFile = args[i].Substring(4);
					}
					else if (args[i].ToUpper().StartsWith("-FILELIST=") || args[i].ToUpper().StartsWith("/FILELIST="))
					{
						fileList = args[i].Substring(10);
					}
					else 
					{
						inspectingOptions = false;
						files.Add(args[i]);
					}
				}
				else 
				{
					files.Add(args[i]);
				}
			}

			// Show logo, unless explicitly disabled
			if (!nologo) 
			{
				PrintLogo();
			}

			// Show help on request and with invalid arguments
			if (weaveSpecificationFile.Equals("") || (files.Count == 0 && fileList.Equals("")) || showhelp)
			{
				PrintHelp();
				Environment.Exit((int)IlWeaverExitCodes.UnknownFailure);
			}
			else 
			{
				if ( !fileList.Equals("")) 
				{
					System.IO.StreamReader sr = System.IO.File.OpenText(fileList);
					string line = "";
					while (sr.Peek() >= 0)
					{
						line = sr.ReadLine().Replace(";", ",");
						files.AddRange(line.Split(','));
					}
					sr.Close();
				}

				// Backup files
				if (backup)
				{
					//TODO: files = Backup(files);
				}

				try
				{
					exitcode = ProcessFiles(files, weaveSpecificationFile);
					Environment.Exit((int)exitcode);
				}
				catch (Exception e)
				{
					Console.WriteLine(e.ToString());
					Environment.Exit((int)IlWeaverExitCodes.UnknownFailure);
				}
			}
		}

		private static IlWeaverExitCodes ProcessFiles(StringCollection files, string weaveSpecificationFile)
		{
			WeaveSpecification ws = new WeaveSpecification(quiet, debug);
			if (!ws.Load(weaveSpecificationFile))
				return IlWeaverExitCodes.WeaveFileNotFound;

			for (int i = 0; i < files.Count; i++)
			{
				IlFileReader ifr = new IlFileReader(quiet, debug);
				ArrayList il = ifr.Read(files[i]);

				ws.CurrentAssembly = files[i];
				if (debug && !quiet) Console.WriteLine("Starting weaving of '" + ws.CurrentAssembly + "'");
				Weaver w = new Weaver(quiet, debug);
				il = w.Process(ws, il, quiet, debug);

				IlFileWriter ifw = new IlFileWriter(quiet, debug);
				ifw.Save(il, files[i]);
			}

			return IlWeaverExitCodes.Success;
		}
	}
}
