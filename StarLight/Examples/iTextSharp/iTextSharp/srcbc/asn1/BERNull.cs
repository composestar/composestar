namespace org.bouncycastle.asn1
{
    /**
     * A BER NULL object.
     */
    public class BERNull
        : DERNull
    {
        public BERNull()
        {
        }
    
        internal override void encode(
            DEROutputStream  derOut)
        {
            if (derOut is ASN1OutputStream || derOut is BEROutputStream)
            {
                derOut.WriteByte((byte) ASN1Tags.NULL);
            }
            else
            {
                base.encode(derOut);
            }
        }
    }
}
