using System;
using System.Collections.Generic;
using System.Text;

using Microsoft.Build.Utilities;
using Microsoft.Build.Framework;

namespace Composestar.StarLight.MSBuild.Tasks
{
	/// <summary>
	/// Task to generate dummy sources.
	/// </summary>
	public class DummiesTask : Task
	{
		private ITaskItem[] m_sources;

		public DummiesTask()
		{
		}

		[Required]
		public ITaskItem[] Sources
		{
			set { m_sources = value; }
		}

		public override bool Execute()
		{
			Log.LogMessage("m_sources={0}", m_sources);
			foreach (ITaskItem source in m_sources)
			{
				Log.LogMessage("-{0}", source.ToString());
			}
			return true;
		}
	}
}
