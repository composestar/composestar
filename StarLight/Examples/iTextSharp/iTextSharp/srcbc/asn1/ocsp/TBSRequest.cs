using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;

using System;

namespace org.bouncycastle.asn1.ocsp
{
    public class TBSRequest
        : ASN1Encodable
    {
        DERInteger      version;
        GeneralName     requestorName;
        ASN1Sequence    requestList;
        X509Extensions  requestExtensions;
    
        public TBSRequest(
            GeneralName     requestorName,
            ASN1Sequence    requestList,
            X509Extensions  requestExtensions)
        {
            this.version = new DERInteger(0);
            this.requestorName = requestorName;
            this.requestList = requestList;
            this.requestExtensions = requestExtensions;
        }
    
        public TBSRequest(
            ASN1Sequence    seq)
        {
            int    index = 0;
    
            if (seq.getObjectAt(0) is ASN1TaggedObject)
            {
                ASN1TaggedObject    o = (ASN1TaggedObject)seq.getObjectAt(0);
    
                if (o.getTagNo() == 0)
                {
                    version = DERInteger.getInstance((ASN1TaggedObject)seq.getObjectAt(0), true);
                    index++;
                }
                else
                {
                    version = new DERInteger(0);
                }
            }
            else
            {
                version = new DERInteger(0);
            }
    
            if (seq.getObjectAt(index) is ASN1TaggedObject)
            {
                requestorName = GeneralName.getInstance((ASN1TaggedObject)seq.getObjectAt(index++), true);
            }
            
            requestList = (ASN1Sequence)seq.getObjectAt(index++);
    
            if (seq.size() == (index + 1))
            {
                requestExtensions = X509Extensions.getInstance((ASN1TaggedObject)seq.getObjectAt(index), true);
            }
        }
    
        public static TBSRequest getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static TBSRequest getInstance(
            object  obj)
        {
            if (obj == null || obj is TBSRequest)
            {
                return (TBSRequest)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new TBSRequest((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public DERInteger getVersion()
        {
            return version;
        }
    
        public GeneralName getRequestorName()
        {
            return requestorName;
        }
    
        public ASN1Sequence getRequestList()
        {
            return requestList;
        }
    
        public X509Extensions getRequestExtensions()
        {
            return requestExtensions;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * TBSRequest      ::=     SEQUENCE {
         *     version             [0]     EXPLICIT Version DEFAULT v1,
         *     requestorName       [1]     EXPLICIT GeneralName OPTIONAL,
         *     requestList                 SEQUENCE OF Request,
         *     requestExtensions   [2]     EXPLICIT Extensions OPTIONAL }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector    v = new ASN1EncodableVector();
    
            //
            // if default don't include.
            //
            if (version.getValue().intValue() != 0)
            {
                v.add(new DERTaggedObject(true, 0, version));
            }
            
            if (requestorName != null)
            {
                v.add(new DERTaggedObject(true, 1, requestorName));
            }
    
            v.add(requestList);
    
            if (requestExtensions != null)
            {
                v.add(new DERTaggedObject(true, 2, requestExtensions));
            }
    
            return new DERSequence(v);
        }
    }
}
