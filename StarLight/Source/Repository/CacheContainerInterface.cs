using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.Repository
{
    public interface CacheContainerInterface
    {
        string CacheFolder{ get; set; }

        void CloseContainer();

        bool IsCached(String AFQN);

        IList<T> GetObjectQuery<T>(Predicate<T> match, String AFQN);

        void StoreObject(Object o, String AFQN);
    }
}
