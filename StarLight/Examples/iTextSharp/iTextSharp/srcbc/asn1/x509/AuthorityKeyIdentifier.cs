using org.bouncycastle.asn1;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.digests;
using org.bouncycastle.math;

using System;
using System.Collections;

namespace org.bouncycastle.asn1.x509
{
    /**
     * The AuthorityKeyIdentifier object.
     * <pre>
     * id-ce-authorityKeyIdentifier OBJECT IDENTIFIER ::=  { id-ce 35 }
     *
     *   AuthorityKeyIdentifier ::= SEQUENCE {
     *      keyIdentifier             [0] IMPLICIT KeyIdentifier           OPTIONAL,
     *      authorityCertIssuer       [1] IMPLICIT GeneralNames            OPTIONAL,
     *      authorityCertSerialNumber [2] IMPLICIT CertificateSerialNumber OPTIONAL  }
     *
     *   KeyIdentifier ::= OCTET STRING
     * </pre>
     *
     */
    public class AuthorityKeyIdentifier
        : ASN1Encodable
    {
        ASN1OctetString keyidentifier=null;
        GeneralNames certissuer=null;
        DERInteger certserno=null;
    
        public static AuthorityKeyIdentifier getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static AuthorityKeyIdentifier getInstance(
            object  obj)
        {
            if (obj is AuthorityKeyIdentifier)
            {
                return (AuthorityKeyIdentifier)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new AuthorityKeyIdentifier((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public AuthorityKeyIdentifier(
            ASN1Sequence   seq)
        {
            IEnumerator     e = seq.getObjects();
    
            while (e.MoveNext())
            {
                DERTaggedObject o = (DERTaggedObject)e.Current;
    
                switch ((int) o.getTagNo())
                {
                case 0:
                    this.keyidentifier = ASN1OctetString.getInstance(o, false);
                    break;
                case 1:
                    this.certissuer = GeneralNames.getInstance(o, false);
                    break;
                case 2:
                    this.certserno = DERInteger.getInstance(o, false);
                    break;
                default:
                    throw new ArgumentException("illegal tag");
                }
            }
        }
    
        /**
         *
         * Calulates the keyidentifier using a SHA1 hash over the BIT STRING
         * from SubjectPublicKeyInfo as defined in RFC2459.
         *
         * Example of making a AuthorityKeyIdentifier:
         * <pre>
         *   SubjectPublicKeyInfo apki = new SubjectPublicKeyInfo((ASN1Sequence)new DERInputStream(
         *       new ByteArrayInputStream(publicKey.getEncoded())).readObject());
         *   AuthorityKeyIdentifier aki = new AuthorityKeyIdentifier(apki);
         * </pre>
         *
         **/
        public AuthorityKeyIdentifier(
            SubjectPublicKeyInfo    spki)
        {
            Digest  digest = new SHA1Digest();
            byte[]  resBuf = new byte[digest.getDigestSize()];
    
            byte[] bytes = spki.getPublicKeyData().getBytes();
            digest.update(bytes, 0, bytes.Length);
            digest.doFinal(resBuf, 0);
            this.keyidentifier = new DEROctetString(resBuf);
        }
    
        /**
         * create an AuthorityKeyIdentifier with the GeneralNames tag and
         * the serial number provided as well.
         */
        public AuthorityKeyIdentifier(
            SubjectPublicKeyInfo    spki,
            GeneralNames            name,
            BigInteger              serialNumber)
        {
            Digest  digest = new SHA1Digest();
            byte[]  resBuf = new byte[digest.getDigestSize()];
    
            byte[] bytes = spki.getPublicKeyData().getBytes();
            digest.update(bytes, 0, bytes.Length);
            digest.doFinal(resBuf, 0);
    
            this.keyidentifier = new DEROctetString(resBuf);
            this.certissuer = name;
            this.certserno = new DERInteger(serialNumber);
        }
    
        public byte[] getKeyIdentifier()
        {
            if (keyidentifier != null)
            {
                return keyidentifier.getOctets();
            }
    
            return null;
        }

        public GeneralNames getAuthorityCertIssuer()
        {
            return certissuer;
        }

        public BigInteger getAuthorityCertSerialNumber()
        {
            if (certserno != null)
            {
                return certserno.getValue();
            }
                                                                                
            return null;
        }

        /**
         * Produce an object suitable for an ASN1OutputStream.
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            if (keyidentifier != null)
            {
                v.add(new DERTaggedObject(false, 0, keyidentifier));
            }
    
            if (certissuer != null)
            {
                v.add(new DERTaggedObject(false, 1, certissuer));
            }
    
            if (certserno != null)
            {
                v.add(new DERTaggedObject(false, 2, certserno));
            }
    
    
            return new DERSequence(v);
        }
    
        public override string ToString()
        {
            return ("AuthorityKeyIdentifier: KeyID(" + this.keyidentifier.getOctets() + ")");
        }
    }
}
