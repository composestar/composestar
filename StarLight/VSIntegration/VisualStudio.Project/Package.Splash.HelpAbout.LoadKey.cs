/***************************************************************************

Copyright (c) Microsoft Corporation. All rights reserved.
This code is licensed under the Visual Studio SDK license terms.
THIS CODE IS PROVIDED *AS IS* WITHOUT WARRANTY OF
ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING ANY
IMPLIED WARRANTIES OF FITNESS FOR A PARTICULAR
PURPOSE, MERCHANTABILITY, OR NON-INFRINGEMENT.

***************************************************************************/

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
	/// This sample demonstrates how to put together all the pieces from
	/// the other package classes.
	/// 
	/// The PackageSplashHelpAboutLoadKey class is an extension of the BasicPackage.
	/// 
	/// This package:
	///		- Has a package load key (PLK) to enable loading on end user machines
	///		- Appears in Help About
	///		- Appears on the Splash Screen
	/// </summary>
	[MsVsShell.ProvideLoadKey("standard", "8.0", "ComposeStar StarLight", "University of Twente", 151)]
	[MsVsShell.InstalledProductRegistration(true, null, null, null)]
	[MsVsShell.DefaultRegistryRoot("Software\\Microsoft\\VisualStudio\\8.0Exp")]
	[MsVsShell.PackageRegistration(UseManagedResourcesOnly = true)]
	[Guid("EEE474A0-083B-4e9c-B453-F6FCCEDA2577")]
	public class PackageSplashHelpAboutLoadKey : MsVsShell.Package, IVsInstalledProduct
	{
		/// <summary>
		/// PackageSplashHelpAboutLoadKey contructor.
		/// While we could have used the default constructor, adding the Trace makes it
		/// possible to verify that the package was created without having to set a break
		/// point in the debugger.
		/// </summary>
		public PackageSplashHelpAboutLoadKey()
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
			pbstrName = GetResourceString("@100");
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
