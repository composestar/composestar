#region Using directives

using System;
using System.Text;
using System.Collections;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.asn1.ocsp;
using org.bouncycastle.asn1;
using System.IO;
using org.bouncycastle.crypto;
using org.bouncycastle.security;
using org.bouncycastle.crypto.parameters;
using org.bouncycastle.x509;

#endregion

namespace org.bouncycastle.ocsp
{
    /// <summary>
    /// A OCSP Basic Response Generator.
    /// </summary>
    public class BasicOCSPRespGenerator
    {
       internal ArrayList list = new ArrayList();
       internal GeneralName responderName = null;
       internal X509Extensions responseExtensions = null;
       internal RespID responderID;


    private ASN1Object makeObj(byte[]  encoding)
    {
        if (encoding == null)
        {
            return null;
        }
        MemoryStream    bIn = new MemoryStream(encoding);
        ASN1InputStream aIn = new ASN1InputStream(bIn);
        return aIn.readObject();
    }

 
        
        /// <summary>
        /// A basic contructor setting the RespID.
        /// </summary>
        /// <param name="responderID">An asn1 RespID object.</param>
        public BasicOCSPRespGenerator(RespID  responderID)
        {
            this.responderID = responderID;
        }

    
    
    
    /// <summary>
    /// Construct with the responderID to be the SHA-1 keyHash of the passed in public key.
    /// </summary>
    /// <param name="key">The key for the hash to be taken.</param>
    
        public BasicOCSPRespGenerator(AsymmetricKeyParameter key)
        {
            this.responderID = new RespID(key);
        }


        /// <summary>
        /// 
        /// </summary>
        /// <param name="certID"></param>
        /// <param name="certStatus"></param>
        public void addResponse(CertificateID certID, CertificateStatus certStatus)
        {
            list.Add(new ResponseObject(certID, certStatus, DateTime.Now.ToUniversalTime(), null, null));
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="certID"></param>
        /// <param name="certStatus"></param>
        /// <param name="nextUpdate"></param>
        /// <param name="singleExtensions"></param>
        public void addResponse(CertificateID certID,CertificateStatus certStatus,DateTime nextUpdate,X509Extensions singleExtensions)
        {
            list.Add(new ResponseObject(certID, certStatus, DateTime.Now.ToUniversalTime(), nextUpdate, singleExtensions));
        }


        /// <summary>
        /// 
        /// </summary>
        /// <param name="certID"></param>
        /// <param name="certStatus"></param>
        /// <param name="thisUpdate"></param>
        /// <param name="nextUpdate"></param>
        /// <param name="singleExtensions"></param>
        public void addResponse(
                CertificateID certID,
                CertificateStatus certStatus,
                DateTime thisUpdate,
                DateTime nextUpdate,
                X509Extensions singleExtensions)
        {
            list.Add(new ResponseObject(certID, certStatus, thisUpdate, nextUpdate, singleExtensions));
        }

    
        public void setResponseExtensions(X509Extensions responseExtensions)
        {
            this.responseExtensions = responseExtensions;
        }

/// <summary>
/// 
/// </summary>
/// <param name="signingAlgorithm">The OID of the signing algorithm.</param>
/// <param name="privkey">The signing private key.</param>
/// <param name="chain">An array containing X509Certificate objects, can be null.</param>
/// <param name="producedAt">The time this response is produced at.</param>
/// <param name="random">A SecureRandom instance.</param>
/// <returns></returns>
public BasicOCSPResp generateResponse(
        DERObjectIdentifier signingAlgorithm,
        AsymmetricKeyParameter privkey,
        X509Certificate[]   chain,
        DateTime					producedAt,
        SecureRandom        random)
     {
         IEnumerator it = list.GetEnumerator();
         ASN1EncodableVector responses = new ASN1EncodableVector();

        while (it.MoveNext())
        {
            try
            {
                responses.add(((ResponseObject)it.Current).toResponse());
            }
            catch (Exception e)
            {
                throw new OCSPException("exception creating Request", e);
            }
        }

        ResponseData  tbsResp = new ResponseData(new DERInteger(0), responderID.toASN1Object(), new DERGeneralizedTime(producedAt), new DERSequence(responses), responseExtensions);

        Signer sig = null;

        try
        {
            sig = SignerUtil.getSigner(signingAlgorithm);
            if (random != null)
            {
                sig.init(true,new ParametersWithRandom(privkey, random));
            }
            else
            {
                sig.init(true,privkey);
            }

        }
        catch (Exception e)
        {
            throw new OCSPException("exception creating signature: " + e, e);
        }
        
        DERBitString    bitSig = null;

        try
        {
            MemoryStream    bOut = new MemoryStream();
            DEROutputStream dOut = new DEROutputStream(bOut);

            dOut.writeObject(tbsResp);
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
            catch (IOException e)
            {
                throw new OCSPException("error processing certs", e);
            }
            

            return new BasicOCSPResp(new BasicOCSPResponse(tbsResp, sigAlgId, bitSig, new DERSequence(v)));
        }
        else
        {
            return new BasicOCSPResp(new BasicOCSPResponse(tbsResp, sigAlgId, bitSig, null));
        }
    }



    } // end of BasicOCSPRespGenerator.





    /// <summary>
    /// An internal ResponseObject class.
    /// Not intended for external use.
    /// </summary>
    internal class ResponseObject
    {
        CertificateID       		certId;
        CertStatus          		certStatus;
        DERGeneralizedTime	thisUpdate;
        DERGeneralizedTime  	nextUpdate;
        X509Extensions      	extensions;

        public ResponseObject(CertificateID certId, CertificateStatus certStatus,DateTime thisUpdate,Object nextUpdate, X509Extensions extensions)
        {
            this.certId = certId;
            if (certStatus == null)
            {
                this.certStatus = new CertStatus();
            }
	        else if (certStatus is UnknownStatus)
            {
		        this.certStatus = new CertStatus(2, new DERNull());
            }
            else 
            {
				RevokedStatus rs = (RevokedStatus)certStatus;
				
				if (rs.hasRevocationReason())
				{
					this.certStatus = new CertStatus(
                            new RevokedInfo(new DERGeneralizedTime(rs.getRevocationTime()),
                            new CRLReason(rs.getRevocationReason()))
                            );
				}
				else
				{
					this.certStatus = new CertStatus(
											new RevokedInfo(new DERGeneralizedTime(rs.getRevocationTime()), null));
				}
            }

			this.thisUpdate = new DERGeneralizedTime(thisUpdate);
			
            if (nextUpdate != null)
            {
                this.nextUpdate = new DERGeneralizedTime((DateTime)nextUpdate);
            }
            
            this.extensions = extensions;
        }

        public SingleResponse toResponse()
          
        {
            return new SingleResponse(certId.toASN1Object(), certStatus, thisUpdate, nextUpdate, extensions);
        }
} // end of internal class.




}
