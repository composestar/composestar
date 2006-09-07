using System;
using System.Collections.Specialized;
using System.Diagnostics;

namespace Weavers.PeWeaver
{
	/// <summary>
	/// Summary description for ProcessManager.
	/// </summary>
	public class ProcessManager
	{
		private Process mProcess = null;
		private int mProcessExitCode = -1;
		private int mWin32ExitCode = 0;
		private bool mDirectOutput = false;
		private StringCollection mOutput;

		public ProcessManager()
		{
			this.mProcess = new Process();
			this.mProcess.StartInfo.CreateNoWindow = true;

			this.mProcess.StartInfo.RedirectStandardOutput = true;
			this.mProcess.StartInfo.UseShellExecute = false;

			this.mOutput = new StringCollection();
		}

		public ProcessManager(string FileName) : this()
		{
			this.FileName = FileName;            
		}

		public ProcessManager(string FileName, string Arguments) : this()
		{
			this.FileName = FileName;
			this.Arguments = Arguments;
		}

		public string FileName
		{
			get 
			{
				return this.mProcess.StartInfo.FileName;
			}
			set 
			{
				this.mProcess.StartInfo.FileName = value;
			}
		}

		public string Arguments
		{
			get 
			{
				return this.mProcess.StartInfo.Arguments;
			}
			set 
			{
				this.mProcess.StartInfo.Arguments = value;
			}
		}

		public bool DirectOutput
		{
			get 
			{
				return this.mDirectOutput;
			}
			set 
			{
				this.mDirectOutput = value;
			}
		}

		/// <summary>
		/// The exit code returned by the os while runnung the process. Returns 0 if no error occurred, otherwise it return the NativeErrorCode of the thrown Win32Exception.
		/// </summary>
		public int Win32ExitCode
		{
			get 
			{
				return this.mWin32ExitCode;
			}
		}

		public int ProcessExitCode
		{
			get
			{
				return this.mProcessExitCode;
			}
		}

		public void Run()
		{
			try 
			{
				this.mProcess.Start();

				// While the process is executing, read its output
				while ( this.mProcess.HasExited )
				{
					if ( this.DirectOutput ) 
					{
						Console.WriteLine(this.mProcess.StandardOutput.ReadLine());
					}
					else 
					{
						this.mOutput.Add(this.mProcess.StandardOutput.ReadLine());
					}
				}
							
				// Read output still remaining in the output buffer of the process after is has exited
				string line = "";
				while ((line = this.mProcess.StandardOutput.ReadLine()) != null)
				{
					if ( this.DirectOutput ) 
					{
						Console.WriteLine(line);
					}
					else 
					{
						this.mOutput.Add(line);
					}
				}
				
				this.mProcessExitCode = this.mProcess.ExitCode;
			}
			catch (System.ComponentModel.Win32Exception e)
			{
				this.mWin32ExitCode = e.NativeErrorCode;
			}


		}
	}
}
