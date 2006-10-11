#region Using directives

using System;
using System.Collections;
using System.Text;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.asn1;
using System.IO;

#endregion

namespace org.bouncycastle.security
{
    /// <summary>
    /// A class containing methods to interface the BouncyCastle world to the D
    /// </summary>
    public class DotNetUtils
    {
        public DotNetUtils()
        {
            
        }

        /// <summary>
        /// Create an System.Security.Cryptography.X509Certificate from an X509Certificate Structure.
        /// </summary>
        /// <param name="x509struct"></param>
        /// <returns>An System.Security.Cryptography.X509Certificate.</returns>
         public static System.Security.Cryptography.X509Certificates.X509Certificate toX509Certificate(X509CertificateStructure x509struct)
        {
            MemoryStream mStr = new MemoryStream();
            ASN1OutputStream aOut = new ASN1OutputStream(mStr);
            aOut.writeObject(x509struct);
            aOut.Flush();
            mStr.Seek(0, 0);
            return new System.Security.Cryptography.X509Certificates.X509Certificate(mStr.ToArray());
        }
    }
}
