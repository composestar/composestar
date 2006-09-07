using Win32 = Microsoft.Win32;
using System;

namespace ComposestarVSAddin
{

	/// <summary>
	/// Provide methods to interact with the Windows Registry.
	/// </summary>
	public class Registry
	{

		/// <summary>
		/// Get the 'value' from the Windows
		/// Registry key 'path', starting from the
		/// 'root' key.
		/// </summary>
		/// <param name="key">Microsoft.Win32.Registry.LocalMachine</param>
		/// <param name="path">SOFTWARE\Microsoft\ASP.NET</param>
		/// <param name="name">LastInstallTime</param>
		/// <returns>Value from the key.</returns>
		/// Usage:
		///   string val = OperatingSystem.Registry(Registry.LocalMachine, path, name)
		public string GetKeyValue(Win32.RegistryKey root, string path, string name)
		{

			// Open the registry key 'path', starting from the root
			//   (Root = Registry.LocalMachine, Registry.ClassesRoot
			//           Registry.CurrentUser, Registry.Users)
			Win32.RegistryKey key = root.OpenSubKey(path);

			// Return the 'valueName' for 'keyPath'.
			return key.GetValue(name).ToString();

		}


		/// <summary>
		/// Set the 'value' from the Windows
		/// Registry key 'keyPath', starting from the
		/// 'root' key.
		/// </summary>
		/// <param name="key">Microsoft.Win32.Registry.LocalMachine</param>
		/// <param name="path">SOFTWARE\Microsoft\ASP.NET</param>
		/// <param name="name">LastInstallTime</param>
		/// Usage:
		///   OperatingSystem.Registry.SetKeyValue(Registry.LocalMachine, path, value)
		public void SetKeyValue(Win32.RegistryKey root, string path, string name, object val)
		{

			// Open the registry key 'path', starting from the root
			//   (Root = Registry.LocalMachine, Registry.ClassesRoot
			//           Registry.CurrentUser, Registry.Users)
			Win32.RegistryKey key = root.CreateSubKey(path);

			// Set the value at 'name' to 'val'.
			key.SetValue(name, val);

		}

	}

}
