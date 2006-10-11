using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.x509
{
    public class V2Form
        : ASN1Encodable
    {
        internal GeneralNames        issuerName;
        internal IssuerSerial        baseCertificateID = null;
        internal ObjectDigestInfo    objectDigestInfo = null;
    
        public static V2Form getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static V2Form getInstance(
            object  obj)
        {
            if (obj is V2Form)
            {
                return (V2Form)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new V2Form((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
        
        public V2Form(
            GeneralNames    issuerName)
        {
            this.issuerName = issuerName;
        }
        
        public V2Form(
            ASN1Sequence seq)
        {
            int    index = 0;

            if (!(seq.getObjectAt(0) is ASN1TaggedObject))
            {
                index++;
                this.issuerName = GeneralNames.getInstance(seq.getObjectAt(0));
            }

            for (int i = index; i != seq.size(); i++)
            {
                ASN1TaggedObject o = (ASN1TaggedObject)seq.getObjectAt(i);
                if (o.getTagNo() == 0)
                {
                    baseCertificateID = IssuerSerial.getInstance(o, false);
                }
                else if (o.getTagNo() == 1)
                {
                    objectDigestInfo = ObjectDigestInfo.getInstance(o, false);
                }
            }
        }
        
        public GeneralNames getIssuerName()
        {
            return issuerName;
        }
    
        public IssuerSerial getBaseCertificateID()
        {
            return baseCertificateID;
        }
    
        public ObjectDigestInfo getObjectDigestInfo()
        {
            return objectDigestInfo;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         *  V2Form ::= SEQUENCE {
         *       issuerName            GeneralNames  OPTIONAL,
         *       baseCertificateID     [0] IssuerSerial  OPTIONAL,
         *       objectDigestInfo      [1] ObjectDigestInfo  OPTIONAL
         *         -- issuerName MUST be present in this profile
         *         -- baseCertificateID and objectDigestInfo MUST NOT
         *         -- be present in this profile
         *  }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            if (issuerName != null)
            {
                v.add(issuerName);
            }
    
            if (baseCertificateID != null)
            {
                v.add(new DERTaggedObject(false, 0, baseCertificateID));
            }
    
            if (objectDigestInfo != null)
            {
                v.add(new DERTaggedObject(false, 1, objectDigestInfo));
            }
    
            return new DERSequence(v);
        }
    }
}
