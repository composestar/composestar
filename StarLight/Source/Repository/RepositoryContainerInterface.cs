using System;
using System.Collections.Generic;
using System.Text;

using com.db4o;

namespace Composestar.Repository
{
    public interface RepositoryContainerInterface
    {
        bool DebugMode {get; set;}

        void OpenContainer(String fileName);
        void CloseContainer();

        void StoreObject(Object o);

        IList<T> GetObjects<T>();
        
        IList<T> GetObjectQuery<T>(Predicate<T> match);

        void DeleteObjects<T>();
        void DeleteObjects<T>(Predicate<T> match);

        void Commit();
    }
}
