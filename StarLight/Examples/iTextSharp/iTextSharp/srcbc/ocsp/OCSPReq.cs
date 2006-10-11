#region Using directives

using System;
using System.Text;
using System.IO;
using org.bouncycastle.asn1.ocsp;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.asn1;
using System.Collections;
using org.bouncycastle.x509;
using org.bouncycastle.crypto;
using org.bouncycastle.security;

#endregion

namespace org.bouncycastle.ocsp
{
    public class OCSPReq
    {

        private OCSPRequest req;
        private X509Certificate[] chain = null;

        public OCSPReq(OCSPRequest req)
        {
            this.req = req;
        }

        public OCSPReq(byte[] req): this(new MemoryStream(req))
        {
        
        }

        public OCSPReq(Stream instr)
        {
            req = OCSPRequest.getInstance(new ASN1InputStream(instr).readObject());
        }

        public int getVersion()
        {
            return req.getTbsRequest().getVersion().getValue().intValue() + 1;
        }

        public GeneralName getRequestorName()
        {
            return GeneralName.getInstance(req.getTbsRequest().getRequestorName());
        }

        public Req[] getRequestList()
        {
            ASN1Sequence seq = req.getTbsRequest().getRequestList();
            Req[] requests = new Req[seq.size()];

            for (int i = 0; i != requests.Length; i++)
            {
                requests[i] = new Req(Request.getInstance(seq.getObjectAt(i)));
            }

            return requests;
        }


        public X509Extensions getRequestExtensions()
        {
            return X509Extensions.getInstance(req.getTbsRequest().getRequestExtensions());
        }

        /**
     * return the object identifier representing the signature algorithm
     */
        public String getSignatureAlgOID()
        {
            if (!this.isSigned())
            {
                return null;
            }

            return req.getOptionalSignature().getSignatureAlgorithm().getObjectId().getId();
        }

        public byte[] getSignature()
        {
            if (!this.isSigned())
            {
                return null;
            }

            return req.getOptionalSignature().getSignature().getBytes();
        }


        /// <summary>
        /// Return a the certificates associated with this request.
        /// </summary>
        /// <returns>An array of certs or null is none were present in this request.</returns>
        public X509Certificate[] getCerts()
        {
            if (!this.isSigned())
            {
                return null;
            }

            if (chain == null)
            {
                ASN1Sequence s = req.getOptionalSignature().getCerts();

                if (s != null)
                {
                    ArrayList lst = new ArrayList();

                    IEnumerator e = s.getObjects();
                    while (e.MoveNext())
                    {
                        lst.Add(new X509CertificateStructure((ASN1Sequence)e.Current));
                    }

                    chain = (X509Certificate[])lst.ToArray(Type.GetType("org.bouncycastle.x509.X509Certificate"));

                }

                return chain;
            }
            return null;
        }

   
    public bool isSigned()
    {
        return req.getOptionalSignature() != null;
    }

   
   
    public bool verify(AsymmetricKeyParameter key)
        {
        try {
            if (!this.isSigned())
            {
                throw new OCSPException("attempt to verify signature on unsigned object");
            }
       
            // -------------------------------------------------------------
            Signer sig = SignerUtil.getSigner(this.getSignatureAlgOID());
            // The above may not work as not sure if key is dotted decimal oid as string or an ALG mechanism name.
            // -------------------------------------------------------------
               

            sig.init(false,key);

            MemoryStream mStr = new MemoryStream();
            ASN1OutputStream        aOut = new ASN1OutputStream(mStr);

            aOut.writeObject(req.getTbsRequest());

            byte[] b = mStr.ToArray();

            sig.update(b,0,b.Length);

            return sig.verifySignature(this.getSignature());
        }
        catch (Exception e)
        {
            throw new OCSPException("exception processing sig: " + e, e);
        }
    }

    
        
    public byte[] getEncoded()
       {
           MemoryStream mStr = new MemoryStream();
           ASN1OutputStream aOut = new ASN1OutputStream(mStr);
           aOut.writeObject(req);
           return mStr.ToArray();
        }
    }
}
