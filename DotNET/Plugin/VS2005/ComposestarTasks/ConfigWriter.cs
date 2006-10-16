using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;

namespace Trese.ComposestarTasks
{
	internal class ConfigWriter
	{
		const string c_filename = "BuildConfiguration.xml";

		private readonly Config m_config;

		public ConfigWriter(Config config)
		{
			m_config = config;
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
				string info = String.Format("This file was automaticly generated on {0} by Compose* GenConfigTask", DateTime.Now.ToString());
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
			xw.WriteAttributeString("buildDebugLevel", m_config.buildDebugLevel);
			xw.WriteAttributeString("runDebugLevel", m_config.runDebugLevel);
			xw.WriteAttributeString("outputPath", m_config.outputPath);
			xw.WriteAttributeString("applicationStart", m_config.applicationStart);

			// individual project items
			foreach (Project p in m_config.projects)
				WriteProject(xw, p);

			// concern sources
			WriteConcernSources(xw);

			// custom filters
			WriteCustomFilters(xw);

			xw.WriteEndElement();
		}

		private void WriteProject(XmlWriter xw, Project p)
		{
			xw.WriteStartElement("Project");
			xw.WriteAttributeString("name", p.name);
			xw.WriteAttributeString("language", p.language);
			xw.WriteAttributeString("basePath", p.basePath);

			// sources
			xw.WriteStartElement("Sources");
			foreach (String s in p.sources)
			{
				xw.WriteStartElement("Source");
				xw.WriteAttributeString("fileName", s);
				xw.WriteEndElement();
			}
			xw.WriteEndElement();

			// Dependencies
			xw.WriteStartElement("Dependencies");
			foreach (String d in p.deps)
			{
				xw.WriteStartElement("Dependency");
				xw.WriteAttributeString("fileName", d);
				xw.WriteEndElement();
			}
			xw.WriteEndElement();

			// /Project
			xw.WriteEndElement();
		}

		private void WriteConcernSources(XmlWriter xw)
		{
			xw.WriteStartElement("ConcernSources");
			foreach (String s in m_config.concernSources)
			{
				xw.WriteStartElement("ConcernSource");
				xw.WriteAttributeString("fileName", s);
				xw.WriteEndElement();
			}
			xw.WriteEndElement();
		}

		private void WriteCustomFilters(XmlWriter xw)
		{
			xw.WriteStartElement("CustomFilters");
			foreach (Filter f in m_config.customFilters)
			{
				xw.WriteStartElement("Filter");
				xw.WriteAttributeString("filterName", f.name);
				xw.WriteAttributeString("library", f.library);
				xw.WriteEndElement();
			}
			xw.WriteEndElement();
		}
		
		private void WriteSettings(XmlWriter xw)
		{
			xw.WriteStartElement("Settings");

			// modules
			WriteModuleSettings(xw);

			// paths
			WritePaths(xw);

			xw.WriteEndElement();
		}

		private void WriteModuleSettings(XmlWriter xw)
		{
			xw.WriteStartElement("Modules");
			foreach (ModuleSettings m in m_config.modules)
			{
				xw.WriteStartElement("Module");
				xw.WriteAttributeString("name", m.name);

				// add all properties
				foreach (KeyValuePair<String,String> p in m.props)
					xw.WriteAttributeString(p.Key, p.Value);

				xw.WriteEndElement();
			}
			xw.WriteEndElement();
		}

		private void WritePaths(XmlWriter xw)
		{
			xw.WriteStartElement("Paths");
			foreach (Path p in m_config.paths)
			{
				xw.WriteStartElement("Path");
				xw.WriteAttributeString("name", p.name);
				xw.WriteAttributeString("pathName", p.pathName);
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
			// or better yet: include the path in Settings.
		}
	}
}
