#region Using directives

using System;
using System.Text;
using org.bouncycastle.asn1.ocsp;
using org.bouncycastle.asn1.x509;

#endregion

namespace org.bouncycastle.ocsp
{
    public class SingleResp
    {
        SingleResponse resp;

        public SingleResp(
            SingleResponse resp)
        {
            this.resp = resp;
        }

        public CertificateID getCertID()
        {
            return new CertificateID(resp.getCertID());
        }

        public Object getCertStatus()
        {
            CertStatus s = resp.getCertStatus();

            if (s.getTagNo() == 0)
            {
                return null;            // good
            }
            else if (s.getTagNo() == 1)
            {
                return new RevokedStatus(RevokedInfo.getInstance(s.getStatus()));
            }

            return new UnknownStatus();
        }

        public DateTime getThisUpdate()
        {
            return resp.getThisUpdate().toDateTime();
        }

       
        public DateTime getNextUpdate()
        {
            if (resp.getNextUpdate() == null)
            {
                throw new OCSPException("Next update time has not been specified.");
            }
            return resp.getNextUpdate().toDateTime();
        }

        public X509Extensions getSingleExtensions()
        {
            return resp.getSingleExtensions();
        }

    }
}
