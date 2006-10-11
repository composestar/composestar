using System;
using System.Collections;
using System.Text;
using System.IO;
using org.bouncycastle.x509;
using org.bouncycastle.asn1;
using org.bouncycastle.security;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;
using org.bouncycastle.math;
using org.bouncycastle.crypto.digests;
using org.bouncycastle.util;

/*
 * Copyright 2004 by Paulo Soares.
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is 'iText, a free JAVA-PDF library'.
 *
 * The Initial Developer of the Original Code is Bruno Lowagie. Portions created by
 * the Initial Developer are Copyright (C) 1999, 2000, 2001, 2002 by Bruno Lowagie.
 * All Rights Reserved.
 * Co-Developer of the code is Paulo Soares. Portions created by the Co-Developer
 * are Copyright (C) 2000, 2001, 2002 by Paulo Soares. All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * LGPL license (the "GNU LIBRARY GENERAL PUBLIC LICENSE"), in which case the
 * provisions of LGPL are applicable instead of those above.  If you wish to
 * allow use of your version of this file only under the terms of the LGPL
 * License and not to allow others to use your version of this file under
 * the MPL, indicate your decision by deleting the provisions above and
 * replace them with the notice and other provisions required by the LGPL.
 * If you do not delete the provisions above, a recipient may use your version
 * of this file under either the MPL or the GNU LIBRARY GENERAL PUBLIC LICENSE.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the MPL as stated above or under the terms of the GNU
 * Library General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library general Public License for more
 * details.
 *
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * http://www.lowagie.com/iText/
 */
namespace iTextSharp.text.pdf {
    /**
    * This class does all the processing related to signing and verifying a PKCS#7
    * signature.
    * <p>
    * It's based in code found at org.bouncycastle.
    */
    public class PdfPKCS7 {

        private byte[] sigAttr;
        private byte[] digestAttr;
        private int version, signerversion;
        private Hashtable digestalgos;
        private ArrayList certs, crls;
        private X509Certificate signCert;
        private byte[] digest;
        private Digest messageDigest;
        private String digestAlgorithm, digestEncryptionAlgorithm;
        private Signer sig;
        private CipherParameters privKey;
        private byte[] RSAdata;
        private bool verified;
        private bool verifyResult;
        private byte[] externalDigest;
        private byte[] externalRSAdata;
        
        private const String ID_PKCS7_DATA = "1.2.840.113549.1.7.1";
        private const String ID_PKCS7_SIGNED_DATA = "1.2.840.113549.1.7.2";
        private const String ID_MD5 = "1.2.840.113549.2.5";
        private const String ID_MD2 = "1.2.840.113549.2.2";
        private const String ID_SHA1 = "1.3.14.3.2.26";
        private const String ID_RSA = "1.2.840.113549.1.1.1";
        private const String ID_DSA = "1.2.840.10040.4.1";
        private const String ID_CONTENT_TYPE = "1.2.840.113549.1.9.3";
        private const String ID_MESSAGE_DIGEST = "1.2.840.113549.1.9.4";
        private const String ID_SIGNING_TIME = "1.2.840.113549.1.9.5";
        private const String ID_MD2RSA = "1.2.840.113549.1.1.2";
        private const String ID_MD5RSA = "1.2.840.113549.1.1.4";
        private const String ID_SHA1RSA = "1.2.840.113549.1.1.5";
        /**
        * Holds value of property reason.
        */
        private String reason;
        
        /**
        * Holds value of property location.
        */
        private String location;
        
        /**
        * Holds value of property signDate.
        */
        private DateTime signDate;
        
        /**
        * Holds value of property signName.
        */
        private String signName;
        
        /**
        * Verifies a signature using the sub-filter adbe.x509.rsa_sha1.
        * @param contentsKey the /Contents key
        * @param certsKey the /Cert key
        * @param provider the provider or <code>null</code> for the default provider
        * @throws SecurityException on error
        * @throws CRLException on error
        * @throws InvalidKeyException on error
        * @throws CertificateException on error
        * @throws NoSuchProviderException on error
        * @throws NoSuchAlgorithmException on error
        * @throws IOException on error
        */    
        public PdfPKCS7(byte[] contentsKey, byte[] certsKey) {

            X509CertificateParser cf = new X509CertificateParser(certsKey);
            certs = new ArrayList();
            signCert = cf.ReadCertificate();
            certs.Add(signCert);
            while (true) {
                X509Certificate cc = cf.ReadCertificate();
                if (cc == null)
                    break;
                certs.Add(cc);
            }
            crls = new ArrayList();
            ASN1InputStream inp = new ASN1InputStream(new MemoryStream(contentsKey));
            digest = ((DEROctetString)inp.readObject()).getOctets();
            sig = SignerUtil.getSigner("SHA1withRSA");
            sig.init(false, signCert.getPublicKey());
        }
        
