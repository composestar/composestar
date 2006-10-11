using System;

using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.x509
{
    public class AttCertIssuer
        : ASN1Encodable
    {
        ASN1Encodable   obj;
        ASN1Object      choiceObj;
    
        public static AttCertIssuer getInstance(
                object  obj)
        {
            if (obj is AttCertIssuer)
            {
                return (AttCertIssuer)obj;
            }
            else if (obj is ASN1TaggedObject)
            {
                return new AttCertIssuer(V2Form.getInstance((ASN1TaggedObject)obj, false));
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        
        public AttCertIssuer(
            V2Form  v2Form)
        {
            obj = v2Form;
            choiceObj = new DERTaggedObject(false, 0, obj);
        }
    
        public ASN1Encodable getIssuer()
        {
            return obj;
        }
        
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         *  AttCertIssuer ::= CHOICE {
         *       v1Form   GeneralNames,  -- MUST NOT be used in this
         *                               -- profile
         *       v2Form   [0] V2Form     -- v2 only
         *  }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            return choiceObj;
        }
    }
}
