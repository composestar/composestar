using System;
using Microsoft.Office.Core;
using Extensibility;
using System.Runtime.InteropServices;
using System.IO;
using EnvDTE;
using Ini;
using System.Collections;
using System.Globalization; 
using System.Windows.Forms;
using System.Drawing;

namespace ComposestarVSAddin
{

	#region Read me for Add-in installation and setup information.
	// When run, the Add-in wizard prepared the registry for the Add-in.
	// At a later time, if the Add-in becomes unavailable for reasons such as:
	//   1) You moved this project to a computer other than which is was originally created on.
	//   2) You chose 'Yes' when presented with a message asking if you wish to remove the Add-in.
	//   3) Registry corruption.
	// you will need to re-register the Add-in by building the ComposestarVSAddin project 
	// by right clicking the project in the Solution Explorer, then choosing install.
	#endregion

	#region Read me for Add-in debugging using Visual Studio
	// It is possible to debug the addin using Visual Studio. 
	// This is useful for stepping into the code and examing values.
	// 
	// 1. Start a Visual Studio instance or use the current one.
	// 2. Open the properties of the ComposestarVSAddin project. 
	// 3. Go to the Configuration Properties -> Debugging tab.
	// 4. Under Start Actions select 'program' as the debug mode.
	// 5. In the 'Start Application' field fill in the path and filename of the devenv (Visual Studio)
	// 6. Close the property settings dialog.
	// 7. Make sure the application is in Debug mode.
	// 8. Use Debug -> Start to start debugging the application.
	//
	// A new instance of the Visual Studio will be started. Open a Composestar project in this new instance.
	// Enable the addin using Tools->Add-in Manager.
	// Start the Build with Compose* or Run with Compose*. By settings breakpoints in the Addin the debugger 
	// will jump into the Visual Studio instance with the addin when it detectes a breakpoint.
	//
	// NOTE: Make sure the addin is not active (disabled in the add-in manager) otherelse you will get 
	// access errors because the file cannot be written.
	// If it still does not work, run 'regasm /codebase ComposestarVSAddin.dll'.
	#endregion
	
	/// <summary>
	///   The object for implementing an Add-in.
	/// </summary>
	/// <seealso class='IDTExtensibility2' />
	[GuidAttribute("2A23A699-C931-40A7-A52D-67D1224565E8"), ProgId("ComposestarVSAddin.Connect")]
	public class Connect : Object, Extensibility.IDTExtensibility2, IDTCommandTarget
	{
		private const string RegistryPath = "Software\\Microsoft\\VisualStudio\\7.1\\Addins\\ComposestarVSAddin.Connect";
                                                           
		private DummyManager dummymanager;
		private ConfigManager configmanager;
		private MasterManager mastermanager;
		private AssemblyManager assemblymanager;
		private AttributeManager attributemanager;
		private EmbeddedSourceManager embsrcmanager;

		private IniFile iniconfig = null;
		private bool buildSuccess = false;
		private EnvDTE.TaskListEvents taskListEvents;

		private _DTE applicationObject;
		private AddIn addInInstance;

		private const string commandBarValue = "ComposeStar";
		private const string CommandBarName = "Composestar";

		private CommandBarControl m_Menu = null;
		private CommandBar        m_Bar  = null;

		/// <summary>
		///     Name of the add-in command.
		/// </summary>
		private readonly string   m_commandNameBuild;
		private readonly string   m_commandNameRun;
		private readonly string   m_commandNameDebugRun;
		private readonly string   m_commandNameSettings;
		private readonly string   m_commandNameClean;

		private const string ADDIN_BUILD        = "Build";
		private const string ADDIN_RUN          = "Run";
		private const string ADDIN_DEBUGRUN     = "Debug";
		private const string ADDIN_CLEAN        = "Clean";
		private const string ADDIN_SETTINGS     = "Settings";

		private const int    BITMAP_ID_BUILD    = 5830;
		private const int    BITMAP_ID_CLEAN    = 1786;    // 1088 1786 67 
		private const int    BITMAP_ID_RUN      = 2945; // alternatives: 186 2997 3820
		private const int    BITMAP_ID_DEBUGRUN = 3820; // alternatives: 186 2997 3820
		private const int    BITMAP_ID_SETTINGS = 642;  // alternatives: 1951 2597 2770

		private string m_captionBuild ="Build with Compose*" ;
		private string m_captionRun = "Run with Compose*";
		private string m_captionDebugRun = "Debug with Compose*";
		private string m_captionSettings = "Compose* Settings";
		private string m_captionClean = "Clean with Compose*";
		private string m_toolTipBuild = "Will invoke Compose* to build the current solution";
		private string m_toolTipRun = "Will invoke application build with Compose*";
		private string m_toolTipDebugRun = "Will debug application build with Compose*";
		private string m_toolTipSettings = "Opens the Compose* settings dialog";
		private string m_toolTipClean = "Cleans the previous Compose* build information";

		/// <summary>
		///     References to <c>Command</c>s which are used to 
		///     query add-in command status.
		/// </summary>
		private Command           m_referenceBuildCommand = null;
		private Command           m_referenceCancelBuildCommand = null;

		private const string ENVIRONMENT        = "Environment";
		private const string HELP               = "Help";
		private const string INTERNATIONAL      = "International";
		private const string LANGUAGE           = "Language";
		private const string PREFERREDLANGUAGE  = "PreferredLanguage";

		public const string ProgId = "ComposestarVSAddin.Connect";

		// Added highresolution performance counters - Michiel (28/2/2005)
		[DllImport("kernel32.dll")]
		extern static short QueryPerformanceCounter(ref long x);
		[DllImport("kernel32.dll")]
		extern static short QueryPerformanceFrequency(ref long x);

		public Connect()
		{
			object attribute = GetType().GetCustomAttributes(typeof(System.Runtime.InteropServices.ProgIdAttribute), true)[0];
    
			m_commandNameBuild = ((System.Runtime.InteropServices.ProgIdAttribute)attribute).Value + "." + ADDIN_BUILD;
			m_commandNameRun = ((System.Runtime.InteropServices.ProgIdAttribute)attribute).Value + "." + ADDIN_RUN;
			m_commandNameDebugRun = ((System.Runtime.InteropServices.ProgIdAttribute)attribute).Value + "." + ADDIN_DEBUGRUN;
			m_commandNameSettings = ((System.Runtime.InteropServices.ProgIdAttribute)attribute).Value + "." + ADDIN_SETTINGS;
			m_commandNameClean = ((System.Runtime.InteropServices.ProgIdAttribute)attribute).Value + "." + ADDIN_CLEAN;
		}

		#region Add in

