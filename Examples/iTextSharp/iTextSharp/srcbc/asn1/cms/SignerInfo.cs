using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;

using System;
using System.Collections;

namespace org.bouncycastle.asn1.cms
{
    public class SignerInfo
        : ASN1Encodable
    {
        private DERInteger              version;
        private SignerIdentifier        sid;
        private AlgorithmIdentifier     digAlgorithm;
        private ASN1Set                 authenticatedAttributes;
        private AlgorithmIdentifier     digEncryptionAlgorithm;
        private ASN1OctetString         encryptedDigest;
        private ASN1Set                 unauthenticatedAttributes;
    
        public static SignerInfo getInstance(
            object  o)
        {
            if (o == null || o is SignerInfo)
            {
                return (SignerInfo)o;
            }
            else if (o is ASN1Sequence)
            {
                return new SignerInfo((ASN1Sequence)o);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public SignerInfo(
            SignerIdentifier        sid,
            AlgorithmIdentifier     digAlgorithm,
            ASN1Set                 authenticatedAttributes,
            AlgorithmIdentifier     digEncryptionAlgorithm,
            ASN1OctetString         encryptedDigest,
            ASN1Set                 unauthenticatedAttributes)
        {
            if (sid.isTagged())
            {
                this.version = new DERInteger(3);
            }
            else
            {
                this.version = new DERInteger(1);
            }
    
            this.sid = sid;
            this.digAlgorithm = digAlgorithm;
            this.authenticatedAttributes = authenticatedAttributes;
            this.digEncryptionAlgorithm = digEncryptionAlgorithm;
            this.encryptedDigest = encryptedDigest;
            this.unauthenticatedAttributes = unauthenticatedAttributes;
        }
    
        public SignerInfo(
            ASN1Sequence seq)
        {
            IEnumerator     e = seq.getObjects();

            e.MoveNext();
            version = (DERInteger)e.Current;
            e.MoveNext();
            sid = SignerIdentifier.getInstance(e.Current);
            e.MoveNext();
            digAlgorithm = AlgorithmIdentifier.getInstance(e.Current);

            e.MoveNext();
            object obj = e.Current;
    
            if (obj is ASN1TaggedObject)
            {
                authenticatedAttributes = ASN1Set.getInstance((ASN1TaggedObject)obj, false);

                e.MoveNext();
                digEncryptionAlgorithm = AlgorithmIdentifier.getInstance(e.Current);
            }
            else
            {
                authenticatedAttributes = null;
                digEncryptionAlgorithm = AlgorithmIdentifier.getInstance(obj);
            }
    
            e.MoveNext();
            encryptedDigest = DEROctetString.getInstance(e.Current);
    
            if (e.MoveNext())
            {
                unauthenticatedAttributes = ASN1Set.getInstance((ASN1TaggedObject)e.Current, false);
            }
            else
            {
                unauthenticatedAttributes = null;
            }
        }
    
        public DERInteger getVersion()
        {
            return version;
        }
    
        public SignerIdentifier getSID()
        {
            return sid;
        }
    
        public ASN1Set getAuthenticatedAttributes()
        {
            return authenticatedAttributes;
        }
    
        public AlgorithmIdentifier getDigestAlgorithm()
        {
            return digAlgorithm;
        }
    
        public ASN1OctetString getEncryptedDigest()
        {
            return encryptedDigest;
        }
    
        public AlgorithmIdentifier getDigestEncryptionAlgorithm()
        {
            return digEncryptionAlgorithm;
        }
    
        public ASN1Set getUnauthenticatedAttributes()
        {
            return unauthenticatedAttributes;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         *  SignerInfo ::= SEQUENCE {
         *      version Version,
         *      SignerIdentifier sid,
         *      digestAlgorithm DigestAlgorithmIdentifier,
         *      authenticatedAttributes [0] IMPLICIT Attributes OPTIONAL,
         *      digestEncryptionAlgorithm DigestEncryptionAlgorithmIdentifier,
         *      encryptedDigest EncryptedDigest,
         *      unauthenticatedAttributes [1] IMPLICIT Attributes OPTIONAL
         *  }
         *
         *  EncryptedDigest ::= OCTET STRING
         *
         *  DigestAlgorithmIdentifier ::= AlgorithmIdentifier
         *
         *  DigestEncryptionAlgorithmIdentifier ::= AlgorithmIdentifier
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector v = new ASN1EncodableVector();
    
            v.add(version);
            v.add(sid);
            v.add(digAlgorithm);
    
            if (authenticatedAttributes != null)
            {
                v.add(new DERTaggedObject(false, 0, authenticatedAttributes));
            }
    
            v.add(digEncryptionAlgorithm);
            v.add(encryptedDigest);
    
            if (unauthenticatedAttributes != null)
            {
                v.add(new DERTaggedObject(false, 1, unauthenticatedAttributes));
            }
    
            return new DERSequence(v);
        }
    }
}
