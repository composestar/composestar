using System;
using System.IO;

namespace org.bouncycastle.asn1
{
    /**
     * DER TaggedObject - in ASN.1 nottation this is any object proceeded by
     * a [n] where n is some number - these are assume to follow the construction
     * rules (as with sequences).
     */
    public class DERTaggedObject
        : ASN1TaggedObject
    {
        /**
         * @param tagNo the tag number for this object.
         * @param obj the tagged object.
         */
        public DERTaggedObject(
            int              tagNo,
            ASN1Encodable    obj)
             : base(tagNo, obj)
        {
        }
    
        /**
         * @param explicit true if an explicitly tagged object.
         * @param tagNo the tag number for this object.
         * @param obj the tagged object.
         */
        public DERTaggedObject(
            bool            explicitly,
            int             tagNo,
            ASN1Encodable   obj)
             : base(explicitly, tagNo, obj)
        {
        }
    
        /**
         * create an implicitly tagged object that contains a zero
         * length sequence.
         */
        public DERTaggedObject(int tagNo) : base(false, tagNo, new DERSequence())
        {
        }
    
        internal override void encode(
            DEROutputStream  derOut)
        {
            if (!empty)
            {
                MemoryStream   bOut = new MemoryStream();
                DEROutputStream         dOut = new DEROutputStream(bOut);
    
                dOut.writeObject(obj);
                dOut.Close();
    
                byte[]  bytes = bOut.ToArray();
    
                if (explicitly)
                {
                    derOut.writeEncoded((int)(ASN1Tags.CONSTRUCTED | ASN1Tags.TAGGED) | tagNo, bytes);
                }
                else
                {
                    //
                    // need to mark constructed types...
                    //
                    if ((bytes[0] & (byte) ASN1Tags.CONSTRUCTED) != 0)
                    {
                        bytes[0] = (byte)((int)(ASN1Tags.CONSTRUCTED | ASN1Tags.TAGGED) | tagNo);
                    }
                    else
                    {
                        bytes[0] = (byte)((int)(ASN1Tags.TAGGED) | tagNo);
                    }
    
                    derOut.Write(bytes, 0, bytes.Length);
                }
            }
            else
            {
                derOut.writeEncoded((int)(ASN1Tags.CONSTRUCTED | ASN1Tags.TAGGED) | tagNo, new byte[0]);
            }
        }
    }
}
