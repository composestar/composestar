using System;
using System.IO;
using System.Collections;

using org.bouncycastle.asn1;
using org.bouncycastle.asn1.pkcs;
using org.bouncycastle.x509;

using org.bouncycastle.crypto;

namespace org.bouncycastle.pkcs
{
    public class X509CertificateEntry
        : PKCS12Entry
    {
        X509Certificate cert;

        public X509CertificateEntry(
            X509Certificate cert) : base(new Hashtable())
        {
            this.cert = cert;
        }

        public X509CertificateEntry(
            X509Certificate cert,
            Hashtable attributes) : base(attributes)
        {
            this.cert = cert;
        }

        public X509Certificate getCertificate()
        {
            return this.cert;
        }
    }
}
