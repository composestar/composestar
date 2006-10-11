#region Using directives

using System;
using System.Collections;
using System.Text;
using org.bouncycastle.asn1.x509;
using System.IO;
using org.bouncycastle.asn1;
using org.bouncycastle.crypto;
using org.bouncycastle.math;
using org.bouncycastle.security;

#endregion

namespace org.bouncycastle.x509
{
    /// <summary>
    /// An Object representing an X509 Certificate.
    /// Has static methods for loading Certificates encoded in many forms that return X509Certificate Objects.
    /// </summary>
    public class X509Certificate
    {
        private X509CertificateStructure xStruct = null;
        private Hashtable pkcs12Attributes = new Hashtable();
        private ArrayList pkcs12Ordering = new ArrayList();
        
        internal X509Certificate(ASN1Sequence seq)
        {
            this.xStruct = X509CertificateStructure.getInstance(seq);
        }


        /// <summary>
        /// Load certificate from byte array.
        /// </summary>
        /// <param name="encoded">Byte array containing encoded X509Certificate.</param>
        public X509Certificate(
            byte[] encoded) : this(new MemoryStream(encoded))
        {
        }

        /// <summary>
        /// Load certificate from Stream.
        /// Must be positioned at start of certificate.
        /// </summary>
        /// <param name="input"></param>
        public X509Certificate(
            Stream input) : this((ASN1Sequence)new ASN1InputStream(input).readObject())
        {
        }

        /// <summary>
        /// Retrun true if the current time is within the start and end times nominated on the certificate.
        /// </summary>
        /// <returns>true id certificate is valid for the current time.</returns>
        public bool isValid()
        {
            return isValid(DateTime.Now);
        }


        /// <summary>
        /// Return true if the nominated time is within the start and end times nominated on the certificate.
        /// </summary>
        /// <param name="time">The time to test validity against.</param>
        /// <returns>True if certificate is valid for nominated time.</returns>
        public bool isValid(DateTime time)
        {
            return (time.CompareTo(getNotBefore()) >= 0 && time.CompareTo(getNotAfter()) <= 0);
        }

        /// <summary>
        /// Verify the certificates signature using the nominated public key.
        /// </summary>
        /// <param name="key">An appropriate public key parameter object, RSAPublicKeyParameters, DSAPublicKeyParameters or ECDSAPublicKeyParameters</param>
        /// <returns>True if the signature is valid.</returns>
        /// <exception cref="Exception">If key submitted is not of the above nomiated types.</exception>
        public bool verify(AsymmetricKeyParameter key)
        {
            Signer signature = null;

            try
            {
                signature = SignerUtil.getSigner(xStruct.getSignatureAlgorithm().getObjectId());

            }
            catch
            {
                throw new Exception("Signature not recognised.");
            }

            signature.init(false,key);
            byte[] b = this.getTBSCertificate();
            signature.update(b,0,b.Length);

            if (!signature.verifySignature(this.getSignature()))
            {
                throw new Exception("Public key presented not for certificate signature");
            }
            return true;
        }



        /// <summary>
        /// Return the DER encoded TBSCertificate data.
        /// This is the certificate component less the signature.
        /// To get the whole certificate call the getEncoded() member.
        /// </summary>
        /// <returns>A byte array containing the DER encoded Certificate component.</returns>
        
        public byte[] getTBSCertificate()
        {
            MemoryStream   bOut = new MemoryStream();
            DEROutputStream dOut = new DEROutputStream(bOut);
            dOut.writeObject(xStruct.getTBSCertificate());
            return bOut.ToArray();
        }


        /// <summary>
        /// Return the certificate's version.
        /// </summary>
        /// <returns>An integer whose value equals the version of the cerficate.</returns>
        public int getVersion()
        {
            return xStruct.getVersion();
        }


        /// <summary>
        /// Return a <see cref="org.bouncycastle.math.BigInteger">BigInteger</see> containing the serial number.
        /// </summary>
        /// <returns>The Serial number.</returns>
        public BigInteger getSerialNumber()
        {
            return xStruct.getSerialNumber().getValue();
        }


        /// <summary>
        /// Get the Issuer Distinguished Name. (Who signed the certificate.)
        /// </summary>
        /// <returns>And X509Object containing name and value pairs.</returns>
        
        public X509Name getIssuerDN()
        {
            return xStruct.getIssuer();
        }


        /// <summary>
        /// Get the subject of this certificate.
        /// </summary>
        /// <returns>An X509Name object containing name and value pairs.</returns>
        public X509Name getSubjectDN()
        {
            return xStruct.getSubject();
        }

       
        /// <summary>
        /// The time that this certificate is valid up to.
        /// </summary>
        /// <returns>A DateTime object representing that time in the local time zone.</returns>
        public DateTime getNotAfter()
        {
            return xStruct.getEndDate().toDateTime();
        }


        /// <summary>
        /// The time that this certificate is valid from.
        /// </summary>
        /// <returns>A DateTime object representing that time in the local time zone.</returns>
        public DateTime getNotBefore()
        {
            return xStruct.getStartDate().toDateTime();
        }


        /// <summary>
        /// Get the public key of the subject of the certificate.
        /// </summary>
        /// <returns>The public key parameters.</returns>
        public AsymmetricKeyParameter getPublicKey()
        {
            return PublicKeyFactory.CreateKey(xStruct.getSubjectPublicKeyInfo());
        }

