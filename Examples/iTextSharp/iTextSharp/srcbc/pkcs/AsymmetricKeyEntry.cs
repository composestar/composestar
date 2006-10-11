using System;
using System.IO;
using System.Collections;
using System.Text;

using org.bouncycastle.crypto.parameters;

using org.bouncycastle.asn1;
using org.bouncycastle.asn1.pkcs;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.asn1.x9;
using org.bouncycastle.asn1.sec;
using org.bouncycastle.asn1.oiw;

using org.bouncycastle.crypto;

namespace org.bouncycastle.pkcs
{
    public class AsymmetricKeyEntry
        : PKCS12Entry
    {
        AsymmetricKeyParameter key;

        public AsymmetricKeyEntry(
            AsymmetricKeyParameter key) : base(new Hashtable())
        {
            this.key = key;
        }

        public AsymmetricKeyEntry(
            AsymmetricKeyParameter key,
            Hashtable attributes) : base(attributes)
        {
            this.key = key;
        }

        public AsymmetricKeyParameter getKey()
        {
            return this.key;
        }
    }
}
