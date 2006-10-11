namespace org.bouncycastle.asn1.x509
{
    /**
     * The KeyUsage object.
     * <pre>
     *    id-ce-keyUsage OBJECT IDENTIFIER ::=  { id-ce 15 }
     *
     *    KeyUsage ::= BIT STRING {
     *         digitalSignature        (0),
     *         nonRepudiation          (1),
     *         keyEncipherment         (2),
     *         dataEncipherment        (3),
     *         keyAgreement            (4),
     *         keyCertSign             (5),
     *         cRLSign                 (6),
     *         encipherOnly            (7),
     *         decipherOnly            (8) }
     * </pre>
     */
    public class KeyUsage
        : DERBitString
    {
        public const int        digitalSignature = (1 << 7); 
        public const int        nonRepudiation   = (1 << 6);
        public const int        keyEncipherment  = (1 << 5);
        public const int        dataEncipherment = (1 << 4);
        public const int        keyAgreement     = (1 << 3);
        public const int        keyCertSign      = (1 << 2);
        public const int        cRLSign          = (1 << 1);
        public const int        encipherOnly     = (1 << 0);
        public const int        decipherOnly     = (1 << 15);
    
        /**
         * Basic constructor.
         * 
         * @param usage - the bitwise OR of the Key Usage flags giving the
         * allowed uses for the key.
         * e.g. (KeyUsage.keyEncipherment | KeyUsage.dataEncipherment)
         */
        public KeyUsage(int usage) : base(getBytes(usage), getPadBits(usage))
        {
        }
    
        public KeyUsage(DERBitString usage) : base(usage.getBytes(), usage.getPadBits())
        {
        }

        public override string ToString()
        {
            if (data.Length == 1)
            {
       			return "KeyUsage: 0x" + (data[0] & 0xff).ToString("X");
            }
            return "KeyUsage: 0x" + ((data[1] & 0xff) << 8 | (data[0] & 0xff)).ToString("X");
        }
    }
}
