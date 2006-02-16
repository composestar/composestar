using System;
using System.Collections;
using System.Collections.Specialized;
using System.Diagnostics;
using System.Runtime.InteropServices;

using Weavers.ExitCodes;

namespace Weavers.PeWeaver
{
	/// <summary>
	/// 
	/// </summary>
	class PeWeaver
	{
		static PeWeaverExitCodes exitcode = PeWeaverExitCodes.Success;
		static bool quiet = false;
		static bool debug = false;

		[DllImport("kernel32.dll")]
		extern static short QueryPerformanceCounter(ref long x);
		[DllImport("kernel32.dll")]
		extern static short QueryPerformanceFrequency(ref long x);

		private static void PrintLogo() 
		{
			Version PeWeaverVersion = System.Reflection.Assembly.GetExecutingAssembly().GetName().Version;
			Console.WriteLine("Compose* PE Weaver  Version " + PeWeaverVersion.ToString());
			Console.WriteLine("Copyright (C) 2003,2004 University of Twente.");
			Console.WriteLine();
		}

		private static void PrintHelp()
		{
			Console.WriteLine("Usage: peweaver [Options] <sourcefiles>");
			Console.WriteLine("");
			Console.WriteLine("Options:");
			Console.WriteLine("/NOLOGO");
			Console.WriteLine("/QUIET");
			Console.WriteLine("/DEBUG");
			Console.WriteLine("/VERIFY");
			Console.WriteLine("/BACKUP");
			Console.WriteLine("/OUT=<file name> (cannot be used with multiple sourcefiles)");
			Console.WriteLine("/WS=<weave specification>");
			Console.WriteLine("/?");
		}

