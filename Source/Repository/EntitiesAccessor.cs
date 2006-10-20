using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Xml;
using System.Xml.Serialization;
  
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.Concerns;
using Composestar.StarLight.Configuration;
using Composestar.StarLight.LanguageModel;
 
namespace Composestar.Repository
{
    /// <summary>
    /// Functionality to read and store the entities.
    /// </summary>
    public class EntitiesAccessor : IEntitiesAccessor 
    {
        // TODO Concert to a singleton and use caching?
        // TODO Singleton using ObjectBuilder?

        #region Configuration

        /// <summary>
        /// Loads the configuration.
        /// </summary>
        /// <param name="filename">The filename.</param>
        /// <returns></returns>
        public ConfigurationContainer LoadConfiguration(string filename)
        {
            if (string.IsNullOrEmpty(filename))
                throw new ArgumentNullException("filename");

            ConfigurationContainer configContainer;

            if (File.Exists(filename))
            {
                configContainer = ObjectXMLSerializer<ConfigurationContainer>.Load(filename, SerializedFormat.Document);
            } // if
            else
            {
                configContainer = new ConfigurationContainer();
            } // else
            
            return configContainer;
        }


        /// <summary>
        /// Saves the configuration.
        /// </summary>
        /// <param name="filename">The filename.</param>
        /// <param name="configContainer">The config container.</param>
        /// <returns></returns>
        public bool SaveConfiguration(string filename, ConfigurationContainer configContainer)
        {
            if (configContainer == null)
                throw new ArgumentNullException("configContainer");

            if (string.IsNullOrEmpty(filename))
                throw new ArgumentNullException("filename");

            ObjectXMLSerializer<ConfigurationContainer>.Save(configContainer, filename, SerializedFormat.Document);

            return true;
        }

        #endregion

        #region Assemblies

        /// <summary>
        /// Loads the assembly element.
        /// </summary>
        /// <param name="filename">The filename.</param>
        /// <returns></returns>
        public AssemblyElement LoadAssemblyElement(string filename)
        {
            if (string.IsNullOrEmpty(filename))
                throw new ArgumentNullException("filename");

            AssemblyElement assemblyElement;

            if (File.Exists(filename))
            {
                assemblyElement = ObjectXMLSerializer<AssemblyElement>.Load(filename, SerializedFormat.Document);
            } // if
            else
            {
                throw new FileNotFoundException(filename); 
            } // else

            return assemblyElement;
        }

        /// <summary>
        /// Saves the assembly element.
        /// </summary>
        /// <param name="filename">The filename.</param>
        /// <param name="assemblyElement">The assembly element.</param>
        /// <returns></returns>
        public bool SaveAssemblyElement(string filename, AssemblyElement assemblyElement)
        {
            if (assemblyElement == null)
                throw new ArgumentNullException("assemblyElement");

            if (string.IsNullOrEmpty(filename))
                throw new ArgumentNullException("filename");

            ObjectXMLSerializer<AssemblyElement>.Save(assemblyElement, filename, SerializedFormat.Document);

            return true;
        }

        #endregion

    }
}