        /**
        * Verifies a signature using the sub-filter adbe.pkcs7.detached or
        * adbe.pkcs7.sha1.
        * @param contentsKey the /Contents key
        * @param provider the provider or <code>null</code> for the default provider
        * @throws SecurityException on error
        * @throws CRLException on error
        * @throws InvalidKeyException on error
        * @throws CertificateException on error
        * @throws NoSuchProviderException on error
        * @throws NoSuchAlgorithmException on error
        */    
        public PdfPKCS7(byte[] contentsKey) {
            ASN1InputStream din = new ASN1InputStream(new MemoryStream(contentsKey));
            
            //
            // Basic checks to make sure it's a PKCS#7 SignedData Object
            //
            ASN1Object pkcs;
            
            try {
                pkcs = din.readObject();
            }
            catch  {
                throw new ArgumentException("can't decode PKCS7SignedData object");
            }
            if (!(pkcs is ASN1Sequence)) {
                throw new ArgumentException("Not a valid PKCS#7 object - not a sequence");
            }
            ASN1Sequence signedData = (ASN1Sequence)pkcs;
            DERObjectIdentifier objId = (DERObjectIdentifier)signedData.getObjectAt(0);
            if (!objId.getId().Equals(ID_PKCS7_SIGNED_DATA))
                throw new ArgumentException("Not a valid PKCS#7 object - not signed data");
            ASN1Sequence content = (ASN1Sequence)((DERTaggedObject)signedData.getObjectAt(1)).getObject();
            // the positions that we care are:
            //     0 - version
            //     1 - digestAlgorithms
            //     2 - possible ID_PKCS7_DATA
            //     (the certificates and crls are taken out by other means)
            //     last - signerInfos
            
            // the version
            version = ((DERInteger)content.getObjectAt(0)).getValue().intValue();
            
            // the digestAlgorithms
            digestalgos = new Hashtable();
            IEnumerator e = ((ASN1Set)content.getObjectAt(1)).getObjects();
            while (e.MoveNext())
            {
                ASN1Sequence s = (ASN1Sequence)e.Current;
                DERObjectIdentifier o = (DERObjectIdentifier)s.getObjectAt(0);
                digestalgos[o.getId()] = null;
            }
            
            // the certificates and crls
            X509CertificateParser cf = new X509CertificateParser(contentsKey);
            certs = new ArrayList();
            while (true) {
                X509Certificate cc = cf.ReadCertificate();
                if (cc == null)
                    break;
                certs.Add(cc);
            }
            crls = new ArrayList();
            
            // the possible ID_PKCS7_DATA
            ASN1Sequence rsaData = (ASN1Sequence)content.getObjectAt(2);
            if (rsaData.size() > 1) {
                DEROctetString rsaDataContent = (DEROctetString)((DERTaggedObject)rsaData.getObjectAt(1)).getObject();
                RSAdata = rsaDataContent.getOctets();
            }
            
            // the signerInfos
            int next = 3;
            while (content.getObjectAt(next) is DERTaggedObject)
                ++next;
            ASN1Set signerInfos = (ASN1Set)content.getObjectAt(next);
            if (signerInfos.size() != 1)
                throw new ArgumentException("This PKCS#7 object has multiple SignerInfos - only one is supported at this time");
            ASN1Sequence signerInfo = (ASN1Sequence)signerInfos.getObjectAt(0);
            // the positions that we care are
            //     0 - version
            //     1 - the signing certificate serial number
            //     2 - the digest algorithm
            //     3 or 4 - digestEncryptionAlgorithm
            //     4 or 5 - encryptedDigest
            signerversion = ((DERInteger)signerInfo.getObjectAt(0)).getValue().intValue();
            // Get the signing certificate
            ASN1Sequence issuerAndSerialNumber = (ASN1Sequence)signerInfo.getObjectAt(1);
            BigInteger serialNumber = ((DERInteger)issuerAndSerialNumber.getObjectAt(1)).getValue();
            foreach (X509Certificate cert in certs) {                                                            
                if (serialNumber.Equals(cert.getSerialNumber())) {
                    signCert = cert;                                                                             
                    break;                                                                                            
                }                                                                                                
            }
            if (signCert == null) {
                throw new ArgumentException("Can't find signing certificate with serial " + serialNumber.ToString(16));
            }
            digestAlgorithm = ((DERObjectIdentifier)((ASN1Sequence)signerInfo.getObjectAt(2)).getObjectAt(0)).getId();
            next = 3;
            if (signerInfo.getObjectAt(next) is ASN1TaggedObject) {
                ASN1TaggedObject tagsig = (ASN1TaggedObject)signerInfo.getObjectAt(next);
                ASN1Sequence sseq = (ASN1Sequence)tagsig.getObject();
                MemoryStream bOut = new MemoryStream();            
                ASN1OutputStream dout = new ASN1OutputStream(bOut);
                try {
                    ASN1EncodableVector attribute = new ASN1EncodableVector();
                    for (int k = 0; k < sseq.size(); ++k) {
                        attribute.add(sseq.getObjectAt(k));
                    }
                    dout.writeObject(new DERSet(attribute));
                    dout.Close();
                }
                catch (IOException){}
                sigAttr = bOut.ToArray();
                
                for (int k = 0; k < sseq.size(); ++k) {
                    ASN1Sequence seq2 = (ASN1Sequence)sseq.getObjectAt(k);
                    if (((DERObjectIdentifier)seq2.getObjectAt(0)).getId().Equals(ID_MESSAGE_DIGEST)) {
                        ASN1Set sset = (ASN1Set)seq2.getObjectAt(1);
                        digestAttr = ((DEROctetString)sset.getObjectAt(0)).getOctets();
                        break;
                    }
                }
                if (digestAttr == null)
                    throw new ArgumentException("Authenticated attribute is missing the digest.");
                ++next;
            }
            digestEncryptionAlgorithm = ((DERObjectIdentifier)((ASN1Sequence)signerInfo.getObjectAt(next++)).getObjectAt(0)).getId();
            digest = ((DEROctetString)signerInfo.getObjectAt(next)).getOctets();
            if (RSAdata != null || digestAttr != null) {
                messageDigest = GetHashClass();
            }
            sig = SignerUtil.getSigner(GetDigestAlgorithm());
            sig.init(false, signCert.getPublicKey());
        }

