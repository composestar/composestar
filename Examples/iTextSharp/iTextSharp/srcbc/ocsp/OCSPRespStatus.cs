#region Using directives

using System;
using System.Text;

#endregion

namespace org.bouncycastle.ocsp
{
    public class OCSPRespStatus
    {
        /// <summary>
        /// Response has valid confirmations.
        /// </summary>
        public static readonly int SUCCESSFUL = 0;
        
        /// <summary>
        /// Illegal confirmation request.
        /// </summary>
        public static readonly int MALFORMED_REQUEST = 1; 
        
        /// <summary>
        /// Internal error in issuer.
        /// </summary>
        public static readonly int INTERNAL_ERROR = 2;
        
        /// <summary>
        /// Try again later.
        /// </summary>
        public static readonly int TRY_LATER = 3;
        
        /// <summary>
        /// Must sign the request.
        /// </summary>
        public static readonly int SIGREQUIRED = 5; 
        
        /// <summary>
        /// Request unauthorized.
        /// </summary>
        public static readonly int UNAUTHORIZED = 6;
          
    }
}
