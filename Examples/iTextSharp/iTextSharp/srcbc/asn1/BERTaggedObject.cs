using System;
using System.Collections;

namespace org.bouncycastle.asn1
{
    /**
     * BER TaggedObject - in ASN.1 nottation this is any object proceeded by
     * a [n] where n is some number - these are assume to follow the construction
     * rules (as with sequences).
     */
    public class BERTaggedObject
        : DERTaggedObject
    {
        /**
         * @param tagNo the tag number for this object.
         * @param obj the tagged object.
         */
        public BERTaggedObject(
            int             tagNo,
            ASN1Encodable   obj)
             : base(tagNo, obj)
        {
            
        }
    
        /**
         * @param explicit true if an explicitly tagged object.
         * @param tagNo the tag number for this object.
         * @param obj the tagged object.
         */
        public BERTaggedObject(
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
        public BERTaggedObject(int tagNo) : base(false, tagNo, new BERSequence())
        {
        }
    
        internal override void encode(
            DEROutputStream  derOut)
        {
            if (derOut is ASN1OutputStream || derOut is BEROutputStream)
            {
                derOut.WriteByte((byte)(((int)(ASN1Tags.CONSTRUCTED | ASN1Tags.TAGGED)) | tagNo));
                derOut.WriteByte(0x80);
    
                if (!empty)
                {
                    if (!explicitly)
                    {
                        if (obj is ASN1OctetString)
                        {
                            IEnumerator  e;
    
                            if (obj is BEROctetString)
                            {
                                e = ((BEROctetString)obj).getObjects();
                            }
                            else
                            {
                                ASN1OctetString             octs = (ASN1OctetString)obj;
                                BEROctetString   berO = new BEROctetString(octs.getOctets());
    
                                e = berO.getObjects();
                            }
    
                            while (e.MoveNext())
                            {
                                derOut.writeObject(e.Current);
                            }
                        }
                        else if (obj is ASN1Sequence)
                        {
                            IEnumerator  e = ((ASN1Sequence)obj).getObjects();
    
                            while (e.MoveNext())
                            {
                                derOut.writeObject(e.Current);
                            }
                        }
                        else if (obj is ASN1Set)
                        {
                            IEnumerator  e = ((ASN1Set)obj).getObjects();
    
                            while (e.MoveNext())
                            {
                                derOut.writeObject(e.Current);
                            }
                        }
                        else
                        {
                            throw new Exception("not implemented: " + obj.GetType().Name);
                        }
                    }
                    else
                    {
                        derOut.writeObject(obj);
                    }
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