        /**
        * Generates a signature.
        * @param privKey the private key
        * @param certChain the certificate chain
        * @param crlList the certificate revocation list
        * @param hashAlgorithm the hash algorithm
        * @param provider the provider or <code>null</code> for the default provider
        * @param hasRSAdata <CODE>true</CODE> if the sub-filter is adbe.pkcs7.sha1
        * @throws SecurityException on error
        * @throws InvalidKeyException on error
        * @throws NoSuchProviderException on error
        * @throws NoSuchAlgorithmException on error
        */    
        public PdfPKCS7(CipherParameters privKey, X509Certificate[] certChain, object[] crlList,
                        String hashAlgorithm, bool hasRSAdata) {
            this.privKey = privKey;
            
            if (hashAlgorithm.Equals("MD5")) {
                digestAlgorithm = ID_MD5;
            }
            else if (hashAlgorithm.Equals("MD2")) {
                digestAlgorithm = ID_MD2;
            }
            else if (hashAlgorithm.Equals("SHA")) {
                digestAlgorithm = ID_SHA1;
            }
            else if (hashAlgorithm.Equals("SHA1")) {
                digestAlgorithm = ID_SHA1;
            }
            else {
                throw new ArgumentException("Unknown Hash Algorithm "+hashAlgorithm);
            }
            
            version = signerversion = 1;
            certs = new ArrayList();
            crls = new ArrayList();
            digestalgos = new Hashtable();
            digestalgos[digestAlgorithm] = null;
            
            //
            // Copy in the certificates and crls used to sign the private key.
            //
            signCert = certChain[0];
            for (int i = 0;i < certChain.Length;i++) {
                certs.Add(certChain[i]);
            }
            
//            if (crlList != null) {
//                for (int i = 0;i < crlList.length;i++) {
//                    crls.add(crlList[i]);
//                }
//            }
            
            if (privKey != null) {
                //
                // Now we have private key, find out what the digestEncryptionAlgorithm is.
                //
                if (privKey is RSAKeyParameters)
                    digestEncryptionAlgorithm = ID_RSA;
                else if (privKey is DSAKeyParameters)
                    digestEncryptionAlgorithm = ID_DSA;
                else
                    throw new ArgumentException("Unknown Key Algorithm "+privKey.ToString());

            }
            if (hasRSAdata) {
                RSAdata = new byte[0];
                messageDigest = GetHashClass();
            }

            if (privKey != null) {
                sig = SignerUtil.getSigner(GetDigestAlgorithm());
                sig.init(true, privKey);
            }
        }

        /**
        * Update the digest with the specified bytes. This method is used both for signing and verifying
        * @param buf the data buffer
        * @param off the offset in the data buffer
        * @param len the data length
        * @throws SignatureException on error
        */
        public void Update(byte[] buf, int off, int len) {
            if (RSAdata != null || digestAttr != null)
                messageDigest.update(buf, off, len);
            else
                sig.update(buf, off, len);
        }
        
        /**
        * Verify the digest.
        * @throws SignatureException on error
        * @return <CODE>true</CODE> if the signature checks out, <CODE>false</CODE> otherwise
        */
        public bool Verify() {
            if (verified)
                return verifyResult;
            if (sigAttr != null) {
                byte[] msd = new byte[messageDigest.getDigestSize()];
                sig.update(sigAttr, 0, sigAttr.Length);
                if (RSAdata != null) {
                    messageDigest.doFinal(msd, 0);
                    messageDigest.update(msd, 0, msd.Length);
                }
                messageDigest.doFinal(msd, 0);
                verifyResult = (Arrays.areEqual(msd, digestAttr) && sig.verifySignature(digest));
            }
            else {
                if (RSAdata != null){
                    byte[] msd = new byte[messageDigest.getDigestSize()];
                    messageDigest.doFinal(msd, 0);
                    sig.update(msd, 0, msd.Length);
                }
                verifyResult = sig.verifySignature(digest);
            }
            verified = true;
            return verifyResult;
        }
        
        /**
        * Get the X.509 certificates associated with this PKCS#7 object
        * @return the X.509 certificates associated with this PKCS#7 object
        */
        public X509Certificate[] Certificates {
            get {
                X509Certificate[] c = new X509Certificate[certs.Count];
                certs.CopyTo(c);
                return c;
            }
        }
        
