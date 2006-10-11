using System;
using System.IO;
using System.Collections;
using System.Text;

using org.bouncycastle.crypto.parameters;
using org.bouncycastle.asn1;
using org.bouncycastle.asn1.oiw;
using org.bouncycastle.asn1.pkcs;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.asn1.x9;
using org.bouncycastle.asn1.sec;
using org.bouncycastle.asn1.util;
using org.bouncycastle.crypto;
using org.bouncycastle.security;
using org.bouncycastle.x509;
using org.bouncycastle.util.encoders;

namespace org.bouncycastle.pkcs
{
    public class PKCS12Store
    {
        Hashtable keys = new Hashtable();
        Hashtable localIds = new Hashtable();
        Hashtable certs = new Hashtable();
        Hashtable chainCerts = new Hashtable();
        Hashtable keyCerts = new Hashtable();

        DERObjectIdentifier  keyAlgorithm = PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC;
        DERObjectIdentifier  certAlgorithm = PKCSObjectIdentifiers.pbewithSHAAnd40BitRC2_CBC;
        int                  minIterations = 1024;
        int                  saltSize = 20;

        class CertId
        {
            byte[] id;

            internal CertId(
                AsymmetricKeyParameter key)
            {
                this.id = new SubjectKeyIdentifier(SubjectPublicKeyInfoFactory.CreateSubjectPublicKeyInfo(key)).getKeyIdentifier();
            }

            internal CertId(
                byte[] id)
            {
                this.id = id;
            }

            public override int GetHashCode()
            {
                int hash = id[0] & 0xff;

                for (int i = 1; i != id.Length - 4; i++)
                {
                    hash ^= ((id[i] & 0xff) << 24) | ((id[i + 1] & 0xff) << 16)
                              | ((id[i + 2] & 0xff) << 8) | (id[i + 3] & 0xff);
                }

                return hash;
            }

            public override bool Equals(
                Object o)
            {
                if (!(o is CertId))
                {
                    return false;
                }

                CertId  cId = (CertId)o;

                if (cId.id.Length != id.Length)
                {
                    return false;
                }

                for (int i = 0; i != id.Length; i++)
                {
                    if (cId.id[i] != id[i])
                    {
                        return false;
                    }
                }

                return true;
            }
        }

        String byteArrayToString(byte[] bs)
        {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < bs.Length; i++)
            {
                s.Append(Convert.ToChar(bs[i]));
            }
            return s.ToString();
        }

        ASN1Sequence decryptData(
            AlgorithmIdentifier   algId,
            byte[]                data,
            char[]                password)
        {
            PKCS12PBEParams     pbeParams = PKCS12PBEParams.getInstance(algId.getParameters());
            CipherParameters    keyParameters = PBEUtil.generateCipherParameters(algId.getObjectId(), password, pbeParams);
            byte[]              encoding = null;
            Object              engine = PBEUtil.createEngine(algId.getObjectId());

            if (engine is BufferedBlockCipher)
            {
                BufferedBlockCipher cipher = (BufferedBlockCipher)engine;

                cipher.init(false, keyParameters);

                int encLen = cipher.getOutputSize(data.Length);

                encoding = new byte[encLen];

                int off = cipher.processBytes(data, 0, data.Length, encoding, 0);

                cipher.doFinal(encoding, off);
            }
            else if (engine is StreamCipher)
            {
                StreamCipher cipher = (StreamCipher)engine;

                cipher.init(false, keyParameters);

                encoding = new byte[data.Length];

                cipher.processBytes(data, 0, data.Length, encoding, 0);
            }

            ASN1InputStream  bIn = new ASN1InputStream(new MemoryStream(encoding));

            return (ASN1Sequence)bIn.readObject();
        }

        byte[] encryptData(
            AlgorithmIdentifier   algId,
            byte[]                data,
            char[]                password)
        {
            PKCS12PBEParams     pbeParams = PKCS12PBEParams.getInstance(algId.getParameters());
            CipherParameters    keyParameters = PBEUtil.generateCipherParameters(algId.getObjectId(), password, pbeParams);
            byte[]              encoding = null;
            Object              engine = PBEUtil.createEngine(algId.getObjectId());

            if (engine is BufferedBlockCipher)
            {
                BufferedBlockCipher cipher = (BufferedBlockCipher)engine;

                cipher.init(true, keyParameters);

                int encLen = cipher.getOutputSize(data.Length);

                encoding = new byte[encLen];

                int off = cipher.processBytes(data, 0, data.Length, encoding, 0);

                cipher.doFinal(encoding, off);
            }
            else if (engine is StreamCipher)
            {
                StreamCipher cipher = (StreamCipher)engine;

                cipher.init(true, keyParameters);

                encoding = new byte[data.Length];

                cipher.processBytes(data, 0, data.Length, encoding, 0);
            }

            return encoding;
        }

