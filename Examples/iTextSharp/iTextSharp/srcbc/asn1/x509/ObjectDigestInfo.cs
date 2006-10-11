using System;

using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.x509
{
    public class ObjectDigestInfo
        : ASN1Encodable
    {
        internal DEREnumerated       digestedObjectType = null;
        internal DERObjectIdentifier otherObjectTypeID = null;
        internal AlgorithmIdentifier digestAlgorithm = null;
        internal DERBitString        objectDigest = null;
    
    
        public static ObjectDigestInfo getInstance(
            object  obj)
        {
            if (obj == null || obj is ObjectDigestInfo)
            {
                return (ObjectDigestInfo)obj;
            }
    
            if (obj is ASN1Sequence)
            {
                return new ObjectDigestInfo((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("illegal object in getInstance: " + obj);
        }
    
        public static ObjectDigestInfo getInstance(
            ASN1TaggedObject obj,
            bool             isExplicit)
        {
            return getInstance(ASN1Sequence.getInstance(obj, isExplicit));
        }
        
        public ObjectDigestInfo(ASN1Sequence seq)
        {
            digestedObjectType = DEREnumerated.getInstance(seq.getObjectAt(0));
    
            int offset = 0;
    
            if (seq.size() == 4)
            {
                otherObjectTypeID = DERObjectIdentifier.getInstance(seq.getObjectAt(1));
                offset++;
            }
    
            digestAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(1 + offset));
    
            objectDigest = new DERBitString(seq.getObjectAt(2 + offset));
        }

        public DEREnumerated getDigestedObjectType()
        {
            return digestedObjectType;
        }
    
        public DERObjectIdentifier getOtherObjectTypeID()
        {
            return otherObjectTypeID;
        }
    
        public AlgorithmIdentifier getDigestAlgorithm()
        {
            return digestAlgorithm;
        }
    
        public DERBitString getObjectDigest()
        {
            return objectDigest;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         *  ObjectDigestInfo ::= SEQUENCE {
         *       digestedObjectType  ENUMERATED {
         *               publicKey            (0),
         *               publicKeyCert        (1),
         *               otherObjectTypes     (2) },
         *                       -- otherObjectTypes MUST NOT
         *                       -- be used in this profile
         *       otherObjectTypeID   OBJECT IDENTIFIER OPTIONAL,
         *       digestAlgorithm     AlgorithmIdentifier,
         *       objectDigest        BIT STRING
         *  }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(digestedObjectType);
    
            if (otherObjectTypeID != null)
            {
                v.add(otherObjectTypeID);
            }
    
            v.add(digestAlgorithm);
            v.add(objectDigest);
    
            return new DERSequence(v);
        }
    }
}
