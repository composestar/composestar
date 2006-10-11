#region Using directives

using System;
using System.Text;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.asn1.ocsp;

#endregion

namespace org.bouncycastle.ocsp
{
    public class Req
    {
        private Request req;

        public Req(Request req)
        {
            this.req = req;
        }

        public CertificateID getCertID()
        {
            return new CertificateID(req.getReqCert());
        }

        public X509Extensions getSingleRequestExtensions()
        {
            return req.getSingleRequestExtensions();
        }

    }
}
