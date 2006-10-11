
using System;
using System.IO;
using System.Collections;
using System.Text;

using org.bouncycastle.crypto.parameters;
using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x9;
using org.bouncycastle.asn1.pkcs;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.asn1.oiw;
using org.bouncycastle.crypto;

namespace org.bouncycastle.x509
{
    /// <summary>
    /// A factory to produce Public Key Info Objects.
    /// </summary>
    public class SubjectPublicKeyInfoFactory
    {
        /// <summary>
        /// Create a Subject Public Key Info object for a given public key.
        /// </summary>
        /// <param name="key">One of ElGammalPublicKeyParameters, DSAPublicKeyParameter, DHPublicKeyParameters, RSAKeyParameters or ECPublicKeyParameters</param>
        /// <returns>A subject public key info object.</returns>
        /// <exception cref="Exception">Throw exception if object provided is not one of the above.</exception>
        public static SubjectPublicKeyInfo CreateSubjectPublicKeyInfo(AsymmetricKeyParameter key)
        {
            if (key.isPrivate())
            {
                throw (new Exception("Private key passed - public key expected."));
            }

            if (key is ElGamalPublicKeyParameters)
            {
                ElGamalPublicKeyParameters _key = (ElGamalPublicKeyParameters)key;
                SubjectPublicKeyInfo info = 
                    new SubjectPublicKeyInfo(
                        new AlgorithmIdentifier(
                            OIWObjectIdentifiers.elGamalAlgorithm,
                            new ElGamalParameter(
                                _key.getParameters().getP(),
                                _key.getParameters().getG()
                                ).toASN1Object()), new DERInteger(_key.getY()));

                return info;
            }


            if (key is DSAPublicKeyParameters)
            {
                DSAPublicKeyParameters _key = (DSAPublicKeyParameters)key;
                SubjectPublicKeyInfo info =
                    new SubjectPublicKeyInfo(
                    new AlgorithmIdentifier(
                    X9ObjectIdentifiers.id_dsa,
                    new DSAParameter(_key.getParameters().getP(),_key.getParameters().getQ(),_key.getParameters().getG()).toASN1Object()),
                    new DERInteger(_key.getY())
                    );

                               return info;
            }


            if (key is DHPublicKeyParameters)
            {
                DHPublicKeyParameters _key = (DHPublicKeyParameters)key;
                SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(
                    new AlgorithmIdentifier(X9ObjectIdentifiers.dhpublicnumber,
                    new DHParameter(_key.getParameters().getP(),
                    _key.getParameters().getG(),
                    _key.getParameters().getJ()).toASN1Object()), new DERInteger(_key.getY()));

                return info;
            } // End of DH

            if (key is RSAKeyParameters)
            {
                RSAKeyParameters _key = (RSAKeyParameters)key;
                if (_key.isPrivate()) throw (new Exception("Private RSA Key provided."));

                SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, new DERNull()), new RSAPublicKeyStructure(_key.getModulus(), _key.getExponent()).toASN1Object());
                return info;
            } // End of RSA.

            if (key is ECPublicKeyParameters)
            {
                ECPublicKeyParameters _key = (ECPublicKeyParameters)key;
                X9ECParameters ecP = new X9ECParameters(
                                            _key.getParameters().getCurve(),
                                            _key.getParameters().getG(),
                                            _key.getParameters().getN(),
                                            _key.getParameters().getH(),
                                            _key.getParameters().getSeed());
            X962Parameters x962 = new X962Parameters(ecP);
            ASN1OctetString p = (ASN1OctetString)(new X9ECPoint(_key.getQ()).toASN1Object());
            SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, x962.toASN1Object()), p.getOctets());
            return info;
        } // End of EC

        throw (new Exception("Class provided no convertable:" + key.GetType()));
    }


    }
}
