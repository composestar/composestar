using System;
using System.Collections.Generic;
using System.Text;

using Microsoft.Build.Utilities;
using Microsoft.Build.Framework;

namespace Trese.ComposestarTasks
{
	public class ComposestarCompilerTask : Task
	{
		private string[] m_sourceFiles;
		private string m_outputAssembly;

		public ComposestarCompilerTask()
		{
		}

		[Required()]
		public string OutputAssembly
		{
			get { return m_outputAssembly; }
			set { m_outputAssembly = value; }
		}

		[Required()]
		public string[] SourceFiles
		{
			get { return m_sourceFiles; }
			set { m_sourceFiles = value; }
		}

		public override bool Execute()
		{
			ConfigWriter cw = new ConfigWriter();
			cw.write();

			return true;
		}
	}
}
