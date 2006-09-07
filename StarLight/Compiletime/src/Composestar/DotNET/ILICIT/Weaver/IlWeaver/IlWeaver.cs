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
			}
			else 
			{
				// Backup files
				if ( backup ) 
				{
					//TODO: files = Backup(files);
				}

				WeaveSpecification ws = new WeaveSpecification(quiet, debug);

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

				if ( ws.Load(weaveSpecificationFile) )
				{

					for (int i=0; i < files.Count; i++) 
					{
						long StartTime=0, FileReadEndTime=0, WeaveEndTime=0, FileWriteEndTime=0;
						bool HighTimerEnabled=false;
						long StartCounter=0, FileReadCounter=0, WeaveCounter=0, FileWriteCounter=0, freq=0;

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

                        IlFileReader ifr = new IlFileReader(quiet, debug);
						ArrayList il = ifr.Read(files[i]);

						if (debug)
						{
							if (HighTimerEnabled)
							{
								QueryPerformanceCounter(ref FileReadCounter);
							}
							else 
							{
								FileReadEndTime = DateTime.Now.Ticks;
							}
						}

						ws.CurrentAssembly = files[i];
						if (debug && !quiet) Console.WriteLine("Starting weaving of '" + ws.CurrentAssembly + "'");
						Weaver w = new Weaver(quiet, debug);
						il = w.Process(ws, il, quiet, debug);

						if (debug)
						{
							if (HighTimerEnabled)
							{
								QueryPerformanceCounter(ref WeaveCounter);
							}
							else 
							{
								WeaveEndTime = DateTime.Now.Ticks;
							}
						}

						IlFileWriter ifw = new IlFileWriter(quiet, debug);
						ifw.Save(il, files[i]);

						if (debug)
						{
							if (HighTimerEnabled)
							{
								QueryPerformanceCounter(ref FileWriteCounter);
							}
							else 
							{
								FileWriteEndTime = DateTime.Now.Ticks;
							}
						}

						if (debug) 
						{
							double FileReadTime=0, WeaveTime=0, FileWriteTime=0;

							if (HighTimerEnabled)
							{								
								QueryPerformanceFrequency(ref freq);
								FileReadTime = (double)(FileReadCounter - StartCounter) / freq;
								WeaveTime = (double)(WeaveCounter - FileReadCounter) / freq;
								FileWriteTime = (double)(FileWriteCounter - WeaveCounter) / freq;
							}
							else
							{
								FileReadTime = new TimeSpan(FileReadEndTime - StartTime).Seconds;
								WeaveTime = new TimeSpan(WeaveEndTime - FileReadEndTime).Seconds;
								FileWriteTime = new TimeSpan(FileWriteEndTime - WeaveEndTime).Seconds;
							}

							Console.WriteLine("Summary for '" + ws.CurrentAssembly + "': Reading file: {0:0.00}s   Weaving file: {1:0.00}s   Writing file: {2:0.00}s", FileReadTime, WeaveTime, FileWriteTime);
						}
					}
				}
			}

			Environment.Exit((int)exitcode);
		}
	}
}
