using org.bouncycastle.asn1;

using System;
using System.Collections;
using System.IO;

namespace org.bouncycastle.asn1.x509
{
    /**
     * Generator for Version 2 TBSCertList structures.
     * <pre>
     *  TBSCertList  ::=  SEQUENCE  {
     *       version                 Version OPTIONAL,
     *                                    -- if present, shall be v2
     *       signature               AlgorithmIdentifier,
     *       issuer                  Name,
     *       thisUpdate              Time,
     *       nextUpdate              Time OPTIONAL,
     *       revokedCertificates     SEQUENCE OF SEQUENCE  {
     *            userCertificate         CertificateSerialNumber,
     *            revocationDate          Time,
     *            crlEntryExtensions      Extensions OPTIONAL
     *                                          -- if present, shall be v2
     *                                 }  OPTIONAL,
     *       crlExtensions           [0]  EXPLICIT Extensions OPTIONAL
     *                                          -- if present, shall be v2
     *                                 }
     * </pre>
     *
     * <b>Note: This class may be subject to change</b>
     */
    public class V2TBSCertListGenerator
    {
        DERInteger version = new DERInteger(1);
    
        AlgorithmIdentifier     signature;
        X509Name                issuer;
        Time                    thisUpdate, nextUpdate=null;
        X509Extensions          extensions=null;
        private ArrayList          crlentries=null;
    
        public V2TBSCertListGenerator()
        {
        }
    
    
        public void setSignature(
            AlgorithmIdentifier    signature)
        {
            this.signature = signature;
        }
    
        public void setIssuer(
            X509Name    issuer)
        {
            this.issuer = issuer;
        }
    
        public void setThisUpdate(
            DERUTCTime thisUpdate)
        {
            this.thisUpdate = new Time(thisUpdate);
        }
    
        public void setNextUpdate(
            DERUTCTime nextUpdate)
        {
            this.nextUpdate = new Time(nextUpdate);
        }
    
        public void setThisUpdate(
            Time thisUpdate)
        {
            this.thisUpdate = thisUpdate;
        }
    
        public void setNextUpdate(
            Time nextUpdate)
        {
            this.nextUpdate = nextUpdate;
        }
    
        public void addCRLEntry(
            ASN1Sequence crlEntry)
        {
            if (crlentries == null)
                crlentries = new ArrayList();
            crlentries.Add(crlEntry);
        }
    
        public void addCRLEntry(DERInteger userCertificate, DERUTCTime revocationDate, int reason)
        {
            addCRLEntry(userCertificate, new Time(revocationDate), reason);
        }
    
        public void addCRLEntry(DERInteger userCertificate, Time revocationDate, int reason)
        {
            ASN1EncodableVector v = new ASN1EncodableVector();
    
            v.add(userCertificate);
            v.add(revocationDate);
    
            if (reason != 0)
            {
                CRLReason rf = new CRLReason(reason);
                MemoryStream   bOut = new MemoryStream();
                DEROutputStream         dOut = new DEROutputStream(bOut);
                try
                {
                    dOut.writeObject(rf);
                }
                catch (IOException e)
                {
                    throw new ArgumentException("error encoding value: " + e);
                }
                byte[] value = bOut.ToArray();
                ASN1EncodableVector v1 = new ASN1EncodableVector();
                v1.add(X509Extensions.ReasonCode);
                v1.add(new DEROctetString(value));
                X509Extensions ex = new X509Extensions(new DERSequence(
                                                            new DERSequence(v1)));
                v.add(ex);
            }
    
            if (crlentries == null)
            {
                crlentries = new ArrayList();
            }
    
            crlentries.Add(new DERSequence(v));
        }
    
        public void setExtensions(
            X509Extensions    extensions)
        {
            this.extensions = extensions;
        }
    
        public TBSCertList generateTBSCertList()
        {
            if ((signature == null) || (issuer == null) || (thisUpdate == null))
            {
                throw new InvalidOperationException("Not all mandatory fields set in V2 TBSCertList generator.");
            }
    
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(version);
            v.add(signature);
            v.add(issuer);
    
            v.add(thisUpdate);
            if (nextUpdate != null)
            {
                v.add(nextUpdate);
            }
    
            // Add CRLEntries if they exist
            if (crlentries != null)
            {
                ASN1EncodableVector certs = new ASN1EncodableVector();
                IEnumerator it = crlentries.GetEnumerator();
                while( it.MoveNext() )
                {
                    certs.add((ASN1Sequence)it.Current);
                }
                v.add(new DERSequence(certs));
            }
    
            if (extensions != null)
            {
                v.add(new DERTaggedObject(0, extensions));
            }
    
            return new TBSCertList(new DERSequence(v));
        }
    }
}
