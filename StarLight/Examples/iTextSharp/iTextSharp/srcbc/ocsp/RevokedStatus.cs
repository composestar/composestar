#region Using directives

using System;
using System.Text;
using org.bouncycastle.asn1.ocsp;

#endregion

namespace org.bouncycastle.ocsp
{
    public class RevokedStatus : CertificateStatus
    {
        public RevokedInfo info = null;

        public RevokedStatus(RevokedInfo info)
        {
            this.info = info;
        }

        public DateTime getRevocationTime()
        {
            return info.getRevocationTime().toDateTime();
        }

        public bool hasRevocationReason()
        {
            return (info.getRevocationReason() != null);
        }
        
        /// <summary>
        /// Return the revocation reason. Note: this field is optional, test for it with hasRevocationReason() first.
        /// </summary>
        /// <returns>A reason code.</returns>
        /// <exception cref="System.InvalidOperationException">If there is no reason code.</exception>
        public int getRevocationReason()
        {
            if (info.getRevocationReason() == null)
            {
                throw new InvalidOperationException("attempt to get a reason where none is available");
            }

            return info.getRevocationReason().getValue().intValue();
        }



    }
}