		private static void SetExitCode(PeWeaverExitCodes code)
		{
			if ( exitcode == ExitCodes.PeWeaverExitCodes.Success ) 
			{
				exitcode = code;
			}
			else 
			{
				// An error already occurred
				exitcode = ExitCodes.PeWeaverExitCodes.WithWarnings;
			}
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="files">The list of files to backup.</param>
		/// <returns>The list of files which were actually backupped.</returns>
		private static StringCollection Backup(StringCollection files) 
		{
			StringCollection result = new StringCollection();

			// Process all files
			for (int i=0; i < files.Count; i++) 
			{
				string filename = files[i].ToString();

				if ( !System.IO.File.Exists(filename) ) 
				{
					if ( !quiet ) Console.WriteLine("Warning: file '" + filename + "' not found");
					SetExitCode(ExitCodes.PeWeaverExitCodes.InputFileNotFound);
				}
				else 
				{
					if ( !quiet) Console.WriteLine("Backing-up '" + filename + "'...");

					try 
					{
						System.IO.File.Copy(filename, filename + ".bak", true);
						result.Add(filename);
					}
					catch (System.IO.IOException) {}

				}
			}

			return result;
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="files">The list of files to disassemble.</param>
		/// <returns>The list of files which were actually disassembled.</returns>
		private static StringCollection Disassemble(StringCollection files) 
		{
			StringCollection result = new StringCollection();

			// Process all files
			for (int i=0; i < files.Count; i++) 
			{
				string filename = files[i].ToString();

				if ( filename.Length <= 4 || !System.IO.File.Exists(filename) ) 
				{
					if ( !quiet ) Console.WriteLine("Warning: file '" + filename + "' not found");
					SetExitCode(ExitCodes.PeWeaverExitCodes.InputFileNotFound);
				}
				else 
				{
					string filenameBase = filename.Substring(0, filename.Length-4);

					if ( !quiet) Console.WriteLine("Disassembling '" + filename + "'...");
						
					// Run ildasm on specified file
					ProcessManager pm = new ProcessManager("ildasm.exe", "/utf8 /nobar /raweh /out=\"" + filenameBase + ".il\" \"" + filename + "\"");
					pm.Run();

					if ( pm.Win32ExitCode != 0 ) 
					{
						if ( pm.Win32ExitCode == 2 ) 
						{
							if ( !quiet ) Console.WriteLine("Executable 'ildasm' could not be located");
							SetExitCode(ExitCodes.PeWeaverExitCodes.IldasmNotFound);
							return result;
						}
						else 
						{
							SetExitCode(ExitCodes.PeWeaverExitCodes.IldasmExecutionFailure);
						}

						//System.ComponentModel.Win32Exception w32e = new System.ComponentModel.Win32Exception(pm.Win32ExitCode);
						//Console.WriteLine("Win32 exit code: " + pm.Win32ExitCode + " (" + w32e.Message + ")");
					}
					else if (pm.ProcessExitCode != 0 ) 
					{
						IlWeaverExitCodes iec = (IlWeaverExitCodes)Enum.ToObject(typeof(IlWeaverExitCode), (int)pm.ProcessExitCode);
						ExitCodes.IlWeaverExitCode iwec = new ExitCodes.IlWeaverExitCode(iec);
						Console.WriteLine("Process exit code: " + pm.ProcessExitCode + " (" + iwec.Message + ")");
					}
					else 
					{
						result.Add(filename);
					}
				}
			}

			return result;
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="files">The list of files to weave.</param>
		/// <returns>The list of woven files.</returns>
		private static StringCollection Weave(string weavefile, StringCollection files, bool quiet, bool debug) 
		{
			StringCollection result = new StringCollection();

			string args = "";
			string filelist = "";

			args = "/nologo /ws=\"" + weavefile + "\"";
			if ( quiet ) args += " /quiet";
			if ( debug ) args += " /debug";

			string filelistFile = "";
			System.IO.StreamWriter sw = null;
			if ( files.Count > 20 )
			{
				filelistFile = "filelist.ilweaver";
				sw = new System.IO.StreamWriter(filelistFile, false);
			}

			// Process all files and create the file list
			for (int i=0; i < files.Count; i++) 
			{
				string filename = files[i].ToString();
				string filenameBase = "";

				if ( filename.LastIndexOf(".") <= filename.Length - 4 )
				{
					filenameBase = filename.Substring(0, filename.Length-4);
				}

				if ( !System.IO.File.Exists(filenameBase + ".il") ) 
				{
					if ( !quiet ) Console.WriteLine("Warning: file '" + filenameBase + ".il' not found");
					SetExitCode(ExitCodes.PeWeaverExitCodes.IlFileNotFound);
				}
				else 
				{
					if (sw == null)
					{
						filelist += " \"" + filenameBase + ".il\"";
					}
					else 
					{
						sw.WriteLine(filenameBase + ".il");
					}
					
					result.Add(filename);
				}
			}

			if (sw != null) 
			{
				sw.Flush();
				sw.Close();
			}
		
			if ( !quiet ) Console.WriteLine("Running IlWeaver...");

			// Run ilweaver on specified files
			if ( !filelistFile.Equals("") )
			{
				filelist = " /filelist=" + filelistFile;
			}
			ProcessManager pm = new ProcessManager("ilweaver.exe", args + " " + filelist);
			pm.DirectOutput = true;
			pm.Run();

			if ( pm.Win32ExitCode != 0 ) 
			{
				if ( pm.Win32ExitCode == 2 ) 
				{
					if ( !quiet ) Console.WriteLine("Executable 'ilweaver' could not be located");
					SetExitCode(ExitCodes.PeWeaverExitCodes.IlweaverNotFound);
					return result;
				}
				else 
				{
					SetExitCode(ExitCodes.PeWeaverExitCodes.IlweaverExecutionFailure);
				}

				//System.ComponentModel.Win32Exception w32e = new System.ComponentModel.Win32Exception(pm.Win32ExitCode);
				//Console.WriteLine("Win32 exit code: " + pm.Win32ExitCode + " (" + w32e.Message + ")");
			}
			else if (pm.ProcessExitCode != 0 ) 
			{
				IlWeaverExitCodes iec = (IlWeaverExitCodes)Enum.ToObject(typeof(IlWeaverExitCode), (int)pm.ProcessExitCode);
				IlWeaverExitCode iwec = new IlWeaverExitCode(iec);
				Console.WriteLine("Process exit code: " + pm.ProcessExitCode + " (" + iwec.Message + ")");
			}
			else 
			{
				
			}

			return result;
		}

		private static StringCollection Assemble(String filename, String outputname)
		{
			StringCollection inputname = new StringCollection();
			inputname.Add(filename);

			return Assemble(inputname, outputname);
		}

		private static StringCollection Assemble(StringCollection files)
		{
			return Assemble(files, "");
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="files">The list of files to assemble.</param>
		/// <returns>The list of assembled files.</returns>
		private static StringCollection Assemble(StringCollection files, String outputname) 
		{
			StringCollection result = new StringCollection();

			// Process all files
			for (int i=0; i < files.Count; i++) 
			{
				string filename = files[i].ToString();
				string filenameBase = "";
				string filenameExt = "";

				if ( filename.LastIndexOf(".") <= filename.Length - 4 )
				{
					filenameBase = filename.Substring(0, filename.Length-4);
					filenameExt = filename.Substring(filename.Length-3, 3);
				}

				if ( !System.IO.File.Exists(filenameBase + ".il") ) 
				{
					if ( !quiet ) Console.WriteLine("Warning: file '" + filename + "' not found");
					SetExitCode(ExitCodes.PeWeaverExitCodes.IlFileNotFound);
				}
				else 
				{
					if ( !quiet) 
					{
						if (filenameExt.ToLower().Equals("exe"))
						{
							Console.WriteLine("Assembling '" + filenameBase + ".il' into executable...");
						}
						else if (filenameExt.ToLower().Equals("dll"))
						{
							Console.WriteLine("Assembling '" + filenameBase + ".il' into library (dll)...");
						}
						else 
						{
							Console.WriteLine("Unsupported file format '" + filenameExt + "'!");
							SetExitCode(ExitCodes.PeWeaverExitCodes.UnsupportedFileFormat);
						}
					}

					if (filenameExt.ToLower().Equals("exe") || filenameExt.ToLower().Equals("dll"))
					{
						string format = "";
						string output = "";

						if ( filenameExt.ToLower().Equals("exe") ) format = " /exe";
						else format = " /dll";
						
						if ( files.Count == 1 && !outputname.Equals("") )
						{
							output = " /output=\"" + outputname + "\"";
						}
						else 
						{
							output = " /output=\"" + filename + "\"";
						}
					
						// Run ilasm on specified file
						ProcessManager pm = new ProcessManager("ilasm.exe", "/nologo /debug" + format + output + " \"" + filenameBase + ".il\"");
						pm.Run();

						if ( pm.Win32ExitCode != 0 ) 
						{
							if ( pm.Win32ExitCode == 2 ) 
							{
								if ( !quiet ) Console.WriteLine("Executable 'ilasm' could not be located");
								SetExitCode(ExitCodes.PeWeaverExitCodes.IlasmNotFound);
								return result;
							}
							else 
							{
								SetExitCode(ExitCodes.PeWeaverExitCodes.IlasmExecutionFailure);
								if ( !quiet ) Console.WriteLine("     ERROR: assembling '" + filename + "' failed!");
							}
						}
						else if (pm.ProcessExitCode != 0 ) 
						{
							SetExitCode(ExitCodes.PeWeaverExitCodes.AssembleFailure);
							if ( !quiet ) Console.WriteLine("     ERROR: assembling '" + filename + "' failed!");
						}
						else 
						{
							result.Add(filename);
						}
					}
				}
			}

			return result;
		}

		/// <summary>
		/// 
		/// </summary>
		/// <param name="files">The list of files to verify.</param>
		/// <returns>The list of verified files.</returns>
		private static StringCollection Verify(StringCollection files) 
		{
			StringCollection result = new StringCollection();

			// Process all files
			for (int i=0; i < files.Count; i++) 
			{
				string filename = files[i].ToString();

				if ( !System.IO.File.Exists(filename) ) 
				{
					if ( !quiet ) Console.WriteLine("Warning: file '" + filename + "' not found");
					SetExitCode(ExitCodes.PeWeaverExitCodes.OutputFileNotFound);
				}
				else 
				{
					if ( !quiet) Console.WriteLine("Verifying '" + filename + "'...");

					// Run peverify on specified file
					ProcessManager pm = new ProcessManager("peverify.exe", "\"" + filename + "\"");
					pm.Run();

					if ( pm.Win32ExitCode != 0 ) 
					{
						if ( pm.Win32ExitCode == 2 ) 
						{
							if ( !quiet ) Console.WriteLine("Executable 'peverify' could not be located");
							SetExitCode(ExitCodes.PeWeaverExitCodes.PeverifyNotFound);
							return result;
						}
						else 
						{
							SetExitCode(ExitCodes.PeWeaverExitCodes.PeverifyExecutionFailure);
						}

						//System.ComponentModel.Win32Exception w32e = new System.ComponentModel.Win32Exception(pm.Win32ExitCode);
						//Console.WriteLine("Win32 exit code: " + pm.Win32ExitCode + " (" + w32e.Message + ")");
					}
					else if (pm.ProcessExitCode != 0 ) 
					{
						SetExitCode(ExitCodes.PeWeaverExitCodes.VerificationFailure);
						if ( !quiet ) Console.WriteLine("     WARNING: verification of '" + filename + "' failed!");
					}
					else 
					{
						result.Add(filename);
					}
					
				}
			}

			return result;
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
			bool verify = false;
			string weaveSpecificationFile = "";
			string outFile = "";
			string fileList = "";
			StringCollection files = new StringCollection();
			bool inspectingOptions = true;

			// Inspect arguments
			for (int i=0; i < args.Length; i++)
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
					else if (args[i].ToUpper().Equals("-VERIFY") || args[i].ToUpper().Equals("/VERIFY"))
					{
						verify = true;
					}
					else if (args[i].ToUpper().Equals("-HELP") || args[i].ToUpper().Equals("/HELP") || args[i].Equals("-?") || args[i].Equals("/?"))
					{
						showhelp = true;
					}
					else if (args[i].ToUpper().StartsWith("-WS=") || args[i].ToUpper().StartsWith("/WS="))
					{
						weaveSpecificationFile = args[i].Substring(4);
					}
					else if (args[i].ToUpper().StartsWith("-OUT=") || args[i].ToUpper().StartsWith("/OUT="))
					{
						outFile = args[i].Substring(5);
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
			}
			else 
			{
				long StartTime=0, EndTime=0;
				bool HighTimerEnabled=false;
				long StartCounter=0, EndCounter = 0, freq=1;
				
				if (debug) 
				{
					if (QueryPerformanceCounter(ref StartCounter)!=0)
					{
						HighTimerEnabled = true;
					}
					else
					{
						StartTime = DateTime.Now.Ticks;
						HighTimerEnabled = false;
					}
				}

				if ( !fileList.Equals("")) 
				{
					if ( System.IO.File.Exists(fileList) ) 
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
					else 
					{
						Console.WriteLine("Error: File containting the filelist not found!");
					}
				}

				int filecount = files.Count;

				// Verify files
				if ( verify )
				{
					files = Verify(files);
				}

				int verifiedfilecount = files.Count;

				// Backup files
				if ( backup ) 
				{
					files = Backup(files);
				}

				// Disassemble all files
				long DisassembleStartCounter = 0, DisassembleEndCounter = 0;
				if (debug)
				{
					if (HighTimerEnabled)
					{
						QueryPerformanceCounter(ref DisassembleStartCounter);
					}
					else 
					{
						//EndTime = DateTime.Now.Ticks;
					}
				}
				files = Disassemble(files);
				int disassembledfilecount = files.Count;
				if (debug) 
				{
					double WeaveTime=0;

					if (HighTimerEnabled)
					{	
						QueryPerformanceCounter(ref DisassembleEndCounter);	
						QueryPerformanceFrequency(ref freq);
						WeaveTime = (double)(DisassembleEndCounter - DisassembleStartCounter) / freq;
					}
					else
					{
						//WeaveTime = new TimeSpan(EndTime - StartTime).Seconds;
					}
					Console.WriteLine("Disassembling summary: Files: {0}   Time: {1:0.00}s\n", disassembledfilecount, WeaveTime);
				}
				
				// Weave files
				files = Weave( weaveSpecificationFile, files, quiet, debug);
				int weavedfilecount = files.Count;
				Console.WriteLine("");

				// Assemble files
				long AssembleStartCounter = 0, AssembleEndCounter = 0;
				if (debug)
				{
					if (HighTimerEnabled)
					{
						QueryPerformanceCounter(ref AssembleStartCounter);
					}
					else 
					{
						//EndTime = DateTime.Now.Ticks;
					}
				}
				if ( files.Count == 1 && !outFile.Equals("") )
				{
					files = Assemble(files[0], outFile);
				}
				else 
				{
					files = Assemble(files);
				}
				int assembledfilecount = files.Count;
				if (debug) 
				{
					double WeaveTime=0;

					if (HighTimerEnabled)
					{	
						QueryPerformanceCounter(ref AssembleEndCounter);	
						QueryPerformanceFrequency(ref freq);
						WeaveTime = (double)(AssembleEndCounter - AssembleStartCounter) / freq;
					}
					else
					{
						//WeaveTime = new TimeSpan(EndTime - StartTime).Seconds;
					}
					Console.WriteLine("Assembling summary: Files: {0}   Time: {1:0.00}s\n", assembledfilecount, WeaveTime);
				}

				// Verify files
				if ( verify )
				{
					files = Verify(files);
				}

				int correctfilecount = files.Count;

				if (debug)
				{
					if (HighTimerEnabled)
					{
						QueryPerformanceCounter(ref EndCounter);
					}
					else 
					{
						EndTime = DateTime.Now.Ticks;
					}
				}

				if (debug) 
				{
					double WeaveTime=0;

					if (HighTimerEnabled)
					{								
						QueryPerformanceFrequency(ref freq);
						WeaveTime = (double)(EndCounter - StartCounter) / freq;
					}
					else
					{
						WeaveTime = new TimeSpan(EndTime - StartTime).Seconds;
					}
 
					Console.WriteLine("\n\nFile summary: submitted {0}  accepted {1}  disassembled {2}  woven: {3}  assembled {4}  verified {5}", filecount, verifiedfilecount, disassembledfilecount, weavedfilecount, assembledfilecount, correctfilecount);
					Console.WriteLine("Total procesing time: {0:0.00}s",  WeaveTime);
				}
			}

			ExitCodes.PeWeaverExitCode pwec = new ExitCodes.PeWeaverExitCode(exitcode);
			Console.WriteLine("\nExitcode returned: " + (int)exitcode + " - " + pwec.Message);
			Environment.Exit((int)exitcode);
		}
	}
}
