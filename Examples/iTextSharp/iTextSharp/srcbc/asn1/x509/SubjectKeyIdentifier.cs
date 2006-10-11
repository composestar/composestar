using org.bouncycastle.asn1;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.digests;

using System;

namespace org.bouncycastle.asn1.x509
{
    /**
     * The SubjectKeyIdentifier object.
     * <pre>
     * SubjectKeyIdentifier::= OCTET STRING
     * </pre>
     */
    public class SubjectKeyIdentifier
        : ASN1Encodable
    {
        private byte[] keyidentifier;
    
        public static SubjectKeyIdentifier getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1OctetString.getInstance(obj, explicitly));
        }
    
        public static SubjectKeyIdentifier getInstance(
            object obj)
        {
            if (obj == null || obj is SubjectKeyIdentifier) 
            {
                return (SubjectKeyIdentifier)obj;
            }
            
            if (obj is SubjectPublicKeyInfo) 
            {
                return new SubjectKeyIdentifier((SubjectPublicKeyInfo)obj);
            }
            
            if (obj is ASN1OctetString) 
            {
                return new SubjectKeyIdentifier((ASN1OctetString)obj);
            }
            
            throw new ArgumentException("Invalid SubjectKeyIdentifier: " + obj.GetType().Name);
        }
        
        public SubjectKeyIdentifier(
            byte[] keyid)
        {
            this.keyidentifier=keyid;
        }
    
        public SubjectKeyIdentifier(
            ASN1OctetString  keyid)
        {
            this.keyidentifier=keyid.getOctets();
    
        }
    
        /**
         *
         * Calulates the keyidentifier using a SHA1 hash over the BIT STRING
         * from SubjectPublicKeyInfo as defined in RFC2459.
         *
         **/
        public SubjectKeyIdentifier(
            SubjectPublicKeyInfo    spki)
        {
            Digest  digest = new SHA1Digest();
            byte[]  resBuf = new byte[digest.getDigestSize()];
    
            byte[] bytes = spki.getPublicKeyData().getBytes();
            digest.update(bytes, 0, bytes.Length);
            digest.doFinal(resBuf, 0);
            this.keyidentifier=resBuf;
        }
    
        public byte[] getKeyIdentifier()
        {
            return keyidentifier;
        }
    
        public override ASN1Object toASN1Object()
        {
            return new DEROctetString(keyidentifier);
        }
    }
}
