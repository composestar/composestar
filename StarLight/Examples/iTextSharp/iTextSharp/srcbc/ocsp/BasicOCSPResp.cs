#region Using directives

using System;
using System.Text;
using org.bouncycastle.asn1.ocsp;
using org.bouncycastle.x509;
using org.bouncycastle.asn1;
using System.Collections;
using org.bouncycastle.crypto;
using org.bouncycastle.security;
using System.IO;

#endregion

namespace org.bouncycastle.ocsp
{
    public class BasicOCSPResp
    {
        
        BasicOCSPResponse resp;

        /// <summary>
        /// Construct using an asn1 BasicOCSPResponse object as a reference.
        /// </summary>
        /// <param name="resp">The reference BasicOCSPResponse object.</param>
        public BasicOCSPResp(BasicOCSPResponse resp)
        {
            this.resp = resp;
        }

        /// <summary>
        /// Return then dotted decimal OID of the sugnature algorithm.
        /// </summary>
        /// <returns>Dotted decimal string.</returns>
        public String getSignatureAlgOID()
        {
            return resp.getSignatureAlgorithm().getObjectId().getId();
        }

        /// <summary>
        /// Fetch the Response Data.
        /// </summary>
        /// <returns>A RespData object.</returns>
        public RespData getResponseData()
        {
            return new RespData(resp.getTbsResponseData());
        }

        /// <summary>
        /// Return the raw signature.
        /// </summary>
        /// <returns>byte array.</returns>
        public byte[] getSignature()
        {
            return resp.getSignature().getBytes();
        }

        
        /// <summary>
        /// Return a list of certificates.
        /// </summary>
        /// <returns>X509Certificate array or null if no certificates found.</returns>
        public X509Certificate[] getCerts()
        {
            ArrayList list = new ArrayList();
            ASN1Sequence s = resp.getCerts();

            if (s != null)
            {
                IEnumerator e = s.getObjects();
                while (e.MoveNext())
                {
                    list.Add(e.Current);
                }

                return (X509Certificate[])list.ToArray(Type.GetType("org.bouncycastle.x509.X509Certificate"));
            }
            return null;
        }
    
        /// <summary>
        /// Verify that requests' signature is valid.
        /// </summary>
        /// <param name="key">A public key to verify with.</param>
        /// <returns>true = verified.</returns>
         public bool verify(AsymmetricKeyParameter key)
         {
            Signer signer = SignerUtil.getSigner(new DERObjectIdentifier(this.getSignatureAlgOID()));
            signer.init(false,key);
            byte[] b = resp.getTbsResponseData().getEncoded();
            signer.update(b,0,b.Length);
            return signer.verifySignature(this.getSignature());
         }

        /// <summary>
        /// Return a the DER encoded response.
        /// </summary>
        /// <returns>A byte array.</returns>
         public byte[] getEncoded() 
         {
            MemoryStream   bOut = new MemoryStream();
            ASN1OutputStream   aOut = new ASN1OutputStream(bOut);
            aOut.writeObject(resp);
            return bOut.ToArray();
        }

    } // end of class.
    
    
}
