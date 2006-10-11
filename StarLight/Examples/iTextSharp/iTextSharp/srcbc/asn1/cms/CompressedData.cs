using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;

using System;

namespace org.bouncycastle.asn1.cms
{
    /** 
     * RFC 3274 - CMS Compressed Data.
     * <pre>
     * CompressedData ::= SEQUENCE {
     *  version CMSVersion,
     *  compressionAlgorithm CompressionAlgorithmIdentifier,
     *  encapContentInfo EncapsulatedContentInfo
     * }
     * </pre>
     */
    public class CompressedData
        : ASN1Encodable
    {
        private DERInteger           version;
        private AlgorithmIdentifier  compressionAlgorithm;
        private ContentInfo          encapContentInfo;
    
        public CompressedData(
            AlgorithmIdentifier compressionAlgorithm,
            ContentInfo         encapContentInfo)
        {
            this.version = new DERInteger(0);
            this.compressionAlgorithm = compressionAlgorithm;
            this.encapContentInfo = encapContentInfo;
        }
        
        public CompressedData(
            ASN1Sequence seq)
        {
            this.version = (DERInteger)seq.getObjectAt(0);
            this.compressionAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(1));
            this.encapContentInfo = ContentInfo.getInstance(seq.getObjectAt(2));
    
        }
    
        /**
         * return a CompressedData object from a tagged object.
         *
         * @param _ato the tagged object holding the object we want.
         * @param _explicit true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the object held by the
         *          tagged object cannot be converted.
         */
        public static CompressedData getInstance(
            ASN1TaggedObject _ato,
            bool _explicit)
        {
            return getInstance(ASN1Sequence.getInstance(_ato, _explicit));
        }
        
        /**
         * return a CompressedData object from the given object.
         *
         * @param _obj the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static CompressedData getInstance(
            object _obj)
        {
            if (_obj == null || _obj is CompressedData)
            {
                return (CompressedData)_obj;
            }
            
            if (_obj is ASN1Sequence)
            {
                return new CompressedData((ASN1Sequence)_obj);
            }
            
            throw new ArgumentException("Invalid CompressedData: " + _obj.GetType().Name);
        }
    
        public DERInteger getVersion()
        {
            return version;
        }
    
        public AlgorithmIdentifier getCompressionAlgorithmIdentifier()
        {
            return compressionAlgorithm;
        }
    
        public ContentInfo getEncapContentInfo()
        {
            return encapContentInfo;
        }
    
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector v = new ASN1EncodableVector();
    
            v.add(version);
            v.add(compressionAlgorithm);
            v.add(encapContentInfo);
    
            return new BERSequence(v);
        }
    }
}