		/// <summary>
		///      Implements the OnConnection method of the IDTExtensibility2 interface.
		///      Receives notification that the Add-in is being loaded.
		/// </summary>
		/// <param term='application'>
		///      Root object of the host application.
		/// </param>
		/// <param term='connectMode'>
		///      Describes how the Add-in is being loaded.
		/// </param>
		/// <param term='addInInst'>
		///      Object representing this Add-in.
		/// </param>
		/// <seealso class='IDTExtensibility2' />
		public void OnConnection(object application, Extensibility.ext_ConnectMode connectMode, object addInInst, ref System.Array custom)
		{
			applicationObject = (_DTE)application;
			addInInstance = (AddIn)addInInst;
		
			Debug.Instance.Init(DebugModes.Information, applicationObject);
            
			if (connectMode == Extensibility.ext_ConnectMode.ext_cm_Startup || connectMode == Extensibility.ext_ConnectMode.ext_cm_AfterStartup) 
			{
				try 
				{
					SetCurrentThreadUICulture();
					SetDebugLevel();
				}
				catch (Exception e) 
				{
					Debug.Instance.Log(DebugModes.Crucial,"AddIn","Compose* VS add-in version " + System.Reflection.Assembly.GetExecutingAssembly().GetName().Version.ToString() + " failed to load.");
					Debug.Instance.Log(DebugModes.Crucial,"AddIn","Exception caught: " + e.ToString());
					return;
				}
				try 
				{
					// get references to Build Solution and Cancel Build commands which are 
					// used to synchronize enabled/disabled state
					EnvDTE.Commands cmds = applicationObject.Commands;
					m_referenceBuildCommand = cmds.Item("Build.BuildSolution", 0);
					m_referenceCancelBuildCommand = cmds.Item("Build.Cancel", 0);
				
					CreateToolBar();
					CreateMenu();

					// checks if menu exists
					m_Menu = applicationObject.CommandBars.ActiveMenuBar.FindControl(MsoControlType.msoControlPopup, 1, CommandBarName, false, false);
					// if not found, appends it to the menu bar
					if (m_Menu == null) 
					{
						m_Menu = applicationObject.CommandBars.ActiveMenuBar.Controls.Add(MsoControlType.msoControlPopup, Type.Missing, Type.Missing, Type.Missing, false);
						m_Menu.Caption = "&Compose*";
						m_Menu.Tag = CommandBarName;
					}

					// Hook an event listener for the tasklist
					EnvDTE.Events events = applicationObject.Events;
					taskListEvents = (EnvDTE.TaskListEvents)events.get_TaskListEvents("Composestar");
					taskListEvents.TaskNavigated += new 
						_dispTaskListEvents_TaskNavigatedEventHandler(this.TaskNavigated);
 
					// Adds commands to the environment
					AddCommand((AddIn)addInInst, ADDIN_BUILD, m_captionBuild, m_toolTipBuild, true, BITMAP_ID_BUILD, false, true);
					AddCommand((AddIn)addInInst, ADDIN_RUN, m_captionRun, m_toolTipRun, true, BITMAP_ID_RUN, false, true);
					AddCommand((AddIn)addInInst, ADDIN_DEBUGRUN, m_captionDebugRun, m_toolTipDebugRun, true, BITMAP_ID_DEBUGRUN, false, true);
					AddCommand((AddIn)addInInst, ADDIN_CLEAN, m_captionClean, m_toolTipClean, true, BITMAP_ID_CLEAN, false, false);
					AddCommand((AddIn)addInInst, ADDIN_SETTINGS, m_captionSettings, m_toolTipSettings, true, BITMAP_ID_SETTINGS, true, true);
	
					UpdateAddinControlStatus();
				}
				catch (Exception e) 
				{
					Debug.Instance.Log(DebugModes.Crucial,"AddIn","Compose* VS add-in version " + System.Reflection.Assembly.GetExecutingAssembly().GetName().Version.ToString() + " failed to load.");
					Debug.Instance.Log(DebugModes.Crucial,"AddIn","Exception caught: " + e.ToString());
				}
			}

			Debug.Instance.Log("Compose* VS add-in version " + System.Reflection.Assembly.GetExecutingAssembly().GetName().Version.ToString() + " loaded.");
				
		}

		/// <summary>
		///      Implements the QueryStatus method of the IDTCommandTarget interface.
		///      This is called when the command's availability is updated
		/// </summary>
		/// <param term='commandName'>
		///		The name of the command to determine state for.
		/// </param>
		/// <param term='neededText'>
		///		Text that is needed for the command.
		/// </param>
		/// <param term='status'>
		///		The state of the command in the user interface.
		/// </param>
		/// <param term='commandText'>
		///		Text requested by the neededText parameter.
		/// </param>
		/// <seealso class='Exec' />
		public void QueryStatus(string commandName, EnvDTE.vsCommandStatusTextWanted neededText, ref EnvDTE.vsCommandStatus status, ref object commandText)
		{
			if (neededText == EnvDTE.vsCommandStatusTextWanted.vsCommandStatusTextWantedNone) 
			{
				if (commandName == m_commandNameBuild || commandName == m_commandNameRun || commandName == m_commandNameDebugRun || commandName == m_commandNameSettings || commandName == m_commandNameClean) 
				{
					UpdateStatus(commandName, ref status);
				}
			}
		}

		/// <summary>
		///   Updates the status of the addin control(s).
		/// </summary>
		private void UpdateAddinControlStatus() 
		{
			vsCommandStatus commandStatus = vsCommandStatus.vsCommandStatusUnsupported;
			object commandText = null;
			QueryStatus(m_commandNameBuild, vsCommandStatusTextWanted.vsCommandStatusTextWantedNone, ref commandStatus, ref commandText);
			QueryStatus(m_commandNameRun, vsCommandStatusTextWanted.vsCommandStatusTextWantedNone, ref commandStatus, ref commandText);
			QueryStatus(m_commandNameDebugRun, vsCommandStatusTextWanted.vsCommandStatusTextWantedNone, ref commandStatus, ref commandText);
			QueryStatus(m_commandNameSettings, vsCommandStatusTextWanted.vsCommandStatusTextWantedNone, ref commandStatus, ref commandText);
			QueryStatus(m_commandNameClean, vsCommandStatusTextWanted.vsCommandStatusTextWantedNone, ref commandStatus, ref commandText);
		}

