using org.bouncycastle.asn1;

using System.Collections;

namespace org.bouncycastle.asn1.pkcs
{
    public class PBES2Parameters
        : ASN1Encodable//, PKCSObjectIdentifiers
    {
        private KeyDerivationFunc   func;
        private EncryptionScheme    scheme;
    
        public PBES2Parameters(
            ASN1Sequence  obj)
        {
            IEnumerator e = obj.getObjects();
            e.MoveNext();
            ASN1Sequence  funcSeq = (ASN1Sequence)e.Current;
    
            if (funcSeq.getObjectAt(0).Equals(PKCSObjectIdentifiers.id_PBKDF2))
            {
                func = new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, funcSeq.getObjectAt(1));
            }
            else
            {
                func = new KeyDerivationFunc(funcSeq);
            }
    
            e.MoveNext();
            scheme = new EncryptionScheme((ASN1Sequence)e.Current);
        }
    
        public KeyDerivationFunc getKeyDerivationFunc()
        {
            return func;
        }
    
        public EncryptionScheme getEncryptionScheme()
        {
            return scheme;
        }
    
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(func);
            v.add(scheme);
    
            return new DERSequence(v);
        }
    }
}
