using System;
using System.IO;
using Microsoft.Win32;
using System.Reflection;

namespace AntHelper
{
	class MainClass
	{
		static int Main(string[] args)
		{
			if (args.Length == 0)
			{
				Console.Out.WriteLine("Usage: AntHelper [command]\n");
				Console.Out.WriteLine("Where [command] is one of the following:");
				Console.Out.WriteLine(" getsystemproperties <filename>");
				Console.Out.WriteLine("\tWrite out various system properties to <filename>");
				Console.Out.WriteLine(" lookupAssembly <assembly>");
				Console.Out.WriteLine("\tFind the full path to the requested assembly");
				return 1;
			}
			else if (args[0].Equals("getsystemproperties"))
			{
                return getSystemProperties(args);
			}
			else if (args[0].Equals("lookupAssembly"))
			{
				return lookupAssembly(args);
			}
			return 1;
		}

		static int getSystemProperties(string[] args)
		{
			if (args.Length != 2)
			{
				Console.WriteLine("Usage: AntHelper getsystemproperties <filename>");
				return 1;
			}

			string filename = args[1];
			using (TextWriter writer = new StreamWriter(filename, false))
			{
				writer.WriteLine("# Autogenerated file by AntHelper at {0}", DateTime.Now);
				writeComposestarDir(writer);
				writeStarlightDir(writer);
			}
			
			return 0;
		}

		private static void writeComposestarDir(TextWriter writer)
		{
			string path = getRegistryString("Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Composestar", "UninstallString");
			if (path != null)
			{
				path = strReplace(path.Substring(0, path.LastIndexOf("\\")), "\\", "/");
				writer.WriteLine("composestar.installdir={0}", path);
			}
			else
			{
				writer.WriteLine("# Unable to retrieve the Compose* install directory");
			}
		}

		private static void writeStarlightDir(TextWriter writer)
		{
			string path = getRegistryString("Software\\Composestar\\Starlight", "StarLightInstallFolder");
			if (path != null)
			{
				path = strReplace(path.Substring(0, path.LastIndexOf("\\")), "\\", "/");
				writer.WriteLine("starlight.installdir={0}", path);
			}
			else
			{
				writer.WriteLine("# Unable to retrieve the Compose* install directory");
			}
		}

		private static string getRegistryString(string parent, string key)
		{
			RegistryKey rk = Registry.LocalMachine.OpenSubKey(parent, false);
			return (rk == null ? null : (string)rk.GetValue(key));
		}

		static string strReplace(string input, string from, string to)
		{
			int i = input.IndexOf(from);
			while (i > -1)
			{
				input = input.Substring(0, i) + to + input.Substring(i + from.Length);
				i = input.IndexOf(from);
			}
			return input;
		}

		static int lookupAssembly(string[] args)
		{
			if (args.Length < 2) return 1;
			try
			{
				Assembly asm = Assembly.LoadWithPartialName(args[1]);
				Console.Out.WriteLine("{0}", asm.Location);
				return 0;
			}
			catch (Exception)
			{
				return 2;
			}
		}
	}
}
