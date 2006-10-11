using System;

using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.x509
{
    public class AttCertValidityPeriod
        : ASN1Encodable
    {
        DERGeneralizedTime  notBeforeTime;
        DERGeneralizedTime  notAfterTime;

        public static AttCertValidityPeriod getInstance(
            object obj)
        {
            if (obj is AttCertValidityPeriod || obj == null)
            {
                return (AttCertValidityPeriod)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new AttCertValidityPeriod((ASN1Sequence)obj);
            }

            throw new ArgumentException("unknown object in factory");
        }

        public static AttCertValidityPeriod getInstance(
            ASN1TaggedObject obj,
            bool explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
        
        public AttCertValidityPeriod(
            ASN1Sequence    seq)
        {
            notBeforeTime = (DERGeneralizedTime)seq.getObjectAt(0);
            notAfterTime = (DERGeneralizedTime)seq.getObjectAt(1);
        }

        public AttCertValidityPeriod(
            DERGeneralizedTime  notBeforeTime,
            DERGeneralizedTime  notAfterTime)
        {
            this.notBeforeTime = notBeforeTime;
            this.notAfterTime = notAfterTime;
        }
        
        public DERGeneralizedTime getNotBeforeTime()
        {
            return notBeforeTime;
        }
    
        public DERGeneralizedTime getNotAfterTime()
        {
            return notAfterTime;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         *  AttCertValidityPeriod  ::= SEQUENCE {
         *       notBeforeTime  GeneralizedTime,
         *       notAfterTime   GeneralizedTime
         *  } 
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(notBeforeTime);
            v.add(notAfterTime);
    
            return new DERSequence(v);
        }
    }
}
