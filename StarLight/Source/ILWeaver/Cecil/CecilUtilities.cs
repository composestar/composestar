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

namespace Composestar.StarLight.ILWeaver
{

    public class CecilUtilities
    {

        private static string _binFolder;
        private static ILWeaverAssemblyResolver _resolver;
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
                _resolver =  new ILWeaverAssemblyResolver(_binFolder);
            }
        }

        /// <summary>
        /// Returns a method signature.
        /// </summary>
        /// <param name="methodName">Name of the method.</param>
        /// <param name="returnType">Type of the return.</param>
        /// <param name="paramTypes">The param types.</param>
        /// <returns></returns>
        [Obsolete("Use Method.ToString() to create a signature", true)]
        public static String MethodSignature(string methodName, string returnType, string[] paramTypes)
        {
            StringBuilder signature = new StringBuilder();
            signature.AppendFormat("{0} {1}(", returnType, methodName);

            for (int i = 0; i < paramTypes.Length; i++)
            {
                if (i < paramTypes.Length - 1)
                    signature.AppendFormat("{0}, ", paramTypes[i]);
                else
                    signature.AppendFormat("{0}", paramTypes[i]);
            }
            signature.Append(")");

            return signature.ToString();
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
        /// Gets the parameter types list.
        /// </summary>
        /// <param name="method">The method.</param>
        /// <returns></returns>
        public static String[] GetParameterTypesList(MethodDefinition method)
        {
            List<String> ret = new List<String>();
            foreach (ParameterDefinition param in method.Parameters)
            {
                ret.Add(param.ParameterType.FullName);
            }

            return ret.ToArray();
        }

        /// <summary>
        /// Resolves the method based on the selector.
        /// </summary>
        /// <param name="selector">The selector.</param>
        /// <returns></returns>
        public static MethodBase ResolveMethod(string selector)
        {
            return null;

        }

        public static TypeReference ResolveType(string typeName, string assemblyName, string assemblyFile)
        {
            if (_resolver == null)
            {
                _resolver = new ILWeaverAssemblyResolver(System.IO.Path.GetDirectoryName(assemblyFile));
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
            string CacheKey = CreateCacheKey(methodName, typeName, assemblyName);

            if (_methodsCache.ContainsKey(CacheKey))
                return _methodsCache[CacheKey];

            // TODO make sure we can use the assemblyName

            // Not in the cache, so get from the assembly and store in cache.
            if (_resolver == null)
            {
                _resolver = new ILWeaverAssemblyResolver(System.IO.Path.GetDirectoryName(assemblyFile));
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
            _methodsCache.Add(CacheKey, md);

            return (MethodReference)md;

            //Assembly asm = Assembly.ReflectionOnlyLoadFrom(assemblyFile);


            //Type t = asm.GetType(typeName, false, true);

            //if (t == null)
            //    return null;

            //MethodInfo m;
            //m = t.GetMethod(methodName);

            //if (m == null)
            //    return null;

            //MethodBase mb =  (MethodBase)m;

            //// Add to the cache
            //_methodsCache.Add(CreateCacheKey(methodName, typeName, assemblyFile), mb);

            //return mb;
        }


        /// <summary>
        /// Resolves the method
        /// </summary>
        /// <param name="parentType">The type containing the method</param>
        /// <param name="methodName">The name of the method</param>
        /// <param name="exampleMethod">Examplemethod containing the same parametertypes and returntype of the
        /// wanted method</param>
        /// <returns>The to be resolved method, or <code>null</code> if such method does not exist</returns>
        public static MethodDefinition ResolveMethod( TypeDefinition parentType, string methodName, 
            MethodDefinition exampleMethod )
        {
            MethodDefinition md = parentType.Methods.GetMethod( methodName, exampleMethod.Parameters);

            return md;
        }

        public static MethodDefinition ResolveMethod( TypeDefinition parentType, string methodName,
            Type[] parameterTypes )
        {
            MethodDefinition md = parentType.Methods.GetMethod( methodName, parameterTypes );

            return md;
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
    /// 
    /// </summary>
    public class ILWeaverAssemblyResolver : BaseAssemblyResolver
    {

        Dictionary<string, AssemblyDefinition> m_cache;
        string _binFolder;

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
            string[] dirs = new string[] { _binFolder, ".", "bin"  };

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
