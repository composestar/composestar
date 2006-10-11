using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.ocsp
{
    public class OCSPRequest
        : ASN1Encodable
    {
        TBSRequest      tbsRequest;
        Signature       optionalSignature;
    
        public OCSPRequest(
            TBSRequest  tbsRequest,
            Signature   optionalSignature)
        {
            this.tbsRequest = tbsRequest;
            this.optionalSignature = optionalSignature;
        }
    
        public OCSPRequest(
            ASN1Sequence    seq)
        {
            tbsRequest = TBSRequest.getInstance(seq.getObjectAt(0));
    
            if (seq.size() == 2)
            {
                optionalSignature = Signature.getInstance(
                                    (ASN1TaggedObject)seq.getObjectAt(1), true);
            }
        }
        
        public static OCSPRequest getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static OCSPRequest getInstance(
            object  obj)
        {
            if (obj == null || obj is OCSPRequest)
            {
                return (OCSPRequest)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new OCSPRequest((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
        
        public TBSRequest getTbsRequest()
        {
            return tbsRequest;
        }
    
        public Signature getOptionalSignature()
        {
            return optionalSignature;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * OCSPRequest     ::=     SEQUENCE {
         *     tbsRequest                  TBSRequest,
         *     optionalSignature   [0]     EXPLICIT Signature OPTIONAL }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector    v = new ASN1EncodableVector();
    
            v.add(tbsRequest);
    
            if (optionalSignature != null)
            {
                v.add(new DERTaggedObject(true, 0, optionalSignature));
            }
    
            return new DERSequence(v);
        }
    }
}
