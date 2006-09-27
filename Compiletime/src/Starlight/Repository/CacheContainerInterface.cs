using System;
using System.Collections.Generic;
using System.Text;

namespace Composestar.Repository
{
    public interface CacheContainerInterface
    {
        string CacheFolder{ get; set; }

        void CloseContainer();

        IList<T> GetObjectQuery<T>(Predicate<T> match, String AFQN);
    }
}
