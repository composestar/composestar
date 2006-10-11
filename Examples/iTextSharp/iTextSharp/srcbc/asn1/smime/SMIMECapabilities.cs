using org.bouncycastle.asn1;
using org.bouncycastle.asn1.pkcs;

using System;
using System.Collections;

namespace org.bouncycastle.asn1.smime
{
    /**
     * Handler class for dealing with S/MIME Capabilities
     */
    public class SMIMECapabilities
        : ASN1Encodable
    {
        /**
         * general preferences
         */
        public static readonly DERObjectIdentifier preferSignedData = PKCSObjectIdentifiers.preferSignedData;
        public static readonly DERObjectIdentifier canNotDecryptAny = PKCSObjectIdentifiers.canNotDecryptAny;
        public static readonly DERObjectIdentifier sMIMECapabilitesVersions = PKCSObjectIdentifiers.sMIMECapabilitiesVersions;
    
        /**
         * encryption algorithms preferences
         */
        public static readonly DERObjectIdentifier dES_CBC = new DERObjectIdentifier("1.3.14.3.2.7");
        public static readonly DERObjectIdentifier dES_EDE3_CBC = PKCSObjectIdentifiers.des_EDE3_CBC;
        public static readonly DERObjectIdentifier rC2_CBC = PKCSObjectIdentifiers.RC2_CBC;
    
        private ASN1Sequence         capabilities;
    
        /**
         * return an Attribute object from the given object.
         *
         * @param o the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static SMIMECapabilities getInstance(
            object o)
        {
            if (o == null || o is SMIMECapabilities)
            {
                return (SMIMECapabilities)o;
            }
            
            if (o is ASN1Sequence)
            {
                return new SMIMECapabilities((ASN1Sequence)o);
            }
    
            if (o is org.bouncycastle.asn1.x509.Attribute)
            {
                return new SMIMECapabilities(
                    (ASN1Sequence)(((org.bouncycastle.asn1.x509.Attribute)o).getAttrValues().getObjectAt(0)));
            }
    
            throw new ArgumentException("unknown object in factory");
        }
        
        public SMIMECapabilities(
            ASN1Sequence seq)
        {
            capabilities = seq;
        }
    
        /**
         * returns a vector with 0 or more objects of all the capabilities
         * matching the passed in capability OID. If the OID passed is null the
         * entire set is returned.
         */
        public ArrayList getCapabilities(
            DERObjectIdentifier capability)
        {
            IEnumerator e = capabilities.getObjects();
            ArrayList      list = new ArrayList();
    
            if (capability == null)
            {
                while (e.MoveNext())
                {
                    SMIMECapability  cap = SMIMECapability.getInstance(e.Current);
    
                    list.Add(cap);
                }
            }
            else
            {
                while (e.MoveNext())
                {
                    SMIMECapability  cap = SMIMECapability.getInstance(e.Current);
    
                    if (capability.Equals(cap.getCapabilityID()))
                    {
                        list.Add(cap);
                    }
                }
            }
    
            return list;
        }
    
        /** 
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * SMIMECapabilities ::= SEQUENCE OF SMIMECapability
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            return capabilities;
        }
    }
}
