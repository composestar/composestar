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
    /// </summary>
    public class StarLightAssemblyResolver : BaseAssemblyResolver
    {

        private Dictionary<string, AssemblyDefinition> m_cache;
        private string _binFolder;

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ILWeaverAssemblyResolver"/> class.
        /// </summary>
        /// <param name="binFolder">The bin folder.</param>
        public StarLightAssemblyResolver(string binFolder)
        {
            _binFolder = binFolder;
            m_cache = new Dictionary<string, AssemblyDefinition>();
        }

        /// <summary>
        /// Resolves the specified full name.
        /// </summary>
        /// <param name="fullName">The full name.</param>
        /// <returns></returns>
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
        /// Resolves the specified name.
        /// </summary>
        /// <param name="name">The name.</param>
        /// <returns></returns>
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

            return null;

        }
    }
}
