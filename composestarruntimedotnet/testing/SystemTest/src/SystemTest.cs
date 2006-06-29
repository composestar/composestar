using System;
using System.Collections;
using System.Diagnostics;
using System.Web.Mail;
using System.Text;
using System.Threading; 
using System.IO;
using Ini;

namespace SystemTest
{
	/// <summary>
	/// Summary description for SystemTest
	/// Compiles and runs examples defined in a configuration file
	/// </summary>
	class SystemTest
	{
		private String inifile;
		private ArrayList examples = null;
		private ArrayList runnables = null;
		private ArrayList comp_errors = null;
		private ArrayList run_errors = null;
		private ArrayList comp_failed = null;
		private ArrayList run_failed = null;
		
		private int buildSuccesses = 0;
		private int buildFailures = 0;
		private int runSuccesses = 0;
		private int runFailures = 0;

		private String sender;
		private String recipient;
		private String smtpserver;
		private String mailEnabled = "false";

		private String EXECUTION_AND_SAVE = "";
		private String BUILD_LOG = "buildlog.txt";
		private String EXECUTION_LOG = "runlog.txt";
		private String CORRECT_EXECUTION_LOG = "";

		public SystemTest(String inifile)
		{
			this.inifile = inifile;
			this.examples = new ArrayList();
			this.runnables = new ArrayList();
			this.comp_errors = new ArrayList();
			this.comp_failed = new ArrayList();
			this.run_errors = new ArrayList();
			this.run_failed = new ArrayList();
		}


		public void addCompFailure(string example, string err)
		{
			this.comp_errors.Add(err);
			this.comp_failed.Add(example);
		}

		public void addRunFailure(string example, string err)
		{
			this.run_errors.Add(err);
			this.run_failed.Add(example);
		}

		/**
		 * Compiles examples one by one
		 */
		public void compileExamples()
		{
			IEnumerator enumExamples = this.examples.GetEnumerator();
			while ( enumExamples.MoveNext() ) 
			{
				this.compileExample((String)enumExamples.Current);
			}

			Console.WriteLine("");
			Console.WriteLine("all examples compiled...");
			Console.WriteLine("");
		}

		/**
		 * Compiles an example
		 * The example is built with the help of a Visual Studio Macro
		 */
		public void compileExample(String example)
		{
			Console.WriteLine("compiling example: "+example+"...");
			Process p = new Process();

			p.StartInfo.FileName= "devenv"; 
			p.StartInfo.Arguments= "/command Macros.MyMacros.SystemTest.BuildWithComposeStarAndExit \""+ example+ '\"'; 
			p.StartInfo.CreateNoWindow= true; 
			p.StartInfo.UseShellExecute = false;
			try
			{
				p.Start();
				p.WaitForExit();
			}
			catch (System.ComponentModel.Win32Exception wn)
			{
				Console.WriteLine(wn.Message);
			}
			
			this.onBuildDone(example);
		}

		/**
		 * runs examples one by one
		 */
		public void executeExamples()
		{
			IEnumerator enumExamples = this.runnables.GetEnumerator();
			while ( enumExamples.MoveNext() ) 
			{
				this.executeExample((String)enumExamples.Current);
			}
		}
		

		/**
		 * Executes an example
		 * The output of the example is stored in a file
		 */
		public void executeExample(String example)
		{
			int index = example.LastIndexOf("/");
			
			String binDir = ".";
			if(index > 0)
			{
				 binDir = example.Substring(0, index);
			}

			String exec = example.Substring(index+1);
           	Process p = new Process();
			p.StartInfo.FileName = this.EXECUTION_AND_SAVE;
			p.StartInfo.Arguments= exec + " \"" + binDir+ "\" \"" +binDir+'/'+this.EXECUTION_LOG + "\""; 
			p.StartInfo.CreateNoWindow= true; 
			p.StartInfo.RedirectStandardError= false; 
			
			try{
				p.Start();
				Console.WriteLine("running example: "+example);
				p.WaitForExit();
			}
			catch (Exception wn)
			{
				String inner = "";
				while(wn != null)
				{
					Console.WriteLine(inner + "Exception:" + wn.Message);
					inner += "Inner";
					wn = wn.InnerException;
				}
			}
			
			this.onExecutionDone(example);
		}