        public PKCS12Store()
        {
        }

        public PKCS12Store(
            Stream input,
            char[] password)
        {
            if (password == null)
            {
                throw new ArgumentException("No password supplied for PKCS12Store.");
            }

            ASN1InputStream bIn = new ASN1InputStream(input);
            ASN1Sequence obj = (ASN1Sequence)bIn.readObject();
            Pfx bag = new Pfx(obj);
            ContentInfo info = bag.getAuthSafe();
            ArrayList chain = new ArrayList();
            bool unmarkedKey = false;

            if (bag.getMacData() != null)           // check the mac code
            {
                MemoryStream bOut = new MemoryStream();
                BEROutputStream berOut = new BEROutputStream(bOut);
                MacData mData = bag.getMacData();
                DigestInfo dInfo = mData.getMac();
                AlgorithmIdentifier algId = dInfo.getAlgorithmId();
                byte[] salt = mData.getSalt();
                int itCount = mData.getIterationCount().intValue();

                berOut.writeObject(info);

                byte[] data = ((ASN1OctetString)info.getContent()).getOctets();

                try
                {
                    ASN1Encodable parameters = PBEUtil.generateAlgorithmParameters(algId.getObjectId(), mData.getSalt(), mData.getIterationCount().intValue());
                    CipherParameters keyParameters = PBEUtil.generateCipherParameters(algId.getObjectId(), password, parameters);
                    Mac mac = (Mac)PBEUtil.createEngine(algId.getObjectId());

                    mac.init(keyParameters);

                    mac.update(data, 0, data.Length);

                    byte[] res = new byte[mac.getMacSize()];

                    mac.doFinal(res, 0);

                    byte[] dig = dInfo.getDigest();

                    if (res.Length != dig.Length)
                    {
                        throw new Exception("PKCS12 key store mac invalid - wrong password or corrupted file.");
                    }

                    for (int i = 0; i != res.Length; i++)
                    {
                        if (res[i] != dig[i])
                        {
                            throw new Exception("PKCS12 key store mac invalid - wrong password or corrupted file.");
                        }
                    }
                }
                catch (Exception e)
                {
                    throw new Exception("error constructing MAC: " + e.Message);
                }
            }

            keys = new Hashtable();
            localIds = new Hashtable();

            if (info.getContentType().Equals(PKCSObjectIdentifiers.data))
            {
                bIn = new ASN1InputStream(new MemoryStream(((ASN1OctetString)info.getContent()).getOctets()));

                AuthenticatedSafe authSafe = new AuthenticatedSafe((ASN1Sequence)bIn.readObject());
                ContentInfo[] c = authSafe.getContentInfo();

                for (int i = 0; i != c.Length; i++)
                {
                    if (c[i].getContentType().Equals(PKCSObjectIdentifiers.data))
                    {
                        ASN1InputStream dIn = new ASN1InputStream(new MemoryStream(((ASN1OctetString)c[i].getContent()).getOctets()));
                        ASN1Sequence seq = (ASN1Sequence)dIn.readObject();

                        for (int j = 0; j != seq.size(); j++)
                        {
                            SafeBag b = new SafeBag((ASN1Sequence)seq.getObjectAt(j));
                            if (b.getBagId().Equals(PKCSObjectIdentifiers.pkcs8ShroudedKeyBag))
                            {
                                EncryptedPrivateKeyInfo eIn = EncryptedPrivateKeyInfo.getInstance(b.getBagValue());
                                PrivateKeyInfo          privInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(password, eIn);
                                AsymmetricKeyParameter  privKey = PrivateKeyFactory.CreateKey(privInfo);

                                //
                                // set the attributes on the key
                                //
                                Hashtable attributes = new Hashtable();
                                AsymmetricKeyEntry pkcs12Key = new AsymmetricKeyEntry(privKey, attributes);
                                String alias = null;
                                ASN1OctetString localId = null;

                                if (b.getBagAttributes() != null)
                                {
                                    IEnumerator e = b.getBagAttributes().getObjects();
                                    while (e.MoveNext())
                                    {
                                        ASN1Sequence sq = (ASN1Sequence)e.Current;
                                        DERObjectIdentifier aOid = (DERObjectIdentifier)sq.getObjectAt(0);
                                        ASN1Set attrSet = (ASN1Set)sq.getObjectAt(1);
                                        ASN1Encodable attr = null;

                                        if (attrSet.size() > 0)
                                        {
                                            attr = attrSet.getObjectAt(0);

                                            attributes.Add(aOid.getId(), attr);
                                        }

                                        if (aOid.Equals(PKCSObjectIdentifiers.pkcs_9_at_friendlyName))
                                        {
                                            alias = ((DERBMPString)attr).getString();
                                            keys.Add(alias, pkcs12Key);
                                        }
                                        else if (aOid.Equals(PKCSObjectIdentifiers.pkcs_9_at_localKeyId))
                                        {
                                            localId = (ASN1OctetString)attr;
                                        }
                                    }
                                }

                                if (localId != null)
                                {
                                    String name = byteArrayToString(Hex.encode(localId.getOctets()));

                                    if (alias == null)
                                    {
                                        keys.Add(name, pkcs12Key);
                                    }
                                    else
                                    {
                                        localIds.Add(alias, name);
                                    }
                                }
                                else
                                {
                                    unmarkedKey = true;
                                    keys.Add("unmarked", privKey);
                                }
                            }
                            else if (b.getBagId().Equals(PKCSObjectIdentifiers.certBag))
                            {
                                chain.Add(b);
                            }
                            else
                            {
                                Console.WriteLine("extra " + b.getBagId());
                                Console.WriteLine("extra " + ASN1Dump.dumpAsString(b));
                            }
                        }
                    }
                    else if (c[i].getContentType().Equals(PKCSObjectIdentifiers.encryptedData))
                    {
                        EncryptedData d = new EncryptedData((ASN1Sequence)c[i].getContent());
                        ASN1Sequence seq = decryptData(d.getEncryptionAlgorithm(), d.getContent().getOctets(), password);

                        for (int j = 0; j != seq.size(); j++)
                        {
                            SafeBag b = new SafeBag((ASN1Sequence)seq.getObjectAt(j));

                            if (b.getBagId().Equals(PKCSObjectIdentifiers.certBag))
                            {
                                chain.Add(b);
                            }
                            else if (b.getBagId().Equals(PKCSObjectIdentifiers.pkcs8ShroudedKeyBag))
                            {
                                EncryptedPrivateKeyInfo eIn = EncryptedPrivateKeyInfo.getInstance(b.getBagValue());
                                PrivateKeyInfo privInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(password, eIn);
                                AsymmetricKeyParameter privKey = PrivateKeyFactory.CreateKey(privInfo);

                                //
                                // set the attributes on the key
                                //
                                Hashtable attributes = new Hashtable();
                                AsymmetricKeyEntry pkcs12Key = new AsymmetricKeyEntry(privKey, attributes);
                                String alias = null;
                                ASN1OctetString localId = null;

                                IEnumerator e = b.getBagAttributes().getObjects();
                                while (e.MoveNext())
                                {
                                    ASN1Sequence sq = (ASN1Sequence)e.Current;
                                    DERObjectIdentifier aOid = (DERObjectIdentifier)sq.getObjectAt(0);
                                    ASN1Set attrSet = (ASN1Set)sq.getObjectAt(1);
                                    ASN1Encodable attr = null;

                                    if (attrSet.size() > 0)
                                    {
                                        attr = attrSet.getObjectAt(0);

                                        attributes.Add(aOid.getId(), attr);
                                    }

                                    if (aOid.Equals(PKCSObjectIdentifiers.pkcs_9_at_friendlyName))
                                    {
                                        alias = ((DERBMPString)attr).getString();
                                        keys.Add(alias, pkcs12Key);
                                    }
                                    else if (aOid.Equals(PKCSObjectIdentifiers.pkcs_9_at_localKeyId))
                                    {
                                        localId = (ASN1OctetString)attr;
                                    }
                                }

                                String name = byteArrayToString(Hex.encode(localId.getOctets()));

                                if (alias == null)
                                {
                                    keys.Add(name, pkcs12Key);
                                }
                                else
                                {
                                    localIds.Add(alias, name);
                                }
                            }
                            else if (b.getBagId().Equals(PKCSObjectIdentifiers.keyBag))
                            {
                                PrivateKeyInfo pIn = PrivateKeyInfo.getInstance(b.getBagValue());
                                AsymmetricKeyParameter privKey = PrivateKeyFactory.CreateKey(pIn);

                                //
                                // set the attributes on the key
                                //
                                String alias = null;
                                ASN1OctetString localId = null;
                                Hashtable attributes = new Hashtable();
                                AsymmetricKeyEntry pkcs12Key = new AsymmetricKeyEntry(privKey, attributes);

                                IEnumerator e = b.getBagAttributes().getObjects();
                                while (e.MoveNext())
                                {
                                    ASN1Sequence sq = (ASN1Sequence)e.Current;
                                    DERObjectIdentifier aOid = (DERObjectIdentifier)sq.getObjectAt(0);
                                    ASN1Set attrSet = (ASN1Set)sq.getObjectAt(1);
                                    ASN1Encodable attr = null;

                                    if (attrSet.size() > 0)
                                    {
                                        attr = attrSet.getObjectAt(0);

                                        attributes.Add(aOid.getId(), attr);
                                    }

                                    if (aOid.Equals(PKCSObjectIdentifiers.pkcs_9_at_friendlyName))
                                    {
                                        alias = ((DERBMPString)attr).getString();
                                        keys.Add(alias, pkcs12Key);
                                    }
                                    else if (aOid.Equals(PKCSObjectIdentifiers.pkcs_9_at_localKeyId))
                                    {
                                        localId = (ASN1OctetString)attr;
                                    }
                                }

                                String name = byteArrayToString(Hex.encode(localId.getOctets()));

                                if (alias == null)
                                {
                                    keys.Add(name, pkcs12Key);
                                }
                                else
                                {
                                    localIds.Add(alias, name);
                                }
                            }
                            else
                            {
                                Console.WriteLine("extra " + b.getBagId());
                                Console.WriteLine("extra " + ASN1Dump.dumpAsString(b));
                            }
                        }
                    }
                    else
                    {
                        Console.WriteLine("extra " + c[i].getContentType().getId());
                        Console.WriteLine("extra " + ASN1Dump.dumpAsString(c[i].getContent()));
                    }
                }
            }

            certs = new Hashtable();
            chainCerts = new Hashtable();
            keyCerts = new Hashtable();

            for (int i = 0; i != chain.Count; i++)
            {
                SafeBag b = (SafeBag)chain[i];
                CertBag cb = new CertBag((ASN1Sequence)b.getBagValue());
                X509Certificate cert = new X509Certificate(((ASN1OctetString)cb.getCertValue()).getOctets());

                //
                // set the attributes
                //
                Hashtable attributes = new Hashtable();
                X509CertificateEntry pkcs12cert = new X509CertificateEntry(cert, attributes);
                ASN1OctetString localId = null;
                String alias = null;

                if (b.getBagAttributes() != null)
                {
                    IEnumerator e = b.getBagAttributes().getObjects();
                    while (e.MoveNext())
                    {
                        ASN1Sequence sq = (ASN1Sequence)e.Current;
                        DERObjectIdentifier aOid = (DERObjectIdentifier)sq.getObjectAt(0);
                        ASN1Set attrSet = (ASN1Set)sq.getObjectAt(1);

                        if (attrSet.size() > 0)
                        {
                            ASN1Encodable attr = attrSet.getObjectAt(0);

                            attributes.Add(aOid.getId(), attr);

                            if (aOid.Equals(PKCSObjectIdentifiers.pkcs_9_at_friendlyName))
                            {
                                alias = ((DERBMPString)attr).getString();
                            }
                            else if (aOid.Equals(PKCSObjectIdentifiers.pkcs_9_at_localKeyId))
                            {
                                localId = (ASN1OctetString)attr;
                            }
                        }
                    }
                }

                chainCerts.Add(new CertId(cert.getPublicKey()), pkcs12cert);

                if (unmarkedKey)
                {
                    if (keyCerts.Count == 0)
                    {
                        String name = byteArrayToString(Hex.encode(new SubjectKeyIdentifier(SubjectPublicKeyInfoFactory.CreateSubjectPublicKeyInfo(cert.getPublicKey())).getKeyIdentifier()));

                        keyCerts.Add(name, pkcs12cert);
                        keys.Add(name, keys["unmarked"]);

                        keys.Remove("unmarked");
                    }
                }
                else
                {
                    if (alias == null)
                    {
                        if (localId != null)
                        {
                            String name = byteArrayToString(Hex.encode(localId.getOctets()));

                            keyCerts.Add(name, pkcs12cert);
                        }
                    }
                    else
                    {
                        certs.Add(alias, pkcs12cert);
                    }
                }
            }
        }

