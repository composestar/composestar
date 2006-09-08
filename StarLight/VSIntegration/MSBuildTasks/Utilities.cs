using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Xml;

using Microsoft.Build.BuildEngine;
using Microsoft.Build.Framework;
using Microsoft.Build.Utilities;
using Composestar.StarLight.MSBuild.Tasks.BuildConfiguration;

namespace Composestar.StarLight.MSBuild.Tasks
{
    /// <summary>
    /// Contains shared methods.
    /// </summary>
    public class Utilities
    {

        public static bool searchComposeStarIniFile(TaskLoggingHelper log)
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
                    log.LogError("Could not locate ComposeStar.ini file, please reinstall Compose*!");
                    return false;
                }
                else
                {
                    Settings.ComposestarIni = fullname;
                    return true;
                }
            }
            catch (Exception)
            {
                log.LogError("Could not locate ComposeStar.ini file, please reinstall Compose*!");
                return false;
            }
        }

           #region Process Compose* ini file

        public static void ReadIniFile()
        {
            if (File.Exists(Settings.ComposestarIni) == false)
                return;
            
            IniReader ini = new IniReader(Settings.ComposestarIni);

            String composeStarPath = ini.ReadString("Global Composestar configuration", "ComposestarPath", "");
            if (composeStarPath.Length > 0)
            {
                Settings.Paths.Set("Composestar", composeStarPath);
                //System.Windows.Forms.MessageBox.Show("Found composestar installation: " + composeStarPath);
            }

            String NETPath = ini.ReadString("Global Composestar configuration", ".NETPath", "");
            if (NETPath.Length > 0)
                Settings.Paths.Add("NET", NETPath);

            String NETSDKPath = ini.ReadString("Global Composestar configuration", ".NETSDKPath", "");
            if (NETSDKPath.Length > 0)
                Settings.Paths.Add("NETSDK", NETSDKPath);

            String EmbeddedSourcesFolder = ini.ReadString("Global Composestar configuration", "EmbeddedSourcesFolder", "");
            if (NETSDKPath.Length > 0)
                Settings.Paths.Add("EmbeddedSources", EmbeddedSourcesFolder);

            String SECRETMode = ini.ReadString("Global Composestar configuration", "SECRETMode", "");
            if (SECRETMode.Length > 0)
            {
                ModuleSetting SecretModule = new ModuleSetting();
                SecretModule.Name = "SECRET";
                SecretModule.Elements.Add("mode", SECRETMode);
                Settings.SetModule(SecretModule);
            }

            String RunDebugLevel = ini.ReadString("Common", "RunDebugLevel", "");
            if (RunDebugLevel.Length > 0)
                Settings.RunDebugLevel = (DebugModes)Convert.ToInt32(RunDebugLevel);

            String BuildDebugLevel = ini.ReadString("Common", "BuildDebugLevel", "");
            if (BuildDebugLevel.Length > 0)
                Settings.BuildDebugLevel = (DebugModes)Convert.ToInt32(BuildDebugLevel);

            String INCRE_ENABLED = ini.ReadString("Common", "INCRE_ENABLED", "");
            if (INCRE_ENABLED.Length > 0)
            {
                ModuleSetting IncreModule = new ModuleSetting();
                IncreModule.Name = "INCRE";
                IncreModule.Elements.Add("enabled", INCRE_ENABLED);
                Settings.SetModule(IncreModule);
            }

            String FilterModuleOrder = ini.ReadString("Global Composestar configuration", "FILTH_INPUT", "");
            if (FilterModuleOrder.Length > 0)
            {
                ModuleSetting filthModule = new ModuleSetting();
                filthModule.Name = "FILTH";
                filthModule.Elements.Add("input", FilterModuleOrder);
                Settings.SetModule(filthModule);
            }

            String VerifyAssemblies = ini.ReadString("Common", "VerifyAssemblies", "");
            if (VerifyAssemblies.Length > 0)
            {
                ModuleSetting ilicitModule = new ModuleSetting();
                ilicitModule.Name = "ILICIT";
                ilicitModule.Elements.Add("verifyAssemblies", VerifyAssemblies);
                Settings.SetModule(ilicitModule);
            }

            // Debugger
            String DebuggerType = ini.ReadString("Global Composestar configuration", "DebuggerType", "");
            if (DebuggerType.Length > 0)
            {
                ModuleSetting debuggerModule = new ModuleSetting();
                debuggerModule.Name = "CODER";
                debuggerModule.Elements.Add("DebuggerType", DebuggerType);
                Settings.SetModule(debuggerModule);
            }

            // Custom Filters
            // Custom filters are stored in the CustomFilters section
            String[] customFilters;
            if (ini.ReadSectionValues("CustomFilters", out customFilters))
            {
                foreach (String cusFil in customFilters)
                {
                    String[] parsed = cusFil.Split('=');
                    Settings.CustomFilters.Add(new CustomFilter(parsed[0], parsed[1]));
                }
            }
        }

        #endregion

        #region Read dotNet platform settings

        public static void ReadDotNetPlatform()
        {
            string filePlatformConfig = Path.Combine(Settings.Paths["Composestar"], "PlatformConfigurations.xml");
            ReadDotNetPlatform(filePlatformConfig);
        }

        public static void ReadDotNetPlatform(string fileName)
        {
            Platform p = new Platform();

            XmlTextReader reader = null;

            try
            {
                reader = new XmlTextReader(fileName);
                while (reader.Read())
                {
                    if (reader.NodeType == XmlNodeType.Element)
                    {
                        if (reader.Name.Equals("Platform"))
                        {
                            if (reader.GetAttribute("name").Equals("dotNET"))
                            {

                                p.ClassPath = reader.GetAttribute("classPath");
                                p.MainClass = reader.GetAttribute("mainClass");
                                p.Options = reader.GetAttribute("options");


                                while (reader.Read())
                                {
                                    if (reader.NodeType == XmlNodeType.Element)
                                    {
                                        if (reader.Name.Equals("RequiredFile"))
                                            p.RequiredFiles.Add(reader.GetAttribute("fileName"));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                throw new ApplicationException(String.Format("Could not open the file '{0}' to read the platform settings.", fileName), ex);
            }
            finally
            {
                if (reader != null) reader.Close();
            }

            BuildConfigurationManager.Instance.DotNetPlatform = p;   

        }
        
        #endregion
    }
}