		/**
		 * Creates a report of the system test
		 * Sends the report by mail
		 */
		public void mailResults()
		{
			try
			{
				MailMessage oMsg = new MailMessage();
				oMsg.From = this.sender;
				oMsg.To = this.recipient;
				oMsg.Subject = "System test results: "+DateTime.Today.ToLongDateString();

				StringBuilder message = new StringBuilder();
				message.Append("System test results: "+DateTime.Today.ToLongDateString()+"\n");
				message.Append("Successfull compilations: "+(this.buildSuccesses)+"\n");
				message.Append("Failed compilations ("+(this.buildFailures)+"):\n");
				
				if(this.buildFailures>0)
				{
					IEnumerator failures = this.comp_failed.GetEnumerator();
					while ( failures.MoveNext() ) 
					{
						message.Append("\t"+(String)failures.Current + "\n");
					}
				}	
				
				message.Append("Successfull executions: "+(this.runSuccesses)+"\n");
				message.Append("Failed executions ("+(this.runFailures)+"):\n");
			
				if(this.runFailures>0)
				{
					IEnumerator runs = this.run_failed.GetEnumerator();
					while ( runs.MoveNext() ) 
					{
						message.Append("\t"+(String)runs.Current + "\n");
					}
				}

				Console.WriteLine(message.ToString());

                if(this.buildFailures>0)
				{
					message.Append("\nCompilation errors:\n");
					IEnumerator builderr = this.comp_errors.GetEnumerator();
					while ( builderr.MoveNext() ) 
					{
						message.Append((String)builderr.Current + "\n");
						message.Append("*******************************\n");
					}
				}

				if(this.runFailures>0)
				{
					message.Append("\nExecution errors:\n");
					IEnumerator runerr = this.run_errors.GetEnumerator();
					while ( runerr.MoveNext() ) 
					{
						message.Append((String)runerr.Current + "\n");
						message.Append("*******************************\n");
					}
				}

				// set body of mail
				oMsg.Body = message.ToString();

				// set smtp server and send mail
				SmtpMail.SmtpServer = this.smtpserver;
				
				if(this.mailEnabled.CompareTo("true")>=0)
				{
					Console.WriteLine("Mailing results...");
					SmtpMail.Send(oMsg);
				}
			}
			catch (Exception e)
			{
				Console.WriteLine("{0} Exception caught.", e);
			}
		}

		/**
		 * Method called after an example is built
		 * Tests whether built was succesfull or not
		 * If not, adds build output to compilation errors
		 */
		public void onBuildDone(string example)
		{
			// Get contents build log file
				
			String example_path = example.Substring(0,example.LastIndexOf("\\"));
			String buildoutput = "";
						
			FileManager fm = new FileManager();

			buildoutput = fm.getContents(example_path.Replace("\"","")+"\\"+this.BUILD_LOG); 
				
			// if buildoutput contains 'build complete' then successfull
			// otherwise, build was not successfull
			if(buildoutput.IndexOf("build complete")>0)
			{	//build successfull
				//Console.WriteLine("successful");
				this.buildSuccesses++;
			}
			else 
			{
				//build failed
				Console.WriteLine("failed");
				this.buildFailures++;
				this.addCompFailure(example, buildoutput);
			}
			
		}

		/**
		 * Method called after an example is executed
		 * Tests whether execution was succesfull or not
		 * If not, adds error to execution errors
		 */
		public void onExecutionDone(string example)
		{
			FileManager fm = new FileManager();

			// Get bin directory of example
			int index = example.LastIndexOf('/');
			
			String binDir = ".";
			if(index > 0)
			{
				binDir = example.Substring(0, index);
			}


			// Get log of execution
			string log = fm.getContents(binDir+ '/' + this.EXECUTION_LOG);

			// Get the 'correct' output
			string correct = fm.getContents(binDir+ '/' + this.CORRECT_EXECUTION_LOG);

			if(log.Equals(correct))
			{
				// run was successfull
				this.runSuccesses++;
			}
			else 
			{
				// run was not successfull
				this.runFailures++;
				string reason = log + "\n";

				if(correct=="")
				{
					reason += "Correct output for example "+example+" not found!\n";
				}

				this.addRunFailure(example,reason);
			}
		}

