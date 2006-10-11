using org.bouncycastle.asn1;
using org.bouncycastle.asn1.cms;
using org.bouncycastle.asn1.x509;

namespace org.bouncycastle.asn1.smime
{
    /**
     * The SMIMEEncryptionKeyPreference object.
     * <pre>
     * SMIMEEncryptionKeyPreference ::= CHOICE {
     *     issuerAndSerialNumber   [0] IssuerAndSerialNumber,
     *     receipentKeyId          [1] RecipientKeyIdentifier,
     *     subjectAltKeyIdentifier [2] SubjectKeyIdentifier
     * }
     * </pre>
     */
    public class SMIMEEncryptionKeyPreferenceAttribute
        : Attribute
    {
        public SMIMEEncryptionKeyPreferenceAttribute(
            IssuerAndSerialNumber issAndSer)
            : base(SMIMEAttributes.encrypKeyPref,
                new DERSet(new DERTaggedObject(false, 0, issAndSer)))
        {
        }
        
        public SMIMEEncryptionKeyPreferenceAttribute(
            RecipientKeyIdentifier rKeyId)
            : base(SMIMEAttributes.encrypKeyPref,
                new DERSet(new DERTaggedObject(false, 1, rKeyId)))
        {
        }
        
        /**
         * @param sKeyId the subjectKeyIdentifier value (normally the X.509 one)
         */
        public SMIMEEncryptionKeyPreferenceAttribute(
            ASN1OctetString sKeyId)
            : base(SMIMEAttributes.encrypKeyPref,
                new DERSet(new DERTaggedObject(false, 2, sKeyId)))
        {
        }
    }
}
