using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.x509
{
    /**
     * PKIX RFC-2459 - TBSCertList object.
     * <pre>
     * TBSCertList  ::=  SEQUENCE  {
     *      version                 Version OPTIONAL,
     *                                   -- if present, shall be v2
     *      signature               AlgorithmIdentifier,
     *      issuer                  Name,
     *      thisUpdate              Time,
     *      nextUpdate              Time OPTIONAL,
     *      revokedCertificates     SEQUENCE OF SEQUENCE  {
     *           userCertificate         CertificateSerialNumber,
     *           revocationDate          Time,
     *           crlEntryExtensions      Extensions OPTIONAL
     *                                         -- if present, shall be v2
     *                                }  OPTIONAL,
     *      crlExtensions           [0]  EXPLICIT Extensions OPTIONAL
     *                                         -- if present, shall be v2
     *                                }
     * </pre>
     */
    public class TBSCertList
        : ASN1Encodable
    {
        public class CRLEntry
            : ASN1Encodable
        {
            ASN1Sequence  seq;
    
            DERInteger          userCertificate;
            Time                revocationDate;
            X509Extensions      crlEntryExtensions;
    
            public CRLEntry(
                ASN1Sequence  seq)
            {
                this.seq = seq;
    
                userCertificate = (DERInteger)seq.getObjectAt(0);
                revocationDate = Time.getInstance(seq.getObjectAt(1));
                if (seq.size() == 3)
                {
                    crlEntryExtensions = X509Extensions.getInstance(seq.getObjectAt(2));
                }
            }
    
            public DERInteger getUserCertificate()
            {
                return userCertificate;
            }
    
            public Time getRevocationDate()
            {
                return revocationDate;
            }
    
            public X509Extensions getExtensions()
            {
                return crlEntryExtensions;
            }
    
            public override ASN1Object toASN1Object()
            {
                return seq;
            }
        }
    
        ASN1Sequence     seq;
    
        DERInteger              version;
        AlgorithmIdentifier     signature;
        X509Name                issuer;
        Time                    thisUpdate;
        Time                    nextUpdate;
        CRLEntry[]              revokedCertificates;
        X509Extensions          crlExtensions;
    
        public static TBSCertList getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static TBSCertList getInstance(
            object  obj)
        {
            if (obj is TBSCertList)
            {
                return (TBSCertList)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new TBSCertList((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public TBSCertList(
            ASN1Sequence  seq)
        {
            int seqPos = 0;
    
            this.seq = seq;
    
            if (seq.getObjectAt(seqPos) is DERInteger)
            {
                version = (DERInteger)seq.getObjectAt(seqPos++);
            }
            else
            {
                version = new DERInteger(0);
            }
    
            signature = AlgorithmIdentifier.getInstance(seq.getObjectAt(seqPos++));
            issuer = X509Name.getInstance(seq.getObjectAt(seqPos++));
            thisUpdate = Time.getInstance(seq.getObjectAt(seqPos++));
    
            if (seqPos < seq.size()
                && (seq.getObjectAt(seqPos) is DERUTCTime
                   || seq.getObjectAt(seqPos) is DERGeneralizedTime
                   || seq.getObjectAt(seqPos) is Time))
            {
                nextUpdate = Time.getInstance(seq.getObjectAt(seqPos++));
            }
    
            if (seqPos < seq.size()
                && !(seq.getObjectAt(seqPos) is DERTaggedObject))
            {
                ASN1Sequence certs = (ASN1Sequence)seq.getObjectAt(seqPos++);
                revokedCertificates = new CRLEntry[certs.size()];
    
                for ( int i = 0; i < revokedCertificates.Length; i++)
                {
                    revokedCertificates[i] = new CRLEntry((ASN1Sequence)certs.getObjectAt(i));
                }
            }
    
            if (seqPos < seq.size()
                && seq.getObjectAt(seqPos) is DERTaggedObject)
            {
                crlExtensions = X509Extensions.getInstance(seq.getObjectAt(seqPos++));
            }
        }
    
        public int getVersion()
        {
            return version.getValue().intValue() + 1;
        }
    
        public DERInteger getVersionNumber()
        {
            return version;
        }
    
        public AlgorithmIdentifier getSignature()
        {
            return signature;
        }
    
        public X509Name getIssuer()
        {
            return issuer;
        }
    
        public Time getThisUpdate()
        {
            return thisUpdate;
        }
    
        public Time getNextUpdate()
        {
            return nextUpdate;
        }
    
        public CRLEntry[] getRevokedCertificates()
        {
            return revokedCertificates;
        }
    
        public X509Extensions getExtensions()
        {
            return crlExtensions;
        }
    
        public override ASN1Object toASN1Object()
        {
            return seq;
        }
    }
}
