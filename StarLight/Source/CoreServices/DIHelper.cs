using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Practices.ObjectBuilder;
using System.Diagnostics;
using System.ComponentModel.Design;

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
        public static TServiceType CreateObject<TServiceType>(IServiceProvider serviceProvider)
        {
            IBuilderConfigurator<BuilderStage> configurator = (IBuilderConfigurator<BuilderStage>) serviceProvider.GetService(typeof(IBuilderConfigurator<BuilderStage>));
            Builder builder = (configurator == null) ? new Builder() : new Builder(configurator);
            return builder.BuildUp<TServiceType>(new ServiceContainerLocator(serviceProvider), null, null);
        }

        #region nested class ServiceContainerLocator

        /// <summary>
        /// Locator implementation.
        /// </summary>
        private class ServiceContainerLocator : Locator
        {
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
                get{return false;}
            }

            /// <summary>
            /// Gets the type from key.
            /// </summary>
            /// <param name="key">The key.</param>
            /// <returns></returns>
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