        /**
        * Get the X.509 certificate revocation lists associated with this PKCS#7 object
        * @return the X.509 certificate revocation lists associated with this PKCS#7 object
        */
        public ArrayList CRLs {
            get {
                return crls;
            }
        }
        
        /**
        * Get the X.509 certificate actually used to sign the digest.
        * @return the X.509 certificate actually used to sign the digest
        */
        public X509Certificate SigningCertificate {
            get {
                return signCert;
            }
        }
        
        /**
        * Get the version of the PKCS#7 object. Always 1
        * @return the version of the PKCS#7 object. Always 1
        */
        public int Version {
            get {
                return version;
            }
        }
        
        /**
        * Get the version of the PKCS#7 "SignerInfo" object. Always 1
        * @return the version of the PKCS#7 "SignerInfo" object. Always 1
        */
        public int SigningInfoVersion {
            get {
                return signerversion;
            }
        }
        
        /**
        * Get the algorithm used to calculate the message digest
        * @return the algorithm used to calculate the message digest
        */
        public String GetDigestAlgorithm() {
            String dea = digestEncryptionAlgorithm;
            
            if (digestEncryptionAlgorithm.Equals(ID_RSA)) {
                dea = "RSA";
            }
            else if (digestEncryptionAlgorithm.Equals(ID_DSA)) {
                dea = "DSA";
            }
            
            return GetHashAlgorithm() + "with" + dea;
        }

        /**
        * Returns the algorithm.
        * @return the digest algorithm
        */
        public String GetHashAlgorithm() {
            String da = digestAlgorithm;
            
            if (digestAlgorithm.Equals(ID_MD5) || digestAlgorithm.Equals(ID_MD5RSA)) {
                da = "MD5";
            }
            else if (digestAlgorithm.Equals(ID_MD2) || digestAlgorithm.Equals(ID_MD2RSA)) {
                da = "MD2";
            }
            else if (digestAlgorithm.Equals(ID_SHA1) || digestAlgorithm.Equals(ID_SHA1RSA)) {
                da = "SHA1";
            }
            return da;
        }

        public Digest GetHashClass() {
            if (digestAlgorithm.Equals(ID_MD5) || digestAlgorithm.Equals(ID_MD5RSA)) {
                return new MD5Digest();
            }
            else if (digestAlgorithm.Equals(ID_MD2) || digestAlgorithm.Equals(ID_MD2RSA)) {
                return new MD2Digest();
            }
            else if (digestAlgorithm.Equals(ID_SHA1) || digestAlgorithm.Equals(ID_SHA1RSA)) {
                return new SHA1Digest();
            }
            return null;
        }

        /**
        * Loads the default root certificates at &lt;java.home&gt;/lib/security/cacerts
        * with the default provider.
        * @return a <CODE>KeyStore</CODE>
        */    
//        public static KeyStore loadCacertsKeyStore() {
//            return loadCacertsKeyStore(null);
//        }

        /**
        * Loads the default root certificates at &lt;java.home&gt;/lib/security/cacerts.
        * @param provider the provider or <code>null</code> for the default provider
        * @return a <CODE>KeyStore</CODE>
        */    
//        public static KeyStore loadCacertsKeyStore(String provider) {
//            File file = new File(System.getProperty("java.home"), "lib");
//            file = new File(file, "security");
//            file = new File(file, "cacerts");
//            FileInputStream fin = null;
//            try {
//                fin = new FileInputStream(file);
//                KeyStore k;
//                if (provider == null)
//                    k = KeyStore.getInstance("JKS");
//                else
//                    k = KeyStore.getInstance("JKS", provider);
//                k.load(fin, null);
//                return k;
//            }
//            catch (Exception e) {
//                throw new ExceptionConverter(e);
//            }
//            finally {
//                try{fin.close();}catch(Exception ex){}
//            }
//        }
        
        /**
        * Verifies a single certificate.
        * @param cert the certificate to verify
        * @param crls the certificate revocation list or <CODE>null</CODE>
        * @param calendar the date or <CODE>null</CODE> for the current date
        * @return a <CODE>String</CODE> with the error description or <CODE>null</CODE>
        * if no error
        */    
        public static String VerifyCertificate(X509Certificate cert, object[] crls, DateTime calendar) {
            try {
                if (!cert.isValid(calendar))
                    return "The certificate has expired or is not yet valid";
            }
            catch (Exception e) {
                return e.ToString();
            }
            return null;
        }
        
