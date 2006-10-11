using org.bouncycastle.asn1;

using System;
using System.Collections;

namespace org.bouncycastle.asn1.x509
{
    public class X509Extensions
        : ASN1Encodable
    {
        /**
         * Subject Key Identifier 
         */
        public static readonly DERObjectIdentifier SubjectKeyIdentifier = new DERObjectIdentifier("2.5.29.14");
    
        /**
         * Key Usage 
         */
        public static readonly DERObjectIdentifier KeyUsage = new DERObjectIdentifier("2.5.29.15");
    
        /**
         * Private Key Usage Period 
         */
        public static readonly DERObjectIdentifier PrivateKeyUsagePeriod = new DERObjectIdentifier("2.5.29.16");
    
        /**
         * Subject Alternative Name 
         */
        public static readonly DERObjectIdentifier SubjectAlternativeName = new DERObjectIdentifier("2.5.29.17");
    
        /**
         * Issuer Alternative Name 
         */
        public static readonly DERObjectIdentifier IssuerAlternativeName = new DERObjectIdentifier("2.5.29.18");
    
        /**
         * Basic Constraints 
         */
        public static readonly DERObjectIdentifier BasicConstraints = new DERObjectIdentifier("2.5.29.19");
    
        /**
         * CRL Number 
         */
        public static readonly DERObjectIdentifier CRLNumber = new DERObjectIdentifier("2.5.29.20");
    
        /**
         * Reason code 
         */
        public static readonly DERObjectIdentifier ReasonCode = new DERObjectIdentifier("2.5.29.21");
    
        /**
         * Hold Instruction Code 
         */
        public static readonly DERObjectIdentifier InstructionCode = new DERObjectIdentifier("2.5.29.23");
    
        /**
         * Invalidity Date 
         */
        public static readonly DERObjectIdentifier InvalidityDate = new DERObjectIdentifier("2.5.29.24");
    
        /**
         * Delta CRL indicator 
         */
        public static readonly DERObjectIdentifier DeltaCRLIndicator = new DERObjectIdentifier("2.5.29.27");
    
        /**
         * Issuing Distribution Point 
         */
        public static readonly DERObjectIdentifier IssuingDistributionPoint = new DERObjectIdentifier("2.5.29.28");
    
        /**
         * Certificate Issuer 
         */
        public static readonly DERObjectIdentifier CertificateIssuer = new DERObjectIdentifier("2.5.29.29");
    
        /**
         * Name Constraints 
         */
        public static readonly DERObjectIdentifier NameConstraints = new DERObjectIdentifier("2.5.29.30");
    
        /**
         * CRL Distribution Points 
         */
        public static readonly DERObjectIdentifier CRLDistributionPoints = new DERObjectIdentifier("2.5.29.31");
    
        /**
         * Certificate Policies 
         */
        public static readonly DERObjectIdentifier CertificatePolicies = new DERObjectIdentifier("2.5.29.32");
    
        /**
         * Policy Mappings 
         */
        public static readonly DERObjectIdentifier PolicyMappings = new DERObjectIdentifier("2.5.29.33");
    
        /**
         * Authority Key Identifier 
         */
        public static readonly DERObjectIdentifier AuthorityKeyIdentifier = new DERObjectIdentifier("2.5.29.35");
    
        /**
         * Policy Constraints 
         */
        public static readonly DERObjectIdentifier PolicyConstraints = new DERObjectIdentifier("2.5.29.36");
    
        /**
         * Extended Key Usage 
         */
        public static readonly DERObjectIdentifier ExtendedKeyUsage = new DERObjectIdentifier("2.5.29.37");
    
        /**
         * Inhibit Any Policy
         */
        public static readonly DERObjectIdentifier InhibitAnyPolicy = new DERObjectIdentifier("2.5.29.54");
    
        /**
         * Authority Info Access
         */
        public static readonly DERObjectIdentifier AuthorityInfoAccess= new DERObjectIdentifier("1.3.6.1.5.5.7.1.1");
    
        private Hashtable               extensions = new Hashtable();
        private ArrayList                  ordering = new ArrayList();
    
        public static X509Extensions getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static X509Extensions getInstance(
            object  obj)
        {
            if (obj == null || obj is X509Extensions)
            {
                return (X509Extensions)obj;
            }
    
            if (obj is ASN1Sequence)
            {
                return new X509Extensions((ASN1Sequence)obj);
            }
    
            if (obj is ASN1TaggedObject)
            {
                return getInstance(((ASN1TaggedObject)obj).getObject());
            }
    
            throw new ArgumentException("illegal object in getInstance: " + obj.GetType().Name);
        }
    
        /**
         * Constructor from ASN1Sequence.
         *
         * the extensions are a list of constructed sequences, either with (OID, OctetString) or (OID, Boolean, OctetString)
         */
        public X509Extensions(
            ASN1Sequence  seq)
        {
            IEnumerator e = seq.getObjects();
    
            while (e.MoveNext())
            {
                ASN1Sequence            s = (ASN1Sequence)e.Current;
    
                if (s.size() == 3)
                {
                    extensions.Add(s.getObjectAt(0), new X509Extension((DERBoolean)s.getObjectAt(1), (ASN1OctetString)s.getObjectAt(2)));
                }
                else
                {
                    extensions.Add(s.getObjectAt(0), new X509Extension(false, (ASN1OctetString)s.getObjectAt(1)));
                }
    
                ordering.Add(s.getObjectAt(0));
            }
        }
    
        /**
         * constructor from a table of extensions.
         * <p>
         * it's is assumed the table contains OID/String pairs.
         */
        public X509Extensions(
            Hashtable  extensions)
             : this(null, extensions)
        {
        }
    
        /**
         * Constructor from a table of extensions with ordering.
         * <p>
         * It's is assumed the table contains OID/String pairs.
         */
        public X509Extensions(
            ArrayList      ordering,
            Hashtable   extensions)
        {
            IEnumerator e;
    
            if (ordering == null)
            {
                e = extensions.Keys.GetEnumerator();
            }
            else
            {
                e = ordering.GetEnumerator();
            }
    
            while (e.MoveNext())
            {
                this.ordering.Add(e.Current); 
            }
    
            e = this.ordering.GetEnumerator();
    
            while (e.MoveNext())
            {
                DERObjectIdentifier     oid = (DERObjectIdentifier)e.Current;
                X509Extension           ext = (X509Extension)extensions[oid];
    
                this.extensions.Add(oid, ext);
            }
        }
    
        /**
         * return an Enumeration of the extension field's object ids.
         */
        public IEnumerator oids()
        {
            return ordering.GetEnumerator();
        }
    
        /**
         * return the extension represented by the object identifier
         * passed in.
         *
         * @return the extension if it's present, null otherwise.
         */
        public X509Extension getExtension(
            DERObjectIdentifier oid)
        {
             return (X509Extension)extensions[oid];
        }
    
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector     vec = new ASN1EncodableVector();
            IEnumerator             e = ordering.GetEnumerator();
    
            while (e.MoveNext())
            {
                DERObjectIdentifier     oid = (DERObjectIdentifier)e.Current;
                X509Extension           ext = (X509Extension)extensions[oid];
                ASN1EncodableVector     v = new ASN1EncodableVector();
    
                v.add(oid);
    
                if (ext.isCritical())
                {
                    v.add(new DERBoolean(true));
                }
    
                v.add(ext.getValue());
    
                vec.add(new DERSequence(v));
            }
    
            return new DERSequence(vec);
        }
    
        public override int GetHashCode()
        {
            IEnumerator     e = extensions.Keys.GetEnumerator();
            int             hashCode = 0;
    
            while (e.MoveNext())
            {
                object  o = e.Current;
    
                hashCode ^= o.GetHashCode();
                hashCode ^= extensions[o].GetHashCode();
            }
    
            return hashCode;
        }
    
        public override bool Equals(
            object o)
        {
            if (o == null || !(o is X509Extensions))
            {
                return false;
            }
    
            X509Extensions  other = (X509Extensions)o;
    
            IEnumerator     e1 = extensions.Keys.GetEnumerator();
            IEnumerator     e2 = other.extensions.Keys.GetEnumerator();
    
            while (e1.MoveNext() && e2.MoveNext())
            {
                object  o1 = e1.Current;
                object  o2 = e2.Current;
                
                if (!o1.Equals(o2))
                {
                    return false;
                }
            }
    
            if (e1.MoveNext() || e2.MoveNext())
            {
                return false;
            }
    
            return true;
        }
    }
}
