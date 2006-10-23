#region Using directives
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

using Composestar.Repository;
using Composestar.StarLight.Entities.Configuration;   
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions; 
using Composestar.StarLight.Utilities.Cecil;
using Composestar.StarLight.ContextInfo;
  
#endregion
   
namespace Composestar.StarLight.ILWeaver
{

    /// <summary>
    /// Contains functions used by the Cecil visitors and weaver.
    /// </summary>
    public class CecilUtilities
    {
        #region Constants

        public const string VoidType = "System.Void";

        #endregion

        #region Variables

        private static string _binFolder;
        private static StarLightAssemblyResolver _resolver;
        private static Dictionary<string, MethodReference> _methodsCache = new Dictionary<string, MethodReference>();
        private static Dictionary<CachedMethodDefinition, MethodBase> _methodSignaturesCache = new Dictionary<CachedMethodDefinition, MethodBase>();
        private static Dictionary<CachedTypeDefinition, Type> _typesSignaturesCache = new Dictionary<CachedTypeDefinition, Type>();
        
        #endregion

        #region Constructor

        /// <summary>
        /// Initializes the <see cref="T:CecilUtilities"/> class.
        /// </summary>
        static CecilUtilities()
        {
            AddDefaultMethodsToCache();
            AddDefaultTypesToCache(); 
        }

        #endregion

        #region Properties

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

        #endregion

        #region MethodReference and caching

        /// <summary>
        /// Creates the method reference.
        /// </summary>
        /// <param name="assemblyDefinition">The assembly definition.</param>
        /// <param name="methodBase">The method info.</param>
        /// <returns></returns>
        public static MethodReference CreateMethodReference(AssemblyDefinition assemblyDefinition, MethodBase methodBase)
        {
            return assemblyDefinition.MainModule.Import(methodBase);
        }

        /// <summary>
        /// Creates the method reference using a cached version.
        /// </summary>
        /// <param name="assemblyDefinition">The assembly definition.</param>
        /// <param name="methodSignature">The method signature.</param>
        /// <returns></returns>
        public static MethodReference CreateMethodReference(AssemblyDefinition assemblyDefinition, CachedMethodDefinition methodDefinitionType)
        {
            MethodBase mb = null;
            if (_methodSignaturesCache.TryGetValue(methodDefinitionType, out mb))
                return CreateMethodReference(assemblyDefinition, mb);
            else
                throw new ILWeaverException(String.Format(Properties.Resources.MethodSignatureNotFound, methodDefinitionType));
        }

        /// <summary>
        /// Add method to cache
        /// </summary>
        /// <param name="methodSignature">Method signature</param>
        /// <param name="methodBase">Method base</param>
        private static void AddMethodToCache(CachedMethodDefinition methodDefinitionType, MethodBase methodBase)
        {
            _methodSignaturesCache[methodDefinitionType] = methodBase;
 
        } // AddMethodsToCache(methodSignature, methodBase)