        /**
        * Verifies a certificate chain against a KeyStore.
        * @param certs the certificate chain
        * @param keystore the <CODE>KeyStore</CODE>
        * @param crls the certificate revocation list or <CODE>null</CODE>
        * @param calendar the date or <CODE>null</CODE> for the current date
        * @return <CODE>null</CODE> if the certificate chain could be validade or a
        * <CODE>Object[]{cert,error}</CODE> where <CODE>cert</CODE> is the
        * failed certificate and <CODE>error</CODE> is the error message
        */    
        public static Object[] VerifyCertificates(X509Certificate[] certs, ArrayList keystore, object[] crls, DateTime calendar) {
            for (int k = 0; k < certs.Length; ++k) {
                X509Certificate cert = certs[k];
                String err = VerifyCertificate(cert, crls, calendar);
                if (err != null)
                    return new Object[]{cert, err};
                foreach (X509Certificate certStoreX509 in keystore) {
                    try {
                        if (VerifyCertificate(certStoreX509, crls, calendar) != null)
                            continue;
                        try {
                            if (cert.verify(certStoreX509.getPublicKey()))
                                return null;
                        }
                        catch {
                            continue;
                        }
                    }
                    catch {
                    }
                }
                int j;
                for (j = 0; j < certs.Length; ++j) {
                    if (j == k)
                        continue;
                    X509Certificate certNext = certs[j];
                    try {
                        if (cert.verify(certNext.getPublicKey()))
                            break;
                    }
                    catch {
                    }
                }
                if (j == certs.Length)
                    return new Object[]{cert, "Cannot be verified against the KeyStore or the certificate chain"};
            }
            return new Object[]{null, "Invalid state. Possible circular certificate chain"};
        }

        /**
        * Get the "issuer" from the TBSCertificate bytes that are passed in
        * @param enc a TBSCertificate in a byte array
        * @return a DERObject
        */
        private static ASN1Object GetIssuer(byte[] enc) {
            ASN1InputStream inp = new ASN1InputStream(new MemoryStream(enc));
            ASN1Sequence seq = (ASN1Sequence)inp.readObject();
            return (ASN1Object)seq.getObjectAt(seq.getObjectAt(0) is DERTaggedObject ? 3 : 2);
        }

        /**
        * Get the "subject" from the TBSCertificate bytes that are passed in
        * @param enc A TBSCertificate in a byte array
        * @return a DERObject
        */
        private static ASN1Object GetSubject(byte[] enc) {
            ASN1InputStream inp = new ASN1InputStream(new MemoryStream(enc));
            ASN1Sequence seq = (ASN1Sequence)inp.readObject();
            return (ASN1Object)seq.getObjectAt(seq.getObjectAt(0) is DERTaggedObject ? 5 : 4);
        }

        /**
        * Get the issuer fields from an X509 Certificate
        * @param cert an X509Certificate
        * @return an X509Name
        */
        public static X509Name GetIssuerFields(X509Certificate cert) {
            return new X509Name((ASN1Sequence)GetIssuer(cert.getTBSCertificate()));
        }

        /**
        * Get the subject fields from an X509 Certificate
        * @param cert an X509Certificate
        * @return an X509Name
        */
        public static X509Name GetSubjectFields(X509Certificate cert) {
            return new X509Name((ASN1Sequence)GetSubject(cert.getTBSCertificate()));
        }
        
        /**
        * Gets the bytes for the PKCS#1 object.
        * @return a byte array
        */
        public byte[] GetEncodedPKCS1() {
            if (externalDigest != null)
                digest = externalDigest;
            else
                digest = sig.generateSignature();
            MemoryStream bOut = new MemoryStream();
            
            ASN1OutputStream dout = new ASN1OutputStream(bOut);
            dout.writeObject(new DEROctetString(digest));
            dout.Close();
            
            return bOut.ToArray();
        }
        
        /**
        * Sets the digest/signature to an external calculated value.
        * @param digest the digest. This is the actual signature
        * @param RSAdata the extra data that goes into the data tag in PKCS#7
        * @param digestEncryptionAlgorithm the encryption algorithm. It may must be <CODE>null</CODE> if the <CODE>digest</CODE>
        * is also <CODE>null</CODE>. If the <CODE>digest</CODE> is not <CODE>null</CODE>
        * then it may be "RSA" or "DSA"
        */    
        public void SetExternalDigest(byte[] digest, byte[] RSAdata, String digestEncryptionAlgorithm) {
            externalDigest = digest;
            externalRSAdata = RSAdata;
            if (digestEncryptionAlgorithm != null) {
                if (digestEncryptionAlgorithm.Equals("RSA")) {
                    this.digestEncryptionAlgorithm = ID_RSA;
                }
                else if (digestEncryptionAlgorithm.Equals("DSA")) {
                    this.digestEncryptionAlgorithm = ID_DSA;
                }
                else
                    throw new ArgumentException("Unknown Key Algorithm "+digestEncryptionAlgorithm);
            }
        }
        
        /**
        * Gets the bytes for the PKCS7SignedData object.
        * @return the bytes for the PKCS7SignedData object
        */
        public byte[] GetEncodedPKCS7() {
            return GetEncodedPKCS7(null, DateTime.Now);
        }
        
