using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;

namespace org.bouncycastle.asn1.smime
{
    public class SMIMECapabilitiesAttribute
        : Attribute
    {
        public SMIMECapabilitiesAttribute(
            SMIMECapabilityVector capabilities)
            : base(SMIMEAttributes.smimeCapabilities,
                    new DERSet(new DERSequence(capabilities.toASN1EncodableVector())))
        {
        }
    }
}
