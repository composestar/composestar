using System.Reflection;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;
using System;
using System.Diagnostics.CodeAnalysis;
using System.Security.Permissions;  

// General Information about an assembly is controlled through the following 
// set of attributes. Change these attribute values to modify the information
// associated with an assembly.
[assembly: AssemblyTitle("ILWeaver")]
[assembly: AssemblyDescription("IL Weaver for the StarLight project")]
[assembly: AssemblyConfiguration("")]
[assembly: AssemblyCompany("University of Twente")]
[assembly: AssemblyProduct("ILWeaver")]
[assembly: AssemblyCopyright("Copyright ©  2006")]
[assembly: AssemblyTrademark("")]
[assembly: AssemblyCulture("")]

// Setting ComVisible to false makes the types in this assembly not visible 
// to COM components.  If you need to access a type in this assembly from 
// COM, set the ComVisible attribute to true on that type.
[assembly: ComVisible(false)]

// The following GUID is for the ID of the typelib if this project is exposed to COM
[assembly: Guid("382bcef9-c7b1-44a5-9ed4-dd476e5141b7")]

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

// Needed for registry access, unmanaged code call
[assembly: SecurityPermissionAttribute(SecurityAction.RequestMinimum, Flags = SecurityPermissionFlag.UnmanagedCode)]
  
[assembly: CLSCompliant(true)]
[module: SuppressMessage("Microsoft.Design", "CA1020:AvoidNamespacesWithFewTypes", Scope = "namespace", Target = "Composestar.StarLight.ILWeaver")]
[module: SuppressMessage("Microsoft.Design", "CA1020:AvoidNamespacesWithFewTypes", Scope = "namespace", Target = "Composestar.StarLight.ILWeaver.WeaveStrategy")]
[module: SuppressMessage("Microsoft.Naming", "CA1701:ResourceStringCompoundWordsShouldBeCasedCorrectly", Scope = "resource", Target = "Composestar.StarLight.ILWeaver.Properties.Resources.resources", MessageId = "filename")]