        /// <summary>
        /// Return a DER encoded version of this certificate.
        /// </summary>
        /// <returns>A byte array.</returns>
        public byte[] getEncoded()
        {
            MemoryStream bOut = new MemoryStream();
            DEROutputStream dOut = new DEROutputStream(bOut);

            try
            {
                dOut.writeObject(xStruct);
                return bOut.ToArray();
            }
            catch (IOException e)
            {
                throw new Exception(e.Message);
            }
        }

        /// <summary>
        /// The signature.
        /// </summary>
        /// <returns>A byte array containg the signature of the certificate.</returns>
        public byte[] getSignature()
        {
            return xStruct.getSignature().getBytes();
        }

        /// <summary>
        /// A meaningful version of the Signature Algorithm. (EG SHA1WITHRSA)
        /// </summary>
        /// <returns>A sting representing the signature algorithm.</returns>
        public string getSigAlgName()
        {
            return SignerUtil.getEncodingName(xStruct.getSignatureAlgorithm().getObjectId());
        }


        /// <summary>
        /// Get the Signature Algorithms Object ID. 
        /// </summary>
        /// <returns>A string containg a '.' separated object id.</returns>
        public string getSigAlgOID()
        {
            return xStruct.getSignatureAlgorithm().getObjectId().getId();
        }


        /// <summary>
        /// Get the signature algorithms parameters. (EG DSA Parameters)
        /// </summary>
        /// <returns>A byte array containing the DER encoded version of the parameters or null if there are none.</returns>
        public byte[] getSigAlgParams()
        {
            MemoryStream bOut = new MemoryStream();

            if (xStruct.getSignatureAlgorithm().getParameters() != null)
            {
                try
                {
                    DEROutputStream dOut = new DEROutputStream(bOut);
                    dOut.writeObject(xStruct.getSignatureAlgorithm().getParameters());
                }
                catch (Exception e)
                {
                    throw new Exception("exception getting sig parameters " + e);
                }

                return bOut.ToArray();
            }
            else
            {
                return null;
            }
        }

        /// <summary>
        /// Get the issuers UID.
        /// </summary>
        /// <returns>A DERBitString.</returns>
        public DERBitString getIssuerUniqueID()
        {
            return xStruct.getTBSCertificate().getIssuerUniqueId();
        }


        /// <summary>
        /// Get the subjects UID.
        /// </summary>
        /// <returns>A DERBitString.</returns>
        public DERBitString getSubjectUniqueID()
        {
            return xStruct.getTBSCertificate().getSubjectUniqueId();
        }


        /// <summary>
        /// Get a key usage guidlines.
        /// </summary>
        /// <returns>A DER it string or null if they are not specified.</returns>
        public KeyUsage getKeyUsage()
        {
            byte[]  bytes = this.getExtensionBytes("2.5.29.15");
            if (bytes != null)
            {
                try
                {
                    ASN1InputStream  dIn = new ASN1InputStream(new MemoryStream(bytes));
                    return new KeyUsage(DERBitString.getInstance(dIn.readObject()));
                }
                catch
                {
                    throw new Exception("error processing key usage extension");
                }

            }

            return null;
        }

        /// <summary>
        /// Get the value of a given extension.
        /// </summary>
        /// <param name="oid">The object ID of the extension. </param>
        /// <returns>An X509Entention object if that extension is found or null if not.</returns>
        public X509Extension getExtensionValue(String oid)
        {
            X509Extensions exts = xStruct.getTBSCertificate().getExtensions();
            if (exts == null)
            {
                return null;
            }

            return exts.getExtension(new DERObjectIdentifier(oid));
        }


        /// <summary>
        /// Get non critical extensions.
        /// </summary>
        /// <returns>A sorted list of non critical extensions.</returns>
        public SortedList getNonCriticalExtensionOIDs()
        {
            if (xStruct.getVersion() == 3)
            {
                SortedList set = new SortedList();
                X509Extensions extensions = xStruct.getTBSCertificate().getExtensions();

                if (extensions != null)
                {
                    IEnumerator e = extensions.oids();

                    while (e.MoveNext())
                    {
                        DERObjectIdentifier oid = (DERObjectIdentifier)e.Current; ;
                        X509Extension ext = extensions.getExtension(oid);

                        if (!ext.isCritical())
                        {
                            set.Add(oid.getId(), ext);
                        }
                    }

                    return set;
                }
            }

            return null;
        }

        /// <summary>
        /// Get any critical extensions.
        /// </summary>
        /// <returns>A sorted list of critical entension.</returns>
        public SortedList getCriticalExtensionOIDs()
        {
            if (xStruct.getVersion() == 3)
            {
                SortedList set = new SortedList();
                X509Extensions extensions = xStruct.getTBSCertificate().getExtensions();

                if (extensions != null)
                {
                    IEnumerator e = extensions.oids();

                    while (e.MoveNext())
                    {
                        DERObjectIdentifier oid = (DERObjectIdentifier)e.Current; ;
                        X509Extension ext = extensions.getExtension(oid);

                        if (ext.isCritical())
                        {
                            set.Add(oid.getId(), ext);
                        }
                    }

                    return set;
                }
            }

            return null;
        }

        /// <summary>
        /// Fetch the value of a given Extenstion.
        /// </summary>
        /// <param name="oid">The string representation of the oid.</param>
        /// <returns>A byte array or null if not found.</returns>
        private byte[] getExtensionBytes(string oid)
        {
            X509Extensions exts = xStruct.getTBSCertificate().getExtensions();

            if (exts != null)
            {
                X509Extension ext = exts.getExtension(new DERObjectIdentifier(oid));
                if (ext != null)
                {
                    return ext.getValue().getOctets();
                }
            }

            return null;
        }
    } // Class
}