        /// <summary>
        /// Add default methods to _methodSignaturesCache for quick lookup.
        /// </summary>
        public static void AddDefaultMethodsToCache()
        {
            AddMethodToCache(CachedMethodDefinition.IsInnerCall, typeof(FilterContext).GetMethod("IsInnerCall", new Type[] { typeof(object), typeof(int) })); 
            AddMethodToCache(CachedMethodDefinition.SetInnerCall, typeof(FilterContext).GetMethod("SetInnerCall", new Type[] { typeof(object), typeof(int) })) ;
            AddMethodToCache(CachedMethodDefinition.ResetInnerCall, typeof(FilterContext).GetMethod("ResetInnerCall", new Type[] { }));
            AddMethodToCache(CachedMethodDefinition.FilterContextConstructor, typeof(FilterContext).GetConstructors()[0]);
            AddMethodToCache(CachedMethodDefinition.StoreAction, typeof(FilterContext).GetMethod("StoreAction", new Type[] { typeof(int) }));
            AddMethodToCache(CachedMethodDefinition.JoinPointContextConstructor, typeof(JoinPointContext).GetConstructors()[0]); 
            AddMethodToCache(CachedMethodDefinition.JoinPointContextSetSender, typeof(JoinPointContext).GetMethod("set_Sender", new Type[] { typeof(object) })); 
            AddMethodToCache(CachedMethodDefinition.GetTypeFromHandle,  typeof(System.Type).GetMethod("GetTypeFromHandle", new Type[] { typeof(System.RuntimeTypeHandle)})); 
            AddMethodToCache(CachedMethodDefinition.JoinPointContextSetReturnType, typeof(JoinPointContext).GetMethod("set_ReturnType", new Type[] { typeof(System.Type) })); 
            AddMethodToCache(CachedMethodDefinition.JoinPointContextAddArgument, typeof(JoinPointContext).GetMethod("AddArgument",
                            new Type[] { 
                            typeof(object), 
                            typeof(Int16), 
                            typeof(System.Type), 
                            typeof(ArgumentAttributes),
                            typeof(JoinPointContext) 
                        }));
            AddMethodToCache(CachedMethodDefinition.JoinPointContextSetStartTarget, typeof(JoinPointContext).GetMethod("set_StartTarget", new Type[] { typeof(object) }));
            AddMethodToCache(CachedMethodDefinition.JoinPointContextSetTarget, typeof(JoinPointContext).GetMethod("SetTarget",  new Type[] { typeof(object), typeof(JoinPointContext) }));   
            AddMethodToCache(CachedMethodDefinition.JoinPointContextSetStartSelector, typeof(JoinPointContext).GetMethod("set_StartSelector", new Type[] { typeof(string) }));   
            AddMethodToCache(CachedMethodDefinition.JoinPointContextGetReturnValue, typeof(JoinPointContext).GetMethod("get_ReturnValue", new Type[] { }));
            AddMethodToCache(CachedMethodDefinition.JoinPointContextGetStartTarget, typeof(JoinPointContext).GetMethod("get_StartTarget", new Type[0]));
            AddMethodToCache(CachedMethodDefinition.JoinPointContextSetCurrentTarget, typeof(JoinPointContext).GetMethod("set_CurrentTarget", new Type[] { typeof(object) }));
            AddMethodToCache(CachedMethodDefinition.JoinPointContextGetSubstitutionTarget, typeof(JoinPointContext).GetMethod("set_SubstitutionTarget", new Type[] { typeof(object) }));
            AddMethodToCache(CachedMethodDefinition.JoinPointContextSetSubstitutionTarget, typeof(JoinPointContext).GetMethod("set_SubstitutionTarget", new Type[] { typeof(object) }));
            AddMethodToCache(CachedMethodDefinition.JoinPointContextGetArgumentValue, typeof(JoinPointContext).GetMethod("GetArgumentValue", new Type[] { typeof(Int16) })); 
            AddMethodToCache(CachedMethodDefinition.JoinPointContextSetSubstitutionSelector, typeof(JoinPointContext).GetMethod("set_SubstitutionSelector", new Type[] { typeof(string) })); 
            AddMethodToCache(CachedMethodDefinition.JoinPointContextSetReturnValue, typeof(JoinPointContext).GetMethod("SetReturnValue", new Type[] { typeof(object), typeof(JoinPointContext) }));
            AddMethodToCache(CachedMethodDefinition.HasMoreStoredActions, typeof(FilterContext).GetMethod("HasMoreStoredActions", new Type[] { })); 
            AddMethodToCache(CachedMethodDefinition.NextStoredAction, typeof(FilterContext).GetMethod("NextStoredAction", new Type[] { }));
            AddMethodToCache(CachedMethodDefinition.ExceptionConstructor, typeof(Exception).GetConstructors()[0]); 

        
        } // AddDefaultMethodsToCache()
        
