using System;

using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.x509
{
    public class AttributeCertificateInfo
        : ASN1Encodable
    {
        DERInteger              version;
        Holder                  holder;
        AttCertIssuer           issuer;
        AlgorithmIdentifier     signature;
        DERInteger              serialNumber;
        AttCertValidityPeriod   attrCertValidityPeriod;
        ASN1Sequence            attributes;
        DERBitString            issuerUniqueID;
        X509Extensions          extensions;
    
        public static AttributeCertificateInfo getInstance(
            ASN1TaggedObject obj,
            bool             isExplicit)
        {
            return getInstance(ASN1Sequence.getInstance(obj, isExplicit));
        }
    
        public static AttributeCertificateInfo getInstance(
            object  obj)
        {
            if (obj is AttributeCertificateInfo)
            {
                return (AttributeCertificateInfo)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new AttributeCertificateInfo((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public AttributeCertificateInfo(
            ASN1Sequence   seq)
        {
            this.version = DERInteger.getInstance(seq.getObjectAt(0));
            this.holder = Holder.getInstance(seq.getObjectAt(1));
            this.issuer = AttCertIssuer.getInstance(seq.getObjectAt(2));
            this.signature = AlgorithmIdentifier.getInstance(seq.getObjectAt(3));
            this.serialNumber = DERInteger.getInstance(seq.getObjectAt(4));
            this.attrCertValidityPeriod = AttCertValidityPeriod.getInstance(seq.getObjectAt(5));
            this.attributes = ASN1Sequence.getInstance(seq.getObjectAt(6));
            
            for (int i = 7; i < seq.size(); i++)
            {
                ASN1Encodable	obj = (ASN1Encodable)seq.getObjectAt(i);
    
                if (obj is DERBitString)
                {
                    this.issuerUniqueID = DERBitString.getInstance(seq.getObjectAt(i));
                }
                else if (obj is ASN1Sequence || obj is X509Extensions)
                {
                    this.extensions = X509Extensions.getInstance(seq.getObjectAt(i));
                }
            }
        }
        
        public DERInteger getVersion()
        {
            return version;
        }
    
        public Holder getHolder()
        {
            return holder;
        }
    
        public AttCertIssuer getIssuer()
        {
            return issuer;
        }
    
        public AlgorithmIdentifier getSignature()
        {
            return signature;
        }
    
        public DERInteger getSerialNumber()
        {
            return serialNumber;
        }
    
        public AttCertValidityPeriod getAttrCertValidityPeriod()
        {
            return attrCertValidityPeriod;
        }
    
        public ASN1Sequence getAttributes()
        {
            return attributes;
        }
    
        public DERBitString getIssuerUniqueID()
        {
            return issuerUniqueID;
        }
    
        public X509Extensions getExtensions()
        {
            return extensions;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         *  AttributeCertificateInfo ::= SEQUENCE {
         *       version              AttCertVersion -- version is v2,
         *       holder               Holder,
         *       issuer               AttCertIssuer,
         *       signature            AlgorithmIdentifier,
         *       serialNumber         CertificateSerialNumber,
         *       attrCertValidityPeriod   AttCertValidityPeriod,
         *       attributes           SEQUENCE OF Attribute,
         *       issuerUniqueID       UniqueIdentifier OPTIONAL,
         *       extensions           Extensions OPTIONAL
         *  }
         *
         *  AttCertVersion ::= INTEGER { v2(1) }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(version);
            v.add(holder);
            v.add(issuer);
            v.add(signature);
            v.add(serialNumber);
            v.add(attrCertValidityPeriod);
            v.add(attributes);
            
            if (issuerUniqueID != null)
            {
                v.add(issuerUniqueID);
            }
            
            if (extensions != null)
            {
                v.add(extensions);
            }
            
            return new DERSequence(v);
        }
    }
}
