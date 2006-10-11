#region Using directives

using System;
using System.Collections;
using System.Text;
using org.bouncycastle.crypto;
using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.security;
using org.bouncycastle.x509;
using System.IO;
using org.bouncycastle.asn1.pkcs;

#endregion

namespace org.bouncycastle.pkcs
{
    /// <summary>
    /// A class for verifying and creating PKCS10 Certification requests. 
    /// </summary>
    /// <param name="signatureAlgorithm">Name of Sig Alg.</param>
    /// <param name="subject">X509Name of subject eg OU="My unit." O="My Organisatioin" C="au" </param>
    /// <param name="key">Public Key to be included in cert reqest.</param>
    /// <param name="attributes">ASN1Set of Attributes.</param>
    /// <param name="signingKey">Matching Private key for nominated (above) public key to be used to sign the request.</param>
    /// <code>
    /// CertificationRequest ::= SEQUENCE {
    ///   certificationRequestInfo  CertificationRequestInfo,
    ///   signatureAlgorithm        AlgorithmIdentifier{{ SignatureAlgorithms }},
    ///   signature                 BIT STRING
    /// }
    ///
    /// CertificationRequestInfo ::= SEQUENCE {
    ///   version             INTEGER { v1(0) } (v1,...),
    ///   subject             Name,
    ///   subjectPKInfo   SubjectPublicKeyInfo{{ PKInfoAlgorithms }},
    ///   attributes          [0] Attributes{{ CRIAttributes }}
    ///  }
    ///
    ///  Attributes { ATTRIBUTE:IOSet } ::= SET OF Attribute{{ IOSet }}
    ///
    ///  Attribute { ATTRIBUTE:IOSet } ::= SEQUENCE {
    ///    type    ATTRIBUTE.&id({IOSet}),
    ///    values  SET SIZE(1..MAX) OF ATTRIBUTE.&Type({IOSet}{\@type})
    ///  }
    /// </code>
    /// <see cref="http://www.rsasecurity.com/rsalabs/node.asp?id=2132"/>
    /// 
    public class PKCS10CertificationRequest
    {                               
        CertificationRequestInfo reqInfo;
        AlgorithmIdentifier      sigAlgId;
        DERBitString             sigBits;     
                               
        public PKCS10CertificationRequest(
            byte[] encoded) : this(new MemoryStream(encoded))
        {
        }
                                                                                
        public PKCS10CertificationRequest(
            Stream input) : this((ASN1Sequence)new ASN1InputStream(input).readObject())
        {
        }

       /// <summary>
       /// Instanciate a PKCS10CertificationRequest object with the necessary credentials.
       /// </summary>
       ///<param name="signatureAlgorithm">Name of Sig Alg.</param>
       /// <param name="subject">X509Name of subject eg OU="My unit." O="My Organisatioin" C="au" </param>
       /// <param name="key">Public Key to be included in cert reqest.</param>
       /// <param name="attributes">ASN1Set of Attributes.</param>
       /// <param name="signingKey">Matching Private key for nominated (above) public key to be used to sign the request.</param>
        public PKCS10CertificationRequest(String signatureAlgorithm,
                                            X509Name subject,
                                            AsymmetricKeyParameter key,
                                            ASN1Set attributes,
                                            AsymmetricKeyParameter signingKey)
                        
        {
            DERObjectIdentifier sigOID = SignerUtil.getObjectIdentifier(signatureAlgorithm.ToUpper());

            if (sigOID == null)
            {
                throw new ArgumentException("Unknown signature type requested");
            }

            if (subject == null)
            {
                throw new ArgumentException("subject must not be null");
            }

            if (key == null)
            {
                throw new ArgumentException("public key must not be null");
            }

            

            this.sigAlgId = new AlgorithmIdentifier(sigOID, null);

            SubjectPublicKeyInfo pubInfo = SubjectPublicKeyInfoFactory.CreateSubjectPublicKeyInfo(key);


            this.reqInfo = new CertificationRequestInfo(subject,pubInfo, attributes);

            Signer sig = null;

            // Create appropriate Signature.
            sig = SignerUtil.getSigner(sigAlgId.getObjectId());

            sig.init(true, signingKey);

            // Encode.

            MemoryStream mStr = new MemoryStream();
            DEROutputStream derOut = new DEROutputStream(mStr);
            derOut.writeObject(reqInfo);

            // Sign
            byte[] b = mStr.ToArray();
            sig.update(b,0,b.Length);
            
            // Generate Signature.
            sigBits = new DERBitString(sig.generateSignature());
        }

        internal PKCS10CertificationRequest(ASN1Sequence seq)
        {
            try
            {
                this.reqInfo = CertificationRequestInfo.getInstance(seq.getObjectAt(0));
                this.sigAlgId = AlgorithmIdentifier.getInstance(seq.getObjectAt(1));
                this.sigBits = (DERBitString)seq.getObjectAt(2);
            }
            catch (Exception ex)
            {
                throw new ArgumentException("Create From ASN1Sequence: " + ex.Message);
            }
        }


        /// <summary>
        /// Get the public key.
        /// </summary>
        /// <returns>The public key.</returns>
        public AsymmetricKeyParameter getPublicKey()
        {
            return PublicKeyFactory.CreateKey(reqInfo.getSubjectPublicKeyInfo());
        }


        /// <summary>
        /// Verify PKCS10 Cert Reqest is valid.
        /// </summary>
        /// <returns>true = valid.</returns>
        public bool verify()
        {
            Signer sig = null;

            sig = SignerUtil.getSigner(sigAlgId.getObjectId());

            sig.init(false, getPublicKey());

            MemoryStream mStr = new MemoryStream();
            DEROutputStream derOut = new DEROutputStream(mStr);
            derOut.writeObject(reqInfo);
            derOut.Flush();
            byte[] b = mStr.ToArray();
            sig.update(b,0,b.Length);
            mStr.Close();

            return sig.verifySignature(sigBits.getBytes());

        }

        /// <summary>
        /// Get the DER Encoded PKCS10 Certification Request.
        /// </summary>
        /// <returns>A byte array.</returns>
        public byte[] getEncoded()
        {
            MemoryStream mStr = new MemoryStream();
            DEROutputStream derOut = new DEROutputStream(mStr);
            derOut.writeObject(new CertificationRequest(reqInfo, sigAlgId, sigBits));
            derOut.Flush();
            byte[] _out = mStr.ToArray();
            mStr.Close();
            return _out;
        }


/// <summary>
/// Convert to DER Sequence.
/// </summary>
/// <param name="bytes"></param>
/// <returns></returns>
        private static ASN1Sequence toDERSequence(byte[] bytes)
        {
                MemoryStream bIn = new MemoryStream(bytes);
                ASN1InputStream dIn = new ASN1InputStream(bIn);
                return (ASN1Sequence)dIn.readObject();
        }
    }
}
