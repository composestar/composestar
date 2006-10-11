using System.Collections;

namespace org.bouncycastle.asn1
{
    public class BERSequence
        : DERSequence
    {
        /**
         * create an empty sequence
         */
        public BERSequence()
        {
        }
    
        /**
         * create a sequence containing one object
         */
        public BERSequence(ASN1Encodable obj) : base(obj)
        {
        }
    
        /**
         * create a sequence containing a vector of objects.
         */
        public BERSequence(ASN1EncodableVector v) : base(v)
        {
        }
    
        /*
         */
        internal override void encode(
            DEROutputStream derOut)
        {
            if (derOut is ASN1OutputStream || derOut is BEROutputStream)
            {
                derOut.WriteByte((byte)(ASN1Tags.SEQUENCE | ASN1Tags.CONSTRUCTED));
                derOut.WriteByte(0x80);
                
                IEnumerator e = getObjects();
                while (e.MoveNext())
                {
                    derOut.writeObject(e.Current);
                }
            
                derOut.WriteByte(0x00);
                derOut.WriteByte(0x00);
            }
            else
            {
                base.encode(derOut);
            }
        }
    }
}
