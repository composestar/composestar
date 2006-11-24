using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using System.Threading;
using System.Configuration;
using System.Diagnostics.CodeAnalysis;

using Composestar.StarLight.CoreServices.Settings.Providers;

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

		private static Mutex _mu = new Mutex();
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
