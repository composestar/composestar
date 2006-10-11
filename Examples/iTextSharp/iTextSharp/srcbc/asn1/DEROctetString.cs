namespace org.bouncycastle.asn1
{
    public class DEROctetString
        : ASN1OctetString
    {
        /**
         * @param string the octets making up the octet string.
         */
        public DEROctetString(byte[] str) : base(str)
        {
        }
    
        public DEROctetString(ASN1Encodable obj): base(obj)
        {
        }
    
        internal override void encode(
            DEROutputStream derOut)
        {
            derOut.writeEncoded(ASN1Tags.OCTET_STRING, str);
        }
    }
}
