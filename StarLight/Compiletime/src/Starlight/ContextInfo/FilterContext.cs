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

        private Dictionary<int, InnerFilterContext> _innercalls;

        /// <summary>
        /// Initializes a new instance of the <see cref="T:FilterContext"/> class.
        /// </summary>
        public FilterContext()
        {
            _innercalls = new Dictionary<int, InnerFilterContext>();
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        public bool IsInnerCall(object currentInstance, string methodName)
        {
           
                InnerFilterContext ifc = GetInnerFilterContext();
                if (ifc == null)
                    return false;
                else
                    return true;
         
        }

        /// <summary>
        /// Resets the inner call.
        /// </summary>
        [MethodImpl(MethodImplOptions.Synchronized)]
        public void ResetInnerCall()
        {
            _innercalls.Remove(GetThreadId()); 
        }
        
        /// <summary>
        /// Gets the inner filter context.
        /// </summary>
        /// <returns></returns>
        private InnerFilterContext GetInnerFilterContext()
        {
            int threadId = GetThreadId();
 
            InnerFilterContext ifc;
            if (_innercalls.TryGetValue(threadId, out ifc) )
                return ifc;
            else
                return null;
        }

        /// <summary>
        /// Gets the thread id.
        /// </summary>
        /// <returns></returns>
        private int GetThreadId()
        {
            return Thread.CurrentThread.ManagedThreadId;
        }

        private sealed class InnerFilterContext
        {

            private string _methodName;

            /// <summary>
            /// Gets or sets the name of the method.
            /// </summary>
            /// <value>The name of the method.</value>
            public string MethodName
            {
                get { return _methodName; }
                set { _methodName = value; }
            }
	
        }
    }
}
