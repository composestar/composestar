#region Using directives
using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Diagnostics;
using System.IO;
using System.Reflection;
using System.Text;
using System.Globalization;
using System.Diagnostics.CodeAnalysis;  

using Mono.Cecil;
using Mono.Cecil.Binary;
using Mono.Cecil.Cil;
using Mono.Cecil.Metadata;
using Mono.Cecil.Signatures;

using Composestar.Repository;
using Composestar.StarLight.Entities.Configuration;
using Composestar.StarLight.CoreServices;
using Composestar.StarLight.CoreServices.Exceptions;
using Composestar.StarLight.ContextInfo;

#endregion

namespace Composestar.StarLight.Utilities
{

    /// <summary>
    /// Contains functions used by the Cecil visitors, strategies, and weaver. 
    /// </summary>
    public sealed partial class CecilUtilities
    {
        
        #region Constants

        /// <summary>
        /// A void return type.
        /// </summary>
        public const string VoidType = "System.Void";

        private static object lockObject;

        #endregion

        #region Variables

        private static string _binFolder;
        private static StarLightAssemblyResolver _resolver;
        private static Dictionary<string, MethodDefinition> _methodsCache;
        private static Dictionary<string, TypeDefinition> _typesCache;
        private static Dictionary<CachedMethodDefinition, MethodBase> _methodSignaturesCache;
        private static Dictionary<CachedTypeDefinition, Type> _typesSignaturesCache;

        #endregion

        #region Constructor

        private CecilUtilities()
        {

        }

        /// <summary>
        /// Initializes the <see cref="T:CecilUtilities"/> class.
        /// </summary>
        [SuppressMessage("Microsoft.Performance", "CA1810:InitializeReferenceTypeStaticFieldsInline")]
        static CecilUtilities()
        {
            lockObject = new object();

            _methodsCache = new Dictionary<string, MethodDefinition>();
            _typesCache = new Dictionary<string, TypeDefinition>();
            _methodSignaturesCache = new Dictionary<CachedMethodDefinition, MethodBase>();
            _typesSignaturesCache = new Dictionary<CachedTypeDefinition, Type>();

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

        /// <summary>
        /// Gets the assembly resolver. 
        /// </summary>
        /// <remarks>Use this resolver to retrieve assemblies based on their name. It will first look in the folder specified by the BinFolder, the subfolders and finally the GAC.
        /// Set the <see cref="P:Composestar.StarLight.Utilities.CecilUtilities.BinFolder"></see> first to a location where all the binaries can be found.</remarks> 
        /// <value>The assembly resolver.</value>
        [CLSCompliant(false)]
        public static StarLightAssemblyResolver AssemblyResolver
        {
            get
            {
                if (_resolver  == null)
                {
                    lock (lockObject)
                    {
                        if (_resolver == null)
                        {
                            _resolver = new StarLightAssemblyResolver(BinFolder);
                        } // if
                    } // lock

                } // if
                return _resolver;
            }
        }

        #endregion

        #region MethodReference and caching

        /// <summary>
        /// Creates the method reference in the <paramref name="assemblyDefinition"/>.
        /// </summary>
        /// <param name="assemblyDefinition">The assembly definition to input the method into.</param>
        /// <param name="methodBase">The method information to import.</param>
        /// <returns></returns>
        [CLSCompliant(false)]
        public static MethodReference CreateMethodReference(AssemblyDefinition assemblyDefinition, MethodBase methodBase)
        {
            if (assemblyDefinition == null)
                throw new ArgumentNullException("assemblyDefinition");
 
            return assemblyDefinition.MainModule.Import(methodBase);
        }

        /// <summary>
        /// Creates the method reference using a cached version of the method.
        /// </summary>
        /// <param name="assemblyDefinition">The assembly definition to input the method into.</param>
        /// <param name="methodDefinitionType">Type of the method definition.</param>
        /// <returns>
        /// A <see cref="T:Mono.Cecil.MethodReference"></see> based on the supplied <paramref name="methodDefinitionType"/>.
        /// </returns>
        /// <example>
        /// The following example creates a call instruction to the <c>SetInnerCall</c> function.
        /// <code>
        /// // Call the SetInnerCall
        /// Instructions.Add(Worker.Create(OpCodes.Call,
        /// CecilUtilities.CreateMethodReference(TargetAssemblyDefinition,
        /// CachedMethodDefinition.SetInnerCall)));
        /// </code>
        /// </example>
        /// <exception cref="Composestar.StarLight.CoreServices.Exceptions.ILWeaverException">Thrown when the cached method definition could not be found.</exception>
        [CLSCompliant(false)]
        public static MethodReference CreateMethodReference(AssemblyDefinition assemblyDefinition, CachedMethodDefinition methodDefinitionType)
        {
            MethodBase mb = null;
            if (_methodSignaturesCache.TryGetValue(methodDefinitionType, out mb))
                return CreateMethodReference(assemblyDefinition, mb);
            else
                throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture,
                    Properties.Resources.MethodSignatureNotFound, methodDefinitionType));
        }

