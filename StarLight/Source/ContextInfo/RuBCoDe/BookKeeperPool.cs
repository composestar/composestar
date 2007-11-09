using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.CompilerServices;

namespace Composestar.StarLight.ContextInfo.RuBCoDe
{
    /// <summary>
    /// Manages a pool of free bookkeeper instances
    /// </summary>
    public class BookKeeperPool
    {
        private static Object locker = new Object();
        private static BookKeeperPool _instance;

        private Queue<LocalBookKeeper> freeLocal;

        /// <summary>
        /// Returns a free LocalBookKeeper instance
        /// </summary>
        /// <returns></returns>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public static LocalBookKeeper getLocalBK()
        {
            BookKeeperPool ins = instance();
            if (ins.freeLocal.Count > 0)
            {
                return ins.freeLocal.Dequeue();
            }
            return new LocalBookKeeper();
        }

        /// <summary>
        /// Adds the local bookkeeper to the pool
        /// </summary>
        /// <param name="bk"></param>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public static void releaseLocalBK(LocalBookKeeper bk)
        {
            bk.reset();
            instance().freeLocal.Enqueue(bk);
        }

        private static BookKeeperPool instance()
        {
            if (_instance == null)
            {
                lock (locker)
                {
                    if (_instance == null)
                    {
                        _instance = new BookKeeperPool();
                    }
                }
            }
            return _instance;
        }

        private BookKeeperPool()
        {
            freeLocal = new Queue<LocalBookKeeper>();
        }
    }
}