		/**
		 * Add one directory with examples
		 */
		public void readExampleDirectory(String examplePath)
		{
			if (!Directory.Exists(examplePath))
			{
				Console.WriteLine("ERROR: Directory of examples cannot be found!");
				Environment.Exit(1);
			}
			else 
			{
				Console.WriteLine("Specified example directory:" + examplePath);
				// collect all .sln files in second level
				DirectoryInfo exampleDir = new DirectoryInfo(examplePath);
				for(int i=0;i<exampleDir.GetDirectories().Length;i++)
				{
					FileInfo[] projects = exampleDir.GetDirectories()[i].GetFiles("*.sln");
					for(int k=0;k<projects.Length;k++)
					{
						this.examples.Add(projects[k].FullName);
					}
				}	
			}
		}

		/**
		 * Extract configurations from config file 
		 */
		public void readConfigFile()
		{
			// Make sure the .ini file does exist
			if (!System.IO.File.Exists(this.inifile)) 
			{
				Console.WriteLine("ERROR: Cannot find inifile '" + this.inifile + "'!");
				Environment.Exit(1);
			}

			// Create ini reader
			System.IO.FileInfo fi = new System.IO.FileInfo(this.inifile);
			Ini.IniFile ini = new Ini.IniFile(fi.FullName);

			// Read [Examples] part
			int exampleIndex = 0;
			string examplefile = ini.IniReadValue("EXAMPLES", "Example" + exampleIndex);
			while(! "".Equals(examplefile)) 
			{
				Console.WriteLine("Specified example:" + examplefile);
				examples.Add(examplefile);
				exampleIndex++;
				examplefile = ini.IniReadValue("EXAMPLES", "Example" + exampleIndex);
			}
			//check for a single path
			String examplePath = ini.IniReadValue("EXAMPLES","ExampleDirectory");
			if("".Equals(examplePath))
			{
				//check for a multiple paths
				examplePath = ini.IniReadValue("EXAMPLES","ExampleDirectory0");
				if("".Equals(examplePath))
				{
					//no paths found
					if(examples.Count <= 0)
					{
						Console.WriteLine("ERROR: No examples specified!");
						Environment.Exit(1);
					}
				}
				else
				{
					//add multiple paths
					int examplePathIndex = 0;
					while ( (examplePath = ini.IniReadValue("EXAMPLES", "ExampleDirectory" + examplePathIndex)) != "" ) 
					{
						readExampleDirectory(examplePath);
						examplePathIndex++;
					}
				}
			}
			else
			{
				//add single path
				readExampleDirectory(examplePath);
			}

			// Read [RUNNABLE_EXAMPLES] part
			int j = 0;
			string runnablefile = "";
			while ( (runnablefile = ini.IniReadValue("RUNNABLE_EXAMPLES", "Example" + j)) != "" ) 
			{
				runnables.Add(runnablefile);
				j++;
			}

			// Read [Email] part
			this.mailEnabled = ini.IniReadValue("EMAIL", "Enabled");
			this.sender = ini.IniReadValue("EMAIL", "Sender");
			this.recipient = ini.IniReadValue("EMAIL", "Recipient");
			this.smtpserver = ini.IniReadValue("EMAIL", "SmtpServer");

			// Read extern executables
			this.EXECUTION_AND_SAVE = ini.IniReadValue("EXTERNALS","Execute");

			// Read Logs part
			this.EXECUTION_LOG = ini.IniReadValue("LOGS","ExecutionLog"); 
			this.CORRECT_EXECUTION_LOG = ini.IniReadValue("LOGS","CorrectLog"); 
		}

		/**
		 * Start the system test
		 */
		public void run()
		{
			Console.WriteLine("Reading Configuration File");
			this.readConfigFile();
			Console.WriteLine("Compiling Examples");
			this.compileExamples();
			Console.WriteLine("Executing Examples");
			this.executeExamples();
			
			// mail results
			this.mailResults();
		}

		/// <summary>
		/// The main entry point for the application.
		/// </summary>
		[STAThread]
		static void Main(string[] args)
		{
			if (args.Length == 1) 
			{
				SystemTest stm = new SystemTest(args[0]);
				Console.WriteLine("");
				Console.WriteLine("systemtest started...");
				stm.run();
			}
			else 
			{
				Console.WriteLine("Usage: SystemTest <inifile>");
			}
		}
	}
}
