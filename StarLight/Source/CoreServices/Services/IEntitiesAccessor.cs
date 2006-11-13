using System;
using System.Collections.Generic;

using Composestar.StarLight.Entities.LanguageModel;
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.Entities.Concerns;
using Composestar.StarLight.Entities.WeaveSpec;


namespace Composestar.StarLight.CoreServices
{

    /// <summary>
    /// Combines the interfaces for the entities.
    /// </summary>
    public interface IEntitiesAccessor : IConfigurationAccessors, IAssemblyAccessors, IWeaveSpecAccessors
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
        /// <param name="fileName">The fileName.</param>
        /// <returns></returns>
        ConfigurationContainer LoadConfiguration(string fileName);

        /// <summary>
        /// Saves the configuration.
        /// </summary>
        /// <param name="fileName">The fileName.</param>
        /// <param name="configContainer">The config container.</param>
        /// <returns></returns>
        bool SaveConfiguration(string fileName, ConfigurationContainer configContainer);

    } // IConfigurationAccessors

    /// <summary>
    /// Functions to load and save assemblies.
    /// </summary>
    public interface IAssemblyAccessors
    {

        /// <summary>
        /// Loads the assembly element.
        /// </summary>
        /// <param name="fileName">The fileName.</param>
        /// <returns></returns>
        AssemblyElement LoadAssemblyElement(string fileName);

        /// <summary>
        /// Saves the assembly element.
        /// </summary>
        /// <param name="fileName">The fileName.</param>
        /// <param name="assemblyElement">The assembly element.</param>
        /// <returns></returns>
        bool SaveAssemblyElement(string fileName, AssemblyElement assemblyElement);

    } // IAssemblyAccessors

    /// <summary>
    /// Interface for the weave specification load and save functions.
    /// </summary>
    public interface IWeaveSpecAccessors
    {
        /// <summary>
        /// Loads the weave specification.
        /// </summary>
        /// <param name="fileName">The fileName.</param>
        /// <returns></returns>
        WeaveSpecification LoadWeaveSpecification(string fileName);

        /// <summary>
        /// Saves the weave specification.
        /// </summary>
        /// <param name="fileName">The fileName.</param>
        /// <param name="weaveSpecification">The weave specification.</param>
        /// <returns></returns>
        bool SaveWeaveSpecification(string fileName, WeaveSpecification weaveSpecification);

    } // IWeaveSpecAccessors

}
