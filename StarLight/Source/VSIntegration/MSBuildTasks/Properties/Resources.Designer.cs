﻿//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//     Runtime Version:2.0.50727.42
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace Composestar.StarLight.MSBuild.Tasks.Properties {
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
                    global::System.Resources.ResourceManager temp = new global::System.Resources.ResourceManager("Composestar.StarLight.MSBuild.Tasks.Properties.Resources", typeof(Resources).Assembly);
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
        ///   Looks up a localized string similar to Adding concern file &apos;{0}&apos; to the repository..
        /// </summary>
        internal static string AddingConcernFile {
            get {
                return ResourceManager.GetString("AddingConcernFile", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Analyzing the IL files using the Cecil IL Analyzer..
        /// </summary>
        internal static string AnalyzerStartText {
            get {
                return ResourceManager.GetString("AnalyzerStartText", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Analyzing file &apos;{0}&apos;..
        /// </summary>
        internal static string AnalyzingFile {
            get {
                return ResourceManager.GetString("AnalyzingFile", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Analyzing referenced assemblies for unresolved types..
        /// </summary>
        internal static string AnalyzingUnresolved {
            get {
                return ResourceManager.GetString("AnalyzingUnresolved", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Referenced assemblies analyzed in {1:0.0000} seconds, {0} unresolved assemblies remaining..
        /// </summary>
        internal static string AnalyzingUnresolvedCompleted {
            get {
                return ResourceManager.GetString("AnalyzingUnresolvedCompleted", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Assembly analyzer: {0} types found in {2:0.0000} seconds. ({1} assemblies not resolved)..
        /// </summary>
        internal static string AssemblyAnalyzed {
            get {
                return ResourceManager.GetString("AssemblyAnalyzed", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Assembly &apos;{0}&apos; has not been modified, analysis of this file is skipped..
        /// </summary>
        internal static string AssemblyNotModified {
            get {
                return ResourceManager.GetString("AssemblyNotModified", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Unable to convert the debug level &apos;{0}&apos; to an integer value..
        /// </summary>
        internal static string CouldNotConvertDebugLevel {
            get {
                return ResourceManager.GetString("CouldNotConvertDebugLevel", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Unable to parse the Weave Debug level &apos;{0}&apos;. Make sure you entered a correct debug level (None, Statistics, Detailed)..
        /// </summary>
        internal static string CouldNotParseWeaveDebugLevel {
            get {
                return ResourceManager.GetString("CouldNotParseWeaveDebugLevel", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Unable to read the requested registry values. Make sure the registry contains the correct settings by running the setup application..
        /// </summary>
        internal static string CouldNotReadRegistryValues {
            get {
                return ResourceManager.GetString("CouldNotReadRegistryValues", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to An exception occurred while executing Java. The message is &apos;{0}&apos;..
        /// </summary>
        internal static string ExecutionException {
            get {
                return ResourceManager.GetString("ExecutionException", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Assembly analyzer: {0} filter types and {1} filter actions found..
        /// </summary>
        internal static string FiltersAnalyzed {
            get {
                return ResourceManager.GetString("FiltersAnalyzed", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Retrieved {0} assemblies from the local database in {1:0.0000} seconds..
        /// </summary>
        internal static string FoundInDatabase {
            get {
                return ResourceManager.GetString("FoundInDatabase", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Found {0} referenced types in {1} concern(s) in {2:0.0000} seconds..
        /// </summary>
        internal static string FoundReferenceType {
            get {
                return ResourceManager.GetString("FoundReferenceType", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Could not execute the Java executable &apos;{0}&apos; because of access permission errors..
        /// </summary>
        internal static string JavaExecutableAccessDenied {
            get {
                return ResourceManager.GetString("JavaExecutableAccessDenied", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to The Java executable &apos;{0}&apos; could not be found. Either add the location of this file to the path or place the folder in the JavaFolder key in the HKEY_LOCAL_MACHINE\SOFTWARE\Composestar\StarLight hive in the registry..
        /// </summary>
        internal static string JavaExecutableNotFound {
            get {
                return ResourceManager.GetString("JavaExecutableNotFound", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Java will be executed with the following arguments: {0}.
        /// </summary>
        internal static string JavaStartMessage {
            get {
                return ResourceManager.GetString("JavaStartMessage", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to StarLight Master run completed successfully in {0:0.0000} seconds..
        /// </summary>
        internal static string MasterCompleted {
            get {
                return ResourceManager.GetString("MasterCompleted", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Master run failure reported by process. Exit code is {0}. Messages above this line might give an indication of the problem..
        /// </summary>
        internal static string MasterRunFailed {
            get {
                return ResourceManager.GetString("MasterRunFailed", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Skipping StarLight Java Master, no concerns found..
        /// </summary>
        internal static string MasterSkipNoConcerns {
            get {
                return ResourceManager.GetString("MasterSkipNoConcerns", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Preparing to start StarLight Java Master by collecting data..
        /// </summary>
        internal static string MasterStartText {
            get {
                return ResourceManager.GetString("MasterStartText", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Referenced types to resolve: {0}..
        /// </summary>
        internal static string NumberOfReferencesToResolve {
            get {
                return ResourceManager.GetString("NumberOfReferencesToResolve", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Opening repository file &apos;{0}&apos;..
        /// </summary>
        internal static string OpenDatabase {
            get {
                return ResourceManager.GetString("OpenDatabase", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Parsing and adding concern file &apos;{0}&apos;..
        /// </summary>
        internal static string ParsingConcernFile {
            get {
                return ResourceManager.GetString("ParsingConcernFile", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Could not execute the PEVerify executable &apos;{0}&apos; because of access permission errors. IL verification will be skipped..
        /// </summary>
        internal static string PEVerifyExecutableAccessDenied {
            get {
                return ResourceManager.GetString("PEVerifyExecutableAccessDenied", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to The PEVerify executable &apos;{0}&apos; could not be found. Make sure you have installed the .NET SDK. IL verification will be skipped..
        /// </summary>
        internal static string PEVerifyExecutableNotFound {
            get {
                return ResourceManager.GetString("PEVerifyExecutableNotFound", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to An exception occured while executing PEVerify. The message is &apos;{0}&apos;..
        /// </summary>
        internal static string PEVerifyExecutionException {
            get {
                return ResourceManager.GetString("PEVerifyExecutionException", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Removing &apos;{0}&apos; from the database..
        /// </summary>
        internal static string RemovingFileFromDatabase {
            get {
                return ResourceManager.GetString("RemovingFileFromDatabase", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Searching in local database for {0} unresolved types..
        /// </summary>
        internal static string SearchingInDatabase {
            get {
                return ResourceManager.GetString("SearchingInDatabase", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Skipped weaving of &apos;{0}&apos; because there is no weaving specification for this assembly..
        /// </summary>
        internal static string SkippedWeavingFile {
            get {
                return ResourceManager.GetString("SkippedWeavingFile", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Storing debug level {0} in the repository..
        /// </summary>
        internal static string StoreDebugLevel {
            get {
                return ResourceManager.GetString("StoreDebugLevel", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Saving {0} assemblies to disk..
        /// </summary>
        internal static string StoreInDatabase {
            get {
                return ResourceManager.GetString("StoreInDatabase", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to {0} assemblies with a total of {1} resolved assemblies saved in {2:0.0000} seconds..
        /// </summary>
        internal static string StoreInDatabaseCompleted {
            get {
                return ResourceManager.GetString("StoreInDatabaseCompleted", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Unable to resolve type &apos;{0}&apos;, are you missing an assembly reference?.
        /// </summary>
        internal static string UnableToResolve {
            get {
                return ResourceManager.GetString("UnableToResolve", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to File &apos;{0}&apos; does not require IL weaving, using backup file..
        /// </summary>
        internal static string UsingBackupWeaveFile {
            get {
                return ResourceManager.GetString("UsingBackupWeaveFile", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Verification of {0} assemblies executed in {1:0.0000} seconds..
        /// </summary>
        internal static string VerificationCompleted {
            get {
                return ResourceManager.GetString("VerificationCompleted", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Verification error in &apos;{0}&apos;, method &apos;{2}&apos; at {3}: {1}.
        /// </summary>
        internal static string VerificationError {
            get {
                return ResourceManager.GetString("VerificationError", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to The generated IL in file &apos;{0}&apos; could not be verified as correct IL code..
        /// </summary>
        internal static string VerifyFailure {
            get {
                return ResourceManager.GetString("VerifyFailure", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Verifying assembly &apos;{0}&apos; using PEVerify..
        /// </summary>
        internal static string VerifyingAssembly {
            get {
                return ResourceManager.GetString("VerifyingAssembly", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to File &apos;{0}&apos; has correct IL code..
        /// </summary>
        internal static string VerifySuccess {
            get {
                return ResourceManager.GetString("VerifySuccess", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to The IL Weaver detected the following exception &quot;{0}&quot;..
        /// </summary>
        internal static string WeaverException {
            get {
                return ResourceManager.GetString("WeaverException", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to The following stack trace can indicate the location of the above exception message:
        ///    {0}.
        /// </summary>
        internal static string WeaverExceptionStackTrace {
            get {
                return ResourceManager.GetString("WeaverExceptionStackTrace", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Weaving completed in {0:0.0000} seconds..
        /// </summary>
        internal static string WeavingCompleted {
            get {
                return ResourceManager.GetString("WeavingCompleted", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Weaving file &apos;{0}&apos;, please wait....
        /// </summary>
        internal static string WeavingFile {
            get {
                return ResourceManager.GetString("WeavingFile", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Weaving instructions log file saved to &apos;{0}&apos;..
        /// </summary>
        internal static string WeavingInstructionsLogSaved {
            get {
                return ResourceManager.GetString("WeavingInstructionsLogSaved", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Weaving the filter code using the Cecil IL Weaver..
        /// </summary>
        internal static string WeavingStartText {
            get {
                return ResourceManager.GetString("WeavingStartText", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Weaving statistics for {8}:
        ///Weaved in {13:0.0000} sec.: {9} internal(s), {10} external(s)
        ///                       {11} inputfilter(s), {12} outputfilter(s)
        ///#{7:000}   Types: total {5:0.0000} sec. max {3:0.0000} sec. avg {1:0.0000} sec.
        ///#{6:000} Methods: total {4:0.0000} sec. max {2:0.0000} sec. avg {0:0.0000} sec..
        /// </summary>
        internal static string WeavingStats {
            get {
                return ResourceManager.GetString("WeavingStats", resourceCulture);
            }
        }
        
        /// <summary>
        ///   Looks up a localized string similar to Weaving timing log file saved to &apos;{0}&apos;..
        /// </summary>
        internal static string WeavingTimingLogSaved {
            get {
                return ResourceManager.GetString("WeavingTimingLogSaved", resourceCulture);
            }
        }
    }
}
