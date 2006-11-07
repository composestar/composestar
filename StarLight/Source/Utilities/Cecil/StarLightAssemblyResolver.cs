using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Diagnostics;
using System.IO;
using System.Reflection;
using System.Text;
using System.Globalization;

using Mono.Cecil;
using Mono.Cecil.Binary;
using Mono.Cecil.Cil;
using Mono.Cecil.Metadata;
using Mono.Cecil.Signatures;

namespace Composestar.StarLight.Utilities
{
    /// <summary>
    /// Assembly resolver to resolve assemblies and store those in a cache for quick lookup. 
    /// It will try to lookup assemblies on their assembly name of assembly definition.
    /// </summary>
    /// <remarks>This class holds an internal cache for the found assemblies.</remarks>
    public class StarLightAssemblyResolver : BaseAssemblyResolver
    {

        #region Private variables

        private Dictionary<string, AssemblyDefinition> m_cache;
        private string _binFolder;

        #endregion

        #region ctor

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ILWeaverAssemblyResolver"/> class.
        /// </summary>
        /// <param name="binFolder">The search folder containing the assemblies.</param>
        public StarLightAssemblyResolver(string binFolder)
        {
            _binFolder = binFolder;
            m_cache = new Dictionary<string, AssemblyDefinition>();
        }

        #endregion

        #region Resolvers

        /// <summary>
        /// Resolves the assembly by its full name.
        /// </summary>
        /// <param name="fullName">The full name of an assembly.</param>
        /// <example>
        /// <code>
        /// StarLightAssemblyResolver assemblyResolver = new StarLightAssemblyResolver("c:\project\bin");
        /// String assemblyName = "assembly.dll";
        /// AssemblyDefinition ad = assemblyResolver.Resolve(assemblyName);
        /// </code>
        /// </example>
        /// <remarks>This functions will parse the <paramref name="fullName"/> to create an <see cref="T:Mono.Cecil.AssemblyNameReference"/>.</remarks>
        /// <returns>Return an <see cref="T:Mono.Cecil.AssemblyDefinition"></see> or <see langword="null"/> when the assembly could not be found.</returns>
        public override AssemblyDefinition Resolve(string fullName)
        {
            if (String.IsNullOrEmpty(fullName))
                return null;

            AssemblyNameReference assemblyNameReferenceParsed;

            try
            {
                fullName = fullName.Replace(", PublicKeyToken=null", "");

                assemblyNameReferenceParsed = AssemblyNameReference.Parse(fullName);
            }
            catch (ArgumentException)
            {
                return null;
            }

            return Resolve(assemblyNameReferenceParsed);

        }

        /// <summary>
        /// Resolves the assembly by its <see cref="T:Mono.Cecil.AssemblyNameReference"/>.
        /// </summary>
        /// <param name="name">The name and details of the assembly.</param>
        /// <returns>Return an <see cref="T:Mono.Cecil.AssemblyDefinition"></see> or <see langword="null"/> when the assembly could not be found.</returns>
        public override AssemblyDefinition Resolve(AssemblyNameReference name)
        {
            AssemblyDefinition asm;
            if (!m_cache.TryGetValue(name.FullName, out asm))
            {
                asm = ResolveInternal(name);
                if (asm != null)
                    m_cache[name.FullName] = asm;
            }

            return asm;
        }

        /// <summary>
        /// Resolves the assemblyname.
        /// </summary>
        /// <param name="name">The name.</param>
        /// <returns></returns>
        private AssemblyDefinition ResolveInternal(AssemblyNameReference name)
        {
            string[] exts = new string[] { ".dll", ".exe" };
            string[] dirs = new string[] { _binFolder, ".", "bin" };

            foreach (string dir in dirs)
            {
                foreach (string ext in exts)
                {
                    string file = Path.Combine(dir, name.Name + ext);
                    if (File.Exists(file))
                    {
                        return AssemblyFactory.GetAssembly(file);
                    }
                }
            }
          
            if (name.Name == "mscorlib")
                return GetCorlib(name);
            else if (IsInGac(name))
                return AssemblyFactory.GetAssembly(GetFromGac(name));
            else if (IsInGac32(name))
                return AssemblyFactory.GetAssembly(GetFromGac32(name));

            return null;

        }

        #endregion
    
    }
}
