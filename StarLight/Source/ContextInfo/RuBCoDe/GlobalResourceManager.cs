using System;
using System.Collections.Generic;
using System.Text;
using System.Diagnostics;
using System.Runtime.Serialization;
using System.Runtime.CompilerServices;

namespace Composestar.StarLight.ContextInfo.RuBCoDe
{
    /// <summary>
    /// Keeps record of global message resources
    /// </summary>
    [DebuggerNonUserCode()]
    public sealed class GlobalResourceManager
    {
        private static Object locker = new Object();
        private static GlobalResourceManager _instance;

        private Dictionary<long, SingleResourceBK> objmapping;
        private ObjectIDGenerator idgen;

        /// <summary>
        /// Get a bookkeeper for the specified opbject
        /// </summary>
        /// <returns></returns>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public static SingleResourceBK getBookKeeper(Object obj)
        {
            if (obj == null)
            {
                throw new ArgumentNullException("obj");
            }
            return instance()._getBookKeeper(obj);
        }

        /// <summary>
        /// Get the instance of the resource validator
        /// </summary>
        /// <returns></returns>
        public static GlobalResourceManager instance()
        {
            if (_instance == null)
            {
                lock (locker)
                {
                    if (_instance == null)
                    {
                        _instance = new GlobalResourceManager();
                    }
                }
            }
            return _instance;
        }

        private GlobalResourceManager()
        {
            objmapping = new Dictionary<long, SingleResourceBK>();
            idgen = new ObjectIDGenerator();
        }

        private SingleResourceBK _getBookKeeper(Object obj)
        {
            bool isNew;
            long objid = idgen.GetId(obj, out isNew);
            SingleResourceBK result;
            if (!objmapping.TryGetValue(objid, out result))
            {
                result = new SingleResourceBK(ResourceType.ArgumentEntry, String.Format("{0}#{1}", obj.GetType().Name, objid));
                Console.Error.WriteLine("Created bookkeeper {0} for {1}", result.Name, obj.ToString());
                objmapping.Add(objid, result);
            }
            return result;
        }
    }
}
