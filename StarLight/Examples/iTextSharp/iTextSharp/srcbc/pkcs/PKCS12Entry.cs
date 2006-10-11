using System;
using System.Collections;

using org.bouncycastle.asn1;

namespace org.bouncycastle.pkcs
{
	public class PKCS12Entry
    {
        Hashtable attributes;

        internal PKCS12Entry(
            Hashtable attributes)
        {
            this.attributes = attributes;
        }

        public ASN1Encodable getBagAttribute(
            DERObjectIdentifier oid)
        {
            return (ASN1Encodable)this.attributes[oid.getId()];
        }

        public ASN1Encodable getBagAttribute(
            String oid)
        {
            return (ASN1Encodable)this.attributes[oid];
        }

        public IEnumerator getBagAttributeKeys()
        {
            return this.attributes.Keys.GetEnumerator();
        }
    }
}