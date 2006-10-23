using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Xml;
using System.Xml.Serialization;

using Composestar.StarLight.CoreServices;
using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.WeaveSpec;

namespace Composestar.Repository
{
    /// <summary>
    /// Functionality to read and store the entities.
    /// </summary>
    public sealed class EntitiesAccessor : IEntitiesAccessor
    {

        #region Singleton Instance

        private static readonly EntitiesAccessor m_Instance = new EntitiesAccessor();

        // Explicit static constructor to tell C# compiler
        // not to mark type as beforefieldinit
        static EntitiesAccessor()
        {
            Console.WriteLine("created singleton");
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="T:EntitiesAccessor"/> class.
        /// </summary>
        private EntitiesAccessor()
        {
        }

        /// <summary>
        /// Gets the instance.
        /// </summary>
        /// <value>The instance.</value>
        public static EntitiesAccessor Instance
        {
            get
            {
                return m_Instance;
            }
        }

        #endregion

        private Dictionary<string, AssemblyElement> _assemblyFileCache = new Dictionary<string, AssemblyElement>();
        //     private Dictionary<string, ConfigurationContainer> _configCache = new Dictionary<string, ConfigurationContainer>();

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
            if (!_assemblyFileCache.TryGetValue(filename, out assemblyElement))
            {
                if (File.Exists(filename))
                {
                    assemblyElement = ObjectXMLSerializer<AssemblyElement>.Load(filename, SerializedFormat.Document);
                    _assemblyFileCache.Add(filename, assemblyElement);
                } // if
                else
                {
                    throw new FileNotFoundException(filename);
                } // else
            } // if

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

            // Update or add to cache
            _assemblyFileCache[filename] = assemblyElement;

            return true;
        }

        #endregion

        #region Weave Specification

        /// <summary>
        /// Loads the weave specification.
        /// </summary>
        /// <param name="filename">The filename.</param>
        /// <returns>A <see cref="T:WeaveSpecification"></see> object or <see langword="null" /> if the file could not be found.</returns>
        public WeaveSpecification LoadWeaveSpecification(string filename)
        {
            if (string.IsNullOrEmpty(filename))
                throw new ArgumentNullException("filename");

            WeaveSpecification weaveSpecification;

            if (File.Exists(filename))
            {
                weaveSpecification = ObjectXMLSerializer<WeaveSpecification>.Load(filename, SerializedFormat.Document);
            } // if
            else
            {
                return null;
            } // else

            return weaveSpecification;
        }

        /// <summary>
        /// Saves the weave specification.
        /// </summary>
        /// <param name="weaveSpecification">The weave specification.</param>
        /// <param name="filename">The filename.</param>
        /// <returns></returns>
        public bool SaveWeaveSpecification(WeaveSpecification weaveSpecification, string filename)
        {
            if (weaveSpecification == null)
                throw new ArgumentNullException("weaveSpecification");

            if (string.IsNullOrEmpty(filename))
                throw new ArgumentNullException("filename");

            ObjectXMLSerializer<WeaveSpecification>.Save(weaveSpecification, filename, SerializedFormat.Document);

            return true;
        }

        #endregion
    }
}
