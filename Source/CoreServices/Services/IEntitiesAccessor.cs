using System;
using System.Collections.Generic;

using Composestar.StarLight.LanguageModel;
using Composestar.StarLight.Configuration;
using Composestar.StarLight.Concerns;  


namespace Composestar.StarLight.CoreServices
{

    /// <summary>
    /// Combines the interfaces for the entities.
    /// </summary>
    public interface IEntitiesAccessor : IConfigurationAccessors, IAssemblyAccessors
    {

    }

    /// <summary>
    /// Contains the functions to load and save the configuration.
    /// </summary>
    public interface IConfigurationAccessors
    {

        /// <summary>
        /// Loads the configuration.
        /// </summary>
        /// <param name="filename">The filename.</param>
        /// <returns></returns>
        ConfigurationContainer LoadConfiguration(string filename);

        /// <summary>
        /// Saves the configuration.
        /// </summary>
        /// <param name="filename">The filename.</param>
        /// <param name="configContainer">The config container.</param>
        /// <returns></returns>
        bool SaveConfiguration(string filename, ConfigurationContainer configContainer);

    } // IConfigurationAccessors

    /// <summary>
    /// Functions to load and save assemblies.
    /// </summary>
    public interface IAssemblyAccessors
    {

        /// <summary>
        /// Loads the assembly element.
        /// </summary>
        /// <param name="filename">The filename.</param>
        /// <returns></returns>
        AssemblyElement LoadAssemblyElement(string filename);

        /// <summary>
        /// Saves the assembly element.
        /// </summary>
        /// <param name="filename">The filename.</param>
        /// <param name="assemblyElement">The assembly element.</param>
        /// <returns></returns>
        bool SaveAssemblyElement(string filename, AssemblyElement assemblyElement);

    } // IAssemblyAccessors
           
}
