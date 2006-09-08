using System;
using EnvDTE;
using System.Globalization;

namespace Composestar.StarLight.MSBuild.Tasks.BuildConfiguration
{
	/// <summary>
	/// The Debug class is a singleton class which provides access to the debugging functions of the Composestar addin.
	/// </summary>
	public class Debug 
	{
		private OutputWindowPane owp = null;

		private static Debug debug = null;
		private static readonly object objectLock = new object();
		private _DTE applicationObject;
		private DebugModes currentDebugMode = DebugModes.Crucial;
		private int warnings = 0;
		private EnvDTE.StatusBar SBar = null;
		private EnvDTE.vsStatusAnimation previousAnimation;
		private bool BuildErrorsEncountered = false;

		/// <summary>
		/// Indicates which pane to be used when outputting data.
		/// </summary>
		private const string OutputPaneComposestar = "Composestar";

		/// <summary>
		/// Category name for the tasks.
		/// </summary>
		private const string TaskCategoryComposestar = "Composestar";

		/// <summary>
		/// Private constructor for singleton instance.
		/// </summary>
		private Debug() { }

		/// <summary>
		/// Initialize the debugger by setting the debugmode and providing an ApplicationObject.
		/// </summary>
		/// <remarks>this function have to be called before all other functions so the application object is present.</remarks> 
		/// <param name="mode">Debugmode</param>
		/// <param name="applicationObject">EnvDTE ApplicationObject.</param>
		public void Init(DebugModes mode, _DTE applicationObject)
		{
			currentDebugMode = mode;
			this.applicationObject = applicationObject;
			SBar = applicationObject.StatusBar;
			owp = this.GetOutputWindowPane(OutputPaneComposestar);
		}

		/// <summary>
		/// Returns an instance of the <see cref="Debug">Debug</see> class.
		/// </summary>
		/// <remarks>This is a tread safe method.</remarks> 
		/// <returns></returns>
		public static Debug Instance
		{
			get 
			{
				if(debug == null)
				{
					lock (objectLock)
					{
						if(debug == null)
							debug = new Debug();
					}
				}
				return debug;
			}
		}
	    
		/// <summary>
		/// Set or get the current debug mode.
		/// </summary>
		public DebugModes DebugMode
		{
			get
			{
				return this.currentDebugMode;
			}
			set 
			{
				this.currentDebugMode = value;
			}
		}

		#region Supporting
		
		/// <summary>
		/// Find the outputpane instance.
		/// </summary>
		/// <param name="name">Name of the output pane.</param>
		/// <returns></returns>
		private OutputWindowPane GetOutputWindowPane(string name)
		{

			Window win = applicationObject.Windows.Item(EnvDTE.Constants.vsWindowKindOutput);
			string caption = win.Caption;
			string kind = win.Kind;


			OutputWindow ow = win.Object as OutputWindow;

			OutputWindowPane owp = null;
			if (ow != null)
			{
				try
				{
					try
					{
						owp = ow.OutputWindowPanes.Item(name);
					}
					catch
					{}

					if (owp == null)
						owp = ow.OutputWindowPanes.Add(name);
					
				}
				catch (Exception ex)
				{
					string exmsg = ex.Message;
				}
			}
			return owp;
	
		}

		#endregion
	    
		/// <summary>
		/// Log the message.
		/// </summary>
		/// <param name="debugMode">Debugmode to use.</param>
		/// <param name="module">Module creating this log message.</param>
		/// <param name="message">The logmessage</param>
		public void Log(DebugModes debugMode, string module, string message) 
		{
			this.Log(debugMode,module,message,"",0);
		}

		public void Log(DebugModes debugMode, string module, string message, string filename, int line) 
		{
			if (currentDebugMode >= debugMode) 
			{
				String modeDescription = "";
	    		
				switch(debugMode)
				{
					case DebugModes.Warning:
						warnings++;
						modeDescription = "warning";
						this.PrintMessage(String.Format(CultureInfo.CurrentCulture, "{0} ({1}): {2}", module, modeDescription, message));
						this.AddTaskItem(message,vsTaskPriority.vsTaskPriorityMedium, vsTaskIcon.vsTaskIconUser, filename, line);
						break;
					case DebugModes.Information:
						modeDescription = "info";
						this.PrintMessage(String.Format(CultureInfo.CurrentCulture, "{0} ({1}): {2}", module, modeDescription, message));
						break;
					case DebugModes.Debug:
						modeDescription = "DEBUG";
						this.PrintMessage(String.Format(CultureInfo.CurrentCulture, "{0} ({1}): {2}", module,modeDescription, message));
						break;
					case DebugModes.Crucial:
						modeDescription = "";
						this.PrintMessage(String.Format(CultureInfo.CurrentCulture, "{0} : {1}", module, message));
						break;
					case DebugModes.Error:
						modeDescription = "ERROR";
						this.PrintMessage(String.Format(CultureInfo.CurrentCulture, "{0} ({1}): {2}", module, modeDescription, message));
						this.AddTaskItem(message,vsTaskPriority.vsTaskPriorityHigh,vsTaskIcon.vsTaskIconCompile, filename, line);
						break;
				}     
			}
		}

