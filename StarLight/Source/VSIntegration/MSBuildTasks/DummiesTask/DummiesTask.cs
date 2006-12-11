#region License
/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * ComposeStar StarLight [http://janus.cs.utwente.nl:8000/twiki/bin/view/StarLight/WebHome]
 * Copyright (C) 2003, University of Twente.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification,are permitted provided that the following conditions
 * are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Twente nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
*/
#endregion

#region Using directives
using System;
using System.IO;
using System.Text;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;  

using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;

using Composestar.Repository;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.Entities.Configuration;

using DDW.CSharpUI;
#endregion

namespace Composestar.StarLight.MSBuild.Tasks
{
	/// <summary>
	/// Task to generate dummy sources.
	/// </summary>
	public class DummiesTask : Task
	{
		// inputs
		private string _targetDir;
		private ITaskItem[] _sources;

		// outputs
		private IList<ITaskItem> _dummies;

		/// <summary>
		/// Initializes a new instance of the <see cref="T:DummiesTask"/> class.
		/// </summary>
		public DummiesTask()
		{
			_dummies = new List<ITaskItem>();
		}

		/// <summary>
		/// Sets the project base directory.
		/// </summary>
		[Required]
		[SuppressMessage("Microsoft.Design", "CA1044:PropertiesShouldNotBeWriteOnly")]
		public string TargetDir
		{
			set { _targetDir = value; }
		}

		/// <summary>
		/// Sets the list of sources.
		/// </summary>
		[Required]
		[SuppressMessage("Microsoft.Design", "CA1044:PropertiesShouldNotBeWriteOnly")]
		public ITaskItem[] Sources
		{
			set { _sources = value; }
		}

		/// <summary>
		/// Gets a list of generated dummy sources.
		/// </summary>
		[Output]
		public ITaskItem[] Dummies
		{
			get { return ToArray(_dummies); }
		}

		/// <summary>
		/// Executes this task.
		/// </summary>
		/// <returns>true if the task successfully executed; otherwise, false.</returns>
		public override bool Execute()
		{
			if (!Directory.Exists(_targetDir))
				Directory.CreateDirectory(_targetDir);

			foreach (ITaskItem source in _sources)
			{
				string input = source.ToString();
				string output = Path.Combine(_targetDir, Path.GetFileName(input));

				_dummies.Add(new TaskItem(output));
				
				CSharpDummyGenerator.GenerateDummy(input, output);
			}

			return true;
		}

		/// <summary>
		/// Converts a list of taskitems to an array.
		/// </summary>
		/// <param name="items">The list of items.</param>
		/// <returns>Returns an ITaskItem array.</returns>
		private static ITaskItem[] ToArray(IList<ITaskItem> items)
		{
			ITaskItem[] arr = new ITaskItem[items.Count];
			items.CopyTo(arr, 0);
			return arr;
		}
	}
}
