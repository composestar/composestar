using System;

using org.bouncycastle.asn1;
                                                                                                      
namespace org.bouncycastle.asn1.x509
{
    /**
     * Generator for Version 2 AttributeCertificateInfo
     * <pre>
     * AttributeCertificateInfo ::= SEQUENCE {
     *       version              AttCertVersion -- version is v2,
     *       holder               Holder,
     *       issuer               AttCertIssuer,
     *       signature            AlgorithmIdentifier,
     *       serialNumber         CertificateSerialNumber,
     *       attrCertValidityPeriod   AttCertValidityPeriod,
     *       attributes           SEQUENCE OF Attribute,
     *       issuerUniqueID       UniqueIdentifier OPTIONAL,
     *       extensions           Extensions OPTIONAL
     * }
     * </pre>
     *
     */
    public class V2AttributeCertificateInfoGenerator
    {
        DERInteger version;
        Holder holder;
        AttCertIssuer issuer;
        AlgorithmIdentifier signature;
        DERInteger serialNumber;
        ASN1EncodableVector attributes;
        DERBitString issuerUniqueID;
        X509Extensions extensions;
        DERGeneralizedTime startDate, endDate;
    
        public V2AttributeCertificateInfoGenerator()
        {
            this.version = new DERInteger(1);
    	    attributes = new ASN1EncodableVector();
        }
        
        public void setHolder(Holder holder)
        {
            this.holder = holder;
        }
        
        public void addAttribute(string oid, ASN1Encodable value) 
        {
            attributes.add(new Attribute(new DERObjectIdentifier(oid), new DERSet(value)));
        }
    
        /**
         * @param attribute
         */
        public void addAttribute(Attribute attribute)
        {
            attributes.add(attribute);
        }
        
        public void setSerialNumber(
            DERInteger  serialNumber)
        {
            this.serialNumber = serialNumber;
        }
    
        public void setSignature(
            AlgorithmIdentifier    signature)
        {
            this.signature = signature;
        }
    
        public void setIssuer(
            AttCertIssuer    issuer)
        {
            this.issuer = issuer;
        }
    
        public void setStartDate(
            DERGeneralizedTime startDate)
        {
            this.startDate = startDate;
        }
    
        public void setEndDate(
            DERGeneralizedTime endDate)
        {
            this.endDate = endDate;
        }
    
        public void setIssuerUniqueID(
            DERBitString    issuerUniqueID)
        {
            this.issuerUniqueID = issuerUniqueID;
        }
    
        public void setExtensions(
            X509Extensions    extensions)
        {
            this.extensions = extensions;
        }
    
        public AttributeCertificateInfo generateAttributeCertificateInfo()
        {
            if ((serialNumber == null) || (signature == null)
                || (issuer == null) || (startDate == null) || (endDate == null)
                || (holder == null) || (attributes == null))
            {
                throw new Exception("not all mandatory fields set in V2 AttributeCertificateInfo generator");
            }
    
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(version);
            v.add(holder);
            v.add(issuer);
            v.add(signature);
            v.add(serialNumber);
        
            //
            // before and after dates => AttCertValidityPeriod
            //
            AttCertValidityPeriod validity = new AttCertValidityPeriod(startDate, endDate);
    	    v.add(validity);
            
    	    // Attributes
            v.add(new DERSequence(attributes));
            
            if (issuerUniqueID != null)
            {
                v.add(issuerUniqueID);
            }
        
            if (extensions != null)
            {
                v.add(extensions);
            }
    
            return new AttributeCertificateInfo(new DERSequence(v));
        }
    }
}
