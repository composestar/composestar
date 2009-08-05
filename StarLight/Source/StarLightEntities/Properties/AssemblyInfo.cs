using System.Reflection;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;
using System;
using System.Diagnostics.CodeAnalysis;
 
// General Information about an assembly is controlled through the following 
// set of attributes. Change these attribute values to modify the information
// associated with an assembly.
[assembly: AssemblyTitle("Composestar.StarLight.Entities")]
[assembly: AssemblyDescription("Entities used by StarLight.")]
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
[assembly: Guid("e3280e74-4a44-46c1-9e2d-4ddd5639c57a")]

// Version information for an assembly consists of the following four values:
//
//      Major Version
//      Minor Version 
//      Build Number
//      Revision
//
// You can specify all the values or you can default the Revision and Build Numbers 
// by using the '*' as shown below:
[assembly: AssemblyVersion("0.9.6.0")]
[assembly: AssemblyFileVersion("0.9.6.0")]
[assembly: CLSCompliant(true)]

// Globale FXCop suppressions
[module: SuppressMessage("Microsoft.Design", "CA1020:AvoidNamespacesWithFewTypes", Scope = "namespace", Target = "Composestar.StarLight.Entities.Concerns")]
[module: SuppressMessage("Microsoft.Design", "CA1020:AvoidNamespacesWithFewTypes", Scope = "namespace", Target = "Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions.Visitor")]
[module: SuppressMessage("Microsoft.Design", "CA1020:AvoidNamespacesWithFewTypes", Scope = "namespace", Target = "Composestar.StarLight.Entities.WeaveSpec.Instructions.Visitor")]

[module: SuppressMessage("Microsoft.Naming", "CA1702:CompoundWordsShouldBeCasedCorrectly", Scope = "member", Target = "Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions.Visitor.IVisitor.VisitOrRight(Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions.OrCondition):System.Void", MessageId = "VisitOr")]
[module: SuppressMessage("Microsoft.Naming", "CA1702:CompoundWordsShouldBeCasedCorrectly", Scope = "member", Target = "Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions.Visitor.IVisitor.VisitOrLeft(Composestar.StarLight.Entities.WeaveSpec.ConditionExpressions.OrCondition):System.Void", MessageId = "VisitOr")]
[module: SuppressMessage("Microsoft.Naming", "CA1704:IdentifiersShouldBeSpelledCorrectly", Scope = "member", Target = "Composestar.StarLight.Entities.LanguageModel.ParameterOptions.RetVal", MessageId = "Val")]
