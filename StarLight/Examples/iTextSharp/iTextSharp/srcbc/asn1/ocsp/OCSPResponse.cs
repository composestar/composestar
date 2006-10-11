using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.ocsp
{
    public class OCSPResponse
        : ASN1Encodable
    {
        OCSPResponseStatus    responseStatus;
        ResponseBytes        responseBytes;
    
        public OCSPResponse(
            OCSPResponseStatus  responseStatus,
            ResponseBytes       responseBytes)
        {
            this.responseStatus = responseStatus;
            this.responseBytes = responseBytes;
        }
    
        public OCSPResponse(
            ASN1Sequence    seq)
        {
            responseStatus = new OCSPResponseStatus(
                                DEREnumerated.getInstance(seq.getObjectAt(0)));
    
            if (seq.size() == 2)
            {
                responseBytes = ResponseBytes.getInstance(
                                    (ASN1TaggedObject)seq.getObjectAt(1), true);
            }
        }
    
        public static OCSPResponse getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static OCSPResponse getInstance(
            object  obj)
        {
            if (obj == null || obj is OCSPResponse)
            {
                return (OCSPResponse)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new OCSPResponse((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public OCSPResponseStatus getResponseStatus()
        {
            return responseStatus;
        }
    
        public ResponseBytes getResponseBytes()
        {
            return responseBytes;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * OCSPResponse ::= SEQUENCE {
         *     responseStatus         OCSPResponseStatus,
         *     responseBytes          [0] EXPLICIT ResponseBytes OPTIONAL }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector    v = new ASN1EncodableVector();
    
            v.add(responseStatus);
    
            if (responseBytes != null)
            {
                v.add(new DERTaggedObject(true, 0, responseBytes));
            }
    
            return new DERSequence(v);
        }
    }
}
