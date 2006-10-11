using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.misc
{
    /**
     * The NetscapeCertType object.
     * <pre>
     *    NetscapeCertType ::= BIT STRING {
     *         SSLClient               (0),
     *         SSLServer               (1),
     *         S/MIME                  (2),
     *         Object Signing          (3),
     *         Reserved                (4),
     *         SSL CA                  (5),
     *         S/MIME CA               (6),
     *         Object Signing CA       (7) }
     * </pre>
     */
    public class NetscapeCertType
        : DERBitString
    {
        public const int        sslClient        = (1 << 7); 
        public const int        sslServer        = (1 << 6);
        public const int        smime            = (1 << 5);
        public const int        objectSigning    = (1 << 4);
        public const int        reserved         = (1 << 3);
        public const int        sslCA            = (1 << 2);
        public const int        smimeCA          = (1 << 1);
        public const int        objectSigningCA  = (1 << 0);
    
        /**
         * Basic constructor.
         * 
         * @param usage - the bitwise OR of the Key Usage flags giving the
         * allowed uses for the key.
         * e.g. (X509NetscapeCertType.sslCA | X509NetscapeCertType.smimeCA)
         */
        public NetscapeCertType(int usage) : base(getBytes(usage), getPadBits(usage))
        {
        }
    
        public NetscapeCertType(DERBitString usage) : base(usage.getBytes(), usage.getPadBits())
        {
        }

        public override string ToString()
        {
       	    return "NetscapeCertType: 0x" + (data[0] & 0xff).ToString("X");
        }
    }
}
