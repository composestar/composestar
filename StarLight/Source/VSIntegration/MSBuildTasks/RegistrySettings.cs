using System;
using System.Security.Permissions;
using System.Diagnostics.CodeAnalysis;
using System.Security.Permissions;
    
using Microsoft.Win32;

namespace Composestar.StarLight.MSBuild.Tasks
{
    /// <summary>
    /// Retrieves the settings from the registry
    /// </summary>
    public class RegistrySettings
    {

        private String classPath;

        /// <summary>
        /// Gets or sets the class path.
        /// </summary>
        /// <value>The class path.</value>
        public String ClassPath
        {
            get { return classPath; }
            set { classPath = value; }
        }

        private string mainClass;

        /// <summary>
        /// Gets or sets the main class.
        /// </summary>
        /// <value>The main class.</value>
        public string MainClass
        {
            get { return mainClass; }
            set { mainClass = value; }
        }

        private String jvmOptions;

        /// <summary>
        /// Gets or sets the JVM options.
        /// </summary>
        /// <value>The JVM options.</value>
        [SuppressMessage("Microsoft.Naming", "CA1705", Justification="Java Virtual Machine") ]
        public String JVMOptions
        {
            get { return jvmOptions; }
            set { jvmOptions = value; }
        }

        private string javaLocation;

        /// <summary>
        /// Gets or sets the java location.
        /// </summary>
        /// <value>The java location.</value>
        public string JavaLocation
        {
            get { return javaLocation; }
            set { javaLocation = value; }
        }

        private string installFolder;

        /// <summary>
        /// Gets or sets the install folder.
        /// </summary>
        /// <value>The install folder.</value>
        public string InstallFolder
        {
            get { return installFolder; }
            set { installFolder = value; }
        }


        /// <summary>
        /// Reads the settings.
        /// </summary>
        [RegistryPermissionAttribute(System.Security.Permissions.SecurityAction.Demand,
              Read = "HKEY_LOCAL_MACHINE\\Software\\Composestar\\StarLight")]
        public bool ReadSettings()
        {
          
            RegistryKey regKey = Registry.LocalMachine.OpenSubKey(@"Software\Composestar\StarLight");

            if (regKey != null)
            {
                classPath = (string)regKey.GetValue("JavaClassPath", "");
                mainClass = (string)regKey.GetValue("JavaMainClass", "Composestar.DotNET.MASTER.StarLightMaster");
                jvmOptions = (string)regKey.GetValue("JavaOptions", "");
                javaLocation = (string)regKey.GetValue("JavaFolder", "");
                installFolder = (string)regKey.GetValue("StarLightInstallFolder", "");
            }
            else
            {
                return false;              
            }

            // Check for empty values
            if (string.IsNullOrEmpty(classPath) | string.IsNullOrEmpty(mainClass) | string.IsNullOrEmpty(installFolder) )
            {
                return false;
            }

            return true;
        }

        /// <summary>
        /// Gets the NETSDK location.
        /// </summary>
        /// <returns></returns>
        [SuppressMessage("Microsoft.Naming", "CA1705")]
        [RegistryPermissionAttribute(System.Security.Permissions.SecurityAction.Demand,
          Read = "HKEY_LOCAL_MACHINE\\Software\\Composestar\\StarLight")]
        public static string RetrieveNetSDKLocation()
        {
            RegistryKey regKey = Registry.LocalMachine.OpenSubKey(@"SOFTWARE\Microsoft\.NETFramework");

            if (regKey != null)
            {
                return (string)regKey.GetValue("sdkInstallRootv2.0", "");
               
            }
            else
            {
                return "";              
            }


        }

    }
}
