﻿//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//     Runtime Version:2.0.50727.42
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace Composestar.StarLight.ILAnalyzer.Properties {
    using System;
    
    
    /// <summary>
    ///   A strongly-typed resource class, for looking up localized strings, etc.
    /// </summary>
    // This class was auto-generated by the StronglyTypedResourceBuilder
    // class via a tool like ResGen or Visual Studio.
    // To add or remove a member, edit your .ResX file then rerun ResGen
    // with the /str option, or rebuild your VS project.
    [global::System.CodeDom.Compiler.GeneratedCodeAttribute("System.Resources.Tools.StronglyTypedResourceBuilder", "2.0.0.0")]
    [global::System.Diagnostics.DebuggerNonUserCodeAttribute()]
    [global::System.Runtime.CompilerServices.CompilerGeneratedAttribute()]
    internal class Resources {
        
        private static global::System.Resources.ResourceManager resourceMan;
        
        private static global::System.Globalization.CultureInfo resourceCulture;
        
        [global::System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Performance", "CA1811:AvoidUncalledPrivateCode")]
        internal Resources() {
        }
        
        /// <summary>
        ///   Returns the cached ResourceManager instance used by this class.
        /// </summary>
        [global::System.ComponentModel.EditorBrowsableAttribute(global::System.ComponentModel.EditorBrowsableState.Advanced)]
        internal static global::System.Resources.ResourceManager ResourceManager {
            get {
                if (object.ReferenceEquals(resourceMan, null)) {
                    global::System.Resources.ResourceManager temp = new global::System.Resources.ResourceManager("Composestar.StarLight.ILAnalyzer.Properties.Resources", typeof(Resources).Assembly);
                    resourceMan = temp;
                }
                return resourceMan;
            }
        }
        
        /// <summary>
        ///   Overrides the current thread's CurrentUICulture property for all
        ///   resource lookups using this strongly typed resource class.
        /// </summary>
        [global::System.ComponentModel.EditorBrowsableAttribute(global::System.ComponentModel.EditorBrowsableState.Advanced)]
        internal static global::System.Globalization.CultureInfo Culture {
            get {
                return resourceCulture;
            }
            set {
                resourceCulture = value;
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Unable to find the assembly {0}. Make sure all the assemblies are referenced..
        /// </summary>
        internal static string CouldNotFindAssembly {
            get {
                return ResourceManager.GetString("CouldNotFindAssembly", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Unable to find the type {0}. Make sure all the types are referenced..
        /// </summary>
        internal static string CouldNotFindType {
            get {
                return ResourceManager.GetString("CouldNotFindType", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to The supplied filename is null or empty..
        /// </summary>
        internal static string FileNameNullOrEmpty {
            get {
                return ResourceManager.GetString("FileNameNullOrEmpty", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to The file {0} could not be found..
        /// </summary>
        internal static string FileNotFound {
            get {
                return ResourceManager.GetString("FileNotFound", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to The format of binary image {0} is invalid..
        /// </summary>
        internal static string ImageIsBad {
            get {
                return ResourceManager.GetString("ImageIsBad", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to The analyzer has not yet been initialized. Call the Initialize function first..
        /// </summary>
        internal static string NotYetInitialized {
            get {
                return ResourceManager.GetString("NotYetInitialized", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to The config collection did not contain a valid filename to the repository file..
        /// </summary>
        internal static string RepositoryFilenameNotSpecified {
            get {
                return ResourceManager.GetString("RepositoryFilenameNotSpecified", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Unable to resolve assembly &apos;{0}&apos;..
        /// </summary>
        internal static string UnableToResolveAssembly {
            get {
                return ResourceManager.GetString("UnableToResolveAssembly", resourceCulture);
            }
        }
    }
}
