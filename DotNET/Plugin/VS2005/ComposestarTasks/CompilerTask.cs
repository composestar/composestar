using System;
using System.Text;
using System.Diagnostics;
using System.Collections.Generic;

using Microsoft.Build.Utilities;
using Microsoft.Build.Framework;
using System.ComponentModel;
using System.IO;

namespace Trese.ComposestarTasks
{
	public class ComposestarCompilerTask : Task
	{
		public ComposestarCompilerTask()
		{
		}

		public override bool Execute()
		{
			InvokeCompiler();
			return false;
		}

		private void InvokeCompiler()
		{
			Process p = new Process();
			p.StartInfo.FileName = "java";
			p.StartInfo.Arguments = GetCompilerArguments();
			p.StartInfo.CreateNoWindow = true;
			p.StartInfo.UseShellExecute = false;
			p.StartInfo.RedirectStandardOutput = true;
		//	p.StartInfo.RedirectStandardError = true;

			try
			{
				p.Start();

				while (!p.HasExited)
				{
					string line = p.StandardOutput.ReadLine();
					Debug.Print(line);
					Log.LogError(line);
				}

			//	string stderr = p.StandardError.ReadToEnd();
			//	Debug.Print("stderr=" + stderr);
			}
			catch (Exception e)
			{
				Log.LogErrorFromException(e);
				Debug.Print(e.Message);
			}
		}

		private String GetCompilerArguments()
		{
			String mainjar = @"C:\Program Files\ComposeStar\lib\ComposestarDotNET.jar";

			StringBuilder sb = new StringBuilder();
			sb.Append("-jar ");
			sb.Append('"').Append(mainjar).Append('"');
			sb.Append(" BuildConfiguration.xml");

			return sb.ToString();
		}
	}
}
