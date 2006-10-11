using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.ocsp
{
    public class ResponseBytes
        : ASN1Encodable
    {
        DERObjectIdentifier    responseType;
        ASN1OctetString        response;
    
        public ResponseBytes(
            DERObjectIdentifier responseType,
            ASN1OctetString     response)
        {
            this.responseType = responseType;
            this.response = response;
        }
    
        public ResponseBytes(
            ASN1Sequence    seq)
        {
            responseType = (DERObjectIdentifier)seq.getObjectAt(0);
            response = (ASN1OctetString)seq.getObjectAt(1);
        }
    
        public static ResponseBytes getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static ResponseBytes getInstance(
            object  obj)
        {
            if (obj == null || obj is ResponseBytes)
            {
                return (ResponseBytes)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new ResponseBytes((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public DERObjectIdentifier getResponseType()
        {
            return responseType;
        }
    
        public ASN1OctetString getResponse()
        {
            return response;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * ResponseBytes ::=       SEQUENCE {
         *     responseType   OBJECT IDENTIFIER,
         *     response       OCTET STRING }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector    v = new ASN1EncodableVector();
    
            v.add(responseType);
            v.add(response);
    
            return new DERSequence(v);
        }
    }
}
