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
using Microsoft.Practices.ObjectBuilder;
using System;
using System.Collections.Generic;
using System.ComponentModel.Design;
using System.Diagnostics;
using System.Text;
#endregion

namespace Composestar.StarLight.CoreServices
{

	/// <summary>
	/// Support class for the ObjectBuilder.
	/// </summary>
	public static class DIHelper
	{
		/// <summary>
		/// Creates the object.
		/// </summary>
		/// <param name="serviceProvider">The service provider.</param>
		/// <returns></returns>
		[System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1004")]
		public static TServiceType CreateObject<TServiceType>(IServiceProvider serviceProvider)
		{
			IBuilderConfigurator<BuilderStage> configurator = (IBuilderConfigurator<BuilderStage>)serviceProvider.GetService(typeof(IBuilderConfigurator<BuilderStage>));
			Builder builder = (configurator == null) ? new Builder() : new Builder(configurator);
			return builder.BuildUp<TServiceType>(new ServiceContainerLocator(serviceProvider), null, null);
		}

		#region nested class ServiceContainerLocator

		/// <summary>
		/// Locator implementation.
		/// </summary>
		private class ServiceContainerLocator : Locator
		{
			/// <summary>
			/// _service container
			/// </summary>
			private IServiceContainer _serviceContainer;

			/// <summary>
			/// Initializes a new instance of the <see cref="T:ServiceContainerLocator"/> class.
			/// </summary>
			/// <param name="serviceProvider">The service provider.</param>
			public ServiceContainerLocator(IServiceProvider serviceProvider)
			{
				_serviceContainer = serviceProvider.GetService(typeof(IServiceContainer)) as IServiceContainer;

				Debug.Assert(_serviceContainer != null);
				if (_serviceContainer.GetService(typeof(ILifetimeContainer)) == null)
				{
					_serviceContainer.AddService(typeof(ILifetimeContainer), new LifetimeContainer());
				}
			}

			/// <summary>
			/// See <see cref="M:Microsoft.Practices.ObjectBuilder.IReadableLocator.Get(System.Object,Microsoft.Practices.ObjectBuilder.SearchMode)"/> for more information.
			/// </summary>
			/// <param name="key"></param>
			/// <param name="options"></param>
			/// <returns></returns>
			public override object Get(object key, SearchMode options)
			{
				Type serviceType = GetTypeFromKey(key);
				if (serviceType == typeof(IServiceProvider)) return _serviceContainer;
				return _serviceContainer.GetService(serviceType);
			}

			/// <summary>
			/// See <see cref="M:Microsoft.Practices.ObjectBuilder.IReadWriteLocator.Add(System.Object,System.Object)"/> for more information.
			/// </summary>
			/// <param name="key"></param>
			/// <param name="value"></param>
			public override void Add(object key, object value)
			{
				Type serviceType = GetTypeFromKey(key);

				_serviceContainer.AddService(serviceType, value);
			}

			/// <summary>
			/// See <see cref="M:Microsoft.Practices.ObjectBuilder.IReadableLocator.Contains(System.Object,Microsoft.Practices.ObjectBuilder.SearchMode)"/> for more information.
			/// </summary>
			/// <param name="key"></param>
			/// <param name="options"></param>
			/// <returns></returns>
			public override bool Contains(object key, SearchMode options)
			{
				return (null != Get(key, options));
			}

			/// <summary>
			/// See <see cref="M:Microsoft.Practices.ObjectBuilder.IReadWriteLocator.Remove(System.Object)"/> for more information.
			/// </summary>
			/// <param name="key"></param>
			/// <returns></returns>
			public override bool Remove(object key)
			{
				/// <summary>
				/// Get type from key
				/// </summary>
				/// <param name="key">Key</param>
				/// <returns>Type</returns>
				Type serviceType = GetTypeFromKey(key);
				_serviceContainer.RemoveService(serviceType);

				return true;
			}

			/// <summary>
			/// See <see cref="P:Microsoft.Practices.ObjectBuilder.IReadableLocator.ReadOnly"/> for more information.
			/// </summary>
			/// <value></value>
			public override bool ReadOnly
			{
				get { return false; }
			}

			/// <summary>
			/// Gets the type from key.
			/// </summary>
			/// <param name="key">The key.</param>
			/// <returns></returns>
			[System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Performance", "CA1800")]
			[System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Performance", "CA1822")]
			private Type GetTypeFromKey(object key)
			{
				DependencyResolutionLocatorKey locationKey = key as DependencyResolutionLocatorKey;
				if (locationKey != null)
				{
					return locationKey.Type;
				}
				if (key is Type)
				{
					return key as Type;
				}
				return null;
			}
		}
		#endregion
	}
}
