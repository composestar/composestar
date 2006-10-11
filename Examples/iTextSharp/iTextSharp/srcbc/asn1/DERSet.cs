using System;
using System.Collections;
using System.IO;

namespace org.bouncycastle.asn1
{
    /**
     * A DER encoded set object
     */
    public class DERSet
        : ASN1Set
    {
        /**
         * create an empty set
         */
        public DERSet()
        {
        }
    
        /**
         * @param obj - a single object that makes up the set.
         */
        public DERSet(
            ASN1Encodable   obj)
        {
            this.addObject(obj);
        }
    
        /**
         * @param v - a vector of objects making up the set.
         */
        public DERSet(
            ASN1EncodableVector   v) : this(v, true)
        {
        }
    
        internal DERSet(
            ASN1EncodableVector   v,
            bool		  needsSorting)
        {
            for (int i = 0; i != v.size(); i++)
            {
                this.addObject(v.get(i));
            }

            if (needsSorting)
            {
                this.sort();
            }
        }
    
        /*
         * A note on the implementation:
         * <p>
         * As DER requires the constructed, definite-length model to
         * be used for structured types, this varies slightly from the
         * ASN.1 descriptions given. Rather than just outputing SET,
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

            derOut.writeEncoded(ASN1Tags.SET | ASN1Tags.CONSTRUCTED, bytes);
        }
    }
}
