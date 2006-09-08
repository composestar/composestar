
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
	/// This sample demonstrates how to display an image and some text in the
	/// splash screen as well as in the Help About dialog box.
	/// 
	/// The PackageSplashHelpAbout class is an extension of the BasicPackage.
	/// The two are completely independent, but their code is very similar.
	/// One of the key differences is that this package will be loaded by
	/// Visual Studio when displaying the Help About dialog as well as when running
	/// Devenv /setup (as part of creating the splash screen).
	/// 
	/// The first code difference between BasicPackage and this class is that
	/// the InstalledProductRegistration attribute has been added. The
	/// UseInterface value is set to true because IVsInstalledProduct is implemented.
	/// To appear in the Help About but not in the splash screen, this
	/// value would be set to false and we would not have implemented
	/// IVsInstalledProduct. The second parameter for the attribute is
	/// the text that will be displayed in the splash screen for the product.
	/// This should be kept short (the Help About string can be longer) so
	/// that it fits on the splash screen. The 3rd parameter is the package
	/// description, which is displayed in the Help About dialog. The 4th parameter 
	/// is the product version.
	/// In this case we are using the interface to provide the product informations,
	/// so the other parameters are not needed and we can set them to null.
	/// 
	/// Devenv /Splash can be used to see the splash screen for a longer
	/// duration to verify that your package shows in the way you want.
	/// 
	/// Finally, there is a method called GetResourceString. Its purpose is to 
	/// load the strings from resources so that resources
	/// can be localized. While it could be private, we make it public so that
	/// other classes in our package can also make use of it.
	/// </summary>
	[MsVsShell.InstalledProductRegistration(true, null, null, null)]

	[MsVsShell.DefaultRegistryRoot(@"Software\Microsoft\VisualStudio\8.0Exp")]
	[MsVsShell.PackageRegistration(UseManagedResourcesOnly = true)]
	[Guid("48E8F02D-F84A-471d-9F12-E9DE63B1A33E")]
	public class PackageSplashHelpAbout : MsVsShell.Package, IVsInstalledProduct
	{
		/// <summary>
		/// PackageSplashHelpAbout contructor.
		/// While we could have used the default constructor, adding the Trace makes it
		/// possible to verify that the package was created without having to set a break
		/// point in the debugger.
		/// </summary>
		public PackageSplashHelpAbout()
		{
			Trace.WriteLine(String.Format(CultureInfo.CurrentCulture, "Entering constructor for class {0}.", this.GetType().Name));
		}

		#region IVsInstalledProduct Members
		/// <summary>
		/// This method is called during Devenv /Setup to get the bitmap to
		/// display on the splash screen for this package.
		/// </summary>
		/// <param name="pIdBmp">The resource id corresponding to the bitmap to display on the splash screen</param>
		/// <returns>HRESULT, indicating success or failure</returns>
		public int IdBmpSplash(out uint pIdBmp)
		{
			pIdBmp = 300;

			return VSConstants.S_OK;
		}

		/// <summary>
		/// This method is called to get the icon that will be displayed in the
		/// Help About dialog when this package is selected.
		/// </summary>
		/// <param name="pIdIco">The resource id corresponding to the icon to display on the Help About dialog</param>
		/// <returns>HRESULT, indicating success or failure</returns>
		public int IdIcoLogoForAboutbox(out uint pIdIco)
		{
			pIdIco = 400;

			return VSConstants.S_OK;
		}

		/// <summary>
		/// This methods provides the product official name, it will be
		/// displayed in the help about dialog.
		/// </summary>
		/// <param name="pbstrName">Out parameter to which to assign the product name</param>
		/// <returns>HRESULT, indicating success or failure</returns>
		public int OfficialName(out string pbstrName)
		{
			pbstrName = GetResourceString("@101");
			return VSConstants.S_OK;
		}

		/// <summary>
		/// This methods provides the product description, it will be
		/// displayed in the help about dialog.
		/// </summary>
		/// <param name="pbstrProductDetails">Out parameter to which to assign the description of the package</param>
		/// <returns>HRESULT, indicating success or failure</returns>
		public int ProductDetails(out string pbstrProductDetails)
		{
			pbstrProductDetails = GetResourceString("@102");
			return VSConstants.S_OK;
		}

		/// <summary>
		/// This methods provides the product version, it will be
		/// displayed in the help about dialog.
		/// </summary>
		/// <param name="pbstrPID">Out parameter to which to assign the version number</param>
		/// <returns>HRESULT, indicating success or failure</returns>
		public int ProductID(out string pbstrPID)
		{
			pbstrPID = GetResourceString("@104");
			return VSConstants.S_OK;
		}

		#endregion

		/// <summary>
		/// This method loads a localized string based on the specified resource.
		/// </summary>
		/// <param name="resourceName">Resource to load</param>
		/// <returns>String loaded for the specified resource</returns>
		public string GetResourceString(string resourceName)
		{
			string resourceValue;
			IVsResourceManager resourceManager = (IVsResourceManager)GetService(typeof(SVsResourceManager));
			if (resourceManager == null)
			{
				throw new InvalidOperationException("Could not get SVsResourceManager service. Make sure the package is Sited before calling this method");
			}
			Guid packageGuid = this.GetType().GUID;
			int hr = resourceManager.LoadResourceString(ref packageGuid, -1, resourceName, out resourceValue);
			Microsoft.VisualStudio.ErrorHandler.ThrowOnFailure(hr);
			return resourceValue;
		}

	}
}