		/// <summary>
		///   Updates the status of a button
		/// </summary>
		/// <param name="commandName">
		///   Name of the command.
		/// </param>
		/// <param name="status">
		///   Status reference.
		/// </param>
		private void UpdateStatus(string commandName, ref EnvDTE.vsCommandStatus status) 
		{
			if (commandName == m_commandNameSettings)
				status = status = (vsCommandStatus)vsCommandStatus.vsCommandStatusSupported | vsCommandStatus.vsCommandStatusEnabled;
			else
			{
				if (m_referenceBuildCommand.IsAvailable && (!m_referenceCancelBuildCommand.IsAvailable)) 
					status = (vsCommandStatus)vsCommandStatus.vsCommandStatusSupported | vsCommandStatus.vsCommandStatusEnabled;
				else 
					status = (vsCommandStatus)vsCommandStatus.vsCommandStatusSupported & ~(vsCommandStatus)vsCommandStatus.vsCommandStatusEnabled;
			}
		}

		/// <summary>
		///      Implements the Exec method of the IDTCommandTarget interface.
		///      This is called when the command is invoked.
		/// </summary>
		/// <param term='commandName'>
		///		The name of the command to execute.
		/// </param>
		/// <param term='executeOption'>
		///		Describes how the command should be run.
		/// </param>
		/// <param term='varIn'>
		///		Parameters passed from the caller to the command handler.
		/// </param>
		/// <param term='varOut'>
		///		Parameters passed from the command handler to the caller.
		/// </param>
		/// <param term='handled'>
		///		Informs the caller if the command was handled or not.
		/// </param>
		/// <seealso class='Exec' />
		public void Exec(string commandName, EnvDTE.vsCommandExecOption executeOption, ref object varIn, ref object varOut, ref bool handled)
		{
			handled = false;
			if(executeOption == EnvDTE.vsCommandExecOption.vsCommandExecOptionDoDefault)
			{
				if(commandName == m_commandNameBuild)
				{
					this.OnBuildBegin(EnvDTE.vsBuildScope.vsBuildScopeSolution,EnvDTE.vsBuildAction.vsBuildActionBuild);
					handled = true;
					return;
				}
				else if(commandName ==  m_commandNameRun)
				{
					this.OnRun();
					handled = true;
					return;
				}
				else if(commandName ==  m_commandNameDebugRun)
				{
					this.OnDebugRun();
					handled = true;
					return;
				}
				else if(commandName == m_commandNameSettings)
				{
					this.OnSettings();
					handled = true;
					return;
				}
				else if(commandName == m_commandNameClean)
				{
					this.OnClean();
					handled = true;
					return;
				}
			}
		}

		/// <summary>
		///     Implements the OnDisconnection method of the IDTExtensibility2 interface.
		///     Receives notification that the Add-in is being unloaded.
		/// </summary>
		/// <param term='disconnectMode'>
		///      Describes how the Add-in is being unloaded.
		/// </param>
		/// <param term='custom'>
		///      Array of parameters that are host application specific.
		/// </param>
		/// <seealso class='IDTExtensibility2' />
		public void OnDisconnection(Extensibility.ext_DisconnectMode disconnectMode, ref System.Array custom)
		{	
			// Save toolbar
			SaveToolBarLayout();
			
			if (disconnectMode == ext_DisconnectMode.ext_dm_UserClosed ) 
			{
				Debug.Instance.Log(DebugModes.Information, "AddIn", "Unloading Compose* Add-In.");   

				// Disconnect event handler.
				taskListEvents.TaskNavigated -= new 
					_dispTaskListEvents_TaskNavigatedEventHandler(this.TaskNavigated);
				taskListEvents = null;

				m_referenceBuildCommand = null;
				m_referenceCancelBuildCommand = null;

				// Remove the buttons
				this.DeleteCommand( m_commandNameBuild );
				this.DeleteCommand( m_commandNameRun );
				this.DeleteCommand( m_commandNameDebugRun );
				this.DeleteCommand( m_commandNameSettings ); 
				this.DeleteCommand( m_commandNameClean ); 

				Debug.Instance.Log(DebugModes.Information, "AddIn", "Unloaded the Compose* Add-In.");   
				
				this.addInInstance = null;
			}
		}

		/// <summary>
		///      Implements the OnAddInsUpdate method of the IDTExtensibility2 interface.
		///      Receives notification that the collection of Add-ins has changed.
		/// </summary>
		/// <param term='custom'>
		///      Array of parameters that are host application specific.
		/// </param>
		/// <seealso class='IDTExtensibility2' />
		public void OnAddInsUpdate(ref System.Array custom)
		{
		}

		/// <summary>
		///      Implements the OnStartupComplete method of the IDTExtensibility2 interface.
		///      Receives notification that the host application has completed loading.
		/// </summary>
		/// <param term='custom'>
		///      Array of parameters that are host application specific.
		/// </param>
		/// <seealso class='IDTExtensibility2' />
		public void OnStartupComplete(ref System.Array custom)
		{
			UpdateAddinControlStatus();
		}

		/// <summary>
		///      Implements the OnBeginShutdown method of the IDTExtensibility2 interface.
		///      Receives notification that the host application is being unloaded.
		/// </summary>
		/// <param term='custom'>
		///      Array of parameters that are host application specific.
		/// </param>
		/// <seealso class='IDTExtensibility2' />
		public void OnBeginShutdown(ref System.Array custom)
		{
		}

		#endregion

		#region Utils
        
		/// <summary>
		/// Get the debuglevel from the composestar.ini file.
		/// </summary>
		private void SetDebugLevel()
		{
			string path = "";
			string fullname;
			string iniFileName = "Composestar.ini";
			try
			{
				path = Microsoft.Win32.Registry.CurrentUser.OpenSubKey("Software").OpenSubKey("Microsoft").OpenSubKey("VisualStudio").OpenSubKey("7.1").OpenSubKey("Addins").OpenSubKey("ComposestarVSAddin.Connect").GetValue("ComposestarPath").ToString();
				fullname = System.IO.Path.Combine(path, iniFileName); 

				if (!System.IO.File.Exists(fullname)) 
				{
					Debug.Instance.AddTaskItem(String.Format("Composestar; Missing file '{0}' in Composestar installation path. Please reinstall the Composestar tool.", iniFileName), 
						vsTaskPriority.vsTaskPriorityHigh, vsTaskIcon.vsTaskIconUser );
					Debug.Instance.ActivateTaskListWindow();
				}
				else 
				{
					IniFile ifile = new IniFile(fullname);
					if(ifile.IniReadValue("Common","BuildDebugLevel") != null)
					{
						Debug.Instance.DebugMode = (DebugModes)Convert.ToInt32(ifile.IniReadValue("Common","BuildDebugLevel"));
					}
				}
			}
			catch (Exception)
			{}
		}

		/// <summary>
		/// Safe delete a file.
		/// </summary>
		/// <param name="fileName"></param>
		private void DeleteFile(string fileName)
		{
			if (File.Exists(fileName) )
				try
				{
					File.Delete(fileName);
				}
				catch (IOException)
				{
				}
		}

