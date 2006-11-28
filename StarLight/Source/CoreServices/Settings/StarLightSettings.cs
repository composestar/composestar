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
using Composestar.StarLight.CoreServices.Settings.Providers;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Diagnostics.CodeAnalysis;
using System.Text;
using System.Threading;
using System.Xml.Serialization;
#endregion

// Remark; the RegistrySettingsProvider cannot be in the same assembly as this class
// It will throw an exception in the System.Configuration.ApplicationSettingsBase.get_Initializer()
// that it cannot load the class
// Using a simple Type.GetType does load the type, but the ApplicationSettingsBase performs the same command
// and is not able to load the type.
// Placing the class in a different assembly solves this problem

namespace Composestar.StarLight.CoreServices.Settings
{
	/// <summary>
	/// Contains the StarLight specific settings.
	/// </summary>
	[SettingsProvider(typeof(RegistrySettingsProvider))]
	public class StarLightSettings : ApplicationSettingsBase
	{

		#region Settings Handler

		/// <summary>
		/// _mu
		/// </summary>
		private static Mutex _mu = new Mutex();
		/// <summary>
		/// _settings
		/// </summary>
		private static StarLightSettings _settings;

		/// <summary>
		/// Initializes a new instance of the <see cref="T:StarLightSettings"/> class.
		/// </summary>
		public StarLightSettings()
		{

		}

		/// <summary>
		/// Gets the thread safe instance.
		/// </summary>
		/// <value>The instance.</value>
		public static StarLightSettings Instance
		{
			get
			{
				_mu.WaitOne();
				try
				{
					if (_settings == null)
						_settings = new StarLightSettings();
				}
				finally
				{
					_mu.ReleaseMutex();
				}

				return _settings;
			}
		}

		#endregion

		#region Properties

		/// <summary>
		/// Gets or sets the JVM options.
		/// </summary>
		/// <value>The JVM options.</value>
		/// <returns>String</returns>
		[ApplicationScopedSetting(), SettingsSerializeAs(SettingsSerializeAs.String)]
		public String JavaOptions
		{
			get
			{
				return ((String)this["JavaOptions"]);
			}
			set
			{
				this["JavaOptions"] = value;
			}
		}

		/// <summary>
		/// Gets or sets the java location.
		/// </summary>
		/// <value>The java location.</value>
		/// <returns>String</returns>
		[ApplicationScopedSetting(), SettingsSerializeAs(SettingsSerializeAs.String)]
		public string JavaLocation
		{
			get
			{
				return ((String)this["JavaLocation"]);
			}
			set
			{
				this["JavaLocation"] = value;
			}
		}

		/// <summary>
		/// Gets or sets the install folder.
		/// </summary>
		/// <value>The install folder.</value>
		/// <returns>String</returns>
		[ApplicationScopedSetting(), SettingsSerializeAs(SettingsSerializeAs.String)]
		public string StarLightInstallFolder
		{
			get
			{
				return ((String)this["StarLightInstallFolder"]);
			}
			set
			{
				this["StarLightInstallFolder"] = value;
			}
		}

		/// <summary>
		/// Gets or sets the dot NETSDK location.
		/// </summary>
		/// <value>The dot NETSDK location.</value>
		/// <returns>String</returns>
		[SuppressMessage("Microsoft.Naming", "CA1705", Justification = "SDK")]
		[ApplicationScopedSetting(), SettingsSerializeAs(SettingsSerializeAs.String)]
		public string DotNETSDKLocation
		{
			get
			{
				return ((String)this["DotNETSDKLocation"]);
			}
			set
			{
				this["DotNETSDKLocation"] = value;
			}
		}

		/// <summary>
		/// Gets or sets the weave strategies folder location.
		/// </summary>
		/// <value>The weave strategies folder location.</value>
		/// <returns>String</returns>
		[ApplicationScopedSetting(), SettingsSerializeAs(SettingsSerializeAs.String)]
		public string WeaveStrategiesFolder
		{
			get
			{
				return ((String)this["WeaveStrategiesFolder"]);
			}
			set
			{
				this["WeaveStrategiesFolder"] = value;
			}
		}

		#endregion

	}
}
