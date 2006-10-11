#region Using directives

using System;
using System.Text;
using org.bouncycastle.asn1.x509;
using System.Collections;
using org.bouncycastle.asn1;
using System.IO;
using org.bouncycastle.crypto;
using org.bouncycastle.x509;
using org.bouncycastle.security;
using org.bouncycastle.asn1.ocsp;
using org.bouncycastle.crypto.parameters;

#endregion

namespace org.bouncycastle.ocsp
{
    public class OCSPRespGenerator
    {
        private ArrayList list = new ArrayList();

        private GeneralName requestorName = null;
        private X509Extensions requestExtensions = null;

        private ASN1Object makeObj(byte[] encoding)
        {
            if (encoding == null)
            {
                return null;
            }
            MemoryStream bIn = new MemoryStream(encoding);
            ASN1InputStream aIn = new ASN1InputStream(bIn);
            return aIn.readObject();
        }


        public void addRequest(CertificateID certId)
        {
            list.Add(new RRequestObject(certId, null));
        }

        public void addRequest(CertificateID certId,X509Extensions requestExtensions)
        {
            list.Add(new RRequestObject(certId, requestExtensions));
        }

        public void setRequestorName(GeneralName requestorName)
        {
            this.requestorName = requestorName;
        }

        public void setRequestExtensions(X509Extensions requestExtensions)
        {
            this.requestExtensions = requestExtensions;
        }

        /// <summary>
        /// Generate an OCSP Resonse.
        /// </summary>
        /// <param name="signingAlgorithm">The DER OID of the signing algorithm.</param>
        /// <param name="key">The private key used to sign this request.</param>
        /// <param name="chain">A chain of X509Certificates, can be null.</param>
        /// <param name="random">A Secure random instance which can be null.</param>
        /// <returns>An OCSPReq object.</returns>
         public OCSPReq generateRequest(
            DERObjectIdentifier signingAlgorithm,
            AsymmetricKeyParameter key,
            X509Certificate[]   chain,
            SecureRandom        random)
            {
                IEnumerator it = list.GetEnumerator();

                ASN1EncodableVector requests = new ASN1EncodableVector();

                while (it.MoveNext())
                    {
                        try
                            {
                                requests.add(((RRequestObject)it.Current).toRequest());
                            } catch (Exception e)
                                {
                                    throw new OCSPException("exception creating Request", e);
                                }
                    }

        TBSRequest  tbsReq = new TBSRequest(requestorName, new DERSequence(requests), requestExtensions);

        Signer sig = null;
        Signature               signature = null;

        if (signingAlgorithm != null)
        {
            try
            {
                sig = SignerUtil.getSigner(signingAlgorithm);
                if (random != null)
                {
                    sig.init(true, new ParametersWithRandom(key, random));
                }
                else
                {
                    sig.init(true,key);
                }

            }
            catch (Exception e)
            {
                throw new OCSPException("exception creating signature: " + e, e);
            }

            DERBitString    bitSig = null;

            try
            {
                MemoryStream   bOut = new MemoryStream();
                ASN1OutputStream        aOut = new ASN1OutputStream(bOut);
                aOut.writeObject(tbsReq);
                byte[] b = bOut.ToArray();
                sig.update(b,0,b.Length);
                bitSig = new DERBitString(sig.generateSignature());
            }
            catch (Exception e)
            {
                throw new OCSPException("exception processing TBSRequest: " + e, e);
            }

            AlgorithmIdentifier sigAlgId = new AlgorithmIdentifier(signingAlgorithm, new DERNull());

            if (chain != null && chain.Length > 0)
            {
                ASN1EncodableVector v = new ASN1EncodableVector();
                try
                {
                    for (int i = 0; i != chain.Length; i++)
                    {
                        v.add(new X509CertificateStructure(
                                (ASN1Sequence)makeObj(chain[i].getEncoded())));
                    }
                }
                catch (Exception e)
                {
                    throw new OCSPException("error processing certs", e);
                }
                

                signature = new Signature(sigAlgId, bitSig, new DERSequence(v));
            }
            else
            {
                signature = new Signature(sigAlgId, bitSig);
            }
        }

        return new OCSPReq(new OCSPRequest(tbsReq, signature));
    }






    }

    /// <summary>
    /// Response Request Object
    /// </summary>
    internal class RRequestObject
    {
        CertificateID   certId;
        X509Extensions  extensions;

        public RRequestObject(CertificateID   certId, X509Extensions  extensions)
        {
            this.certId = certId;
            this.extensions = extensions;
        }

        public Request toRequest()
        {
            return new Request(certId.toASN1Object(), extensions);
        }
    } // end of RRequestObject


  

}