        public AsymmetricKeyEntry getKey(
            String alias)
        {
            if (alias == null)
            {
                throw new ArgumentException("null alias passed to getKey.");
            }

            return (AsymmetricKeyEntry)keys[alias];
        }

        public bool isCertificateEntry(
            String alias) 
        {
            return (certs[alias] != null && keys[alias] == null);
        }

        public bool isKeyEntry(
            String alias) 
        {
            return (keys[alias] != null);
        }

        public IEnumerator aliases()
        {
            Hashtable tab = new Hashtable();

            IEnumerator e = certs.Keys.GetEnumerator();
            while (e.MoveNext())
            {
                tab.Add(e.Current, "cert");
            }

            e = keys.Keys.GetEnumerator();
            while (e.MoveNext())
            {
                String a = (String)e.Current;
                if (tab[a] == null)
                {
                    tab.Add(a, "key");
                }
            }

            return tab.Keys.GetEnumerator();
        }

        /**
         * simply return the cert entry for the private key
         */
        public X509CertificateEntry getCertificate(
            String alias)
        {
            if (alias == null)
            {
                throw new ArgumentException("null alias passed to getCertificate.");
            }

            X509CertificateEntry c = (X509CertificateEntry)certs[alias];

            //
            // look up the key table - and try the local key id
            //
            if (c == null)
            {
                String id = (String)localIds[alias];
                if (id != null)
                {
                    c = (X509CertificateEntry)keyCerts[id];
                }
                else
                {
                    c = (X509CertificateEntry)keyCerts[alias];
                }
            }

            return c;
        }

