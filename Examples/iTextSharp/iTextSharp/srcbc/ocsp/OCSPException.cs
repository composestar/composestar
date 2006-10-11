#region Using directives

using System;
using System.Text;

#endregion

namespace org.bouncycastle.ocsp
{
    public class OCSPException : Exception
    {
        public OCSPException(string msg): base(msg)
        {

        }

        public OCSPException(String msg, Exception ex):base(msg,ex)
        {

        }
    }
}
