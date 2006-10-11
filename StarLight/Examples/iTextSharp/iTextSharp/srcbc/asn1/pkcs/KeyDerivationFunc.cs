using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;

namespace org.bouncycastle.asn1.pkcs
{
    public class KeyDerivationFunc
        : AlgorithmIdentifier
    {
        internal KeyDerivationFunc(ASN1Sequence  seq) : base(seq)
        {
        }
        internal KeyDerivationFunc(DERObjectIdentifier id, ASN1Encodable parameters) : base(id, parameters)
        {
        }
    }
}
