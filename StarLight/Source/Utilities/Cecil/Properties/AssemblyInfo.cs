using System.Reflection;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;
using System;
using System.Diagnostics.CodeAnalysis;  

// General Information about an assembly is controlled through the following 
// set of attributes. Change these attribute values to modify the information
// associated with an assembly.
[assembly: AssemblyTitle("CecilUtilities")]
[assembly: AssemblyDescription("Utilties for the Cecil part of the StarLight project.")]
[assembly: AssemblyConfiguration("")]
[assembly: AssemblyCompany("University of Twente")]
[assembly: AssemblyProduct("CecilUtilities")]
[assembly: AssemblyCopyright("Copyright ©  2006")]
[assembly: AssemblyTrademark("")]
[assembly: AssemblyCulture("")]

// Setting ComVisible to false makes the types in this assembly not visible 
// to COM components.  If you need to access a type in this assembly from 
// COM, set the ComVisible attribute to true on that type.
[assembly: ComVisible(false)]

// The following GUID is for the ID of the typelib if this project is exposed to COM
[assembly: Guid("4588a624-9636-4fad-a01d-d3aacda79576")]

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

[module: SuppressMessage("Microsoft.Naming", "CA1704:IdentifiersShouldBeSpelledCorrectly", Scope = "type", Target = "Composestar.StarLight.Utilities.Interfaces.ICecilInliningInstructionVisitor", MessageId = "Inlining")]
[module: SuppressMessage("Microsoft.Design", "CA1020:AvoidNamespacesWithFewTypes", Scope = "namespace", Target = "Composestar.StarLight.Utilities.Interfaces")]
