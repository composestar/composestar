#region Using directives

using System;
using System.Collections;
using System.Text;
using System.IO;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.asn1;
using org.bouncycastle.asn1.pkcs;
using org.bouncycastle.util.encoders;

#endregion

namespace org.bouncycastle.x509
{
    /// <summary>
    /// This class will parse X509Certificates presented to it in PEM, BER and PKCS7 format.
    /// </summary>
    public class X509CertificateParser
    {
        private const int readDataLimit = 8192;
        private int sDataObjectCount = 0;
        private SignedData sData = null;
        private Stream inStr = null;
        
        private static String readLine(Stream inStr)
        {
            int c = 0;
            StringBuilder buf = new StringBuilder();
            while (((c = inStr.ReadByte()) != '\n') && (c >= 0))
            {
                if (c == '\r')
                {
                    continue;
                }   

                buf.Append((char)c);
                
                if (buf.Length > readDataLimit) throw (new Exception("Line Reader, Line exceeded line buffer size."));
            }
            if (c < 0)
            {
                return null;
            }
            return buf.ToString();
        }
        
        private X509Certificate readDERCertificate(Stream inStr)
        {
            ASN1InputStream  dIn = new ASN1InputStream(inStr);
            ASN1Sequence    seq = (ASN1Sequence)dIn.readObject();

            if (seq.size() > 1 && seq.getObjectAt(0) is DERObjectIdentifier)
            {
                if (seq.getObjectAt(0).Equals(PKCSObjectIdentifiers.signedData))
                {
                    sData = new SignedData(ASN1Sequence.getInstance((ASN1TaggedObject)seq.getObjectAt(1), true));
                    return new X509Certificate(ASN1Sequence.getInstance(sData.getCertificates().getObjectAt(sDataObjectCount++)));
                }
            }
            return new X509Certificate(seq);
        }

        private X509Certificate readPKCS7Certificate()
        {
            ASN1InputStream  dIn = new ASN1InputStream(inStr);
            ASN1Sequence seq = (ASN1Sequence)dIn.readObject();
            if (seq.size() > 1 && seq.getObjectAt(0) is DERObjectIdentifier)
            {
                if (seq.getObjectAt(0).Equals(PKCSObjectIdentifiers.signedData))
                {
                    sData = new SignedData(ASN1Sequence.getInstance((ASN1TaggedObject)seq.getObjectAt(1), true));
                    return new X509Certificate(ASN1Sequence.getInstance(sData.getCertificates().getObjectAt(sDataObjectCount++)));
                }
            }
            return new X509Certificate(ASN1Sequence.getInstance(seq));
        }
        

        private X509Certificate readPEMCertificate()
        {
            String          line;
            StringBuilder   pemBuf = new StringBuilder();

            while ((line = readLine(inStr)) != null)
            {
                if (line.Equals("-----BEGIN CERTIFICATE-----")
                    || line.Equals("-----BEGIN X509 CERTIFICATE-----"))
                {
                    break;
                }
            }

            while ((line = readLine(inStr)) != null)
            {
                if (line.Equals("-----END CERTIFICATE-----")
                    || line.Equals("-----END X509 CERTIFICATE-----"))
                {
                    break;
                }

                pemBuf.Append(line);
            }

            if (pemBuf.Length != 0)
            {
                MemoryStream bIn = new MemoryStream(Base64.decode(pemBuf.ToString()));
                return readDERCertificate(bIn);
            }
            return null;
        }


        /// <summary>
        /// Create, loading data from stream.
        /// </summary>
        /// <param name="input"></param>
        public X509CertificateParser(
            Stream input)
        {
            if (input.CanSeek) {
                this.inStr = input;
                return;
            }
            MemoryStream ms = new MemoryStream();
            byte[] buf = new byte[8192];
            while (true) {
                int n = input.Read(buf, 0, buf.Length);
                if (n <= 0)
                    break;
                ms.Write(buf, 0, n);
            }
            ms.Seek(0, SeekOrigin.Begin);
            this.inStr = ms;
        }

        /// <summary>
        /// Create loading data from byte array.
        /// </summary>
        /// <param name="input"></param>
        public X509CertificateParser(
            byte[] input)
        {
            this.inStr = new MemoryStream(input);
        }

        /// <summary>
        /// Read a certificate, PEM, DER, BER and PKCS7.
        /// Will also handle PEM encoded with start / stop markers of "-----BEGIN CERTIFICATE-----" or "-----BEGIN X509 CERTIFICATE-----" / "-----END CERTIFICATE-----" or "-----END X509 CERTIFICATE-----". 
        /// </summary>
        /// <returns>An X509Certificate Object.</returns>
        public X509Certificate ReadCertificate()
        {
            return parseCertificate();
        }

        X509Certificate parseCertificate() 
        {

            if (sData != null && sDataObjectCount != sData.getCertificates().size())
            {
                return new X509Certificate(
                            ASN1Sequence.getInstance(
                                    sData.getCertificates().getObjectAt(sDataObjectCount++)));
            }
            
            sData = null;
            sDataObjectCount = 0;

            long pos = inStr.Position;

            int    tag = inStr.ReadByte();   
            if (tag == -1)
            {
                return null;
            }
            
            if (tag != 0x30)  // assume ascii PEM encoded.
            {
                inStr.Seek(pos, SeekOrigin.Begin);
                return readPEMCertificate();
            }
            else if (inStr.ReadByte() == 0x80)    // assume BER encoded.
            {
                inStr.Seek(pos, SeekOrigin.Begin);
                return readPKCS7Certificate();
            }
            else
            {
                inStr.Seek(pos, SeekOrigin.Begin);
                return readDERCertificate(inStr);
            }
        }
    }
}