        /**
        * Gets the bytes for the PKCS7SignedData object. Optionally the authenticatedAttributes
        * in the signerInfo can also be set. If either of the parameters is <CODE>null</CODE>, none will be used.
        * @param secondDigest the digest in the authenticatedAttributes
        * @param signingTime the signing time in the authenticatedAttributes
        * @return the bytes for the PKCS7SignedData object
        */
        public byte[] GetEncodedPKCS7(byte[] secondDigest, DateTime signingTime) {
            if (externalDigest != null) {
                digest = externalDigest;
                if (RSAdata != null)
                    RSAdata = externalRSAdata;
            }
            else if (externalRSAdata != null && RSAdata != null) {
                RSAdata = externalRSAdata;
                sig.update(RSAdata, 0, RSAdata.Length);
                digest = sig.generateSignature();
            }
            else {
                if (RSAdata != null) {
                    RSAdata = new byte[messageDigest.getDigestSize()];
                    messageDigest.doFinal(RSAdata, 0);
                    sig.update(RSAdata, 0, RSAdata.Length);
                }
                digest = sig.generateSignature();
            }
            
            // Create the set of Hash algorithms
            ASN1EncodableVector digestAlgorithms = new ASN1EncodableVector();
            foreach (string dal in digestalgos.Keys) {
                ASN1EncodableVector algos = new ASN1EncodableVector();
                algos.add(new DERObjectIdentifier(dal));
                algos.add(new DERNull());
                digestAlgorithms.add(new DERSequence(algos));
            }
            
            // Create the contentInfo.
            ASN1EncodableVector v = new ASN1EncodableVector();
            v.add(new DERObjectIdentifier(ID_PKCS7_DATA));
            if (RSAdata != null)
                v.add(new DERTaggedObject(0, new DEROctetString(RSAdata)));
            DERSequence contentinfo = new DERSequence(v);
            
            // Get all the certificates
            //
            v = new ASN1EncodableVector();
            foreach (X509Certificate xcert in certs) {
                ASN1InputStream tempstream = new ASN1InputStream(new MemoryStream(xcert.getEncoded()));
                v.add(tempstream.readObject());
            }
            
            DERSet dercertificates = new DERSet(v);
            
            // Create signerinfo structure.
            //
            ASN1EncodableVector signerinfo = new ASN1EncodableVector();
            
            // Add the signerInfo version
            //
            signerinfo.add(new DERInteger(signerversion));
            
            v = new ASN1EncodableVector();
            v.add(GetIssuer(signCert.getTBSCertificate()));
            v.add(new DERInteger(signCert.getSerialNumber()));
            signerinfo.add(new DERSequence(v));
            
            // Add the digestAlgorithm
            v = new ASN1EncodableVector();
            v.add(new DERObjectIdentifier(digestAlgorithm));
            v.add(new DERNull());
            signerinfo.add(new DERSequence(v));
            
            // add the authenticated attribute if present
            if (secondDigest != null /*&& signingTime != null*/) {
                ASN1EncodableVector attribute = new ASN1EncodableVector();
                v = new ASN1EncodableVector();
                v.add(new DERObjectIdentifier(ID_CONTENT_TYPE));
                v.add(new DERSet(new DERObjectIdentifier(ID_PKCS7_DATA)));
                attribute.add(new DERSequence(v));
                v = new ASN1EncodableVector();
                v.add(new DERObjectIdentifier(ID_SIGNING_TIME));
                v.add(new DERSet(new DERUTCTime(signingTime)));
                attribute.add(new DERSequence(v));
                v = new ASN1EncodableVector();
                v.add(new DERObjectIdentifier(ID_MESSAGE_DIGEST));
                v.add(new DERSet(new DEROctetString(secondDigest)));
                attribute.add(new DERSequence(v));
                signerinfo.add(new DERTaggedObject(false, 0, new DERSet(attribute)));
            }
            // Add the digestEncryptionAlgorithm
            v = new ASN1EncodableVector();
            v.add(new DERObjectIdentifier(digestEncryptionAlgorithm));
            v.add(new DERNull());
            signerinfo.add(new DERSequence(v));
            
            // Add the digest
            signerinfo.add(new DEROctetString(digest));
            
            
            // Finally build the body out of all the components above
            ASN1EncodableVector body = new ASN1EncodableVector();
            body.add(new DERInteger(version));
            body.add(new DERSet(digestAlgorithms));
            body.add(contentinfo);
            body.add(new DERTaggedObject(false, 0, dercertificates));
            
//                if (crls.Count > 0) {
//                    v = new ASN1EncodableVector();
//                    for (Iterator i = crls.iterator();i.hasNext();) {
//                        ASN1InputStream t = new ASN1InputStream(new ByteArrayInputStream((((X509CRL)i.next()).getEncoded())));
//                        v.add(t.readObject());
//                    }
//                    DERSet dercrls = new DERSet(v);
//                    body.add(new DERTaggedObject(false, 1, dercrls));
//                }
            
            // Only allow one signerInfo
            body.add(new DERSet(new DERSequence(signerinfo)));
            
            // Now we have the body, wrap it in it's PKCS7Signed shell
            // and return it
            //
            ASN1EncodableVector whole = new ASN1EncodableVector();
            whole.add(new DERObjectIdentifier(ID_PKCS7_SIGNED_DATA));
            whole.add(new DERTaggedObject(0, new DERSequence(body)));
            
            MemoryStream bOut = new MemoryStream();
            
            ASN1OutputStream dout = new ASN1OutputStream(bOut);
            dout.writeObject(new DERSequence(whole));
            dout.Close();
            
            return bOut.ToArray();
        }
        
        
        /**
        * When using authenticatedAttributes the authentication process is different.
        * The document digest is generated and put inside the attribute. The signing is done over the DER encoded
        * authenticatedAttributes. This method provides that encoding and the parameters must be
        * exactly the same as in {@link #getEncodedPKCS7(byte[],Calendar)}.
        * <p>
        * A simple example:
        * <p>
        * <pre>
        * Calendar cal = Calendar.getInstance();
        * PdfPKCS7 pk7 = new PdfPKCS7(key, chain, null, "SHA1", null, false);
        * MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
        * byte buf[] = new byte[8192];
        * int n;
        * Stream inp = sap.getRangeStream();
        * while ((n = inp.read(buf)) &gt; 0) {
        *    messageDigest.update(buf, 0, n);
        * }
        * byte hash[] = messageDigest.digest();
        * byte sh[] = pk7.getAuthenticatedAttributeBytes(hash, cal);
        * pk7.update(sh, 0, sh.length);
        * byte sg[] = pk7.getEncodedPKCS7(hash, cal);
        * </pre>
        * @param secondDigest the content digest
        * @param signingTime the signing time
        * @return the byte array representation of the authenticatedAttributes ready to be signed
        */    
        public byte[] GetAuthenticatedAttributeBytes(byte[] secondDigest, DateTime signingTime) {
            ASN1EncodableVector attribute = new ASN1EncodableVector();
            ASN1EncodableVector v = new ASN1EncodableVector();
            v.add(new DERObjectIdentifier(ID_CONTENT_TYPE));
            v.add(new DERSet(new DERObjectIdentifier(ID_PKCS7_DATA)));
            attribute.add(new DERSequence(v));
            v = new ASN1EncodableVector();
            v.add(new DERObjectIdentifier(ID_SIGNING_TIME));
            v.add(new DERSet(new DERUTCTime(signingTime)));
            attribute.add(new DERSequence(v));
            v = new ASN1EncodableVector();
            v.add(new DERObjectIdentifier(ID_MESSAGE_DIGEST));
            v.add(new DERSet(new DEROctetString(secondDigest)));
            attribute.add(new DERSequence(v));
            MemoryStream bOut = new MemoryStream();
            
            ASN1OutputStream dout = new ASN1OutputStream(bOut);
            dout.writeObject(new DERSet(attribute));
            dout.Close();
            
            return bOut.ToArray();
        }

        
        public string Reason {
            get {
                return reason;
            }
            set {
                reason = value;
            }
        }

        
        public string Location {
            get {
                return location;
            }
            set {
                location = value;
            }
        }

        
        public DateTime SignDate {
            get {
                return signDate;
            }
            set {
                signDate = value;
            }
        }

        
        public string SignName {
            get {
                return signName;
            }
            set {
                signName = value;
            }
        }

