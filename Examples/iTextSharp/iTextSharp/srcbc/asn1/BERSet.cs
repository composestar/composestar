using System.Collections;

namespace org.bouncycastle.asn1
{
    public class BERSet
        : DERSet
    {
        /**
         * create an empty sequence
         */
        public BERSet()
        {
        }
    
        /**
         * create a set containing one object
         */
        public BERSet(ASN1Encodable obj) : base(obj)
        {
        }

        /**
         * create a set containing a vector of objects.
         */
        public BERSet(ASN1EncodableVector v) : base(v)
        {
        }

        internal BERSet(ASN1EncodableVector v, bool needsSorting) : base(v, needsSorting)
        {
        }

        /*
         */
        internal override void encode(
            DEROutputStream derOut)
        {
            if (derOut is ASN1OutputStream || derOut is BEROutputStream)
            {
                derOut.WriteByte((byte)(ASN1Tags.SET | ASN1Tags.CONSTRUCTED));
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
