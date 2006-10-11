﻿using System;
using Composestar.Repository.LanguageModel;
using System.Collections.Generic;

namespace Composestar.StarLight.CoreServices
{

    /// <summary>
    /// Combines the interfaces for the language model.
    /// </summary>
    public interface ILanguageModelAccessor : ILanguageModelGetters, ILanguageModelDeletes, ILanguageModelSetters
    {

        /// <summary>
        /// Closes this instance.
        /// </summary>
        void Close();
    }

    /// <summary>
    /// Contains the interfaces for the getters of the language model.
    /// </summary>
    public interface ILanguageModelGetters
    {
        /// <summary>
        /// Gets the method element by name
        /// </summary>
        /// <param name="typeInfo">The type info.</param>
        /// <param name="methodName">Name of the method.</param>
        /// <returns></returns>
        MethodElement GetMethodElementByName(TypeElement typeInfo, string methodName);

        /// <summary>
        /// Gets the method element by signature.
        /// </summary>
        /// <param name="typeInfo">The type info.</param>
        /// <param name="methodSignature">The method signature.</param>
        /// <returns></returns>
        MethodElement GetMethodElementBySignature(TypeElement typeInfo, string methodSignature);

        /// <summary>
        /// Gets the method elements.
        /// </summary>
        /// <param name="type">The type.</param>
        /// <returns></returns>
        IList<MethodElement> GetMethodElements(TypeElement type);

        /// <summary>
        /// Gets the method elements in a dictionary with the type as key.
        /// </summary>
        /// <returns></returns>
        Dictionary<String, List<MethodElement>> GetMethodElements();

        /// <summary>
        /// Gets the type element.
        /// </summary>
        /// <param name="fullName">The full name.</param>
        /// <returns></returns>
        TypeElement GetTypeElement(string fullName);

        /// <summary>
        /// Gets the type element by Id.
        /// </summary>
        /// <param name="typeId">The type id.</param>
        /// <returns></returns>
        TypeElement GetTypeElementById(string typeId);

        /// <summary>
        /// Gets the type element by AFQN.
        /// </summary>
        /// <param name="fullName">The full name.</param>
        /// <param name="assembly">The assembly.</param>
        /// <returns></returns>
        TypeElement GetTypeElementByAFQN(string fullName, string assembly);

        /// <summary>
        /// Gets the type elemenst by AFQN.
        /// </summary>
        /// <param name="assembly">The assembly.</param>
        /// <returns></returns>
        IList<TypeElement> GetTypeElementsByAFQN(string assembly);

        /// <summary>
        /// Gets the type elements.
        /// </summary>
        /// <returns></returns>
        IList<TypeElement> GetTypeElements();

        /// <summary>
        /// Gets the internals by type element.
        /// </summary>
        /// <param name="typeElement">The type element.</param>
        /// <returns></returns>
        IList<Internal> GetInternalsByTypeElement(TypeElement typeElement);

        /// <summary>
        /// Gets the externals by type element.
        /// </summary>
        /// <param name="typeElement">The type element.</param>
        /// <returns></returns>
        IList<External> GetExternalsByTypeElement(TypeElement typeElement);

        /// <summary>
        /// Gets the externals in a dictionary with the type ID as key.
        /// </summary>
        /// <returns></returns>
        Dictionary<String, List<External>> GetExternals();

        /// <summary>
        /// Gets the internals in a dictionary with the type ID as key.
        /// </summary>
        /// <returns></returns>
        Dictionary<String, List<Internal>> GetInternals();

        /// <summary>
        /// Gets the call by method element.
        /// </summary>
        /// <param name="methodElement">The method element.</param>
        /// <returns></returns>
        IList<CallElement> GetCallByMethodElement(MethodElement methodElement);

        /// <summary>
        /// Gets the condition by name.
        /// </summary>
        /// <param name="name">The name of the condition.</param>
        /// <returns></returns>
        Condition GetConditionByName(string name);


        /// <summary>
        /// Gets all the assembly elements.
        /// </summary>
        /// <returns>List of assembly elements.</returns>
        IList<AssemblyElement> GetAssemblyElements();

        /// <summary>
        /// Gets the name of the assembly element by file.
        /// </summary>
        /// <param name="fileName">Name of the file.</param>
        /// <returns></returns>
        AssemblyElement GetAssemblyElementByFileName(string fileName);

        /// <summary>
        /// Gets the name of the assembly element by.
        /// </summary>
        /// <param name="name">The name.</param>
        /// <returns></returns>
        AssemblyElement GetAssemblyElementByName(string name);

        /// <summary>
        /// Gets the type elements by assembly.
        /// </summary>
        /// <param name="assemblyElement">The assembly element.</param>
        /// <returns></returns>
        Dictionary<String, TypeElement> GetTypeElementsByAssembly(AssemblyElement assemblyElement);

    } // ILanguageModelGetters

    /// <summary>
    /// Contains the interfaces for the language model delete functions.
    /// </summary>
    public interface ILanguageModelDeletes
    {

        /// <summary>
        /// Deletes the concern informations.
        /// </summary>
        void DeleteConcernInformations();

        /// <summary>
        /// Deletes the weaving instructions.
        /// </summary>
        void DeleteWeavingInstructions();

        /// <summary>
        /// Deletes the type elements.
        /// </summary>
        /// <param name="assembly">The assembly.</param>
        void DeleteTypeElements(String assembly);

        /// <summary>
        /// Deletes the assembly elements.
        /// </summary>
        /// <param name="name">The name.</param>
        void DeleteAssembly(String name);


    } // ILanguageModelDeletes

    /// <summary>
    /// Contains the interfaces for the language model setters.
    /// </summary>
    public interface ILanguageModelSetters
    {

        /// <summary>
        /// Adds the assemblies.
        /// </summary>
        /// <param name="assemblies">The assemblies.</param>
        /// <param name="assembliesToSave">The assemblies to save.</param>
        void AddAssemblies(List<AssemblyElement> assemblies, List<String> assembliesToSave);

        /// <summary>
        /// Adds the filtertypes
        /// </summary>
        /// <param name="filterTypes">A list containing all filtertypes that need to be added</param>
        void AddFilterTypes(List<FilterTypeElement> filterTypes);


        /// <summary>
        /// Adds filteractions
        /// </summary>
        /// <param name="filterActions">A list containing all filteractions that need to be added</param>
        void AddFilterActions(List<FilterActionElement> filterActions);

    } // ILanguageModelSetters
}