        /**
        * a class that holds an X509 name
        */
        public class X509Name {
            /**
            * country code - StringType(SIZE(2))
            */
            public static DERObjectIdentifier C = new DERObjectIdentifier("2.5.4.6");

            /**
            * organization - StringType(SIZE(1..64))
            */
            public static DERObjectIdentifier O = new DERObjectIdentifier("2.5.4.10");

            /**
            * organizational unit name - StringType(SIZE(1..64))
            */
            public static DERObjectIdentifier OU = new DERObjectIdentifier("2.5.4.11");

            /**
            * Title
            */
            public static DERObjectIdentifier T = new DERObjectIdentifier("2.5.4.12");

            /**
            * common name - StringType(SIZE(1..64))
            */
            public static DERObjectIdentifier CN = new DERObjectIdentifier("2.5.4.3");

            /**
            * device serial number name - StringType(SIZE(1..64))
            */
            public static DERObjectIdentifier SN = new DERObjectIdentifier("2.5.4.5");

            /**
            * locality name - StringType(SIZE(1..64))
            */
            public static DERObjectIdentifier L = new DERObjectIdentifier("2.5.4.7");

            /**
            * state, or province name - StringType(SIZE(1..64))
            */
            public static DERObjectIdentifier ST = new DERObjectIdentifier("2.5.4.8");

            /** Naming attribute of type X520name */
            public static DERObjectIdentifier SURNAME = new DERObjectIdentifier("2.5.4.4");
            /** Naming attribute of type X520name */
            public static DERObjectIdentifier GIVENNAME = new DERObjectIdentifier("2.5.4.42");
            /** Naming attribute of type X520name */
            public static DERObjectIdentifier INITIALS = new DERObjectIdentifier("2.5.4.43");
            /** Naming attribute of type X520name */
            public static DERObjectIdentifier GENERATION = new DERObjectIdentifier("2.5.4.44");
            /** Naming attribute of type X520name */
            public static DERObjectIdentifier UNIQUE_IDENTIFIER = new DERObjectIdentifier("2.5.4.45");