        public String getCertificateAlias(
            X509Certificate cert)
        {
            IEnumerator k = certs.Keys.GetEnumerator();

            while (k.MoveNext())
            {
                X509CertificateEntry tc = (X509CertificateEntry)certs[k.Current];
                String ta = (String)k.Current;

                if (tc.getCertificate().Equals(cert))
                {
                    return ta;
                }
            }

            return null;
        }

        public X509CertificateEntry[] getCertificateChain(
            String alias)
        {
            if (alias == null)
            {
                throw new ArgumentException("null alias passed to getCertificateChain.");
            }

            X509CertificateEntry c = getCertificate(alias);

            if (c != null)
            {
                ArrayList cs = new ArrayList();

                while (c != null)
                {
                    X509Certificate x509c = c.getCertificate();
                    X509CertificateEntry nextC = null;

                    X509Extension ext = x509c.getExtensionValue(X509Extensions.AuthorityKeyIdentifier.getId());
                    if (ext != null)
                    {
                        ASN1InputStream aIn = new ASN1InputStream(new MemoryStream(ext.getValue().getOctets()));

                        AuthorityKeyIdentifier id = new AuthorityKeyIdentifier((ASN1Sequence)aIn.readObject());
                        if (id.getKeyIdentifier() != null)
                        {
                            nextC = (X509CertificateEntry)chainCerts[new CertId(id.getKeyIdentifier())];
                        }
                    }

                    if (nextC == null)
                    {
                        //
                        // no authority key id, try the Issuer DN
                        //
                        X509Name i = x509c.getIssuerDN();
                        X509Name s = x509c.getSubjectDN();

                        if (!i.Equals(s))
                        {
                            IEnumerator e = chainCerts.Keys.GetEnumerator();

                            while (e.MoveNext())
                            {
                                X509Certificate crt = ((X509CertificateEntry)chainCerts[e.Current]).getCertificate();
                                X509Name sub = crt.getSubjectDN();
                                if (sub.Equals(i))
                                {
                                    try
                                    {
                                        x509c.verify(crt.getPublicKey());
                                        nextC = ((X509CertificateEntry)chainCerts[e.Current]);
                                        break;
                                    }
                                    catch
                                    {
                                        // continue
                                    }
                                }
                            }
                        }
                    }

                    cs.Add(c);
                    if (nextC != c)     // self signed - end of the chain
                    {
                        c = nextC;
                    }
                    else
                    {
                        c = null;
                    }
                }

                X509CertificateEntry[] certChain = new X509CertificateEntry[cs.Count];

                for (int i = 0; i != certChain.Length; i++)
                {
                    certChain[i] = (X509CertificateEntry)cs[i];
                }

                return certChain;
            }

            return null;
        }

