using org.bouncycastle.asn1;
using org.bouncycastle.asn1.pkcs;

using System;

namespace org.bouncycastle.asn1.smime
{
    public class SMIMECapability
        : ASN1Encodable
    {
        /**
         * general preferences
         */
        public static readonly DERObjectIdentifier preferSignedData = PKCSObjectIdentifiers.preferSignedData;
        public static readonly DERObjectIdentifier canNotDecryptAny = PKCSObjectIdentifiers.canNotDecryptAny;
        public static readonly DERObjectIdentifier sMIMECapabilitiesVersions = PKCSObjectIdentifiers.sMIMECapabilitiesVersions;
    
        /**
         * encryption algorithms preferences
         */
        public static readonly DERObjectIdentifier dES_CBC = new DERObjectIdentifier("1.3.14.3.2.7");
        public static readonly DERObjectIdentifier dES_EDE3_CBC = PKCSObjectIdentifiers.des_EDE3_CBC;
        public static readonly DERObjectIdentifier rC2_CBC = PKCSObjectIdentifiers.RC2_CBC;
    
        private DERObjectIdentifier capabilityID;
        private ASN1Encodable        parameters;
    
        public SMIMECapability(
            ASN1Sequence seq)
        {
            capabilityID = (DERObjectIdentifier)seq.getObjectAt(0);
    
            if (seq.size() > 1)
            {
                parameters = (ASN1Object)seq.getObjectAt(1);
            }
        }
    
        public SMIMECapability(
            DERObjectIdentifier capabilityID,
            ASN1Encodable        parameters)
        {
            this.capabilityID = capabilityID;
            this.parameters = parameters;
        }
        
        public static SMIMECapability getInstance(
            object obj)
        {
            if (obj == null || obj is SMIMECapability)
            {
                return (SMIMECapability)obj;
            }
            
            if (obj is ASN1Sequence)
            {
                return new SMIMECapability((ASN1Sequence)obj);
            }
            
            throw new ArgumentException("Invalid SMIMECapability");
        } 
    
        public DERObjectIdentifier getCapabilityID()
        {
            return capabilityID;
        }
    
        public ASN1Encodable getParameters()
        {
            return parameters;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre> 
         * SMIMECapability ::= SEQUENCE {
         *     capabilityID OBJECT IDENTIFIER,
         *     parameters ANY DEFINED BY capabilityID OPTIONAL 
         * }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(capabilityID);
            
            if (parameters != null)
            {
                v.add(parameters);
            }
            
            return new DERSequence(v);
        }
    }
}
