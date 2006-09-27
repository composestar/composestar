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

        // Dit moet weg, maar dan moeten alle native queries wel optimized worden
        com.db4o.query.Query Query();
    }
}