		/// <summary>
		/// Log the message with the current debugmode.
		/// </summary>
		/// <param name="module">Module creating this log message.</param>
		/// <param name="message">The logmessage</param>
		public void Log(string module, string message) 
		{
			Log(this.DebugMode, module, message); 
		}

		/// <summary>
		/// This log method allows you to print a complete string to the output pane. So no formatting etc.
		/// </summary>
		/// <param name="message"></param>
		public void Log(string message)
		{
			this.PrintMessage(message);   
		}

		public void ParseLog(string message)
		{

			if (message == null) return;

			string[] parsed = message.Split("~".ToCharArray(),5);
			if(parsed.Length == 5)
			{
				string module = parsed[0];
				string warninglevel = parsed[1];
				string filename = parsed[2];
				string line = parsed[3];
				string msg = parsed[4];
				DebugModes mode;

				try
				{
					mode = (DebugModes)DebugModes.Parse(typeof(DebugModes), warninglevel, true);
				}
				catch (Exception)
				{
					mode = DebugModes.Warning; 
				}

				int linenumber = 0;
				try
				{
					linenumber = Convert.ToInt32(parsed[3]);
				}
				catch(Exception) { linenumber = 0; }

				if ( this.BuildErrorsEncountered && message.Equals("") )
				{
					this.BuildErrorsEncountered = false;
				}

				// Check for compilation errors
				if ( msg != null && msg.StartsWith("RECOMACOMERROR:")) 
					this.BuildErrorsEncountered = true;
				else
					this.BuildErrorsEncountered = false;

				// Update task list with compilation errors
				if ( this.BuildErrorsEncountered ) 
				{
					try 
					{
						string comError = "RECOMACOMERROR:";
						string file = msg.Substring(comError.Length , msg.IndexOf("(")-comError.Length ).Replace("/", "\\");
						string rest = msg.Substring(file.Length+1+comError.Length);
						int lineRecoma = 0;

						lineRecoma = int.Parse((rest.Substring(0, rest.IndexOf(","))));

						rest = rest.Substring(rest.IndexOf(")")+2).TrimStart();
						DebugModes dm;
						if (rest.StartsWith("warning"))
							dm = DebugModes.Warning;
						else
							dm = DebugModes.Error; 
						this.Log(dm,"RECOMA",rest,file,lineRecoma);
					}
					catch (Exception)
					{
						this.AddTaskItem(msg, vsTaskPriority.vsTaskPriorityHigh, vsTaskIcon.vsTaskIconCompile);   
					}
				}
				else
					this.Log(mode,module,msg,filename,linenumber);

			
				
			}
			else
			{
				this.Log(message);
			}
		}

		/// <summary>
		/// Print a message to the outputwindow.
		/// </summary>
		/// <param name="message">Message to print.</param>
		private void PrintMessage(string message)
		{
			if (owp != null) 
			{
				owp.OutputString(message + "\n");
			}
			else
			{
				Console.WriteLine(message);
			}
		}

		public void ResetWarnings()
		{
			warnings = 0;
		}

		#region Statusbar

		/// <summary>
		/// Show the progressbar with a message.
		/// </summary>
		/// <param name="message">Message to show in the statusbar.</param>
		/// <param name="percentage">Percentage complete as a whole number lower then 100 (inclusive)</param>
		public void ShowProgress(string message, int percentage)
		{
			if (SBar != null) 
			{
				if (percentage > 100)
				{
					percentage = 100;
				}
				SBar.Progress(true, message, percentage, 100); 
			}
		}

		/// <summary>
		/// Hides the progress bar but shows a message.
		/// </summary>
		/// <param name="message">Message to show in the statusbar.</param>
		public void HideProgress(string message)
		{
			SBar.Progress(false, message, 0, 0);
		}

