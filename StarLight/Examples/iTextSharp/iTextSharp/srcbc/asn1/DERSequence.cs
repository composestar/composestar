using System.Collections;
using System.IO;

namespace org.bouncycastle.asn1
{
    public class DERSequence
        : ASN1Sequence
    {
        /**
         * create an empty sequence
         */
        public DERSequence()
        {
        }
    
        /**
         * create a sequence containing one object
         */
        public DERSequence(
            ASN1Encodable    obj)
        {
            this.addObject(obj);
        }
    
        /**
         * create a sequence containing a vector of objects.
         */
        public DERSequence(
            ASN1EncodableVector   v)
        {
            for (int i = 0; i != v.size(); i++)
            {
                this.addObject(v.get(i));
            }
        }
    
        /*
         * A note on the implementation:
         * <p>
         * As DER requires the constructed, definite-length model to
         * be used for structured types, this varies slightly from the
         * ASN.1 descriptions given. Rather than just outputing SEQUENCE,
         * we also have to specify CONSTRUCTED, and the objects length.
         */
        internal override void encode(
            DEROutputStream derOut)
        {
            MemoryStream   bOut = new MemoryStream();
            DEROutputStream         dOut = new DEROutputStream(bOut);
            IEnumerator             e = this.getObjects();
    
            while (e.MoveNext())
            {
                object    obj = e.Current;
    
                dOut.writeObject(obj);
            }
    
            dOut.Close();
    
            byte[]  bytes = bOut.ToArray();
    
            derOut.writeEncoded(ASN1Tags.SEQUENCE | ASN1Tags.CONSTRUCTED, bytes);
        }
    }
}
