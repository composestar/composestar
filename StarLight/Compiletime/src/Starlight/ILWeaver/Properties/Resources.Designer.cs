﻿//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//     Runtime Version:2.0.50727.42
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace Composestar.StarLight.ILWeaver.Properties {
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
                    global::System.Resources.ResourceManager temp = new global::System.Resources.ResourceManager("Composestar.StarLight.ILWeaver.Properties.Resources", typeof(Resources).Assembly);
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
        ///   Looks up a localized string similar to The assembly is not open..
        /// </summary>
        internal static string AssemblyNotOpen {
            get {
                return ResourceManager.GetString("AssemblyNotOpen", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Invalid configuration. Cannot specify delaysign without shouldSign set to true.
        /// </summary>
        internal static string CannotDelaySignWithoutSigning {
            get {
                return ResourceManager.GetString("CannotDelaySignWithoutSigning", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to An exception occured during the creation of IL instructions by the Cecil visitor. See the inner exception for more details..
        /// </summary>
        internal static string CecilVisitorRaisedException {
            get {
                return ResourceManager.GetString("CecilVisitorRaisedException", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to The constructor .ctor() for the type {0} could not be found..
        /// </summary>
        internal static string ConstructorNotFound {
            get {
                return ResourceManager.GetString("ConstructorNotFound", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Unable to resolve the method &apos;{0}&apos; from &apos;{1}&apos; to a Type used by the weaver. Make sure all the references are in place and the method has bee specified correctly..
        /// </summary>
        internal static string CouldNotResolveMethod {
            get {
                return ResourceManager.GetString("CouldNotResolveMethod", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Could not write the assembly to file &apos;{0}&apos;. Check if the file is not in use and has the correct access permissions..
        /// </summary>
        internal static string CouldNotSaveAssembly {
            get {
                return ResourceManager.GetString("CouldNotSaveAssembly", resourceCulture);
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
        ///   Looks up a localized string similar to The label for the JumpFilterAction has not been set..
        /// </summary>
        internal static string FilterJumpLabelIsNotSet {
            get {
                return ResourceManager.GetString("FilterJumpLabelIsNotSet", resourceCulture);
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
        ///   Looks up a localized string similar to The input image {0} could not be found.
        ///.
        /// </summary>
        internal static string InputImageNotFound {
            get {
                return ResourceManager.GetString("InputImageNotFound", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Invalid configuration. No Strong Name Key file specified..
        /// </summary>
        internal static string NoSNKSpecified {
            get {
                return ResourceManager.GetString("NoSNKSpecified", resourceCulture);
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
        ///   Looks up a localized string similar to Failed to find Strong Name Key file at {0}..
        /// </summary>
        internal static string SNKFileNotFound {
            get {
                return ResourceManager.GetString("SNKFileNotFound", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to The type {0} could not be found..
        /// </summary>
        internal static string TypeNotFound {
            get {
                return ResourceManager.GetString("TypeNotFound", resourceCulture);
            }
        }
    }
}