		/// <summary>
		/// Returns the startup project from all the projects in the solution.
		/// </summary>
		/// <returns>Returns an <see cref="EnvDTE.Project">EnvDTE.Project</see> object or <c>null</c> when not found.</returns>
		private EnvDTE.Project GetStartupProject()
		{
			
			System.Array projectNames = (System.Array) applicationObject.Solution.SolutionBuild.StartupProjects;
			if ((projectNames != null) && (projectNames.Length > 0))
			{
				string curPrjName = (string) projectNames.GetValue(0);
   
				foreach( Project project in applicationObject.Solution.Projects ) 
				{
					if (project.UniqueName == curPrjName)
						return project;
				}
			
			}

			return null;
		}

		/// <summary>
		///   Sets the current's thread UI culture to match the VS IDE
		/// </summary>
		private void SetCurrentThreadUICulture() 
		{
			int lcid = 0;
			// first try to obtain language from "Environment"-"International", "Language";
			// this works with VS2005 beta, but will throw exception with VS2002
			try 
			{
				lcid = (int)applicationObject.get_Properties(ENVIRONMENT, INTERNATIONAL).Item(LANGUAGE).Value;
			}
			catch (Exception) 
			{
				// if the above fails, try "Environment"-"Help", "Preferred Language"
				try 
				{
					lcid = (int)applicationObject.get_Properties(ENVIRONMENT, HELP).Item(PREFERREDLANGUAGE).Value;
				}
				catch (Exception) 
				{
					// if both failed, leave it as is
					return;
				}
			}
			System.Threading.Thread.CurrentThread.CurrentUICulture = new CultureInfo(lcid);
		}

		/// <summary>
		/// Performs a check for the existens of the composestar ini file using the registry.
		/// </summary>
		/// <returns>Returns true when the ini file exists.</returns>
		private bool ComposestarIniFileExists()
		{
			// First get the composestar path from the registry
			string path = "";
			string fullname;
			string iniFileName = "Composestar.ini";
			try
			{
				path = Microsoft.Win32.Registry.CurrentUser.OpenSubKey("Software").OpenSubKey("Microsoft").OpenSubKey("VisualStudio").OpenSubKey("7.1").OpenSubKey("Addins").OpenSubKey("ComposestarVSAddin.Connect").GetValue("ComposestarPath").ToString();
				fullname = System.IO.Path.Combine(path, iniFileName); 

				if (!System.IO.File.Exists(fullname)) 
				{
					Debug.Instance.AddTaskItem(String.Format(CultureInfo.CurrentCulture, "Composestar; Missing file '{0}' in Composestar installation path. Please reinstall the Composestar tool.", iniFileName), 
						vsTaskPriority.vsTaskPriorityHigh, vsTaskIcon.vsTaskIconUser );
					Debug.Instance.ActivateTaskListWindow();
					return false;
				}
				else 
				{
					iniconfig.IniWriteValue("Common", "ComposestarIniFile", "\"" + fullname + "\"");
				}
			}
			catch (Exception)
			{
				//System.Windows.Forms.MessageBox.Show("Missing registry key 'ComposestarPath'. Please reinstall the Composestar tool.");
				Debug.Instance.AddTaskItem(String.Format(CultureInfo.CurrentCulture,"Composestar; Missing file '{0}' in Composestar installation path. Please reinstall the Composestar tool.", iniFileName), 
					vsTaskPriority.vsTaskPriorityHigh, vsTaskIcon.vsTaskIconUser );  
				Debug.Instance.ActivateTaskListWindow(); 
				return false;
			}

			return true;
		}

		/// <summary>
		/// Save the toolbar information.
		/// </summary>
		private void SaveToolBarLayout()
		{
			Registry reg = new Registry();
			
			try
			{
				reg.SetKeyValue(Microsoft.Win32.Registry.CurrentUser, RegistryPath, "ToolBarVisible",  m_Bar.Visible.ToString());
				reg.SetKeyValue(Microsoft.Win32.Registry.CurrentUser, RegistryPath, "ToolBarLocationX", m_Bar.Left);
				reg.SetKeyValue(Microsoft.Win32.Registry.CurrentUser, RegistryPath, "ToolBarLocationY", m_Bar.Top);
				reg.SetKeyValue(Microsoft.Win32.Registry.CurrentUser, RegistryPath, "ToolBarRowIndex",  m_Bar.RowIndex.ToString() );
			}
			catch (Exception ex)
			{
				string e = ex.ToString(); 
			}
		}

		/// <summary>
		/// Load the toolbar information
		/// </summary>
		private void LoadToolBarLayout()
		{

			Registry reg = new Registry();

	
			try
			{
				m_Bar.Visible = Convert.ToBoolean(reg.GetKeyValue(Microsoft.Win32.Registry.CurrentUser, RegistryPath, "ToolBarVisible"));
			}
			catch (Exception)
			{
				
			}
			try
			{
				m_Bar.Left = Convert.ToInt32(reg.GetKeyValue(Microsoft.Win32.Registry.CurrentUser, RegistryPath, "ToolBarLocationX"));
			}
			catch (Exception)
			{
				
			}
			try
			{
				m_Bar.Top = Convert.ToInt32(reg.GetKeyValue(Microsoft.Win32.Registry.CurrentUser, RegistryPath, "ToolBarLocationY"));
			}
			catch (Exception)
			{
				
			}
			try
			{
				m_Bar.RowIndex = Convert.ToInt32(reg.GetKeyValue(Microsoft.Win32.Registry.CurrentUser, RegistryPath, "ToolBarRowIndex"));
			}
			catch (Exception)
			{
				
			}
		}

		#endregion

		#region GUI

		/// <summary>
		///   Creates toolbar and displays it 
		/// </summary>
		private void CreateToolBar() 
		{
			
			// checks if toolbar exists
			foreach (CommandBar bar in this.applicationObject.CommandBars) 
			{
				if (bar.Type == MsoBarType.msoBarTypeNormal) 
				{
					if (bar.Name.Equals(commandBarValue)) 
					{
						m_Bar = bar;
						break;
					}
				}
			}
			// if not found, creates it
			if (m_Bar == null) 
			{
				m_Bar = applicationObject.CommandBars.Add(commandBarValue, MsoBarPosition.msoBarTop, null, null);				
			}	
			m_Bar.Visible = true;

			this.LoadToolBarLayout(); 
		}

