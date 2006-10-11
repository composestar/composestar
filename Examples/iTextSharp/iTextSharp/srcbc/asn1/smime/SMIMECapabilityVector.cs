using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.smime
{
    /**
     * Handler for creating a vector S/MIME Capabilities
     */
    public class SMIMECapabilityVector
    {
        private ASN1EncodableVector    capabilities = new ASN1EncodableVector();
    
        public void addCapability(
            DERObjectIdentifier capability)
        {
            capabilities.add(new DERSequence(capability));
        }
    
        public void addCapability(
            DERObjectIdentifier capability,
            int                 value)
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(capability);
            v.add(new DERInteger(value));
    
            capabilities.add(new DERSequence(v));
        }
    
        public void addCapability(
            DERObjectIdentifier capability,
            ASN1Encodable        _params)
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(capability);
            v.add(_params);
    
            capabilities.add(new DERSequence(v));
        }
    
        public ASN1EncodableVector toASN1EncodableVector()
        {
            return capabilities;
        }
    }
}
