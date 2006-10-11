using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.x509
{
    /**
     * The DistributionPointName object.
     * <pre>
     * DistributionPointName ::= CHOICE {
     *     fullName                 [0] GeneralNames,
     *     nameRelativeToCRLIssuer  [1] RelativeDistinguishedName
     * }
     * </pre>
     */
    public class DistributionPointName
        : ASN1Encodable
    {
        ASN1Encodable        name;
        int                  type;
    
        public const int FULL_NAME = 0;
        public const int NAME_RELATIVE_TO_CRL_ISSUER = 1;
    
        public static DistributionPointName getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1TaggedObject.getInstance(obj, explicitly));
        }
    
        public static DistributionPointName getInstance(
            object  obj)
        {
            if (obj == null || obj is DistributionPointName)
            {
                return (DistributionPointName)obj;
            }
            else if (obj is ASN1TaggedObject)
            {
                return new DistributionPointName((ASN1TaggedObject)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public DistributionPointName(
            int              type,
            ASN1Encodable    name)
        {
            this.type = type;
            this.name = name;
        }

        public int getType()
        {
            return type;
        }

        public ASN1Encodable getName()
        {
            return name;
        }

        public DistributionPointName(
            ASN1TaggedObject    obj)
        {
            this.type = obj.getTagNo();
            
            if (type == FULL_NAME)
            {
                this.name = GeneralNames.getInstance(obj, false);
            }
            else
            {
                this.name = ASN1Set.getInstance(obj, false);
            }
        }
        
        public override ASN1Object toASN1Object()
        {
            return new DERTaggedObject(false, type, name);
        }
    }
}