		/// <summary>
		///   Creates menu at the end of the menu bar.
		/// </summary>
		private void CreateMenu() 
		{
			// checks if menu exists
			m_Menu = applicationObject.CommandBars.ActiveMenuBar.FindControl(MsoControlType.msoControlPopup, 1, CommandBarName, false, false);
			// if not found, appends it to the menu bar
			if (m_Menu == null) 
			{
				m_Menu = applicationObject.CommandBars.ActiveMenuBar.Controls.Add(MsoControlType.msoControlPopup, Type.Missing, Type.Missing, Type.Missing, false);
				m_Menu.Caption = "&Compose*";
				m_Menu.Tag = CommandBarName;
				m_Menu.Visible = true;
			}
		}

	
		/// <summary>
		///   Adds a command.
		/// </summary>
		/// <param name="addInInst">
		///   Addin instance.
		/// </param>
		/// <param name="name">
		///   Name of the command.
		/// </param>
		/// <param name="buttonText">
		///   Text on the button.
		/// </param>
		/// <param name="toolTip">
		///   ToolTip text.
		/// </param>
		/// <param name="msoButton">
		///   Flag indicating if MsoButton should be displayed.
		/// </param>
		/// <param name="bitmap">
		///   ID of the bitmap used for the command.
		/// </param>
		private void AddCommand(AddIn addInInst, string name, string buttonText, string toolTip, bool msoButton, int bitmap, bool beginGroup, bool addToToolBar) 
		{
			object[] contextGUIDS = new object[] { };
			// search if command is already added to commands collection
			EnvDTE.Commands commands = applicationObject.Commands;
			EnvDTE.Command command = null;
			string fullName = addInInst.ProgID + "." + name;
			foreach (EnvDTE.Command cmd in commands) 
			{
				if (fullName.Equals(cmd.Name)) 
				{
					command = cmd;
					break;
				}
			}
			DeleteCommand(fullName);
			command = null;
			// if command has not been found, add it
			if (command == null) 
			{
				command = commands.AddNamedCommand(addInInst, name, buttonText, toolTip, msoButton, bitmap, ref contextGUIDS, (int)vsCommandStatus.vsCommandStatusSupported+(int)vsCommandStatus.vsCommandStatusEnabled);
			}
			if (addToToolBar) AddCommandToToolbar(command, buttonText, toolTip, beginGroup);
			AddCommandToMenubar(command, buttonText, toolTip, beginGroup);
		}

		/// <summary>
		///   Adds a command to toolbar.
		/// </summary>
		/// <param name="command">
		///   <c>Command</c> to add.
		/// </param>
		/// <param name="toolbarName">
		///   Name of the toolbar to add the control to.
		/// </param>
		/// <param name="buttonText">
		///   Text on the button.
		/// </param>
		/// <param name="toolTip">
		///   ToolTip text.
		/// </param>
		private void AddCommandToToolbar(EnvDTE.Command command, string buttonText, string toolTip, bool beginGroup) 
		{
			           
			Microsoft.Office.Core.CommandBarButton commandBarButton = (Microsoft.Office.Core.CommandBarButton)m_Bar.FindControl(MsoControlType.msoControlButton, null, command.Name, null, true);
			// control does not exist at all, create it
			if (commandBarButton == null) 
			{
				commandBarButton = AppendControl(command, buttonText, toolTip, m_Bar);
				commandBarButton.Style = MsoButtonStyle.msoButtonAutomatic;
				commandBarButton.BeginGroup = beginGroup;
			}
			
			commandBarButton.Visible = true;
		}

		/// <summary>
		///   Appends <c>Command</c> to the <c>CommandBar</c>.
		/// </summary>
		/// <param name="command">
		///   <c>Command</c> to add.
		/// </param>
		/// <param name="buttonText">
		///   Text on the button.
		/// </param>
		/// <param name="toolTip">
		///   ToolTip text.
		/// </param>
		/// <param name="commandBar">
		///   <c>CommandBar</c> to which command is added.
		/// </param>
		/// <returns>
		///   <c>CommandBarButton</c> which has been added.
		/// </returns>
		private Microsoft.Office.Core.CommandBarButton AppendControl(EnvDTE.Command command, string buttonText, string toolTip, Microsoft.Office.Core.CommandBar commandBar) 
		{
			Microsoft.Office.Core.CommandBarButton commandBarButton = (CommandBarButton)command.AddControl(commandBar, commandBar.Controls.Count + 1);
			
			commandBarButton.Tag = command.Name;
			commandBarButton.Caption = buttonText;
			commandBarButton.TooltipText = toolTip;
			commandBarButton.Visible = true;

			return commandBarButton;
		}

		/// <summary>
		///   Adds a command to a menubar.
		/// </summary>
		/// <param name="command">
		///   <c>Command</c> to add.
		/// </param>
		/// <param name="menuId">
		///   ID of the menu into which command has to be inserted.
		/// </param>
		/// <param name="commandId">
		///   ID of the command after which new command will be inserted.
		/// </param>
		/// <param name="buttonText">
		///   Text on the button.
		/// </param>
		/// <param name="toolTip">
		///   ToolTip text.
		/// </param>
		/// <param name="beginGroup">
		///   Flag if item should begin a group (i.e should it be preceeded by 
		///   separator).
		/// </param>
		private void AddCommandToMenubar(EnvDTE.Command command, string buttonText, string toolTip, bool beginGroup) 
		{
			
			Microsoft.Office.Core._CommandBars commandBars = applicationObject.CommandBars;
			Microsoft.Office.Core.CommandBar menuBar = commandBars.ActiveMenuBar;
			// search if command is already available
			Microsoft.Office.Core.CommandBarControl menuItem = menuBar.FindControl(Microsoft.Office.Core.MsoControlType.msoControlButton, null, command.Name, null, true);
			if (menuItem == null) 
			{
				CommandBarPopup vcbPopupBar = (CommandBarPopup)m_Menu;
				CommandBar vcbCommandBar = (CommandBar)vcbPopupBar.CommandBar;
				menuItem = AppendControl(command, buttonText, toolTip, vcbCommandBar);
				menuItem.BeginGroup = beginGroup;
			}
			else
				menuItem.Visible = true;

		}

		/// <summary>
		///   Deletes a command from menu and tool bar.
		/// </summary>
		/// <param name="commandName">
		///   Name of the command to delete.
		/// </param>
		private void DeleteCommand(string commandName) 
		{
			DeleteCommandFromToolbar(CommandBarName, commandName);
			DeleteCommandFromMenubar(commandName);

			// search if command is in the commands collection
			EnvDTE.Commands commands = applicationObject.Commands;
			EnvDTE.Command command = null;

			foreach (EnvDTE.Command cmd in commands) 
			{
				if (commandName.Equals(cmd.Name)) 
				{
					command = cmd;
					break;
				}
			}

			if (command != null)
				command.Delete(); 
		}

