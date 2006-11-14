using System;
using System.Text;
using System.Collections.Generic;

using Microsoft.Build.Utilities;
using Microsoft.Build.Framework;

namespace Composestar.StarLight.MSBuild.Tasks
{
	/// <summary>
	/// Task to extract embedded code from concern specifications.
	/// </summary>
	public class ExbedTask : Task
	{
		private ITaskItem[] m_concernFiles;
		private ITaskItem[] m_extraSources;

		public ExbedTask()
		{
		}

		[Required]
		public ITaskItem[] ConcernFiles
		{
			set { m_concernFiles = value; }
		}

		[Output]
		public ITaskItem[] ExtraSources
		{
			get { return m_extraSources; }
		}

		public override bool Execute()
		{
			Log.LogMessage("ConcernFiles:");
			foreach (ITaskItem source in m_concernFiles)
			{
				Log.LogMessage("-{0}", source.ToString());
			}
			m_extraSources = new ITaskItem[0];
			return true;
		}
	}
}
