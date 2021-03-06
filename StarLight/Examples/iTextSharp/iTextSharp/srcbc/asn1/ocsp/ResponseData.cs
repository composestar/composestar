using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;

using System;

namespace org.bouncycastle.asn1.ocsp
{
    public class ResponseData
        : ASN1Encodable
    {
        private DERInteger          version;
        private ResponderID         responderID;
        private DERGeneralizedTime  producedAt;
        private ASN1Sequence        responses;
        private X509Extensions      responseExtensions;
    
        public ResponseData(
            DERInteger          version,
            ResponderID         responderID,
            DERGeneralizedTime  producedAt,
            ASN1Sequence        responses,
            X509Extensions      responseExtensions)
        {
            this.version = version;
            this.responderID = responderID;
            this.producedAt = producedAt;
            this.responses = responses;
            this.responseExtensions = responseExtensions;
        }
    
        public ResponseData(
            ASN1Sequence    seq)
        {
            int index = 0;
    
            if (seq.getObjectAt(0) is ASN1TaggedObject)
            {
                ASN1TaggedObject    o = (ASN1TaggedObject)seq.getObjectAt(0);
    
                if (o.getTagNo() == 0)
                {
                    this.version = DERInteger.getInstance(
                                    (ASN1TaggedObject)seq.getObjectAt(0), true);
                    index++;
                }
                else
                {
                    this.version = null;
                }
            }
            else
            {
                this.version = null;
            }
    
            this.responderID = ResponderID.getInstance(seq.getObjectAt(index++));
            this.producedAt = (DERGeneralizedTime)seq.getObjectAt(index++);
            this.responses = (ASN1Sequence)seq.getObjectAt(index++);
    
            if (seq.size() > index)
            {
                this.responseExtensions = X509Extensions.getInstance(
                                    (ASN1TaggedObject)seq.getObjectAt(index), true);
            }
        }
    
        public static ResponseData getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static ResponseData getInstance(
            object  obj)
        {
            if (obj == null || obj is ResponseData)
            {
                return (ResponseData)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new ResponseData((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public DERInteger getVersion()
        {
            return version;
        }
    
        public ResponderID getResponderID()
        {
            return responderID;
        }
    
        public DERGeneralizedTime getProducedAt()
        {
            return producedAt;
        }
    
        public ASN1Sequence getResponses()
        {
            return responses;
        }
    
        public X509Extensions getResponseExtensions()
        {
            return responseExtensions;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * ResponseData ::= SEQUENCE {
         *     version              [0] EXPLICIT Version DEFAULT v1,
         *     responderID              ResponderID,
         *     producedAt               GeneralizedTime,
         *     responses                SEQUENCE OF SingleResponse,
         *     responseExtensions   [1] EXPLICIT Extensions OPTIONAL }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector v = new ASN1EncodableVector();
    
            if (version != null)
            {
                v.add(new DERTaggedObject(true, 0, new DERInteger(0)));
            }
    
            v.add(responderID);
            v.add(producedAt);
            v.add(responses);
            if (responseExtensions != null)
            {
                v.add(new DERTaggedObject(true, 1, responseExtensions));
            }
    
            return new DERSequence(v);
        }
    }
}