		/// <summary>
		///   Deletes a command from the toolbar.
		/// </summary>
		/// <param name="toolbarName">
		///   Name of the toolbar from which command is removed.
		/// </param>
		/// <param name="tag">
		///   Tag identifying control to remove.
		/// </param>
		private void DeleteCommandFromToolbar(string toolbarName, string tag) 
		{
			Microsoft.Office.Core._CommandBars commandBars = applicationObject.CommandBars;
			Microsoft.Office.Core.CommandBar commandBar = (Microsoft.Office.Core.CommandBar)commandBars[toolbarName];
			Microsoft.Office.Core.CommandBarControl commandBarControl = commandBar.FindControl(MsoControlType.msoControlButton, null, tag, null, true);
			if (commandBarControl != null)
				commandBarControl.Delete(false);
		}

		/// <summary>
		///   Deletes a command from a menu.
		/// </summary>
		/// <param name="tag">
		///   Tag identifying command to remove.
		/// </param>
		private void DeleteCommandFromMenubar(string tag) 
		{
			_CommandBars commandBars = applicationObject.CommandBars;
			CommandBar menuBar = commandBars.ActiveMenuBar;
			CommandBarControl addinMenuItem = menuBar.FindControl(MsoControlType.msoControlButton, null, tag, null, true);
			if (addinMenuItem != null)
				addinMenuItem.Delete(false);
		}
		
		#endregion

		#region Event handlers

		public void TaskNavigated(EnvDTE.TaskItem taskItem, ref bool navigateHandled)
		{
			//If the file argument has been specified for this task and does not have a value of null (as text)...
			if(taskItem.FileName != "" && !taskItem.FileName.StartsWith("null")  )
			{
				EnvDTE.Window fileWindow;
				EnvDTE.TextWindow textWindow;
				EnvDTE.TextPane textPane;

				//Then open the file, find the TextWindow and TextPane objects...
				if( Path.GetExtension(taskItem.FileName) == ".html")
				{
					fileWindow = applicationObject.ItemOperations.OpenFile(
						taskItem.FileName, EnvDTE.Constants.vsViewKindDesigner);
				}
				else
				{
					fileWindow = applicationObject.ItemOperations.OpenFile(
						taskItem.FileName, EnvDTE.Constants.vsViewKindTextView);
				}
				textWindow = (EnvDTE.TextWindow)fileWindow.Object;
				textPane = (EnvDTE.TextPane)textWindow.ActivePane;

				//Then move the caret to the correct line:
				textPane.Selection.MoveTo(taskItem.Line, 1, false);
				textPane.Selection.SelectLine();
				navigateHandled = true;
			}
		} 

