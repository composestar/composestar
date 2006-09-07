

using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Build.Utilities;

namespace ComposeStar.MSBuild.Tasks.Compiler
{
	class CompilerErrorSink
	{
		private bool buildSucceeded = true;
		private TaskLoggingHelper taskLogger;
		private string projectDirectory = null;

		/// <summary>
		/// This is the directory where the project is located.
		/// If set, it will be used to calculate full paths of files.
		/// </summary>
		public string ProjectDirectory
		{
			get { return projectDirectory; }
			set { projectDirectory = value; }
		}
	
		/// <summary>
		/// Constructor for the error sink
		/// </summary>
		/// <param name="logger">This parameter should be the logger for the task being executed</param>
		public CompilerErrorSink(TaskLoggingHelper logger)
		{
			if (logger == null)
				throw new ArgumentNullException("logger");
			taskLogger = logger;
		}

		public bool BuildSucceeded
		{
			get { return buildSucceeded; }
		}
        
		/// <summary>
		/// Log Errors/Warnings/Messages when the compiler reports them.
		/// </summary>
		/// <param name="path">Path to the file where the error was found (null/empty if N/A)</param>
		/// <param name="message">Text of the error/warning/message</param>
		/// <param name="startLine">First line of the block containing the error (0 if N/A)</param>
		/// <param name="startColumn">First column of the block containing the error (0 if N/A)</param>
		/// <param name="endLine">Last line of the block containing the error (0 if N/A)</param>
		/// <param name="endColumn">Last column of the block containing the error (0 if N/A)</param>
		/// <param name="errorCode">Code corresponding to the error</param>
		/// <param name="severity">Error/Warning/Message</param>
		public void AddError(string path, string message, string lineText, int startLine, int startColumn, int endLine, int endColumn, int errorCode, int severity)
		{
			if (ProjectDirectory != null && !System.IO.Path.IsPathRooted(path))
				path = System.IO.Path.Combine(ProjectDirectory, path);
			// Based on the type of event (error/warning/message), report the corresponding type of problem to MSBuild
			switch(severity)
			{
				case 1: 
					{
						buildSucceeded = false;
						taskLogger.LogError(String.Empty, "COMPOSESTAR" + errorCode.ToString(), String.Empty, path, startLine, startColumn, endLine, endColumn, message);
						break;
					}
				case 2:
					{
                        taskLogger.LogWarning(String.Empty, "COMPOSESTAR" + errorCode.ToString(), String.Empty, path, startLine, startColumn, endLine, endColumn, message);
						break;
					}
				case 3:
					{
						taskLogger.LogMessage(message);
						break;
					}
			}
		}
	}
}
