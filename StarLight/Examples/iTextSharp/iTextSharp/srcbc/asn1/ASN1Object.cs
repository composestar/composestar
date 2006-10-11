using System;

namespace org.bouncycastle.asn1
{
    public abstract class ASN1Object : ASN1Encodable
    {
        public override ASN1Object toASN1Object()
        {
            return this;
        }

        internal abstract void encode(DEROutputStream derOut);
    }
}