        public void setCertificateEntry(
            String               alias,
            X509CertificateEntry certEntry) 
        {
            if (certs[alias] != null)
            {
                throw new ArgumentException("There is already a certificate with the name " + alias + ".");
            }
    
            certs.Add(alias, certEntry);
            chainCerts.Add(new CertId(certEntry.getCertificate().getPublicKey()), certEntry);
        }

        public void setKeyEntry(
            String                   alias,
            AsymmetricKeyEntry       keyEntry,
            X509CertificateEntry[]   chain) 
        {
            if (keyEntry.getKey().isPrivate() && (chain == null))
            {
                throw new ArgumentException("No certificate chain for private key");
            }
    
            if (keys[alias] != null && !keyEntry.getKey().Equals(keys[alias]))
            {
                throw new ArgumentException("There is already a key with the name " + alias + ".");
            }
    
            keys.Add(alias, keyEntry);
            certs.Add(alias, chain[0]);
    
            for (int i = 0; i != chain.Length; i++)
            {
                chainCerts.Add(new CertId(chain[i].getCertificate().getPublicKey()), chain[i]);
            }
        }

        public void deleteEntry(
            String  alias) 
        {
            if (alias == null)
            {
                throw new ArgumentException("attempt to pass null to deleteEntry");
            }

            AsymmetricKeyEntry k = (AsymmetricKeyEntry)keys[alias];
            if (k != null)
            {
                keys.Remove(alias);
            }

            X509CertificateEntry c = (X509CertificateEntry)certs[alias];

            if (c != null)
            {
                certs.Remove(alias);
                chainCerts.Remove(new CertId(c.getCertificate().getPublicKey()));
            }

            if (k != null)
            {
                String  id = (String)localIds[alias];
                if (id != null)
                {
                    localIds.Remove(alias);
                    c = (X509CertificateEntry)keyCerts[id];
                }
                if (c != null)
                {
                    keyCerts.Remove(id);
                    chainCerts.Remove(new CertId(c.getCertificate().getPublicKey()));
                }
            }

            if (c == null && k == null)
            {
                throw new ArgumentException("no such entry as " + alias);
            }
        }

