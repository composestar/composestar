using System;
using System.Collections.Generic;
using System.Text;
using Microsoft.Practices.ObjectBuilder;
using System.Diagnostics;
using System.ComponentModel.Design;

namespace Composestar.StarLight.CoreServices
{
    public static class DIHelper
    {
        public static TServiceType CreateObject<TServiceType>(IServiceProvider serviceProvider)
        {
            IBuilderConfigurator<BuilderStage> configurator = (IBuilderConfigurator<BuilderStage>) serviceProvider.GetService(typeof(IBuilderConfigurator<BuilderStage>));
            Builder builder = (configurator == null) ? new Builder() : new Builder(configurator);
            return builder.BuildUp<TServiceType>(new ServiceContainerLocator(serviceProvider), null, null);
        }

        #region nested class ServiceContainerLocator
        private class ServiceContainerLocator : Locator
        {
            IServiceContainer _serviceContainer;

            public ServiceContainerLocator(IServiceProvider serviceProvider)
            {
                _serviceContainer = serviceProvider.GetService(typeof(IServiceContainer)) as IServiceContainer;

                Debug.Assert(_serviceContainer != null);
                if (_serviceContainer.GetService(typeof(ILifetimeContainer)) == null)
                {
                    _serviceContainer.AddService(typeof(ILifetimeContainer), new LifetimeContainer());
                }
            }

            public override object Get(object key, SearchMode options)
            {
                Type serviceType = GetTypeFromKey(key);
                if (serviceType == typeof(IServiceProvider)) return _serviceContainer;
                return _serviceContainer.GetService(serviceType);
            }

            public override void Add(object key, object value)
            {
                Type serviceType = GetTypeFromKey(key);

                _serviceContainer.AddService(serviceType, value);
            }

            public override bool Contains(object key, SearchMode options)
            {
                return (null != Get(key, options));
            }

            public override bool Remove(object key)
            {
                Type serviceType = GetTypeFromKey(key);
                _serviceContainer.RemoveService(serviceType);

                return true;
            }

            public override bool ReadOnly
            {
                get{return false;}
            }

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
