
using System;
using System.Diagnostics;
using System.Globalization;
using System.Runtime.InteropServices;
using Microsoft.VisualStudio.Shell.Interop;
using Microsoft.VisualStudio;

using MsVsShell = Microsoft.VisualStudio.Shell;

namespace Composestar.StarLight.VisualStudio.Project
{
	/// <summary>
	/// This sample demonstrates how to add a package load key (PLK) to
	/// a package to enable the package to be used on a machine that
	/// does not have the VS SDK installed.
	/// 
	/// The PackageLoadKey class is an extension of the BasicPackage.
	/// The two are completely independent, but their code is very similar.
	/// The key difference is that this package will be loaded by
	/// Visual Studio when running Devenv /setup /NoVsip
	/// (the NoVsip switch is used to emulate the behavior on a machine
	/// where the VS SDK is not installed).
	/// 
	/// The only code difference between BasicPackage and this class is that
	/// we added the ProvideLoadKey attribute. There is very little restriction
	/// as to what the content of each of the parameters are, except for the fact that
	/// they HAVE TO match what was specified when requesting the PLK from the
	/// VS SDK website (http://msdn.microsoft.com/vstudio/extend/, and search for
	/// PLK). The value 150 is the resource ID of the PLK (found in VSPackage.resx).
	/// If you want to change the entry in the ProvideLoadKey attribute, simply
	/// request a new PLK using the new information so that it matches.
	/// </summary>
	[MsVsShell.ProvideLoadKey("standard", "8.0", "ComposeStar StarLight", "University of Twente", 150)]

	[MsVsShell.DefaultRegistryRoot(@"Software\Microsoft\VisualStudio\8.0Exp")]
	[MsVsShell.PackageRegistration(UseManagedResourcesOnly = true)]
	[Guid("A6304AA0-BF1C-4a80-8165-E4AB1E135B6B")]
	public class PackageLoadKey : MsVsShell.Package
	{
		/// <summary>
		/// PackageLoadKey contructor.
		/// While we could have used the default constructor, adding the Trace makes it
		/// possible to verify that the package was created without having to set a break
		/// point in the debugger.
		/// </summary>
		public PackageLoadKey()
		{
			Trace.WriteLine(String.Format(CultureInfo.CurrentCulture, "Entering constructor for class {0}.", this.GetType().Name));
		}
	}
}