            /**
            * Email address (RSA PKCS#9 extension) - IA5String.
            * <p>Note: if you're trying to be ultra orthodox, don't use this! It shouldn't be in here.
            */
            public static DERObjectIdentifier EmailAddress = new DERObjectIdentifier("1.2.840.113549.1.9.1");

            /**
            * email address in Verisign certificates
            */
            public static DERObjectIdentifier E = EmailAddress;

            /** object identifier */
            public static DERObjectIdentifier DC = new DERObjectIdentifier("0.9.2342.19200300.100.1.25");

            /** LDAP User id. */
            public static DERObjectIdentifier UID = new DERObjectIdentifier("0.9.2342.19200300.100.1.1");

            /** A Hashtable with default symbols */
            public static Hashtable DefaultSymbols = new Hashtable();
            
            static X509Name(){
                DefaultSymbols[C] = "C";
                DefaultSymbols[O] = "O";
                DefaultSymbols[T] = "T";
                DefaultSymbols[OU] = "OU";
                DefaultSymbols[CN] = "CN";
                DefaultSymbols[L] = "L";
                DefaultSymbols[ST] = "ST";
                DefaultSymbols[SN] = "SN";
                DefaultSymbols[EmailAddress] = "E";
                DefaultSymbols[DC] = "DC";
                DefaultSymbols[UID] = "UID";
                DefaultSymbols[SURNAME] = "SURNAME";
                DefaultSymbols[GIVENNAME] = "GIVENNAME";
                DefaultSymbols[INITIALS] = "INITIALS";
                DefaultSymbols[GENERATION] = "GENERATION";
            }
            /** A Hashtable with values */
            public Hashtable values = new Hashtable();

            /**
            * Constructs an X509 name
            * @param seq an ASN1 Sequence
            */
            public X509Name(ASN1Sequence seq) {
                IEnumerator e = seq.getObjects();
                
                while (e.MoveNext()) {
                    ASN1Set sett = (ASN1Set)e.Current;
                    
                    for (int i = 0; i < sett.size(); i++) {
                        ASN1Sequence s = (ASN1Sequence)sett.getObjectAt(i);
                        String id = (String)DefaultSymbols[s.getObjectAt(0)];
                        if (id == null)
                            continue;
                        ArrayList vs = (ArrayList)values[id];
                        if (vs == null) {
                            vs = new ArrayList();
                            values[id] = vs;
                        }
                        vs.Add(((DERString)s.getObjectAt(1)).getString());
                    }
                }
            }
            /**
            * Constructs an X509 name
            * @param dirName a directory name
            */
            public X509Name(String dirName) {
                X509NameTokenizer   nTok = new X509NameTokenizer(dirName);
                
                while (nTok.hasMoreTokens()) {
                    String  token = nTok.nextToken();
                    int index = token.IndexOf('=');
                    
                    if (index == -1) {
                        throw new ArgumentException("badly formated directory string");
                    }
                    
                    String id = token.Substring(0, index).ToUpper();
                    String value = token.Substring(index + 1);
                    ArrayList vs = (ArrayList)values[id];
                    if (vs == null) {
                        vs = new ArrayList();
                        values[id] = vs;
                    }
                    vs.Add(value);
                }
                
            }
            
            public String GetField(String name) {
                ArrayList vs = (ArrayList)values[name];
                return vs == null ? null : (String)vs[0];
            }

            /**
            * gets a field array from the values Hashmap
            * @param name
            * @return an ArrayList
            */
            public ArrayList GetFieldArray(String name) {
                ArrayList vs = (ArrayList)values[name];
                return vs == null ? null : vs;
            }
            
            /**
            * getter for values
            * @return a Hashtable with the fields of the X509 name
            */
            public Hashtable GetFields() {
                return values;
            }
            
            /**
            * @see java.lang.Object#toString()
            */
            public override String ToString() {
                return values.ToString();
            }
        }
        
        /**
        * class for breaking up an X500 Name into it's component tokens, ala
        * java.util.StringTokenizer. We need this class as some of the
        * lightweight Java environment don't support classes like
        * StringTokenizer.
        */
        public class X509NameTokenizer {
            private String          oid;
            private int             index;
            private StringBuilder    buf = new StringBuilder();
            
            public X509NameTokenizer(
            String oid) {
                this.oid = oid;
                this.index = -1;
            }
            
            public bool hasMoreTokens() {
                return (index != oid.Length);
            }
            
            public String nextToken() {
                if (index == oid.Length) {
                    return null;
                }
                
                int     end = index + 1;
                bool quoted = false;
                bool escaped = false;
                
                buf.Length = 0;
                
                while (end != oid.Length) {
                    char    c = oid[end];
                    
                    if (c == '"') {
                        if (!escaped) {
                            quoted = !quoted;
                        }
                        else {
                            buf.Append(c);
                        }
                        escaped = false;
                    }
                    else {
                        if (escaped || quoted) {
                            buf.Append(c);
                            escaped = false;
                        }
                        else if (c == '\\') {
                            escaped = true;
                        }
                        else if (c == ',') {
                            break;
                        }
                        else {
                            buf.Append(c);
                        }
                    }
                    end++;
                }
                
                index = end;
                return buf.ToString().Trim();
            }
        }
    }
}

