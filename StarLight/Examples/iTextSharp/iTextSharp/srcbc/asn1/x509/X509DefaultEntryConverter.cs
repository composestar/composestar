using System;
using System.IO;

namespace org.bouncycastle.asn1.x509
{
    /**
     * The default converter for X509 DN entries when going from their
     * string value to 
     */
    public class X509DefaultEntryConverter
        : X509NameEntryConverter
    {
        /**
         * Apply default coversion for the given value depending on the oid
         * and the character range of the value.
         * 
         * @param oid the object identifier for the DN entry
         * @param value the value associated with it
         * @return the ASN.1 equivalent for the string value.
         */
        public override ASN1Object getConvertedValue(
            DERObjectIdentifier  oid,
            string               value)
        {
             if (value.Length != 0 && value[0] == '#')
            {
                try
                {
                    return convertHexEncoded(value, 1);
                }
                catch (IOException)
                {
                    throw new Exception("can't recode value for oid " + oid.getId());
                }
            }
            else if (oid.Equals(X509Name.EmailAddress))
            {
                return new DERIA5String(value);
            }
            else if (canBePrintable(value))  
            {
                return new DERPrintableString(value);
            }
            else if (canBeUTF8(value))
            {
                return new DERUTF8String(value);
            }
    
            return new DERBMPString(value);
        }
    }
}
