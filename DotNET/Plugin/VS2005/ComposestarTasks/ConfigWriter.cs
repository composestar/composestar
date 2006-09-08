using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;

namespace Trese.ComposestarTasks
{
	internal class ConfigWriter
	{
		const string c_filename = "BuildConfiguration.xml";

		public ConfigWriter()
		{
		}

		public void write()
		{
			XmlWriterSettings settings = new XmlWriterSettings();
			settings.Indent = true;
			settings.IndentChars = "  ";
			using (XmlWriter xw = XmlWriter.Create(c_filename, settings))
			{
				// xml declaration
				xw.WriteStartDocument();

				// generation info
				string info = String.Format("This file was automaticly generated on {0} by the Composestar task", DateTime.Now.ToString());
				xw.WriteComment(info);

				// document root
				xw.WriteStartElement("BuildConfiguration");
				xw.WriteAttributeString("version", "1.00");

				// projects
				WriteProjects(xw);

				// settings
				WriteSettings(xw);

				// platform configuration
				WritePlatformConfiguration(xw);

				// close document root
				xw.WriteEndElement();
				xw.WriteEndDocument();
			}
		}

		private void WriteProjects(XmlWriter xw)
		{
			xw.WriteStartElement("Projects");
			xw.WriteAttributeString("executable", "");
			xw.WriteAttributeString("startupProject", "");
			xw.WriteAttributeString("applicationStart", "");
			xw.WriteAttributeString("runDebugLevel", "");
			xw.WriteAttributeString("outputPath", "");

			// individual project items
			for (int i = 0; i < 2; i++)
				WriteProject(xw);

			// concern sources
			WriteConcernSources(xw);

			// custom filters
			WriteCustomFilters(xw);

			xw.WriteEndElement();
		}

		private void WriteProject(XmlWriter xw)
		{
			xw.WriteStartElement("Project");
			xw.WriteAttributeString("name", "");
			xw.WriteAttributeString("language", "");
			xw.WriteAttributeString("buildPath", "");
			xw.WriteAttributeString("basePath", "");
			xw.WriteAttributeString("outputPath", "");
			xw.WriteAttributeString("assemblyName", "");

			// sources
			xw.WriteStartElement("Sources");
			for (int i = 0; i < 2; i++)
			{
				xw.WriteStartElement("Source");
				xw.WriteAttributeString("fileName", "source.file" + i);
				xw.WriteEndElement();
			}
			xw.WriteEndElement();

			// Dependencies
			xw.WriteStartElement("Dependencies");
			for (int i = 0; i < 2; i++)
			{
				xw.WriteStartElement("Dependency");
				xw.WriteAttributeString("fileName", "dep.file" + i);
				xw.WriteEndElement();
			}
			xw.WriteEndElement();

			// TypeSources
			xw.WriteStartElement("TypeSources");
			for (int i = 0; i < 2; i++)
			{
				xw.WriteStartElement("TypeSource");
				xw.WriteAttributeString("name", "source.name" + i);
				xw.WriteAttributeString("fileName", "source.file" + i);
				xw.WriteEndElement();
			}
			xw.WriteEndElement();

			// /Project
			xw.WriteEndElement();
		}

		private void WriteConcernSources(XmlWriter xw)
		{
			xw.WriteStartElement("ConcernSources");
			for (int i = 0; i < 2; i++)
			{
				xw.WriteStartElement("ConcernSource");
				xw.WriteAttributeString("fileName", "concern.file" + i);
				xw.WriteEndElement();
			}
			xw.WriteEndElement();
		}

		private void WriteCustomFilters(XmlWriter xw)
		{
			xw.WriteStartElement("CustomFilters");
			for (int i = 0; i < 2; i++)
			{
				xw.WriteStartElement("Filter");
				xw.WriteAttributeString("filterName", "filter.name" + i);
				xw.WriteAttributeString("library", "filter.library" + i);
				xw.WriteEndElement();
			}
			xw.WriteEndElement();
		}
		
		private void WriteSettings(XmlWriter xw)
		{
			xw.WriteStartElement("Settings");
			xw.WriteAttributeString("composestarIni", "settings.ini");
			xw.WriteAttributeString("buildDebugLevel", "3");
			xw.WriteAttributeString("platform", "dotNET");

			// modules
			WriteModules(xw);

			// paths
			WritePaths(xw);

			xw.WriteEndElement();
		}

		private void WriteModules(XmlWriter xw)
		{
			xw.WriteStartElement("Modules");
			for (int i = 0; i < 2; i++)
			{
				xw.WriteStartElement("Module");
				xw.WriteAttributeString("name", "module.name");

				// add all properties
				for (int j = 0; j < 2; j++)
					xw.WriteAttributeString("a" + i + "x" + j, "b" + i + "x" + j);

				xw.WriteEndElement();
			}
			xw.WriteEndElement();
		}

		private void WritePaths(XmlWriter xw)
		{
			xw.WriteStartElement("Paths");
			for (int i = 0; i < 2; i++)
			{
				xw.WriteStartElement("Path");
				xw.WriteAttributeString("name", "path.name" + i);
				xw.WriteAttributeString("pathName", "path.pn" + i);
				xw.WriteEndElement();
			}
			xw.WriteEndElement();
		}

		private void WritePlatformConfiguration(XmlWriter xw)
		{
			string uri = @"C:\Program Files\ComposeStar\PlatformConfigurations.xml";
			using (XmlReader xr = XmlReader.Create(uri))
			{
				xr.MoveToContent();
				xw.WriteNode(xr, false);
			}

			// TODO: write something like this instead:
			// <Platforms file="C:\Program Files\ComposeStar\PlatformConfigurations.xml"/>
			// or better yet: include it in Settings.
		}
	}
}
