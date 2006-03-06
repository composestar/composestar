using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
using System.Globalization; 
using System.Reflection; 
using System.IO;
using System.Text;
using EnvDTE;
using Ini;

namespace ComposestarVSAddin 
{
	/// <summary>
	/// Summary description for SettingsForm.
	/// </summary>
	public class SettingsForm : System.Windows.Forms.Form
	{
		private System.Windows.Forms.Button btnOk;
		private System.Windows.Forms.Button btnCancel;
		private System.Windows.Forms.PropertyGrid propertyGrid;
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.Container components = null;
		private System.Windows.Forms.Label lblSettingsType;
		private System.Windows.Forms.ComboBox cmbProjects;

		private ArrayList settingsProjects = new ArrayList(); 
		private bool IsInited = false;

		private _DTE applicationObject;

		public SettingsForm(_DTE appObject)
		{
			//
			// Required for Windows Form Designer support
			//

			applicationObject = appObject;
			InitializeComponent();
			LoadProjects();

		}

		/// <summary>
		/// Clean up any resources being used.
		/// </summary>
		protected override void Dispose( bool disposing )
		{
			if( disposing )
			{
				if(components != null)
				{
					components.Dispose();
				}
			}
			base.Dispose( disposing );
		}

		#region Windows Form Designer generated code
		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{
			this.propertyGrid = new System.Windows.Forms.PropertyGrid();
			this.btnOk = new System.Windows.Forms.Button();
			this.btnCancel = new System.Windows.Forms.Button();
			this.lblSettingsType = new System.Windows.Forms.Label();
			this.cmbProjects = new System.Windows.Forms.ComboBox();
			this.SuspendLayout();
			// 
			// propertyGrid
			// 
			this.propertyGrid.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
				| System.Windows.Forms.AnchorStyles.Left) 
				| System.Windows.Forms.AnchorStyles.Right)));
			this.propertyGrid.CommandsVisibleIfAvailable = true;
			this.propertyGrid.LargeButtons = false;
			this.propertyGrid.LineColor = System.Drawing.SystemColors.ScrollBar;
			this.propertyGrid.Location = new System.Drawing.Point(8, 40);
			this.propertyGrid.Name = "propertyGrid";
			this.propertyGrid.PropertySort = System.Windows.Forms.PropertySort.Categorized;
			this.propertyGrid.Size = new System.Drawing.Size(401, 378);
			this.propertyGrid.TabIndex = 0;
			this.propertyGrid.Text = "propertyGrid";
			this.propertyGrid.ViewBackColor = System.Drawing.SystemColors.Window;
			this.propertyGrid.ViewForeColor = System.Drawing.SystemColors.WindowText;
			// 
			// btnOk
			// 
			this.btnOk.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
			this.btnOk.Location = new System.Drawing.Point(248, 426);
			this.btnOk.Name = "btnOk";
			this.btnOk.Size = new System.Drawing.Size(76, 23);
			this.btnOk.TabIndex = 1;
			this.btnOk.Text = "&Ok";
			this.btnOk.Click += new System.EventHandler(this.btnOk_Click);
			// 
			// btnCancel
			// 
			this.btnCancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
			this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
			this.btnCancel.Location = new System.Drawing.Point(330, 426);
			this.btnCancel.Name = "btnCancel";
			this.btnCancel.Size = new System.Drawing.Size(74, 23);
			this.btnCancel.TabIndex = 2;
			this.btnCancel.Text = "&Cancel";
			this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
			// 
			// lblSettingsType
			// 
			this.lblSettingsType.Location = new System.Drawing.Point(8, 10);
			this.lblSettingsType.Name = "lblSettingsType";
			this.lblSettingsType.Size = new System.Drawing.Size(64, 23);
			this.lblSettingsType.TabIndex = 3;
			this.lblSettingsType.Text = "Project:";
			// 
			// cmbProjects
			// 
			this.cmbProjects.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
				| System.Windows.Forms.AnchorStyles.Right)));
			this.cmbProjects.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
			this.cmbProjects.Location = new System.Drawing.Point(80, 8);
			this.cmbProjects.Name = "cmbProjects";
			this.cmbProjects.Size = new System.Drawing.Size(329, 24);
			this.cmbProjects.TabIndex = 4;
			this.cmbProjects.SelectedIndexChanged += new System.EventHandler(this.cmbProjects_SelectedIndexChanged);
			// 
			// SettingsForm
			// 
			this.AcceptButton = this.btnOk;
			this.AutoScaleBaseSize = new System.Drawing.Size(7, 17);
			this.CancelButton = this.btnCancel;
			this.ClientSize = new System.Drawing.Size(416, 455);
			this.Controls.Add(this.cmbProjects);
			this.Controls.Add(this.lblSettingsType);
			this.Controls.Add(this.btnCancel);
			this.Controls.Add(this.btnOk);
			this.Controls.Add(this.propertyGrid);
			this.Font = new System.Drawing.Font("Tahoma", 8F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((System.Byte)(0)));
			this.MaximizeBox = false;
			this.MinimizeBox = false;
			this.Name = "SettingsForm";
			this.Text = "Compose* Settings";
			this.ResumeLayout(false);

		}
		#endregion

		/// <summary>
		/// Load a list of all projects
		/// </summary>
		private void LoadProjects()
		{
			cmbProjects.Items.Clear();
	
			// Add general settings
			ProjectItem piGeneral = new ProjectItem("General Settings", GetComposeStarIniFileName());
			LoadSettings(piGeneral.Settings, piGeneral.IniFile);
			settingsProjects.Add(piGeneral); 

			// Get all the projects in the solution.
			foreach( Project project in applicationObject.Solution.Projects ) 
			{
				if( project != null  && project.Properties != null )
				{
					ProjectItem pi = new ProjectItem(project.Name, GetProjectIniFileName(project.FileName));
					LoadSettings(pi.Settings, pi.IniFile);
					settingsProjects.Add(pi); 
				}
			}
			
			cmbProjects.DataSource = settingsProjects; 
			cmbProjects.DisplayMember = "ProjectName";
			cmbProjects.ValueMember = "IniFile";
			
			if (settingsProjects.Count > 1)
			{
				// Select first project
				cmbProjects.SelectedValue = ((ProjectItem)settingsProjects[1]).IniFile;
				ShowSettings(((ProjectItem)settingsProjects[1]).IniFile );
			}
			else
			{
				// Select general settings
				ShowSettings(GetComposeStarIniFileName());
			}

			// We are done with init, so enable this.
			IsInited = true;
		}

		/// <summary>
		/// Create an ini filename for a project.
		/// </summary>
		/// <param name="filename"></param>
		/// <returns></returns>
		private string GetProjectIniFileName(string filename)
		{
			return Path.Combine(Path.GetDirectoryName(filename) , "project.ini");
		}

		private void btnCancel_Click(object sender, System.EventArgs e)
		{
			this.Close(); 
		}

		private void btnOk_Click(object sender, System.EventArgs e)
		{
			if (SaveAllSettings()) 
			{
				this.Close();
			}
		}

		/// <summary>
		/// Save the settings. If this is unsuccesful, return false so the user can correct the mistakes.
		/// </summary>
		/// <returns></returns>
		private bool SaveSettings(SettingsContainer settings, string iniFilename)
		{
			bool Result = false;
			if (settings == null || iniFilename == null)
			{
				return Result;
			}
    
			IniSettingFieldAttribute attr; // Custom
			//  ConfigurationFieldAttribute
			object[] attributes;         // Attributes assigned to the object
			PropertyInfo property;      // Object property

			Org.Mentalis.Files.IniReader ini = new Org.Mentalis.Files.IniReader(iniFilename); 

			// List of object properties
			PropertyInfo[] itemTypeProps = settings.GetType().GetProperties();

			// Get a list of the ConfiguratorField attributes for the object
			for( int i = 0; i < itemTypeProps.Length; i++ )
			{
				// Get the current ConfiguratorField attribute
				property = itemTypeProps [i];
				attributes = property.GetCustomAttributes(typeof(IniSettingFieldAttribute), true);

				// Verify the ConfiguratorField attribute was found
				if( attributes != null && attributes.Length == 1 )
				{
					// Determine if the field should be included based on the
					// InConfiguration indicator
					attr = (IniSettingFieldAttribute) attributes[0];
					if( attr.InConfiguration == true && property != null )
					{
						try
						{
							string val = "";
							if (property.PropertyType == typeof(System.Int32))
							{
								val = Convert.ToString(property.GetValue(settings, null));
							}
							else if (property.PropertyType == typeof(DebugModes))
							{
								DebugModes dm;
								dm = (DebugModes)(property.GetValue(settings, null));
								if (dm == DebugModes.NotSet)
									val = "";
								else
                                    val = Convert.ToString(Convert.ToInt32(dm)) ;
							}
							else if (property.PropertyType == typeof(SecretModes))
							{
								SecretModes sm;
								sm = (SecretModes)(property.GetValue(settings, null));
								if (sm == SecretModes.NotSet)
									val = "";
								else
									val = Convert.ToString(Convert.ToInt32(sm)) ;
							}
							else if (property.PropertyType == typeof(TriStateBooleanModes))
							{
								TriStateBooleanModes tsb;
								tsb = (TriStateBooleanModes)(property.GetValue(settings, null));
								if (tsb == TriStateBooleanModes.NotSet)
									val = "";
								else if (tsb == TriStateBooleanModes.False)
                                    val = "False" ;
								else if (tsb == TriStateBooleanModes.True)
									val = "True" ;
							}
							else if (property.PropertyType == typeof(System.Boolean))
							{
								val = Convert.ToBoolean(property.GetValue(settings, null)).ToString(); 
							}
							else 
							{
								val = Convert.ToString(property.GetValue(settings, null));
							}

							if (val.Trim().Length > 0)
							{
								if (ini.Write(attr.Section, attr.Key, val) == false)
								{
									Result = false;
									break;
								}
							}
							else
							{
								if (ini.DeleteKey(attr.Section, attr.Key) == false)
								{
									Result = false;
									break;
								}
							}
						}
						catch (Exception ex)
						{
							string e = ex.ToString();
							MessageBox.Show(String.Format("Could not save the setting for property {3} to the {1} file.{0}Exception reported was {2}", Environment.NewLine, iniFilename, e, property.Name ), 
								"Compose* Settings", MessageBoxButtons.OK, MessageBoxIcon.Error); 
						}
					}
					Result = true;
				}
			}
		

			return Result;
		}

		/// <summary>
		/// Save all the settings
		/// </summary>
		/// <returns></returns>
		private bool SaveAllSettings()
		{
			StringBuilder sb = new StringBuilder();

			for (int i = 0 ; i < settingsProjects.Count ; i++)
			{
				ProjectItem pj = (ProjectItem)settingsProjects[i];

				// Only save when the data is dirty
				if (pj.Settings.IsDirty) 
				{
					bool saveSettings = SaveSettings(pj.Settings, pj.IniFile );
					if (saveSettings == false) 
					{
						sb.AppendFormat("Could not save the file '{0}' for project {1}{2}.",pj.IniFile, pj.ProjectName, Environment.NewLine    ) ;
					}
				}
			}
			if (sb.Length > 0)
			{
				sb.AppendFormat("{0}Not all the files could be saved. Try again or close the form.", Environment.NewLine );
				MessageBox.Show(sb.ToString(), "Composestar Settings", MessageBoxButtons.OK, MessageBoxIcon.Exclamation ) ;
			}

			return true;
		}

		/// <summary>
		/// Loads the settings from the ini file.
		/// </summary>
		private void LoadSettings(SettingsContainer settings, string iniFilename)
		{
			// Safety check for null
			if (settings == null) return;
			if (iniFilename == null) return;

			IniSettingFieldAttribute attr; // Custom
			//  ConfigurationFieldAttribute
			object[] attributes;         // Attributes assigned to the object
			PropertyInfo property;      // Object property

			Org.Mentalis.Files.IniReader ini = new Org.Mentalis.Files.IniReader(iniFilename); 

			// List of object properties
			PropertyInfo[] itemTypeProps = settings.GetType().GetProperties();

			// Get a list of the ConfiguratorField attributes for the object
			for( int i = 0; i < itemTypeProps.Length; i++ )
			{
				// Get the current ConfiguratorField attribute
				property = itemTypeProps [i];
				attributes = property.GetCustomAttributes(typeof(IniSettingFieldAttribute), true);

				// Verify the ConfiguratorField attribute was found
				if( attributes != null && attributes.Length == 1 )
				{
					// Determine if the field should be included based on the
					// InConfiguration indicator
					attr = (IniSettingFieldAttribute) attributes[0];
					if( attr.InConfiguration == true && property != null )
					{
						try
						{
							string val = ini.ReadString(attr.Section, attr.Key, "");
						
							if (val.Trim().Length > 0)
							{
								if (property.PropertyType == typeof(System.Int32))
								{
									property.SetValue(settings, Convert.ToInt32(val) , null);
								}
								else if (property.PropertyType == typeof(DebugModes))
								{
									DebugModes dm;
									if (val.Trim().Length == 0)
										dm = DebugModes.NotSet;
									else
										dm =(DebugModes)Convert.ToInt32(val);
									property.SetValue(settings, dm, null);
								}
								else if (property.PropertyType == typeof(SecretModes))
								{
									SecretModes sm;
									if (val.Trim().Length == 0)
										sm = SecretModes.NotSet;
									else
										sm =(SecretModes)Convert.ToInt32(val);
									property.SetValue(settings, sm, null);
								}
								else if (property.PropertyType == typeof(TriStateBooleanModes))
								{
									TriStateBooleanModes tsb = TriStateBooleanModes.NotSet ;
									if (val.Trim().Length == 0)
										tsb = TriStateBooleanModes.NotSet;
									else if (val.ToLower() == "true") 
										tsb =TriStateBooleanModes.True ;
									else if (val.ToLower() == "false") 
										tsb =TriStateBooleanModes.False ;
									property.SetValue(settings, tsb, null);
								}
								else if (property.PropertyType == typeof(System.Boolean))
								{
									property.SetValue(settings, Convert.ToBoolean(val) , null);
								}
								else 
								{
									property.SetValue(settings, val, null);
								}
							}
							else
							{
								if (property.PropertyType == typeof(System.Int32))
								{
									property.SetValue(settings, 0, null);
								}
							}
						}
						catch (Exception ex)
						{
							string e = ex.ToString();
							MessageBox.Show(String.Format("Could not load the setting {3} from the {1} file.{0}Exception reported was {2}", Environment.NewLine, iniFilename, e, property.Name), 
								"Compose* Settings", MessageBoxButtons.OK, MessageBoxIcon.Error);   
							
						}
					}
				}
			}
			// Just loaded all the settings, so set the dirty state to false.
			settings.SetIsDirty(false);
		}

	
		/// <summary>
		/// Get the composestar.ini file from the registry
		/// </summary>
		/// <returns></returns>
		private string GetComposeStarIniFileName()
		{
			string fullname;
			string path = "";
			string iniFileName = "Composestar.ini";
			try
			{
				path = Microsoft.Win32.Registry.CurrentUser.OpenSubKey("Software").OpenSubKey("Microsoft").OpenSubKey("VisualStudio").OpenSubKey("7.1").OpenSubKey("Addins").OpenSubKey("ComposestarVSAddin.Connect").GetValue("ComposestarPath").ToString();
				fullname = System.IO.Path.Combine(path, iniFileName);
			}
			catch (Exception)
			{
				fullname = "";
			}
			return fullname;
		}

		/// <summary>
		/// Performs a check for the existens of the composestar ini file using the registry.
		/// </summary>
		/// <returns>Returns true when the ini file exists.</returns>
		private bool ComposestarIniFileExists()
		{
			
			string fullname;
			
			try
			{
				// First get the composestar path from the registry
				fullname = GetComposeStarIniFileName();
				if (fullname == "")
				{
					System.Windows.Forms.MessageBox.Show("Missing registry key 'ComposestarPath'. Please reinstall the Composestar tool.");
					return false;
				}
				if (!System.IO.File.Exists(fullname)) 
				{
					return false;
				}
			}
			catch (Exception)
			{
				System.Windows.Forms.MessageBox.Show("Missing registry key 'ComposestarPath'. Please reinstall the Composestar tool.");
				return false;
			}

			return true;
		}

		/// <summary>
		/// User changed the current project. So get the settingscontainer and assign to propertygrid.
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void cmbProjects_SelectedIndexChanged(object sender, System.EventArgs e)
		{
			if (IsInited) ShowSettings((string)cmbProjects.SelectedValue);
		}

		/// <summary>
		/// Show the settings of a specific ini file in the propertygrid.
		/// </summary>
		/// <param name="filename">Ini filename</param>
		private void ShowSettings(string filename)
		{
			SettingsContainer sc = GetSettingsContainer(filename);
			if (sc != null)
			{
				this.propertyGrid.SelectedObject = sc;
			}
		}

		/// <summary>
		/// Retrieve a settingscontainer object from the settingsProject array based on the ini filename.
		/// </summary>
		/// <param name="filename"></param>
		/// <returns></returns>
		private SettingsContainer GetSettingsContainer(string filename)
		{
			for (int i = 0 ; i < settingsProjects.Count ; i++)
			{
				if (((ProjectItem)settingsProjects[i]).IniFile == filename)
				{
					return ((ProjectItem)settingsProjects[i]).Settings;
				}
			}
			return null;
		}

		/// <summary>
		/// Private class used to store the projects.
		/// </summary>
		private class ProjectItem
		{
			private string _ProjectName;
			private string _IniFile;
			private SettingsContainer _settings;

			public string ProjectName
			{
				get
				{
					return this._ProjectName;
				}
			}

			public string IniFile 
			{
				get 
				{
					return this._IniFile ;
				}
			}

			public SettingsContainer Settings
			{
				get
				{
					return this._settings;
				}
				set 
				{
					this._settings = value;
				}
			}

			public ProjectItem(string projectName, string iniFile)
			{
				_ProjectName = projectName;
				_IniFile = iniFile;
				_settings = new SettingsContainer(); 
			}
		}
	}
}
