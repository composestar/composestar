using System.Reflection;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;
using System;
using System.Diagnostics.CodeAnalysis;
 
// General Information about an assembly is controlled through the following 
// set of attributes. Change these attribute values to modify the information
// associated with an assembly.
[assembly: AssemblyTitle("StarLight.Filters")]
[assembly: AssemblyDescription("Filter attributes and build in filters.")]
[assembly: AssemblyConfiguration("")]
[assembly: AssemblyCompany("University of Twente")]
[assembly: AssemblyProduct("ComposeStar StarLight")]
[assembly: AssemblyCopyright("Copyright © 2006 University of Twente")]
[assembly: AssemblyTrademark("")]
[assembly: AssemblyCulture("")]

// Setting ComVisible to false makes the types in this assembly not visible 
// to COM components.  If you need to access a type in this assembly from 
// COM, set the ComVisible attribute to true on that type.
[assembly: ComVisible(false)]

// The following GUID is for the ID of the typelib if this project is exposed to COM
[assembly: Guid("7338184c-21d7-4ba4-a709-72b4c83d1365")]

// Version information for an assembly consists of the following four values:
//
//      Major Version
//      Minor Version 
//      Build Number
//      Revision
//
// You can specify all the values or you can default the Revision and Build Numbers 
// by using the '*' as shown below:
[assembly: AssemblyVersion("0.8.6.0")]
[assembly: AssemblyFileVersion("0.8.6.0")]
[assembly: CLSCompliant(true)]

[assembly: Composestar.StarLight.SkipWeaving()]

[module: SuppressMessage("Microsoft.Naming", "CA1702:CompoundWordsShouldBeCasedCorrectly", Scope = "namespace", Target = "Composestar.StarLight.Filters.BuiltIn", MessageId = "StarLight")]
[module: SuppressMessage("Microsoft.Naming", "CA1704:IdentifiersShouldBeSpelledCorrectly", Scope = "namespace", Target = "Composestar.StarLight.Filters.BuiltIn", MessageId = "Composestar")]
[module: SuppressMessage("Microsoft.Naming", "CA1702:CompoundWordsShouldBeCasedCorrectly", Scope = "namespace", Target = "Composestar.StarLight.Filters.FilterTypes", MessageId = "StarLight")]
[module: SuppressMessage("Microsoft.Naming", "CA1704:IdentifiersShouldBeSpelledCorrectly", Scope = "namespace", Target = "Composestar.StarLight.Filters.FilterTypes", MessageId = "Composestar")]