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

using Composestar.Repository.LanguageModel;
using Composestar.Repository;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.Utilities.Cecil;
   
namespace Composestar.StarLight.ILWeaver
{

    /// <summary>
    /// Contains functions used by the Cecil visitors and weaver.
    /// </summary>
    public class CecilUtilities
    {
        public const string VoidType = "System.Void";

        private static string _binFolder;
        private static StarLightAssemblyResolver _resolver;
        private static Dictionary<string, MethodReference> _methodsCache = new Dictionary<string, MethodReference>();

        /// <summary>
        /// Gets or sets the bin folder used for lookups.
        /// </summary>
        /// <value>The bin folder.</value>
        public static string BinFolder
        {
            get
            {
                return _binFolder;
            }
            set
            {
                _binFolder = value;
                _resolver = new StarLightAssemblyResolver(_binFolder);
            }
        }

        /// <summary>
        /// Returns a method signature.
        /// </summary>
        /// <param name="method">The method.</param>
        /// <returns></returns>
        public static String MethodSignature(MethodDefinition method)
        {
            return method.ToString();
        }

        /// <summary>
        /// Resolves the type.
        /// </summary>
        /// <param name="typeName">Name of the type.</param>
        /// <param name="assemblyName">Name of the assembly.</param>
        /// <param name="assemblyFile">The assembly file.</param>
        /// <returns></returns>
        public static TypeDefinition ResolveTypeDefinition( TypeReference typeRef )
        {
            TypeDefinition typeDef = typeRef.Module.Types[typeRef.FullName];

            if(typeDef == null && typeRef.Scope != null)
            {
                foreach(AssemblyNameReference assembly in typeRef.Module.AssemblyReferences)
                {
                    if(typeRef.Scope.Name == assembly.Name)
                    {
                        //ResolveMethod(methodName, parentTypeRef.FullName, assembly.FullName, 
                        Utilities.Cecil.StarLightAssemblyResolver sar = 
                            new Composestar.StarLight.Utilities.Cecil.StarLightAssemblyResolver(BinFolder);
                        AssemblyDefinition ad = sar.Resolve(assembly);
                        typeDef = ad.MainModule.Types[typeRef.FullName];

                        break;
                    }
                }
            }

            return typeDef;
        }

        public static TypeReference ResolveType(string typeName, string assemblyName, string assemblyFile)
        {
            if (_resolver == null)
            {
                _resolver = new StarLightAssemblyResolver(System.IO.Path.GetDirectoryName(assemblyFile));
            }

            AssemblyDefinition asmDef = _resolver.Resolve(assemblyName);
            if (asmDef == null)
            {
                // Try to read directly using assemblyFilename
                if (!String.IsNullOrEmpty(assemblyFile))
                {
                    asmDef = AssemblyFactory.GetAssembly(assemblyFile);
                }
            }
            if (asmDef == null)
                return null;

            TypeDefinition td = asmDef.MainModule.Types[typeName];

            if (td == null)
                return null;

            return (TypeReference)td;
        }

        

        //public static TypeReference ResolveType( string typeName, ILanguageModelAccessor languageModelAccessor )
        //{
        //    TypeElement typeElement = languageModelAccessor.GetTypeElement( typeName );
        //    if ( typeElement == null ) throw new ILWeaverException(
        //        String.Format( CultureInfo.CurrentCulture, Properties.Resources.TypeNotFound, typeName + " (step 1)" ) );

        //    TypeReference typeRef = CecilUtilities.ResolveType(
        //        internalTypeString, internalTypeElement.Assembly, typeElement.FromDLL );
        //    if ( typeRef == null ) throw new ILWeaverException(
        //        String.Format( CultureInfo.CurrentCulture, Properties.Resources.TypeNotFound, typeName + " (step 2)" ) );

        //    return typeRef;
        //}

        /// <summary>
        /// Resolves the method.
        /// </summary>
        /// <param name="methodName">Name of the method.</param>
        /// <param name="typeName">Name of the type.</param>
        /// <param name="assemblyName">Name of the assembly.</param>
        /// <param name="assemblyFile">The assembly file.</param>
        /// <returns></returns>
        public static MethodReference ResolveMethod(string methodName, string typeName, string assemblyName, string assemblyFile)
        {
            // If in cache, retrieve
            string cacheKey = CreateCacheKey(methodName, typeName, assemblyName);

            if (_methodsCache.ContainsKey(cacheKey))
                return _methodsCache[cacheKey];

            // TODO make sure we can use the assemblyName

            // Not in the cache, so get from the assembly and store in cache.
            if (_resolver == null)
            {
                _resolver = new StarLightAssemblyResolver(System.IO.Path.GetDirectoryName(assemblyFile));
            }
            AssemblyDefinition asmDef = _resolver.Resolve(assemblyName);
            if (asmDef == null)
            {
                // Try to read directly using assemblyFilename
                if (!String.IsNullOrEmpty(assemblyFile))
                {
                    asmDef = AssemblyFactory.GetAssembly(assemblyFile);
                }
            }
            if (asmDef == null)
                return null;

            TypeDefinition td = asmDef.MainModule.Types[typeName];

            if (td == null)
                return null;

            MethodDefinition[] mds = td.Methods.GetMethod(methodName);
            MethodDefinition md;
            if (mds.Length > 0)
                md = mds[0];
            else
                return null;

            // Add to the cache
            _methodsCache.Add(cacheKey, md);

            return (MethodReference)md;

        }