		public void OnBuildBegin(vsBuildScope scope, vsBuildAction action) 
		{
			this.buildSuccess = false;

			// Setup the statusbar
			Debug.Instance.ShowProgress("Building with Compose*", 0);
			Debug.Instance.ShowAnimation(vsStatusAnimation.vsStatusAnimationBuild);
			Debug.Instance.ResetWarnings();
			Application.DoEvents();

			if (action == EnvDTE.vsBuildAction.vsBuildActionBuild || action == EnvDTE.vsBuildAction.vsBuildActionRebuildAll) 
			{

				// Save all open documents
				Documents docs = applicationObject.Documents;
				for(int i=1;i<=docs.Count;i++)
				{
					if(!docs.Item(i).Saved)
						docs.Item(i).Save(docs.Item(i).FullName);
				}

				// Clear and activate the output panels.
				Debug.Instance.ClearOutputWindowPane();
				Debug.Instance.ClearTaskListWindow();
				Debug.Instance.ActivateOutputWindowPane();
				Application.DoEvents();

				Debug.Instance.ShowProgress("Initializing", 5); 
				
				// Changed DateTime timer to HighPerformanceTimer - Michiel (28/02/2005)
				long StartTime=0;
				bool HighTimerEnabled=false;
				long ctr1 = 0, ctr2 = 0, freq = 0;
				if (QueryPerformanceCounter(ref ctr1)!=0)	// Begin timing.
				{
					HighTimerEnabled = true;
				}
				else
				{
					StartTime = DateTime.Now.Ticks;
					HighTimerEnabled = false;
				}

				// Prints the start text
				Debug.Instance.Log("------ Composestar build started: Solution: " + applicationObject.Solution.Properties.Item("Name").Value.ToString() + " ------\n");
							
				try 
				{
					Debug.Instance.ShowProgress("Preparing configuration files", 10); 

					Debug.Instance.Log(DebugModes.Debug,"AddIn","Preparing configuration files...");
					
					string solutionfile = applicationObject.Solution.Properties.Item("Path").Value.ToString();
					string tempfolder = solutionfile.Substring(0, solutionfile.LastIndexOf("\\")+1).Replace("\\", "/");

					DeleteFile(Path.Combine (tempfolder,  "build.ini"));
					iniconfig = new IniFile(Path.Combine (tempfolder , "build.ini"));
				
					iniconfig.IniWriteValue("Common", "TempFolder", tempfolder);
					iniconfig.IniWriteValue("Master", "CompilePhase", "one");
			
					if(ComposestarIniFileExists()) 
					{
						// Generate solution specific configuration file for composestar
						Debug.Instance.ShowProgress("Creating configuration files", 15); 
						Debug.Instance.Log(DebugModes.Debug,"AddIn","Creating solution specific configuration file...");
						
						configmanager = new ConfigManager(iniconfig);
						configmanager.run(applicationObject, scope, action);

						string debuglevel = this.iniconfig.IniReadValue("Common","BuildDebugLevel");
						if(debuglevel != null)
						{
							int debug_level = Convert.ToInt32(this.iniconfig.IniReadValue("Common","BuildDebugLevel"));
							// Use next line when the string is used instead of an integer (the value)
							//Debug.Instance.DebugMode = DebugModes.Parse(typeof(DebugModes), this.iniconfig.IniReadValue("Common","BuildDebugLevel"), true);
							Debug.Instance.DebugMode = (DebugModes) debug_level;
						}

						// Call Master to do its first run
						Debug.Instance.ShowProgress("Running master phase one", 20); 
						Debug.Instance.Log(DebugModes.Debug,"AddIn","Invoking Master to do first run...");

						mastermanager = new MasterManager(iniconfig);
						mastermanager.run(applicationObject, scope, action);

						bool dummiesOK = false;

						if (mastermanager.CompletedSuccessfully()) 
						{
							Debug.Instance.ShowProgress("Processing embedded sources", 30); 
							Debug.Instance.Log(DebugModes.Debug,"AddIn","Processing embedded sources...");

							embsrcmanager = new EmbeddedSourceManager(iniconfig);
							embsrcmanager.run(applicationObject, scope, action);
							
							//							dummymanager = null;
							//							configmanager = null;
							//							iniconfig = null;
							//							mastermanager = null;
							
							//System.IO.File.Delete(tempfolder + "project.ini");
							//iniconfig = new IniFile(tempfolder + "project.ini");
							//iniconfig.IniWriteValue("Common", "TempFolder", tempfolder);
							//iniconfig.IniWriteValue("Master", "CompilePhase", "one");
							//ComposestarIniFileExists();
							
							Debug.Instance.ShowProgress("Creating dummies", 40);
							Debug.Instance.Log(DebugModes.Debug,"AddIn","Creating dummies...");

							dummymanager = new DummyManager(iniconfig);
							dummymanager.run(applicationObject, scope, action);
							dummiesOK = dummymanager.CompletedSuccessfully();

							if (!dummiesOK)
							{
								Debug.Instance.Log("---------------------- Done ----------------------");
								Debug.Instance.Log("Dummy creation failed. See the task window for more information.");
								Debug.Instance.Log("");
								Debug.Instance.Log("Composestar build failed.");
								Debug.Instance.Log("");
								Debug.Instance.Log("");
								Debug.Instance.ActivateTaskListWindow();
							}
							else
							{
								Debug.Instance.ShowProgress("Creating config", 50);
								Debug.Instance.Log(DebugModes.Debug,"AddIn","Creating solution specific configuration file...");
								
								configmanager = new ConfigManager(iniconfig);
								configmanager.run(applicationObject, scope, action);

								// Find and dump attributes (annotations)
								Debug.Instance.ShowProgress("Harvesting annotations", 60);
								Debug.Instance.Log(DebugModes.Debug,"AddIn","Harvesting annotations...");

								attributemanager = new AttributeManager(iniconfig);
								attributemanager.run(applicationObject, scope, action);
							
								// Call Master to do its second run
								Debug.Instance.ShowProgress("Running master phase two", 70);
								iniconfig.IniWriteValue("Master", "CompilePhase", "two");
								Debug.Instance.Log(DebugModes.Debug,"AddIn","Invoking Master to do second run...");
								mastermanager = new MasterManager(iniconfig);
								mastermanager.run(applicationObject, scope, action);

								embsrcmanager.removeEmbeddedSources();
							}
						}

						if (dummiesOK && mastermanager.CompletedSuccessfully()) 
						{
							// Call the assembly manager to copy all the required dll's to their final location
							Debug.Instance.ShowProgress("Copying assemblies", 90);
							Debug.Instance.Log(DebugModes.Debug,"AddIn","Copying assemblies...");
							assemblymanager = new AssemblyManager(iniconfig);
							assemblymanager.run(applicationObject, scope, action);
							this.buildSuccess = true;
						}

						if (this.buildSuccess)
						{
							long EndTime; 
							double TimerValue;
							if (HighTimerEnabled) 
							{
								QueryPerformanceCounter(ref ctr2);
								QueryPerformanceFrequency(ref freq);
								TimerValue = Convert.ToDouble((ctr2 - ctr1) * 1.0 / freq) ;
							}
							else
							{
								EndTime = DateTime.Now.Ticks;
								TimeSpan buildtime = new TimeSpan(EndTime - StartTime);
								TimerValue = Convert.ToDouble(buildtime.Seconds);
							}
							
							Debug.Instance.HideProgress("Composestar build completed");
							Debug.Instance.Log("");
							Debug.Instance.Log("---------------------- Done ----------------------");
							Debug.Instance.Log("");
							Debug.Instance.Log(String.Format(CultureInfo.CurrentCulture," Composestar build complete in {0:0.00} seconds.",TimerValue) );
							Debug.Instance.Log("");
							Debug.Instance.Log("");
							this.buildSuccess = true;
						}
						else 
						{
							Debug.Instance.HideProgress("Composestar build completed");
							Debug.Instance.Log("");
							Debug.Instance.Log("---------------------- Done ----------------------");
							Debug.Instance.Log("");
							Debug.Instance.Log("Composestar build failed.");
							Debug.Instance.Log("");
							Debug.Instance.Log("");
							Debug.Instance.ActivateTaskListWindow();
						}
					}
				}
				catch (Exception e) 
				{
					Debug.Instance.Log("*** Composestar internal error ***"); 
					Debug.Instance.Log(" message : " + e.Message );
					Debug.Instance.Log("   stack : " + e.StackTrace);
					Debug.Instance.ActivateOutputWindowPane();
					Debug.Instance.HideProgress("Composestar build failed");
				}

				Debug.Instance.HideAnimation();
			}
		}

		/// <summary>
		/// Display a modal settings form.
		/// </summary>
		private void OnSettings()
		{
			SettingsForm sf = new SettingsForm(this.applicationObject);
			sf.ShowDialog(); 
		}

		/// <summary>
		/// Cleans the previous build information
		/// </summary>
		private void OnClean()
		{

			// Setup the statusbar
			Debug.Instance.ClearOutputWindowPane();
			Debug.Instance.ClearTaskListWindow();
			Debug.Instance.ShowProgress("Cleaning previous information for Compose*", 0);
			Debug.Instance.ShowAnimation(vsStatusAnimation.vsStatusAnimationGeneral );
			Debug.Instance.ResetWarnings();

			// Prints the start text
			Debug.Instance.Log("------ Composestar clean started: Solution: " + applicationObject.Solution.Properties.Item("Name").Value.ToString() + " ------\n");
			
			Application.DoEvents();

			string solutionfile = applicationObject.Solution.Properties.Item("Path").Value.ToString();
			string tempfolder = solutionfile.Substring(0, solutionfile.LastIndexOf("\\")+1).Replace("\\", "/");

			Debug.Instance.Log("  removing build.ini");
			DeleteFile(Path.Combine(tempfolder, "build.ini") );

			Debug.Instance.Log("  removing incre.html");
			DeleteFile(Path.Combine(tempfolder, "incre.html") );

			Debug.Instance.Log("  removing weavespec.xml");
			DeleteFile(Path.Combine(tempfolder, "weavespec.xml") );

			Debug.Instance.Log("  removing repository.xml");
			DeleteFile(Path.Combine(tempfolder, "repository.xml") );

			Debug.Instance.Log("  removing history.dat");
			DeleteFile(Path.Combine(tempfolder, "history.dat") );

			Debug.Instance.Log("  removing filth.html");
			DeleteFile(Path.Combine(tempfolder, "filth.html") );

			Debug.Instance.Log("  removing filelist.peweaver");
			DeleteFile(Path.Combine(tempfolder, "filelist.peweaver") );

			Debug.Instance.Log("  removing filelist.ilweaver");
			DeleteFile(Path.Combine(tempfolder, "filelist.ilweaver") );

			Debug.Instance.Log("  removing SECRET.html");
			DeleteFile(Path.Combine(tempfolder, "SECRET.html") );

			Debug.Instance.Log("  removing langmap.pro");
			DeleteFile(Path.Combine(tempfolder, "langmap.pro") );

			Debug.Instance.Log("  removing attributes.xml");
			DeleteFile(Path.Combine(tempfolder, "attributes.xml") );

			Debug.Instance.Log("  removing types.xml");
			DeleteFile(Path.Combine(tempfolder, "types.xml") );

			Debug.Instance.Log("  cleaning object folder");
			string[] filesObj = Directory.GetFiles(Path.Combine(tempfolder, "obj") , "*.*");
			foreach (string f in filesObj)
			{
				Debug.Instance.Log(String.Format("    removing {0}", Path.GetFileName( f)));
				DeleteFile( f );
			}
			Debug.Instance.Log("  cleaning object/weaver folder");
			string[] filesWeaver = Directory.GetFiles(Path.Combine(tempfolder, "obj/weaver") , "*.*");
			foreach (string f in filesWeaver)
			{
				Debug.Instance.Log(String.Format("    removing {0}", Path.GetFileName( f)));
				DeleteFile( f );
			}

			// Done
			Debug.Instance.Log("");
			Debug.Instance.Log("---------------------- Done ----------------------");
			Debug.Instance.Log("");
			Debug.Instance.Log("  Cleaning is completed.\n");
			Debug.Instance.HideProgress("Compose* cleaned");
			Debug.Instance.HideAnimation();
			Debug.Instance.ActivateOutputWindowPane();

		}

