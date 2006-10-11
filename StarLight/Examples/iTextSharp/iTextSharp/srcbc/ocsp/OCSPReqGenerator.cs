#region Using directives

using System;
using System.Text;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.asn1.ocsp;
using System.IO;
using org.bouncycastle.asn1;
using System.Collections;
using org.bouncycastle.crypto;
using org.bouncycastle.x509;
using org.bouncycastle.security;
using org.bouncycastle.crypto.parameters;

#endregion

namespace org.bouncycastle.ocsp
{

 class RequestObject
    {
        CertificateID   certId;
        X509Extensions  extensions;

        public RequestObject(
            CertificateID   certId,
            X509Extensions  extensions)
        {
            this.certId = certId;
            this.extensions = extensions;
        }

        public Request toRequest()
        {
            return new Request(certId.toASN1Object(), extensions);
        }
    }





    public class OCSPReqGenerator
    {
        private ArrayList list = new ArrayList();
        private GeneralName requestorName = null;
        private X509Extensions requestExtensions = null;


        public OCSPReqGenerator()
        {
        
        }

    private ASN1Object makeObj(byte[]  encoding)
    {
        if (encoding == null)
        {
            return null;
        }

        MemoryStream mStr = new MemoryStream(encoding);
        ASN1InputStream         aIn = new ASN1InputStream(mStr);
        return aIn.readObject();
    }


    public void addRequest(CertificateID certId)
    {
        list.Add(new RequestObject(certId, null));
    }

    public void addRequest(CertificateID certId, X509Extensions requestExtensions)
    {
        list.Add(new RequestObject(certId, requestExtensions));
    }

    public void setRequestorName(GeneralName requestorName)
    {
        this.requestorName = requestorName;
    }

    public void setRequestExtensions(X509Extensions requestExtensions)
    {
        this.requestExtensions = requestExtensions;
    }



    private OCSPReq generateRequest(DERObjectIdentifier signingAlgorithm,
        AsymmetricKeyParameter          key,
        X509Certificate[]   chain,
        SecureRandom        random)
        
        {
            IEnumerator it = list.GetEnumerator();
            ASN1EncodableVector requests = new ASN1EncodableVector();
            Signature signature = null;

            while (it.MoveNext())
            {
                requests.add(((RequestObject)it.Current).toRequest());
            }
             

            TBSRequest  tbsReq = new TBSRequest(requestorName, new DERSequence(requests), requestExtensions);

        Signer sig = null;

            if (signingAlgorithm != null)
            {
              try {
    
                    
                    sig = SignerUtil.getSigner(signingAlgorithm.getId());
                    
                    if (random != null)
                    {
                        sig.init(true,new ParametersWithRandom(key,random));
                    }
                    else
                    {
                        sig.init(true,key);
                    }

            }
            catch (Exception e)
            {
                throw new OCSPException("exception creating signature: " + e.Message, e);
            }
            
            DERBitString    bitSig = null;

            try
            {
                MemoryStream bOut = new MemoryStream();
                ASN1OutputStream  aOut = new ASN1OutputStream(bOut);

                aOut.writeObject(tbsReq);

                byte[] b= bOut.ToArray();
                sig.update(b,0,b.Length);

                bitSig = new DERBitString(sig.generateSignature());
            }
            catch (Exception e)
            {
                throw new OCSPException("exception processing TBSRequest: " + e.Message, e);
            }

            AlgorithmIdentifier sigAlgId = new AlgorithmIdentifier(signingAlgorithm, new DERNull());

            if (chain != null && chain.Length > 0)
            {
                ASN1EncodableVector v = new ASN1EncodableVector();
                try
                {
                    for (int i = 0; i != chain.Length; i++)
                    {
                        v.add(new X509CertificateStructure((ASN1Sequence)makeObj(chain[i].getEncoded())));
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
    
    public OCSPReq generate()
    {
        try
        {
            return generateRequest(null, null, null, null);
        }
        catch (Exception e)
        {
            
            throw new OCSPException(e.Message, e);
        }
    }



}
}