        /// <summary>
        /// Resolves the method
        /// </summary>
        /// <param name="parentType">The type containing the method</param>
        /// <param name="methodName">The name of the method</param>
        /// <param name="exampleMethod">Examplemethod containing the same parametertypes and returntype of the
        /// wanted method</param>
        /// <returns>
        /// The to be resolved method, or <code>null</code> if such method does not exist
        /// </returns>
        public static MethodDefinition ResolveMethod(TypeDefinition parentType, string methodName,
            MethodDefinition exampleMethod)
        {
            MethodDefinition md = parentType.Methods.GetMethod(methodName, exampleMethod.Parameters);

            return md;
        }

        /// <summary>
        /// Resolves the method.
        /// </summary>
        /// <param name="parentTypeRef">The parent type ref.</param>
        /// <param name="methodName">Name of the method.</param>
        /// <param name="parameterTypes">The parameter types.</param>
        /// <returns></returns>
        public static MethodDefinition ResolveMethod(TypeReference parentTypeRef, string methodName,
            Type[] parameterTypes)
        {
            // Localy declared type
            TypeDefinition parentType = parentTypeRef.Module.Types[parentTypeRef.FullName];

            if (parentType == null && parentTypeRef.Scope != null)
            {
                foreach (AssemblyNameReference assembly in parentTypeRef.Module.AssemblyReferences)
                {
                    if (parentTypeRef.Scope.Name == assembly.Name)
                    {
                        //ResolveMethod(methodName, parentTypeRef.FullName, assembly.FullName, 
                        Utilities.Cecil.StarLightAssemblyResolver sar = new Composestar.StarLight.Utilities.Cecil.StarLightAssemblyResolver(BinFolder);
                        AssemblyDefinition ad = sar.Resolve(assembly);
                        parentType = ad.MainModule.Types[parentTypeRef.FullName];
                        
                        break;
                    }
                }


            }

            //TypeDefinition parentType = parentTypeRef.Module.Types[parentTypeRef.FullName];

            MethodDefinition md = parentType.Methods.GetMethod(methodName, parameterTypes);


            return md;
        }

        /// <summary>
        /// Resolves a MethodDefinition corresponding with a certain MethodReference.
        /// </summary>
        /// <param name="reference">The MethodReference of which the MethodDefinition needs to be resolved</param>
        /// <returns>The resolved MethodDefinition</returns>
        public static MethodDefinition ResolveMethodDefinition(MethodReference reference)
        {
            TypeDefinition declaringType = ResolveTypeDefinition(reference.DeclaringType);

            return declaringType.Methods.GetMethod(reference.Name, reference.Parameters);
        }


        /// <summary>
        /// Creates the cache key.
        /// </summary>
        /// <param name="methodName">Name of the method.</param>
        /// <param name="typeName">Name of the type.</param>
        /// <param name="assemblyName">Name of the assembly.</param>
        /// <returns></returns>
        private static string CreateCacheKey(string methodName, string typeName, string assemblyName)
        {
            return String.Format("[{0}] {1}::{2}", assemblyName, typeName, methodName);
        }

        /// <summary>
        /// Reads data from a stream until the end is reached. The
        /// data is returned as a byte array. An IOException is
        /// thrown if any of the underlying IO calls fail.
        /// </summary>
        /// <param name="stream">The stream to read data from</param>
        /// <param name="initialLength">The initial buffer length</param>
        /// <returns>Byte array with the contents of the file.</returns>
        public static byte[] ReadFully(Stream stream, int initialLength)
        {
            // If we've been passed an unhelpful initial length, just
            // use 32K.
            if (initialLength < 1)
            {
                initialLength = 32768;
            }

            byte[] buffer = new byte[initialLength];
            int read = 0;

            int chunk;
            while ((chunk = stream.Read(buffer, read, buffer.Length - read)) > 0)
            {
                read += chunk;

                // If we've reached the end of our buffer, check to see if there's
                // any more information
                if (read == buffer.Length)
                {
                    int nextByte = stream.ReadByte();

                    // End of stream? If so, we're done
                    if (nextByte == -1)
                    {
                        return buffer;
                    }

                    // Nope. Resize the buffer, put in the byte we've just
                    // read, and continue
                    byte[] newBuffer = new byte[buffer.Length * 2];
                    Array.Copy(buffer, newBuffer, buffer.Length);
                    newBuffer[read] = (byte)nextByte;
                    buffer = newBuffer;
                    read++;
                }
            }
            // Buffer is now too big. Shrink it.
            byte[] ret = new byte[read];
            Array.Copy(buffer, ret, read);
            return ret;
        }
    }

    /// <summary>
    /// Assembly resolver to resolve assemblies and store those in a cache for quick lookup.
    /// </summary>
    [Obsolete("Use the Composestar.StarLight.Utilities.Cecil.StarLightAssemblyResolver")]
    public class ILWeaverAssemblyResolver : BaseAssemblyResolver
    {

        private Dictionary<string, AssemblyDefinition> m_cache;
        private string _binFolder;

        /// <summary>
        /// Initializes a new instance of the <see cref="T:ILWeaverAssemblyResolver"/> class.
        /// </summary>
        /// <param name="binFolder">The bin folder.</param>
        public ILWeaverAssemblyResolver(string binFolder)
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