#region License
/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * ComposeStar StarLight [http://janus.cs.utwente.nl:8000/twiki/bin/view/StarLight/WebHome]
 * Copyright (C) 2003, University of Twente.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification,are permitted provided that the following conditions
 * are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Twente nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
*/
#endregion

#region Using directives
using Microsoft.Win32;
using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Configuration;
using System.Diagnostics.CodeAnalysis;
using System.Globalization;
using System.Security.Permissions;
using System.Text;
using System.Threading;
using System.Xml.Serialization;
using Composestar.StarLight.Entities.Properties;
#endregion

// Technically; this is not an entity. It is used in the CoreServices for the StarLightSettings class.
// If this class is placed inside the CoreServices assembly, the type cannot be found at runtime. 
// Currently I do not know why. A simply Type.GetType loads the type perfectly, but the ApplicationsSettingsBase,
// which performs the same Type.GetType throws an exception.
//
// So this is a dirty fix, a better solution is advisable.

namespace Composestar.StarLight.CoreServices.Settings.Providers
{

	/// <summary>
	/// The settings provider using the registry.
	/// </summary>
	/// <remarks>
	/// <para>Only supported in Microsoft .NET 2.0</para>
	/// <para>A settings provider defines the mechanism for storing configuration data used in the 
	/// application settings architecture. The .NET Framework contains a single default settings provider, 
	/// LocalFileSettingsProvider, which stores configuration data to the local file system. 
	/// However, you can create alternate storage mechanisms by deriving from the abstract SettingsProvider class. 
	/// The provider that a wrapper class uses is determined by decorating the wrapper class with the 
	/// SettingsProviderAttribute. 
	/// If this attribute is not provided, the default, LocalFileSettingsProvider, is used.</para> 	
	/// </remarks>
	public class RegistrySettingsProvider : SettingsProvider   
	{
		private const string ProviderDescription = "StarLight Registry Settings Provider";
		private const string ProviderApplicationName = "StarLight RegistrySettingsProvider";

		public RegistrySettingsProvider()
		{

		}

		/// <summary>
		/// Initializes the provider.
		/// </summary>
		/// <param name="name">The friendly name of the provider.</param>
		/// <param name="config">A collection of the name/value pairs representing the provider-specific attributes specified in the configuration for this provider.</param>
		/// <exception cref="T:System.ArgumentNullException">The name of the provider is null.</exception>
		/// <exception cref="T:System.InvalidOperationException">An attempt is made to call <see cref="M:System.Configuration.Provider.ProviderBase.Initialize(System.String,System.Collections.Specialized.NameValueCollection)"></see> on a provider after the provider has already been initialized.</exception>
		/// <exception cref="T:System.ArgumentException">The name of the provider has a length of zero.</exception>
		public override void Initialize(string name, NameValueCollection config)
		{
			base.Initialize(this.ApplicationName, config);
		}

		/// <summary>
		/// Gets a brief, friendly description suitable for display in administrative tools or other user interfaces (UIs).
		/// </summary>
		/// <value></value>
		/// <returns>A brief, friendly description suitable for display in administrative tools or other UIs.</returns>
		public override string Description
		{
			get
			{
				return ProviderDescription;
			}
		}

		/// <summary>
		/// Gets or sets the name of the currently running application.
		/// </summary>
		/// <value></value>
		/// <returns>A <see cref="T:System.String"></see> that contains the application's shortened name, which does not contain a full path or extension, for example, SimpleAppSettings.</returns>
		public override string ApplicationName
		{
			get
			{
				return ProviderApplicationName;
			}
			set
			{
				// Do nothing
			}
		}

		/// <summary>
		/// Returns the collection of settings property values for the specified application instance and settings property group.
		/// </summary>
		/// <param name="context">A <see cref="T:System.Configuration.SettingsContext"></see> describing the current application use.</param>
		/// <param name="collection">A <see cref="T:System.Configuration.SettingsPropertyCollection"></see> containing the settings property group whose values are to be retrieved.</param>
		/// <returns>
		/// A <see cref="T:System.Configuration.SettingsPropertyValueCollection"></see> containing the values for the specified settings property group.
		/// </returns>
		[RegistryPermissionAttribute(System.Security.Permissions.SecurityAction.Demand,
		   Read = "HKEY_LOCAL_MACHINE\\Software\\ComposeStar\\StarLight")]
		public override SettingsPropertyValueCollection GetPropertyValues(SettingsContext context, SettingsPropertyCollection collection)
		{
			SettingsPropertyValueCollection values;
			SettingsPropertyValue value;

			if (collection == null)
				throw new ArgumentNullException("collection");
			
			// First, create a place to put all the properties and their values.
			values = new SettingsPropertyValueCollection();

			// Get the path to the current version
			string registryPath = RetrieveCurrentVersionPath();

			if (string.IsNullOrEmpty(registryPath))
				throw new ConfigurationErrorsException(Resources.CouldNotReadRegistryValues);

			// Open the hive with the correct version specific settings
			RegistryKey regKey = Registry.LocalMachine.OpenSubKey(registryPath);

			if (regKey != null)
			{
				foreach (SettingsProperty setting in collection)
				{
					value = new SettingsPropertyValue(setting);
					value.SerializedValue = (string)regKey.GetValue(value.Name, setting.DefaultValue);
					value.IsDirty = false;

					values.Add(value);
				}
			}
			else
			{
				throw new ConfigurationErrorsException(Resources.CouldNotReadRegistryValues);
			}

			return values;
		}

		/// <summary>
		/// Sets the values of the specified group of property settings.
		/// </summary>
		/// <remarks>Saving to the registry is currently not implemented. The installer sets the initial values.</remarks> 
		/// <param name="context">A <see cref="T:System.Configuration.SettingsContext"></see> describing the current application usage.</param>
		/// <param name="collection">A <see cref="T:System.Configuration.SettingsPropertyValueCollection"></see> representing the group of property settings to set.</param>
		public override void SetPropertyValues(SettingsContext context, SettingsPropertyValueCollection collection)
		{
			throw new NotImplementedException(Resources.SavingNotImplemented);
		}

		///// <summary>
		///// Retrieves the current version of starlight and creates a full path to that registry hive.
		///// </summary>
		///// <returns></returns>
		/// <returns>String</returns>
		[RegistryPermissionAttribute(SecurityAction.Demand, Read = "HKEY_LOCAL_MACHINE\\Software\\ComposeStar\\StarLight")]
		private static string RetrieveCurrentVersionPath()
		{
			RegistryKey regKeyVersion = Registry.LocalMachine.OpenSubKey(@"Software\ComposeStar\StarLight");

			string currentversion;

			if (regKeyVersion != null)
			{
				currentversion = (string)regKeyVersion.GetValue("CurrentVersion", "");
			}
			else
			{
				return string.Empty;
			}

			if (string.IsNullOrEmpty(currentversion))
				return string.Empty;

			return String.Format(CultureInfo.CurrentCulture, @"Software\ComposeStar\StarLight\{0}", currentversion);
		}

	}
}