		/// <summary>
		/// Run the compiled application.
		/// </summary>
		private void OnDebugRun()
		{
			try
			{
				string dir = this.iniconfig.IniReadValue("Common", "OutputPath");
				string debugger = this.iniconfig.IniReadValue("Common", "Debugger");
				if(dir != null && !"".Equals(dir))
				{
					if(dir.StartsWith("\"") && dir.EndsWith("\"") && dir.Length > 1){
						dir = dir.Substring(1,dir.Length-2);
					}
					if(!dir.EndsWith("\\"))
					{
						dir += '\\';
					}
					if(debugger == null || "".Equals(debugger))
					{
						debugger = "Composestar.RuntimeCore.CODER.VisualDebugger.CodeDebugger.CodeDebugger";
					}
					string debuggerXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<debugger>" + debugger + "<debugger/>";
					string fileName = "\"" + dir + "debugger.xml\"";
					StreamWriter writer = File.CreateText(fileName);
					writer.Write(debuggerXML);
					writer.Close();
				}
			}
			catch(Exception)
			{
			}	
		OnRun();
	}

		/// <summary>
		/// Run the compiled application.
		/// </summary>
		private void OnRun()
		{
			//			if (iniconfig == null) 
			//			{
			//				// Try to load a previous ini file
			//				string solutionfile = applicationObject.Solution.Properties.Item("Path").Value.ToString();
			//				string tempfolder = solutionfile.Substring(0, solutionfile.LastIndexOf("\\")+1).Replace("\\", "/");
			//				if (System.IO.File.Exists(tempfolder + "project.ini"))
			//				{
			//					iniconfig = new IniFile(tempfolder + "project.ini");
			//				}
			//			}

			// Clean panels
			Debug.Instance.ClearOutputWindowPane();
			Debug.Instance.ClearTaskListWindow();
			Application.DoEvents();
						
			// Check for iniconfig object
			if (this.iniconfig == null)
			{
				Debug.Instance.Log("Application cannot be started. Build the application first.");
				Debug.Instance.AddTaskItem("Composestar Run; Application cannot be started. Build the application first.", 
					vsTaskPriority.vsTaskPriorityHigh, vsTaskIcon.vsTaskIconCompile);  
				Debug.Instance.ActivateTaskListWindow();
				return;
			}
			
			Debug.Instance.Log("----------------------- Run ----------------------\n");

			// Set the statusbar
			Debug.Instance.ShowAnimation(vsStatusAnimation.vsStatusAnimationDeploy );

			try
			{
				String savefolder = System.IO.Directory.GetCurrentDirectory();
				System.IO.Directory.SetCurrentDirectory(this.iniconfig.IniReadValue("Common", "OutputPath"));

				System.Diagnostics.Process p = new System.Diagnostics.Process();
				p.StartInfo.FileName = this.iniconfig.IniReadValue("Common", "Executable");		
	
				// If the filename is empty we can stop
				if (p.StartInfo.FileName.Trim().Length == 0)
				{
					Debug.Instance.Log("Application cannot be started. Build the application first.");
					Debug.Instance.AddTaskItem("Composestar Run; Application cannot be started since the filename is missing. Make sure the application has been build before running.", 
						vsTaskPriority.vsTaskPriorityHigh, vsTaskIcon.vsTaskIconCompile);  
					Debug.Instance.ActivateTaskListWindow();
					Debug.Instance.HideAnimation();
					return;
				}

				EnvDTE.Project activeProject = 	GetStartupProject();
 
				if (activeProject != null)
				{
					foreach (EnvDTE.Configuration config in activeProject.ConfigurationManager   )
					{
						if (config.ConfigurationName.Equals(applicationObject.Solution.SolutionBuild.ActiveConfiguration.Name))
						{
							foreach (EnvDTE.Property prop in config.Properties)
							{
								if (prop.Name.Equals("StartArguments") )
									p.StartInfo.Arguments = (string) prop.Value;  

								// Uncomment next line to see all the options
								// this.PrintMessage(String.Format("{0} = {1}",  prop.Name, prop.Value ));
							}
						}			
					}
				}

				Debug.Instance.ActivateOutputWindowPane(); 
				Debug.Instance.Log(String.Format(CultureInfo.CurrentCulture, "Starting application '{0}' with arguments '{1}'.", p.StartInfo.FileName, p.StartInfo.Arguments ));
				
				p.Start();
			
				//p.WaitForExit();

				System.IO.Directory.SetCurrentDirectory(savefolder);
				
				Debug.Instance.Log("\n------------------ Run Complete ------------------\n");
			}
			catch (Exception e)
			{
				Debug.Instance.Log("*** Composestar internal error ***"); 
				Debug.Instance.Log(" message : " + e.Message );
				Debug.Instance.Log("   stack : " + e.StackTrace);
				Debug.Instance.ActivateOutputWindowPane();
			}
		
			Debug.Instance.HideAnimation();
		}

		#endregion
		
	}



}
