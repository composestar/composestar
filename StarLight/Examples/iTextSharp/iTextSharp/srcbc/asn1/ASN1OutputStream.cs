using System;
using System.IO;

namespace org.bouncycastle.asn1
{
    public class ASN1OutputStream
        : DEROutputStream
    {
        public ASN1OutputStream(Stream os) : base(os)
        {
        }
    
        public override void writeObject(
            object    obj)
        {
            if (obj == null)
            {
                writeNull();
            }
            else if (obj is ASN1Object)
            {
                ((ASN1Object)obj).encode(this);
            }
            else if (obj is ASN1Encodable)
            {
                ((ASN1Encodable)obj).toASN1Object().encode(this);
            }
            else
            {
                throw new IOException("object not ASN1Encodable");
            }
        }
    }
}