        /// <summary>
        /// Add method to cache
        /// </summary>
        /// <param name="methodDefinitionType">The method definition type.</param>
        /// <param name="methodBase">Method base</param>
        private static void AddMethodToCache(CachedMethodDefinition methodDefinitionType, MethodBase methodBase)
        {
            _methodSignaturesCache[methodDefinitionType] = methodBase;

        } // AddMethodsToCache(methodSignature, methodBase)

        /// <summary>
        /// Add default methods to _methodSignaturesCache for quick lookup.
        /// </summary>
        private static void AddDefaultMethodsToCache()
        {
            AddMethodToCache(CachedMethodDefinition.IsInnerCall, typeof(FilterContext).GetMethod("IsInnerCall", new Type[] { typeof(object), typeof(int) }));
            AddMethodToCache(CachedMethodDefinition.SetInnerCall, typeof(FilterContext).GetMethod("SetInnerCall", new Type[] { typeof(object), typeof(int) }));
            AddMethodToCache(CachedMethodDefinition.ResetInnerCall, typeof(FilterContext).GetMethod("ResetInnerCall", new Type[] { }));
            AddMethodToCache(CachedMethodDefinition.FilterContextConstructor, typeof(FilterContext).GetConstructors()[0]);
            AddMethodToCache(CachedMethodDefinition.StoreAction, typeof(FilterContext).GetMethod("StoreAction", new Type[] { typeof(int) }));
            AddMethodToCache(CachedMethodDefinition.JoinPointContextConstructor, typeof(JoinPointContext).GetConstructors()[0]);
            AddMethodToCache(CachedMethodDefinition.JoinPointContextSetSender, typeof(JoinPointContext).GetMethod("set_Sender", new Type[] { typeof(object) }));
            AddMethodToCache(CachedMethodDefinition.GetTypeFromHandle, typeof(System.Type).GetMethod("GetTypeFromHandle", new Type[] { typeof(System.RuntimeTypeHandle) }));
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
            AddMethodToCache(CachedMethodDefinition.JoinPointContextSetTarget, typeof(JoinPointContext).GetMethod("SetTarget", new Type[] { typeof(object), typeof(JoinPointContext) }));
            AddMethodToCache(CachedMethodDefinition.JoinPointContextSetStartSelector, typeof(JoinPointContext).GetMethod("set_StartSelector", new Type[] { typeof(string) }));
            AddMethodToCache(CachedMethodDefinition.JoinPointContextGetReturnValue, typeof(JoinPointContext).GetMethod("get_ReturnValue", new Type[] { }));
            AddMethodToCache(CachedMethodDefinition.JoinPointContextGetStartTarget, typeof(JoinPointContext).GetMethod("get_StartTarget", new Type[0]));
            AddMethodToCache(CachedMethodDefinition.JoinPointContextSetCurrentTarget, typeof(JoinPointContext).GetMethod("set_CurrentTarget", new Type[] { typeof(object) }));
            AddMethodToCache(CachedMethodDefinition.JoinPointContextSetCurrentSelector, typeof(JoinPointContext).GetMethod("set_CurrentSelector", new Type[] { typeof(string) }));
            AddMethodToCache(CachedMethodDefinition.JoinPointContextGetSubstitutionTarget, typeof(JoinPointContext).GetMethod("set_SubstitutionTarget", new Type[] { typeof(object) }));
            AddMethodToCache(CachedMethodDefinition.JoinPointContextSetSubstitutionTarget, typeof(JoinPointContext).GetMethod("set_SubstitutionTarget", new Type[] { typeof(object) }));
            AddMethodToCache(CachedMethodDefinition.JoinPointContextGetArgumentValue, typeof(JoinPointContext).GetMethod("GetArgumentValue", new Type[] { typeof(Int16) }));
            AddMethodToCache(CachedMethodDefinition.JoinPointContextSetArgumentValue, typeof(JoinPointContext).GetMethod("SetArgumentValue", new Type[] { typeof(Int16), typeof(object) }));
            AddMethodToCache(CachedMethodDefinition.JoinPointContextSetSubstitutionSelector, typeof(JoinPointContext).GetMethod("set_SubstitutionSelector", new Type[] { typeof(string) }));
            AddMethodToCache(CachedMethodDefinition.JoinPointContextSetReturnValue, typeof(JoinPointContext).GetMethod("SetReturnValue", new Type[] { typeof(object), typeof(JoinPointContext) }));
            AddMethodToCache(CachedMethodDefinition.HasMoreStoredActions, typeof(FilterContext).GetMethod("HasMoreStoredActions", new Type[] { }));
            AddMethodToCache(CachedMethodDefinition.NextStoredAction, typeof(FilterContext).GetMethod("NextStoredAction", new Type[] { }));
            AddMethodToCache(CachedMethodDefinition.ExceptionConstructor, typeof(Exception).GetConstructors()[0]);


        } // AddDefaultMethodsToCache()