        public int size() 
        {
            Hashtable  tab = new Hashtable();
    
            IEnumerator e = certs.Keys.GetEnumerator();
            while (e.MoveNext())
            {
                tab.Add(e.Current, "cert");
            }
    
            e = keys.Keys.GetEnumerator();
            while (e.MoveNext())
            {
                String  a = (String)e.Current;
                if (tab[a] == null)
                {
                    tab.Add(a, "key");
                }
            }
    
            return tab.Count;
        }
    
        public void save(Stream stream, char[] password, SecureRandom random) 
        {
            if (password == null)
            {
                throw new ArgumentException("No password supplied for PKCS12Store.");
            }
    
            ContentInfo[]   c = new ContentInfo[2];
    
    
            //
            // handle the key
            //
            ASN1EncodableVector  keyS = new ASN1EncodableVector();
    
    
            IEnumerator ks = keys.Keys.GetEnumerator();
    
            while (ks.MoveNext())
            {
                byte[]                  kSalt = new byte[saltSize];
    
                random.nextBytes(kSalt);
    
                String                  name = (String)ks.Current;
                AsymmetricKeyEntry      privKey = (AsymmetricKeyEntry)keys[name];
                EncryptedPrivateKeyInfo kInfo = EncryptedPrivateKeyInfoFactory.createEncryptedPrivateKeyInfo(keyAlgorithm, password, kSalt, minIterations, privKey.getKey());
                ASN1EncodableVector     kName = new ASN1EncodableVector();

                IEnumerator e = privKey.getBagAttributeKeys();
    
                while (e.MoveNext())
                {
                    String oid = (String)e.Current;
                    ASN1EncodableVector  kSeq = new ASN1EncodableVector();

                    kSeq.add(new DERObjectIdentifier(oid));
                    kSeq.add(new DERSet(privKey.getBagAttribute(new DERObjectIdentifier(oid))));

                    kName.add(new DERSequence(kSeq));
                }
    
                //
                // make sure we have a local key-id
                //
                if (privKey.getBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId) == null)
                {
                    ASN1EncodableVector     kSeq = new ASN1EncodableVector();
                    X509CertificateEntry    ct = getCertificate(name);

                    kSeq.add(PKCSObjectIdentifiers.pkcs_9_at_localKeyId);
                    kSeq.add(new DERSet(new SubjectKeyIdentifier(SubjectPublicKeyInfoFactory.CreateSubjectPublicKeyInfo(ct.getCertificate().getPublicKey()))));
    
                    kName.add(new DERSequence(kSeq));
                }

                //
                // make sure we are using the local alias on store
                //
                DERBMPString nm = (DERBMPString)privKey.getBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName);
                if (nm == null || !nm.getString().Equals(name))
                {
                    ASN1EncodableVector kSeq = new ASN1EncodableVector();
    
                    kSeq.add(PKCSObjectIdentifiers.pkcs_9_at_friendlyName);
                    kSeq.add(new DERSet(new DERBMPString(name)));
    
                    kName.add(new DERSequence(kSeq));
                }

