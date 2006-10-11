using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;

using System;

namespace org.bouncycastle.asn1.ocsp
{
    public class Request
        : ASN1Encodable
    {
        CertID            reqCert;
        X509Extensions    singleRequestExtensions;
    
        public Request(
            CertID          reqCert,
            X509Extensions  singleRequestExtensions)
        {
            this.reqCert = reqCert;
            this.singleRequestExtensions = singleRequestExtensions;
        }
    
        public Request(
            ASN1Sequence    seq)
        {
            reqCert = CertID.getInstance(seq.getObjectAt(0));
    
            if (seq.size() == 2)
            {
                singleRequestExtensions = X509Extensions.getInstance(
                                    (ASN1TaggedObject)seq.getObjectAt(1), true);
            }
        }
    
        public static Request getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static Request getInstance(
            object  obj)
        {
            if (obj == null || obj is Request)
            {
                return (Request)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new Request((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public CertID getReqCert()
        {
            return reqCert;
        }
    
        public X509Extensions getSingleRequestExtensions()
        {
            return singleRequestExtensions;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * Request         ::=     SEQUENCE {
         *     reqCert                     CertID,
         *     singleRequestExtensions     [0] EXPLICIT Extensions OPTIONAL }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector    v = new ASN1EncodableVector();
    
            v.add(reqCert);
    
            if (singleRequestExtensions != null)
            {
                v.add(new DERTaggedObject(true, 0, singleRequestExtensions));
            }
    
            return new DERSequence(v);
        }
    }
}