        #endregion

        #region TypeReference and caching

        /// <summary>
        /// Creates a type reference based on the supplied <paramref name="type"/>.
        /// </summary>
        /// <param name="assemblyDefinition">The assembly definition.</param>
        /// <param name="type">The type to create a <see cref="T:Mono.Cecil.TypeReference"/> for.</param>
        /// <returns></returns>
        [CLSCompliant(false)]
        public static TypeReference CreateTypeReference(AssemblyDefinition assemblyDefinition, Type type)
        {
            if (assemblyDefinition == null)
                throw new ArgumentNullException("assemblyDefinition"); 

            return assemblyDefinition.MainModule.Import(type);
        }

        /// <summary>
        /// Creates the type reference.
        /// </summary>
        /// <param name="assemblyDefinition">The assembly definition.</param>
        /// <param name="typeName">Name of the type.</param>
        /// <returns>A <see cref="T:Mono.Cecil.TypeReference"></see> based on the supplied <paramref name="typeName"/>.</returns>
        /// <exception cref="Composestar.StarLight.CoreServices.Exceptions.ILWeaverException">Thrown when the cached type definition could not be found.</exception>
        [CLSCompliant(false)]
        public static TypeReference CreateTypeReference(AssemblyDefinition assemblyDefinition, CachedTypeDefinition typeName)
        {
            Type type = null;
            if (_typesSignaturesCache.TryGetValue(typeName, out type))
                return CreateTypeReference(assemblyDefinition, type);
            else
                throw new ILWeaverException(String.Format(CultureInfo.CurrentCulture, Properties.Resources.TypeNotFound, typeName));
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
        private static void AddDefaultTypesToCache()
        {
            AddTypeToCache(CachedTypeDefinition.Int32, typeof(Int32));
            AddTypeToCache(CachedTypeDefinition.Int16, typeof(Int16));
        }

        #endregion
        
        #region Resolve Types

        /// <summary>
        /// Resolves the type using a <paramref name="typeRef">type reference</paramref>.
        /// </summary>
        /// <remarks>This function uses an internal cache for quick lookup of types.</remarks>
        /// <param name="typeRef">The type reference.</param>
        /// <example>
        /// <code>TypeDefinition parentType = CecilUtilities.ResolveTypeDefinition(methodReference.DeclaringType);</code>
        /// </example>
        /// <returns>A <see cref="T:Mono.Cecil.TypeReference"></see> or <see langword="null"></see> when the type could not be found.</returns>
        /// <exception cref="ArgumentNullException">Thrown when the <paramref name="typeRef"/> is <see langword="null"></see>.</exception>
        [CLSCompliant(false)]
        public static TypeDefinition ResolveTypeDefinition(TypeReference typeRef)
        {

            if (typeRef == null)
                throw new ArgumentNullException("typeRef"); 

            TypeDefinition td;
            String cacheKey = string.Empty;
 
            if (typeRef.Scope != null)
            {

                // Get the cachekey
                cacheKey= CreateTypeCacheKey(typeRef.FullName, typeRef.Scope.Name);
                     
                // Lookup in cache.
                if (_typesCache.TryGetValue(cacheKey, out td))
                    return td;
            }
            

            TypeDefinition typeDef = typeRef.Module.Types[typeRef.FullName];

            if (typeDef == null && typeRef.Scope != null)
            {
                foreach (AssemblyNameReference assembly in typeRef.Module.AssemblyReferences)
                {
                    if (typeRef.Scope.Name == assembly.Name)
                    {
                        AssemblyDefinition ad = AssemblyResolver.Resolve(assembly);
                        typeDef = ad.MainModule.Types[typeRef.FullName];

                        break;
                    }
                }
            }

            if (typeDef != null && !string.IsNullOrEmpty(cacheKey))
                _typesCache.Add(cacheKey, typeDef);  

            return typeDef;
        }

        /// <summary>
        /// Resolves the type using its <paramref name="typeName"/> and <paramref name="assemblyName"/>. The <paramref name="assemblyFile"></paramref> is optional.
        /// </summary>
        /// <param name="typeName">Name of the type.</param>
        /// <param name="assemblyName">Name of the assembly.</param>
        /// <param name="assemblyFile">The assembly file.</param>
        /// <returns>A <see cref="T:Mono.Cecil.TypeReference"></see> or <see langword="null"></see> when the type could not be found.</returns>
        /// <remarks>This functions uses an internal cache for the found types for quick lookup.
        /// </remarks>
        /// <example>
        /// <code>
        /// TypeReference typeRef =
        ///    CecilUtilities.ResolveType(filterAction.FullName, filterActionElement.Assembly, null);    
        /// </code>
        /// </example>
        /// <exception cref="ArgumentNullException">Thrown when the <paramref name="typeName"/> is <see langword="null"></see> or empty.</exception>
        /// <exception cref="ArgumentNullException">Thrown when the <paramref name="assemblyName"/> is <see langword="null"></see> or empty.</exception>
        [CLSCompliant(false)]
        public static TypeReference ResolveType(string typeName, string assemblyName, string assemblyFile)
        {

            if (string.IsNullOrEmpty(typeName))
                throw new ArgumentNullException("typeName");

            if (string.IsNullOrEmpty(assemblyName))
                throw new ArgumentNullException("assemblyName"); 

            // Get the cachekey
            String cacheKey = CreateTypeCacheKey(typeName, assemblyName);

            TypeDefinition td;

            // Lookup in cache.
            if (_typesCache.TryGetValue(cacheKey, out td))
                return (TypeReference)td;


            if (_resolver == null)
            {
                if (String.IsNullOrEmpty(assemblyFile))
                    _resolver = new StarLightAssemblyResolver(BinFolder);
                else
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

            td = asmDef.MainModule.Types[typeName];

            if (td == null)
                return null;

            _typesCache.Add(cacheKey, td);

            return (TypeReference)td;
        }



        #endregion

        #region Resolve Methods

        /// <summary>
        /// Resolves a method by its <paramref name="methodName"/>, <paramref name="parameters"/>, <paramref name="typeName"/>, and <paramref name="assemblyname"/>.
        /// The <paramref name="assemblyFilename"/> is optional. The assemby resolver will try to find the assembly in its subfolders and GAC first.
        /// </summary>
        /// <param name="methodName">Name of the method.</param>
        /// <param name="parameters">The parameters of the method. Can be left <see langword="null"></see> to ignore the lookup of parameters.</param>
        /// <param name="typeName">Name of the type.</param>
        /// <param name="assemblyName">Name of the assembly, can be fully qualified.</param>
        /// <param name="assemblyFile">The assembly filename. The location of the assembly on disk. This is optional.</param>
        /// <returns>
        /// Returns a <see cref="T:MethodReference"></see> containing the found method or <see langword="null"></see> when the method, type or assembly could not be found.
        /// </returns>
        /// <example>
        /// 	<code>
        /// MethodReference method = CecilUtilities.ResolveMethod(
        /// "FooBar", new Type[] {typeof(string)}, "Program.Class",
        /// "Program, version=1.0.0.0, culture=neutral, publickey=null", "c:\assembly.dll");
        /// </code>
        /// </example>
        /// <remarks>
        /// 	<para>This function uses an internal cache to store already retrieved methods for quick lookup.</para>        
        /// <note>The first found method is returned.</note>
        /// </remarks>
        /// <exception cref="ArgumentNullException">Thrown when the <paramref name="methodName"/> is <see langword="null"></see> or empty.</exception>
        /// <exception cref="ArgumentNullException">Thrown when the <paramref name="typeName"/> is <see langword="null"></see> or empty.</exception>
        /// <exception cref="ArgumentNullException">Thrown when the <paramref name="assemblyName"/> is <see langword="null"></see> or empty.</exception>
        [CLSCompliant(false)]
        public static MethodReference ResolveMethod(string methodName, Type[] parameters, string typeName, string assemblyName, string assemblyFile)
        {
            if (string.IsNullOrEmpty(methodName))
                throw new ArgumentNullException("methodName");

            if (string.IsNullOrEmpty(typeName))
                throw new ArgumentNullException("typeName");

            if (string.IsNullOrEmpty(assemblyName))
                throw new ArgumentNullException("assemblyName"); 

            // If in cache, retrieve
            string cacheKey = CreateMethodCacheKey(methodName, parameters, typeName, assemblyName);

            MethodDefinition md;

            // Lookup in cache.
            if (_methodsCache.TryGetValue(cacheKey, out md))
                return (MethodReference) md;

            // Not in the cache, so get from the assembly and store in cache.

            // First check if the assembly resolver exists.
            if (_resolver == null)
            {
                _resolver = new StarLightAssemblyResolver(Path.GetDirectoryName(assemblyFile));
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

            MethodDefinition[] mds;
            if (parameters == null)
            {
                mds = td.Methods.GetMethod(methodName);

                if (mds.Length > 0)
                    md = mds[0];
                else
                    return null;

            } // if
            else
            {
                md = td.Methods.GetMethod(methodName, parameters);
                if (md == null)
                    return null;
            } // else

            // Add to the cache
            _methodsCache.Add(cacheKey, md);

            return (MethodReference)md;

        }
        
        /// <summary>
        /// Resolves a method by its <paramref name="methodName"></paramref>, <paramref name="typeName"></paramref>, and <paramref name="assemblyname"></paramref>. 
        /// The <paramref name="assemblyFilename"></paramref> is optional. The assembly resolver will try to find the assembly in its subfolders and GAC first.
        /// </summary>
        /// <param name="methodName">Name of the method.</param>
        /// <param name="typeName">Name of the type.</param>
        /// <param name="assemblyName">Name of the assembly, can be fully qualified.</param>
        /// <param name="assemblyFile">The assembly filename. The location of the assembly on disk. This is optional.</param>
        /// <returns>Returns a <see cref="T:MethodReference"></see> containing the found method or <see langword="null"></see> when the method, type or assembly could not be found.</returns>
        /// <example>
        /// <code>
        /// MethodReference method = CecilUtilities.ResolveMethod(
        /// "FooBar", "Program.Class",
        /// "Program, version=1.0.0.0, culture=neutral, publickey=null", "c:\assembly.dll");
        /// </code></example>
        /// <remarks>
        /// <para>This function uses an internal cache to store already retrieved methods for quick lookup.</para>
        /// <note>You cannot select the parameters with this function. Use the <see cref="M:ResolveMethod(string, Type[], string, string, string)"></see> for this.</note></remarks>
        [CLSCompliant(false)]
        public static MethodReference ResolveMethod(string methodName, string typeName, string assemblyName, string assemblyFile)
        {
            return ResolveMethod(methodName, null, typeName, assemblyName, assemblyFile);
        }

        /// <summary>
        /// Resolves a method by its <paramref name="methodName"></paramref>, <paramref name="typeName"></paramref>, and <paramref name="assemblyname"></paramref>. 
        /// The assembly resolver will try to find the assembly in its subfolders and GAC.
        /// </summary>
        /// <param name="methodName">Name of the method.</param>
        /// <param name="typeName">Name of the type.</param>
        /// <param name="assemblyName">Name of the assembly, can be fully qualified.</param>        
        /// <returns>Returns a <see cref="T:MethodReference"></see> containing the found method or <see langword="null"></see> when the method, type or assembly could not be found.</returns>
        /// <example>
        /// <code>
        /// MethodReference method = CecilUtilities.ResolveMethod(
        /// "FooBar", "Program.Class",
        /// "Program, version=1.0.0.0, culture=neutral, publickey=null");
        /// </code></example>
        /// <remarks>
        /// <para>This function uses an internal cache to store already retrieved methods for quick lookup.</para>
        /// <note>You cannot select the parameters with this function. Use the <see cref="M:ResolveMethod(string, Type[], string, string, string)"></see> for this.</note></remarks>
        [CLSCompliant(false)]
        public static MethodReference ResolveMethod(string methodName, string typeName, string assemblyName)
        {
            return ResolveMethod(methodName, typeName, assemblyName, string.Empty);
        }

        /// <summary>
        /// Resolves the method using its type parent type and an example method.
        /// </summary>
        /// <param name="parentType">The type containing the method.</param>
        /// <param name="methodName">The name of the method.</param>
        /// <param name="exampleMethod">Example method containing the same parametertypes and returntype of the
        /// wanted method.</param>
        /// <returns>
        /// The to be resolved method, or <see langword="null"/> if such method does not exist.
        /// </returns>
        /// <remarks>
        /// <note>This function does not offer caching.</note>
        /// </remarks> 
        /// <example>
        /// <code>
        /// MethodDefinition methodDef = CecilUtilities.ResolveMethod(parentType,
        ///                filterAction.SubstitutionSelector, originalCall);
        /// </code>
        /// </example> 
        /// <exception cref="ArgumentNullException">Thrown when the <paramref name="parentType"/> is <see langword="null"></see>.</exception>
        /// <exception cref="ArgumentNullException">Thrown when the <paramref name="methodName"/> is <see langword="null"></see> or empty.</exception>
        /// <exception cref="ArgumentNullException">Thrown when the <paramref name="exampleMethod"/> is <see langword="null"></see>.</exception>
        [CLSCompliant(false)]
        public static MethodDefinition ResolveMethod(TypeDefinition parentType, string methodName,
            MethodReference exampleMethod)
        {

            if (parentType == null)
                throw new ArgumentNullException("parentType");

            if (exampleMethod == null)
                throw new ArgumentNullException("exampleMethod");

            if (string.IsNullOrEmpty(methodName))
                throw new ArgumentNullException("methodName"); 

            MethodDefinition md = parentType.Methods.GetMethod(methodName, exampleMethod.Parameters);

            return md;
        }

        /// <summary>
        /// Resolves the method using the parent type, methodname and a list of parameters.
        /// </summary>
        /// <param name="parentTypeRef">The parent type reference.</param>
        /// <param name="methodName">The name of the method.</param>
        /// <param name="parameterTypes">The parameter types.</param>
        /// <returns>
        /// The to be resolved method, or <see langword="null"/> if such method does not exist.
        /// </returns>
        /// <example>
        /// Retrieves a MethodDefinition to the <c>Execute</c> function in the <c>typeDef</c> TypeReference with a <c>JoinPointContext</c> object as its parameter.
        /// <code>
        /// methodToCall = CecilUtilities.ResolveMethod(typeDef, "Execute", new Type[] { typeof(JoinPointContext) });
        /// </code>
        /// </example>  
        /// <exception cref="ArgumentNullException">Thrown when the <paramref name="parentTypeRef"/> is <see langword="null"></see>.</exception>
        /// <exception cref="ArgumentNullException">Thrown when the <paramref name="methodName"/> is <see langword="null"></see> or empty.</exception>
        [CLSCompliant(false)]
        public static MethodDefinition ResolveMethod(TypeReference parentTypeRef, string methodName, Type[] parameterTypes)
        {
            if (parentTypeRef == null)
                throw new ArgumentNullException("parentTypeRef");

            if (string.IsNullOrEmpty(methodName))
                throw new ArgumentNullException("methodName"); 

            // Lookup in cache
            string cacheKey = CreateMethodCacheKey(methodName, parameterTypes, parentTypeRef.FullName, parentTypeRef.Scope.Name);

            MethodDefinition md;

            // Lookup in cache.
            if (_methodsCache.TryGetValue(cacheKey, out md))
                return md;

            // Localy declared type
            TypeDefinition parentType = parentTypeRef.Module.Types[parentTypeRef.FullName];

            if (parentType == null && parentTypeRef.Scope != null)
            {
                foreach (AssemblyNameReference assembly in parentTypeRef.Module.AssemblyReferences)
                {
                    if (parentTypeRef.Scope.Name == assembly.Name)
                    {
                         // First check if the assembly resolver exists.
                        if (_resolver == null)
                        {
                            _resolver = new StarLightAssemblyResolver(BinFolder);
                        }
                        AssemblyDefinition ad = _resolver.Resolve(assembly);
                        if (ad == null)
                            return null;

                        parentType = ad.MainModule.Types[parentTypeRef.FullName];

                        break;
                    }
                }
            }

            md = parentType.Methods.GetMethod(methodName, parameterTypes);

            // Add to the cache
            _methodsCache.Add(cacheKey, md);

            return md;
        }

        /// <summary>
        /// Resolves a MethodDefinition corresponding with a certain MethodReference.
        /// </summary>
        /// <param name="reference">The MethodReference of which the MethodDefinition needs to be resolved.</param>
        /// <returns>The resolved MethodDefinition.</returns>
        /// <exception cref="ArgumentNullException">Thrown when the <paramref name="reference"/> is <see langword="null"></see>.</exception>
        [CLSCompliant(false)]
        public static MethodDefinition ResolveMethodDefinition(MethodReference reference)
        {
            if (reference == null)
                throw new ArgumentNullException("reference");

            TypeDefinition declaringType = ResolveTypeDefinition(reference.DeclaringType);

            return declaringType.Methods.GetMethod(reference.Name, reference.Parameters);
        }

        #endregion

        #region Utilities

        /// <summary>
        /// Creates the type cache key.
        /// </summary>
        /// <param name="typeName">Name of the type.</param>
        /// <param name="assemblyName">Name of the assembly.</param>
        /// <returns>A formatted cache key to be used as a unique identifier for the types in the cache.</returns>
        private static string CreateTypeCacheKey(string typeName, string assemblyName)
        {
            return String.Format(CultureInfo.CurrentCulture,  "{1}, {0}", assemblyName, typeName);                       
        }

        /// <summary>
        /// Creates the cache key for a method.
        /// </summary>
        /// <param name="methodName">Name of the method.</param>
        /// <param name="parameters">The parameters.</param>
        /// <param name="typeName">Name of the type.</param>
        /// <param name="assemblyName">Name of the assembly.</param>
        /// <returns>A formatted cache key to be used as a unique identifier for the methods in the cache.</returns>
        private static string CreateMethodCacheKey(string methodName, Type[] parameters, string typeName, string assemblyName)
        {
            StringBuilder sb = new StringBuilder();
            sb.AppendFormat("[{0}] ", assemblyName);
            sb.AppendFormat("{0}::", typeName);
            sb.Append(methodName);
            if (parameters == null)
                sb.Append("()");
            else
            {
                sb.Append("(");
                for (int i = 0; i < parameters.Length; i++)
                {
                    sb.Append(parameters[i].Name);
                    if (i < parameters.Length - 1)
                        sb.Append(",");
                } // for
                sb.Append(")");
            } // else

            return sb.ToString(); 
        }

        /// <summary>
        /// Returns a method signature.
        /// </summary>
        /// <param name="method">The method.</param>
        /// <returns>A formatted signature of a method.</returns>
        [CLSCompliant(false)]
        public static String MethodSignature(MethodDefinition method)
        {
            if (method == null)
                throw new ArgumentNullException("method"); 
            
            return method.ToString();
        }

        /// <summary>
        /// Reads data from a stream until the end is reached. The
        /// data is returned as a byte array. 
        /// </summary>
        /// <param name="stream">The stream to read data from</param>
        /// <param name="initialLength">The initial buffer length</param>
        /// <returns>Byte array with the contents of the file.</returns>
        /// <exception cref="System.IO.EndOfStreamException">An IOException is
        /// thrown if any of the underlying IO calls fail.</exception>
        public static byte[] ReadFileStream(Stream stream, int initialLength)
        {
            if (stream == null)
                throw new ArgumentNullException("stream"); 

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

    #region CachedMethodDefinition Enumeration

    /// <summary>
    /// Cached method definitions for quick lookup. 
    /// These methods are precached and can easily be retrieved by calling the <see cref="M:CecilUtilities.CreateMethodReference(AssemblyDefinition, CachedMethodDefinition)"></see>.
    /// </summary>
    /// <example>
    /// The following example will create a call to the the exception constructor.
    /// <code>
    /// visitor.Instructions.Add(visitor.Worker.Create(OpCodes.Newobj, 
    ///         CecilUtilities.CreateMethodReference(visitor.TargetAssemblyDefinition, 
    ///         CachedMethodDefinition.ExceptionConstructor)));
    /// </code>
    /// </example> 
    public enum CachedMethodDefinition
    {
        /// <summary>
        /// Checks if the current method is making an inner call.
        /// </summary>
        IsInnerCall,
        /// <summary>
        /// Set the inner call context.
        /// </summary>
        SetInnerCall,
        /// <summary>
        /// Reset the inner call context.
        /// </summary>
        ResetInnerCall,
        /// <summary>
        /// Create a new FilterContext object by calling its constructor.
        /// </summary>
        FilterContextConstructor,
        /// <summary>
        /// Store an action.
        /// </summary>
        StoreAction,
        /// <summary>
        /// Create a new JoinPointContext object by callings its constructor.
        /// </summary>
        JoinPointContextConstructor,
        /// <summary>
        /// Add an argument to the JoinPoinContext.
        /// </summary>
        JoinPointContextAddArgument,
        /// <summary>
        /// Retrieve an argument value from the JoinPoinContext.
        /// </summary>
        JoinPointContextGetArgumentValue,
        /// <summary>
        /// Set an argument value in the JoinPoinContext.
        /// </summary>
        JoinPointContextSetArgumentValue,
        /// <summary>
        /// Set the sender.
        /// </summary>
        JoinPointContextSetSender,
        /// <summary>
        /// Set the return type in the JoinPoinContext.
        /// </summary>
        JoinPointContextSetReturnType,
        /// <summary>
        /// Set the start target in the JoinPoinContext.
        /// </summary>
        JoinPointContextSetStartTarget,
        /// <summary>
        /// Set the target in the JoinPoinContext.
        /// </summary>
        JoinPointContextSetTarget,
        /// <summary>
        /// Set the start selector in the JoinPoinContext.
        /// </summary>
        JoinPointContextSetStartSelector,
        /// <summary>
        /// Retrieve the return value from the JoinPoinContext.
        /// </summary>
        JoinPointContextGetReturnValue,
        /// <summary>
        /// Retrieve the start target from the JoinPoinContext.
        /// </summary>
        JoinPointContextGetStartTarget,
        /// <summary>
        /// Set the current target in the JoinPoinContext.
        /// </summary>
        JoinPointContextSetCurrentTarget,
        /// <summary>
        /// Set the current selector in the JoinPoinContext.
        /// </summary>
        JoinPointContextSetCurrentSelector,
        /// <summary>
        /// Retrieve the substitution target from the JoinPoinContext.
        /// </summary>
        JoinPointContextGetSubstitutionTarget,
        /// <summary>
        /// Set the substitition target in the JoinPoinContext.
        /// </summary>
        JoinPointContextSetSubstitutionTarget,
        /// <summary>
        /// Set the substitution selector in the JoinPoinContext.
        /// </summary>
        JoinPointContextSetSubstitutionSelector,
        /// <summary>
        /// Set the return value in the JoinPoinContext.
        /// </summary>
        JoinPointContextSetReturnValue,
        /// <summary>
        /// Call the Type.GetType function.
        /// </summary>
        GetTypeFromHandle,
        /// <summary>
        /// Check if there are more stored actions. 
        /// </summary>
        HasMoreStoredActions,
        /// <summary>
        /// Retrieve the next stored action id.
        /// </summary>
        NextStoredAction,
        /// <summary>
        /// Create an exception by calling its constructor.
        /// </summary>
        ExceptionConstructor
    } // enum CachedMethodDefinition


    #endregion

    #region CachedTypeDefinition Enumeration

    /// <summary>
    /// Contains the cached types.
    /// </summary>
    public enum CachedTypeDefinition
    {
        /// <summary>
        /// Int32
        /// </summary>
        Int32,
        /// <summary>
        /// Int64
        /// </summary>
        Int16,
    } // enum CachedTypeDefinition

    #endregion
}
