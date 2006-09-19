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

        // TODO Not complete yet

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
        /// <param name="methodSignature">The method signature.</param>
        /// <returns>
        /// 	<c>true</c> if [is inner call] [the specified current instance]; otherwise, <c>false</c>.
        /// </returns>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public static bool IsInnerCall(object currentInstance, string methodSignature)
        {

            InnerFilterContext ifc = GetInnerFilterContext();
            if (ifc == null)
                return false;
            else
                return (ifc.Instance.Equals(currentInstance) & ifc.MethodSignature.Equals(methodSignature));

        }

        public static void SetInnerCall(object currentInstance, string methodSignature)
        {
            InnerFilterContext ifc =new InnerFilterContext(currentInstance, methodSignature);
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

        private sealed class InnerFilterContext
        {

            /// <summary>
            /// Initializes a new instance of the <see cref="T:InnerFilterContext"/> class.
            /// </summary>
            /// <param name="instance">The instance.</param>
            /// <param name="methodSignature">The method signature.</param>
            public InnerFilterContext(object instance, string methodSignature)
            {
                _instance = instance;
                _methodSignature = methodSignature;
            }

            private string _methodSignature;

            /// <summary>
            /// Gets or sets the name of the method.
            /// </summary>
            /// <value>The name of the method.</value>
            public string MethodSignature
            {
                get { return _methodSignature; }
                set { _methodSignature = value; }
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