		/// <summary>
		/// Run a animation in the statusbar.
		/// </summary>
		/// <param name="animationType">AnimationType to run.</param>
		public void ShowAnimation(EnvDTE.vsStatusAnimation animationType)
		{
			if (SBar == null)
				return;

			previousAnimation = animationType;
			SBar.Animate(true, animationType);
		}

		/// <summary>
		/// Hide a previous started animation.
		/// </summary>
		public void HideAnimation()
		{
			SBar.Animate(false, previousAnimation);
		}

		#endregion

		#region Output Pane

		/// <summary>
		/// Make the outputpane active.
		/// </summary>
		/// <param name="name"></param>
		public void ActivateOutputWindowPane(string name)
		{
			applicationObject.Windows.Item(Constants.vsWindowKindOutput).Activate();
			OutputWindowPane owp = GetOutputWindowPane(name);
			if (owp != null) 
			{
				owp.Activate();
			}
		}

		/// <summary>
		/// Activate the composestar output window pane.
		/// </summary>
		public void ActivateOutputWindowPane()
		{
			ActivateOutputWindowPane(OutputPaneComposestar);
		}

		/// <summary>
		/// Clear the output window pane.
		/// </summary>
		/// <param name="name">Name of the pane to clear.</param>
		public void ClearOutputWindowPane(string name)
		{
			OutputWindowPane owp = GetOutputWindowPane(name);
			if (owp != null) 
			{
				owp.Clear();
			}
		}

		/// <summary>
		/// Clear the Composestar output window pane.
		/// </summary>
		public void ClearOutputWindowPane()
		{
			ClearOutputWindowPane(OutputPaneComposestar);
		}

				
		#endregion

		#region Tasklist

		/// <summary>
		/// Make the tasklist window active and show all the items.
		/// </summary>
		public void ActivateTaskListWindow()
		{
			TaskList tl = GetTaskListWindow();

			foreach(TaskItem item in tl.TaskItems)
			{
				if(item.Category.Equals(TaskCategoryComposestar)) 
				{
					applicationObject.Windows.Item(Constants.vsWindowKindTaskList).Activate();
					break;
				}
			}

			// Make sure the Composestar category is shown.
			//applicationObject.ExecuteCommand("View.All", ""); 
		}

		/// <summary>
		/// Clear the items in the task list.
		/// </summary>
		/// <remarks>This function will only delete the Composestar items.</remarks> 
		public void ClearTaskListWindow()
		{
			TaskList tl = GetTaskListWindow();

			foreach(TaskItem item in tl.TaskItems)
			{
				if(item.Category.Equals(TaskCategoryComposestar)) 
					try 
					{
						item.Delete(); 
					}
					catch (Exception)
					{
						//System.Windows.Forms.MessageBox.Show("Delete failed: " + e.Message);
					}
			}
		}

		/// <summary>
		/// Get the tasklist from the taskwindow.
		/// </summary>
		/// <returns></returns>
		private TaskList GetTaskListWindow()
		{
			return (TaskList)applicationObject.Windows.Item(Constants.vsWindowKindTaskList).Object;
		}

		/// <summary>
		/// Add a new taskitem to the TaskList.
		/// </summary>
		/// <param name="text">Text to display.</param>
		/// <param name="taskPriority">Tasks priority.</param>
		/// <param name="taskIcon">Task icon.</param>
		public void AddTaskItem(string text, EnvDTE.vsTaskPriority taskPriority, EnvDTE.vsTaskIcon taskIcon )
		{
			EnvDTE.TaskList tl = GetTaskListWindow();
    
			tl.TaskItems.Add(TaskCategoryComposestar , "",
				text,
				taskPriority ,
				taskIcon, false,
				"", 0, true, true);
		}

		/// <summary>
		/// Add a new taskitem to the TaskList.
		/// </summary>
		/// <param name="text">Text to display.</param>
		/// <param name="taskPriority">Tasks priority.</param>
		/// <param name="taskIcon">Task icon.</param>
		/// <param name="sourceFile">Source file.</param>
		/// <param name="lineNumber">Line number.</param>
		public void AddTaskItem(string text, EnvDTE.vsTaskPriority taskPriority, EnvDTE.vsTaskIcon taskIcon, string sourceFile, int lineNumber )
		{
			EnvDTE.TaskList tl = GetTaskListWindow();
    
			tl.TaskItems.Add(TaskCategoryComposestar , "",
				text,
				taskPriority ,
				taskIcon, false,
				sourceFile.Replace("/","\\"), lineNumber, true, true);
		}

		#endregion

	}
}
