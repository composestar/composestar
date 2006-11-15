using System;
using System.Text;
using System.Collections.Generic;

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
			foreach (ITaskItem source in m_sources)
			{
				Log.LogMessage("-{0}", source.ToString());
			}
			return true;
		}
	}
}
