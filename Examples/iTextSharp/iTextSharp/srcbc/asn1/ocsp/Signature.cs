using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;

using System;

namespace org.bouncycastle.asn1.ocsp
{
    public class Signature
        : ASN1Encodable
    {
        AlgorithmIdentifier signatureAlgorithm;
        DERBitString        signature;
        ASN1Sequence        certs;
    
        public Signature(
            AlgorithmIdentifier signatureAlgorithm,
            DERBitString        signature)
        {
            this.signatureAlgorithm = signatureAlgorithm;
            this.signature = signature;
        }
    
        public Signature(
            AlgorithmIdentifier signatureAlgorithm,
            DERBitString        signature,
            ASN1Sequence        certs)
        {
            this.signatureAlgorithm = signatureAlgorithm;
            this.signature = signature;
            this.certs = certs;
        }
    
        public Signature(
            ASN1Sequence    seq)
        {
            signatureAlgorithm  = AlgorithmIdentifier.getInstance(seq.getObjectAt(0));
            signature = (DERBitString)seq.getObjectAt(1);
    
            if (seq.size() == 3)
            {
                certs = ASN1Sequence.getInstance(
                                    (ASN1TaggedObject)seq.getObjectAt(2), true);
            }
        }
    
        public static Signature getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static Signature getInstance(
            object  obj)
        {
            if (obj == null || obj is Signature)
            {
                return (Signature)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new Signature((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public AlgorithmIdentifier getSignatureAlgorithm()
        {
            return signatureAlgorithm;
        }
    
        public DERBitString getSignature()
        {
            return signature;
        }
    
        public ASN1Sequence getCerts()
        {
            return certs;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * Signature       ::=     SEQUENCE {
         *     signatureAlgorithm      AlgorithmIdentifier,
         *     signature               BIT STRING,
         *     certs               [0] EXPLICIT SEQUENCE OF Certificate OPTIONAL}
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector    v = new ASN1EncodableVector();
    
            v.add(signatureAlgorithm);
            v.add(signature);
    
            if (certs != null)
            {
                v.add(new DERTaggedObject(true, 0, certs));
            }
    
            return new DERSequence(v);
        }
    }
}