        #endregion

        #region TypeReference and caching

        /// <summary>
        /// Creates the type reference.
        /// </summary>
        /// <param name="assemblyDefinition">The assembly definition.</param>
        /// <param name="type">The type.</param>
        /// <returns></returns>
        public static TypeReference CreateTypeReference(AssemblyDefinition assemblyDefinition, Type type)
        {
            return assemblyDefinition.MainModule.Import(type);
        }

        /// <summary>
        /// Creates the type reference.
        /// </summary>
        /// <param name="assemblyDefinition">The assembly definition.</param>
        /// <param name="typeName">Name of the type.</param>
        /// <returns></returns>
        public static TypeReference CreateTypeReference(AssemblyDefinition assemblyDefinition, CachedTypeDefinition typeName)
        {
            Type type = null;
            if (_typesSignaturesCache.TryGetValue(typeName, out type))
                return CreateTypeReference(assemblyDefinition, type);
            else
                throw new ILWeaverException(String.Format(Properties.Resources.TypeNotFound, typeName));
        }


        /// <summary>
        /// Adds the types to cache.
        /// </summary>
        /// <param name="typeName">Name of the type.</param>
        /// <param name="type">The type.</param>
        private static void AddTypeToCache(CachedTypeDefinition typeName, Type type)
        {
            _typesSignaturesCache[typeName] = type;

        }


        /// <summary>
        /// Adds the default types to cache.
        /// </summary>
        public static void AddDefaultTypesToCache()
        {
            AddTypeToCache(CachedTypeDefinition.Int32 , typeof(Int32));
            AddTypeToCache(CachedTypeDefinition.Int16, typeof(Int16)); 
        } 

        #endregion

        /// <summary>
        /// Returns a method signature.
        /// </summary>
        /// <param name="method">The method.</param>
        /// <returns></returns>
        public static String MethodSignature(MethodDefinition method)
        {
            return method.ToString();
        }

        #region Resolve Types

        /// <summary>
        /// Get filter action element
        /// </summary>
        /// <param name="elements">Elements</param>
        /// <param name="fullname">The fullname.</param>
        /// <returns>Filter action element</returns>
        public static FilterActionElement GetFilterActionElement(List<FilterActionElement> elements, string fullname)
        {
            foreach (FilterActionElement  fae in elements)
            {
                if (fae.FullName.Equals(fullname))
                    return fae;
            }

            return null;
        } // GetFilterActionElement(elements, name)

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

        #endregion

        #region Resolve Methods

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

#endregion

        #region Utilties

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

        #endregion
    }

    /// <summary>
    /// Cached method definitions for quick lookup.
    /// </summary>
    public enum CachedMethodDefinition
    {
        IsInnerCall,
        SetInnerCall,
        ResetInnerCall,
        FilterContextConstructor,
        StoreAction,
        JoinPointContextConstructor,
        JoinPointContextAddArgument,
        JoinPointContextGetArgumentValue,
        JoinPointContextSetSender,
        JoinPointContextSetReturnType,
        JoinPointContextSetStartTarget,
        JoinPointContextSetTarget,
        JoinPointContextSetStartSelector,
        JoinPointContextGetReturnValue,
        JoinPointContextGetStartTarget,
        JoinPointContextSetCurrentTarget,  
        JoinPointContextGetSubstitutionTarget,
        JoinPointContextSetSubstitutionTarget,
        JoinPointContextSetSubstitutionSelector,
        JoinPointContextSetReturnValue,
        GetTypeFromHandle,
        HasMoreStoredActions,
        NextStoredAction,
        ExceptionConstructor
    } // enum CachedMethodDefinition

    /// <summary>
    /// Contains the cached types.
    /// </summary>
    public enum CachedTypeDefinition
    {
        Int32,
        Int16,
    } // enum CachedTypeDefinition
}