                SafeBag kBag = new SafeBag(PKCSObjectIdentifiers.pkcs8ShroudedKeyBag, kInfo.toASN1Object(), new DERSet(kName));
                keyS.add(kBag);
            }
    
            MemoryStream      bOut = new MemoryStream();
            DEROutputStream   dOut = new DEROutputStream(bOut);
    
            dOut.writeObject(new DERSequence(keyS));
    
            BEROctetString          keyString = new BEROctetString(bOut.ToArray());
    
            //
            // certficate processing
            //
            byte[]                  cSalt = new byte[saltSize];
    
            random.nextBytes(cSalt);
    
            ASN1EncodableVector  certSeq = new ASN1EncodableVector();
            PKCS12PBEParams         cParams = new PKCS12PBEParams(cSalt, minIterations);
            AlgorithmIdentifier     cAlgId = new AlgorithmIdentifier(certAlgorithm, cParams.toASN1Object());
            Hashtable               doneCerts = new Hashtable();
    
            IEnumerator cs = keys.Keys.GetEnumerator();
            while (cs.MoveNext())
            {
                try
                {
                    String              name = (String)cs.Current;
                    X509CertificateEntry         cert = getCertificate(name);
                    CertBag             cBag = new CertBag(
                                            PKCSObjectIdentifiers.x509certType,
                                            new DEROctetString(cert.getCertificate().getEncoded()));
                    ASN1EncodableVector fName = new ASN1EncodableVector();

                    IEnumerator e = cert.getBagAttributeKeys();

                    while (e.MoveNext())
                    {
                        String oid = (String)e.Current;
                        ASN1EncodableVector fSeq = new ASN1EncodableVector();

                        fSeq.add(new DERObjectIdentifier(oid));
                        fSeq.add(new DERSet(cert.getBagAttribute(new DERObjectIdentifier(oid))));
                        fName.add(new DERSequence(fSeq));

                    }

                    //
                    // make sure we are using the local alias on store
                    //
                    DERBMPString nm = (DERBMPString)cert.getBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName);
                    if (nm == null || !nm.getString().Equals(name))
                    {
                        ASN1EncodableVector fSeq = new ASN1EncodableVector();

                        fSeq.add(PKCSObjectIdentifiers.pkcs_9_at_friendlyName);
                        fSeq.add(new DERSet(new DERBMPString(name)));

                        fName.add(new DERSequence(fSeq));
                    }

                    //
                    // make sure we have a local key-id
                    //
                    if (cert.getBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId) == null)
                    {
                        ASN1EncodableVector fSeq = new ASN1EncodableVector();

                        fSeq.add(PKCSObjectIdentifiers.pkcs_9_at_localKeyId);
                        fSeq.add(new DERSet(new SubjectKeyIdentifier(SubjectPublicKeyInfoFactory.CreateSubjectPublicKeyInfo(cert.getCertificate().getPublicKey()))));
                        fName.add(new DERSequence(fSeq));
                    }
    
                    SafeBag sBag = new SafeBag(PKCSObjectIdentifiers.certBag, cBag.toASN1Object(), new DERSet(fName));
    
                    certSeq.add(sBag);
    
                    doneCerts.Add(cert, cert);
                }
                catch (Exception e)
                {
                    throw new Exception("Error encoding certificate: " + e.Message);
                }
            }
    
            cs = certs.Keys.GetEnumerator();
            while (cs.MoveNext())
            {
                try
                {
                    String               certId = (String)cs.Current;
                    X509CertificateEntry cert = (X509CertificateEntry)certs[certId];
    
                    if (keys[certId] != null)
                    {
                        continue;
                    }
    
                    CertBag             cBag = new CertBag(
                                            PKCSObjectIdentifiers.x509certType,
                                            new DEROctetString(cert.getCertificate().getEncoded()));
                    ASN1EncodableVector fName = new ASN1EncodableVector();
                    IEnumerator e = cert.getBagAttributeKeys();

                    while (e.MoveNext())
                    {
                        String oid = (String)e.Current;
                        ASN1EncodableVector fSeq = new ASN1EncodableVector();

                        fSeq.add(new DERObjectIdentifier(oid));
                        fSeq.add(new DERSet(cert.getBagAttribute(new DERObjectIdentifier(oid))));
                        fName.add(new DERSequence(fSeq));
                    }

                    //
                    // make sure we are using the local alias on store
                    //
                    DERBMPString nm = (DERBMPString)cert.getBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName);
                    if (nm == null || !nm.getString().Equals(certId))
                    {
                        ASN1EncodableVector  fSeq = new ASN1EncodableVector();

                        fSeq.add(PKCSObjectIdentifiers.pkcs_9_at_friendlyName);
                        fSeq.add(new DERSet(new DERBMPString(certId)));
    
                        fName.add(new DERSequence(fSeq));
                    }

                    SafeBag sBag = new SafeBag(PKCSObjectIdentifiers.certBag, cBag.toASN1Object(), new DERSet(fName));

                    certSeq.add(sBag);
    
                    doneCerts.Add(cert, cert);
                }
                catch (Exception e)
                {
                    throw new Exception("Error encoding certificate: " + e.Message);
                }
            }
    
            cs = chainCerts.Keys.GetEnumerator();
            while (cs.MoveNext())
            {
                try
                {
                    CertId              certId = (CertId)cs.Current;
                    X509CertificateEntry         cert = (X509CertificateEntry)chainCerts[certId];
    
                    if (doneCerts[cert] != null)
                    {
                        continue;
                    }
    
                    CertBag             cBag = new CertBag(
                                            PKCSObjectIdentifiers.x509certType,
                                            new DEROctetString(cert.getCertificate().getEncoded()));
                    ASN1EncodableVector fName = new ASN1EncodableVector();
    
                    IEnumerator e = cert.getBagAttributeKeys();

                    while (e.MoveNext())
                    {
                        DERObjectIdentifier oid = (DERObjectIdentifier)e.Current;
                        ASN1EncodableVector fSeq = new ASN1EncodableVector();

                        fSeq.add(oid);
                        fSeq.add(new DERSet(cert.getBagAttribute(oid)));
                        fName.add(new DERSequence(fSeq));
                    }
    
                    SafeBag sBag = new SafeBag(PKCSObjectIdentifiers.certBag, cBag.toASN1Object(), new DERSet(fName));
    
                    certSeq.add(sBag);
                }
                catch (Exception e)
                {
                    throw new Exception("Error encoding certificate: " + e.Message);
                }
            }

            bOut = new MemoryStream();

            dOut = new DEROutputStream(bOut);
    
            dOut.writeObject(new DERSequence(certSeq));
    
            dOut.Close();
    
            byte[]                  certBytes = encryptData(new AlgorithmIdentifier(certAlgorithm, cParams), bOut.ToArray(), password);
            EncryptedData           cInfo = new EncryptedData(PKCSObjectIdentifiers.data, cAlgId, new BEROctetString(certBytes));

            c[0] = new ContentInfo(PKCSObjectIdentifiers.data, keyString);

            c[1] = new ContentInfo(PKCSObjectIdentifiers.encryptedData, cInfo.toASN1Object());

            AuthenticatedSafe   auth = new AuthenticatedSafe(c);

            bOut = new MemoryStream();

            BEROutputStream         berOut = new BEROutputStream(bOut);
    
            berOut.writeObject(auth);
    
            byte[]              pkg = bOut.ToArray();
    
            ContentInfo         mainInfo = new ContentInfo(PKCSObjectIdentifiers.data, new BEROctetString(pkg));
    
            //
            // create the mac
            //
            byte[]              mSalt = new byte[20];
            int                 itCount = minIterations;
    
            random.nextBytes(mSalt);
        
            byte[]  data = ((ASN1OctetString)mainInfo.getContent()).getOctets();
    
            MacData                 mData = null;
    
            try
            {
                ASN1Encodable parameters = PBEUtil.generateAlgorithmParameters(OIWObjectIdentifiers.id_SHA1, mSalt, itCount);
                CipherParameters keyParameters = PBEUtil.generateCipherParameters(OIWObjectIdentifiers.id_SHA1, password, parameters);
                Mac mac = (Mac)PBEUtil.createEngine(OIWObjectIdentifiers.id_SHA1);

                mac.init(keyParameters);

                mac.update(data, 0, data.Length);

                byte[] res = new byte[mac.getMacSize()];

                mac.doFinal(res, 0);

                AlgorithmIdentifier     algId = new AlgorithmIdentifier(OIWObjectIdentifiers.id_SHA1, new DERNull());
                DigestInfo              dInfo = new DigestInfo(algId, res);
    
                mData = new MacData(dInfo, mSalt, itCount);
            }
            catch (Exception e)
            {
                throw new Exception("error constructing MAC: " + e.Message);
            }
            
            //
            // output the Pfx
            //
            Pfx                 pfx = new Pfx(mainInfo, mData);
    
            berOut = new BEROutputStream(stream);
    
            berOut.writeObject(pfx);
        }
    }
}
