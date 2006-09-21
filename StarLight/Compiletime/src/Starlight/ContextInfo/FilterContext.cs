using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using System.Runtime.CompilerServices;

namespace Composestar.StarLight.ContextInfo
{
    /// <summary>
    /// 
    /// 
    /// </summary>
    public sealed class FilterContext
    {

        private static Dictionary<int, InnerFilterContext> _innercalls;

        /// <summary>
        /// Initializes a new instance of the <see cref="T:FilterContext"/> class.
        /// </summary>
        static FilterContext()
        {
            _innercalls = new Dictionary<int, InnerFilterContext>();
        }

        /// <summary>
        /// Determines whether [is inner call] [the specified current instance].
        /// </summary>
        /// <param name="currentInstance">The current instance.</param>
        /// <param name="methodId">The method id.</param>
        /// <returns>
        /// 	<c>true</c> if [is inner call] [the specified current instance]; otherwise, <c>false</c>.
        /// </returns>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public static bool IsInnerCall(object currentInstance, long methodId)
        {

            InnerFilterContext ifc = GetInnerFilterContext();
            if (ifc == null)
                return false;
            else
                return (ifc.Instance.Equals(currentInstance) & ifc.MethodId == methodId);

        }

        /// <summary>
        /// Sets the inner call data.
        /// </summary>
        /// <param name="currentInstance">The current instance.</param>
        /// <param name="methodId">The method id.</param>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public static void SetInnerCall(object currentInstance, long methodId)
        {
            InnerFilterContext ifc = new InnerFilterContext(currentInstance, methodId);
            if (_innercalls.ContainsKey(GetThreadId()))
                _innercalls[GetThreadId()] = ifc;
            else
                _innercalls.Add(GetThreadId(), ifc);
        }

        /// <summary>
        /// Resets the inner call.
        /// </summary>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public static void ResetInnerCall()
        {
            _innercalls.Remove(GetThreadId());
        }
                
        /// <summary>
        /// Gets the inner filter context.
        /// </summary>
        /// <returns></returns>
        private static InnerFilterContext GetInnerFilterContext()
        {
            int threadId = GetThreadId();

            InnerFilterContext ifc;
            if (_innercalls.TryGetValue(threadId, out ifc))
                return ifc;
            else
                return null;
        }

        /// <summary>
        /// Gets the thread id.
        /// </summary>
        /// <returns></returns>
        private static int GetThreadId()
        {
            return Thread.CurrentThread.ManagedThreadId;
        }

        /// <summary>
        /// Internal class used to contain the FilterContext
        /// </summary>
        private sealed class InnerFilterContext
        {

            /// <summary>
            /// Initializes a new instance of the <see cref="T:InnerFilterContext"/> class.
            /// </summary>
            /// <param name="instance">The instance.</param>
            /// <param name="methodId">The method id.</param>
            public InnerFilterContext(object instance, long methodId)
            {
                _instance = instance;
                _methodId = methodId;
            }

            private long _methodId;


            /// <summary>
            /// Gets or sets the method id.
            /// </summary>
            /// <value>The method id.</value>
            public long MethodId
            {
                get { return _methodId; }
                set { _methodId = value; }
            }

            private object _instance;

            /// <summary>
            /// Gets or sets the instance.
            /// </summary>
            /// <value>The instance.</value>
            public object Instance
            {
                get { return _instance; }
                set { _instance = value; }
            }
        }
    }
}
