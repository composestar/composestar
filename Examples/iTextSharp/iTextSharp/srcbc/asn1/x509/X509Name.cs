using org.bouncycastle.asn1.pkcs;

using System;
using System.Collections;
using System.Text;

namespace org.bouncycastle.asn1.x509
{
    public class X509Name
        : ASN1Encodable
    {
        /**
         * country code - StringType(SIZE(2))
         */
        public static readonly DERObjectIdentifier C = new DERObjectIdentifier("2.5.4.6");
    
        /**
         * organization - StringType(SIZE(1..64))
         */
        public static readonly DERObjectIdentifier O = new DERObjectIdentifier("2.5.4.10");
    
        /**
         * organizational unit name - StringType(SIZE(1..64))
         */
        public static readonly DERObjectIdentifier OU = new DERObjectIdentifier("2.5.4.11");
    
        /**
         * Title
         */
        public static readonly DERObjectIdentifier T = new DERObjectIdentifier("2.5.4.12");
    
        /**
         * common name - StringType(SIZE(1..64))
         */
        public static readonly DERObjectIdentifier CN = new DERObjectIdentifier("2.5.4.3");
    
        /**
         * device serial number name - StringType(SIZE(1..64))
         */
        public static readonly DERObjectIdentifier SN = new DERObjectIdentifier("2.5.4.5");
    
        /**
         * locality name - StringType(SIZE(1..64))
         */
        public static readonly DERObjectIdentifier L = new DERObjectIdentifier("2.5.4.7");
    
        /**
         * state, or province name - StringType(SIZE(1..64))
         */
        public static readonly DERObjectIdentifier ST = new DERObjectIdentifier("2.5.4.8");
    
        /**
         * Naming attributes of type X520name
         */
        public static readonly DERObjectIdentifier SURNAME = new DERObjectIdentifier("2.5.4.4");
        public static readonly DERObjectIdentifier GIVENNAME = new DERObjectIdentifier("2.5.4.42");
        public static readonly DERObjectIdentifier INITIALS = new DERObjectIdentifier("2.5.4.43");
        public static readonly DERObjectIdentifier GENERATION = new DERObjectIdentifier("2.5.4.44");
        public static readonly DERObjectIdentifier UNIQUE_IDENTIFIER = new DERObjectIdentifier("2.5.4.45");
    
        /**
         * Email address (RSA PKCS#9 extension) - IA5String.
         * <p>Note: if you're trying to be ultra orthodox, don't use this! It shouldn't be in here.
         */
        public static readonly DERObjectIdentifier EmailAddress = PKCSObjectIdentifiers.pkcs_9_at_emailAddress;
        
        /**
         * more from PKCS#9
         */
        public static readonly DERObjectIdentifier UnstructuredName = PKCSObjectIdentifiers.pkcs_9_at_unstructuredName;
        public static readonly DERObjectIdentifier UnstructuredAddress = PKCSObjectIdentifiers.pkcs_9_at_unstructuredAddress;
        
        /**
         * email address in Verisign certificates
         */
        public static readonly DERObjectIdentifier E = EmailAddress;
        
        /*
         * others...
         */
        public static readonly DERObjectIdentifier DC = new DERObjectIdentifier("0.9.2342.19200300.100.1.25");
    
        /**
         * LDAP User id.
         */
        public static readonly DERObjectIdentifier UID = new DERObjectIdentifier("0.9.2342.19200300.100.1.1");
    
        /**
         * look up table translating OID values into their common symbols - this static is scheduled for deletion
         */
        public static Hashtable OIDLookUp = new Hashtable();
    
        /**
         * determines whether or not strings should be processed and printed
         * from back to front.
         */
        public static bool DefaultReverse = false;
    
        /**
         * default look up table translating OID values into their common symbols following
         * the convention in RFC 2253 with a few extras
         */
        public static Hashtable DefaultSymbols = OIDLookUp;
    
        /**
         * look up table translating OID values into their common symbols following the convention in RFC 2253
         * with a few extras
         */
        public static Hashtable RFC2253Symbols = new Hashtable();
    
        /**
         * look up table translating string values into their OIDS -
         * this static is scheduled for deletion
         */
        public static Hashtable SymbolLookUp = new Hashtable();
    
        /**
         * look up table translating common symbols into their OIDS.
         */
        public static Hashtable DefaultLookUp = SymbolLookUp;
    
        static X509Name()
        {
            DefaultSymbols.Add(C, "C");
            DefaultSymbols.Add(O, "O");
            DefaultSymbols.Add(T, "T");
            DefaultSymbols.Add(OU, "OU");
            DefaultSymbols.Add(CN, "CN");
            DefaultSymbols.Add(L, "L");
            DefaultSymbols.Add(ST, "ST");
            DefaultSymbols.Add(SN, "SN");
            DefaultSymbols.Add(EmailAddress, "E");
            DefaultSymbols.Add(DC, "DC");
            DefaultSymbols.Add(UID, "UID");
            DefaultSymbols.Add(SURNAME, "SURNAME");
            DefaultSymbols.Add(GIVENNAME, "GIVENNAME");
            DefaultSymbols.Add(INITIALS, "INITIALS");
            DefaultSymbols.Add(GENERATION, "GENERATION");
            DefaultSymbols.Add(UnstructuredAddress, "unstructuredAddress");
            DefaultSymbols.Add(UnstructuredName, "unstructuredName");
    
            RFC2253Symbols.Add(C, "C");
            RFC2253Symbols.Add(O, "O");
            RFC2253Symbols.Add(T, "T");
            RFC2253Symbols.Add(OU, "OU");
            RFC2253Symbols.Add(CN, "CN");
            RFC2253Symbols.Add(L, "L");
            RFC2253Symbols.Add(ST, "ST");
            RFC2253Symbols.Add(SN, "SN");
            RFC2253Symbols.Add(EmailAddress, "EMAILADDRESS");
            RFC2253Symbols.Add(DC, "DC");
            RFC2253Symbols.Add(UID, "UID");
            RFC2253Symbols.Add(SURNAME, "SURNAME");
            RFC2253Symbols.Add(GIVENNAME, "GIVENNAME");
            RFC2253Symbols.Add(INITIALS, "INITIALS");
            RFC2253Symbols.Add(GENERATION, "GENERATION");
    
            DefaultLookUp.Add("c", C);
            DefaultLookUp.Add("o", O);
            DefaultLookUp.Add("t", T);
            DefaultLookUp.Add("ou", OU);
            DefaultLookUp.Add("cn", CN);
            DefaultLookUp.Add("l", L);
            DefaultLookUp.Add("st", ST);
            DefaultLookUp.Add("sn", SN);
            DefaultLookUp.Add("emailaddress", E);
            DefaultLookUp.Add("dc", DC);
            DefaultLookUp.Add("e", E);
            DefaultLookUp.Add("uid", UID);
            DefaultLookUp.Add("surname", SURNAME);
            DefaultLookUp.Add("givenname", GIVENNAME);
            DefaultLookUp.Add("initials", INITIALS);
            DefaultLookUp.Add("generation", GENERATION);
            DefaultLookUp.Add("unstructuredaddress", UnstructuredAddress);
            DefaultLookUp.Add("unstructuredname", UnstructuredName);
        }
    
        private X509NameEntryConverter  converter = null;
        private ArrayList                  ordering = new ArrayList();
        private ArrayList                  values = new ArrayList();
        private ArrayList                  added = new ArrayList();
        
        private ASN1Sequence            seq;
    
        public static X509Name getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static X509Name getInstance(
            object  obj)
        {
            if (obj == null || obj is X509Name)
            {
                return (X509Name)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new X509Name((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        /**
         * Constructor from ASN1Sequence
         *
         * the principal will be a list of constructed sets, each containing an (OID, String) pair.
         */
        public X509Name(
            ASN1Sequence  seq)
        {
            this.seq = seq;
    
            IEnumerator e = seq.getObjects();
    
            while (e.MoveNext())
            {
                ASN1Set         set = (ASN1Set)e.Current;
    
                for (int i = 0; i < set.size(); i++) 
                {
                       ASN1Sequence s = (ASN1Sequence)set.getObjectAt(i);
                       
                       ordering.Add(s.getObjectAt(0));
                       values.Add(((DERString) s.getObjectAt(1)).getString());
                       added.Add(i != 0);
                }
            }
        }
    
        /**
         * constructor from a table of attributes.
         * <p>
         * it's is assumed the table contains OID/String pairs, and the contents
         * of the table are copied into an internal table as part of the 
         * construction process.
         * <p>
         * <b>Note:</b> if the name you are trying to generate should be
         * following a specific ordering, you should use the constructor
         * with the ordering specified below.
         */
        public X509Name(
            Hashtable  attributes)
             : this(null, attributes)
        {
        }
    
        /**
         * Constructor from a table of attributes with ordering.
         * <p>
         * it's is assumed the table contains OID/String pairs, and the contents
         * of the table are copied into an internal table as part of the 
         * construction process. The ordering vector should contain the OIDs
         * in the order they are meant to be encoded or printed in toString.
         */
        public X509Name(
            ArrayList      ordering,
            Hashtable   attributes)
             : this(ordering, attributes, new X509DefaultEntryConverter())
        {
        }
        
        /**
         * Constructor from a table of attributes with ordering.
         * <p>
         * it's is assumed the table contains OID/String pairs, and the contents
         * of the table are copied into an internal table as part of the 
         * construction process. The ordering vector should contain the OIDs
         * in the order they are meant to be encoded or printed in toString.
         * <p>
         * The passed in converter will be used to convert the strings into their
         * ASN.1 counterparts.
         */
        public X509Name(
            ArrayList                      ordering,
            Hashtable                   attributes,
            X509DefaultEntryConverter   converter)
        {
            this.converter = converter;
            
            if (ordering != null)
            {
                for (int i = 0; i != ordering.Count; i++)
                {
                    this.ordering.Add(ordering[i]);
                    this.added.Add(false);
                }
            }
            else
            {
                IEnumerator     e = attributes.Keys.GetEnumerator();
    
                while (e.MoveNext())
                {
                    this.ordering.Add(e.Current);
                    this.added.Add(false);
                }
            }
    
            for (int i = 0; i != this.ordering.Count; i++)
            {
                DERObjectIdentifier     oid = (DERObjectIdentifier)this.ordering[i];
    
                if (attributes[oid] == null)
                {
                    throw new ArgumentException("No attribute for object id - " + oid.getId() + " - passed to distinguished name");
                }
    
                this.values.Add(attributes[oid]); // copy the hash table
            }
        }
    
        /**
         * Takes two vectors one of the oids and the other of the values.
         */
        public X509Name(
            ArrayList  oids,
            ArrayList  values)
             : this(oids, values, new X509DefaultEntryConverter())
        {
        }
        
        /**
         * Takes two vectors one of the oids and the other of the values.
         * <p>
         * The passed in converter will be used to convert the strings into their
         * ASN.1 counterparts.
         */
        public X509Name(
            ArrayList                  oids,
            ArrayList                  values,
            X509NameEntryConverter  converter)
        {
            this.converter = converter;
            
            if (oids.Count != values.Count)
            {
                throw new ArgumentException("oids vector must be same length as values.");
            }
    
            for (int i = 0; i < oids.Count; i++)
            {
                this.ordering.Add(oids[i]);
                this.values.Add(values[i]);
                this.added.Add(false);
            }
        }
    
        /**
         * Takes an X509 dir name as a string of the format "C=AU, ST=Victoria", or
         * some such, converting it into an ordered set of name attributes.
         */
        public X509Name(
            string  dirName)
             : this(DefaultReverse, DefaultLookUp, dirName)
        {
        }
    
        /**
         * Takes an X509 dir name as a string of the format "C=AU, ST=Victoria", or
         * some such, converting it into an ordered set of name attributes with each
         * string value being converted to its associated ASN.1 type using the passed
         * in converter.
         */
        public X509Name(
            string                  dirName,
            X509NameEntryConverter  converter)
             : this(DefaultReverse, DefaultLookUp, dirName, converter)
        {
        }
        
        /**
         * Takes an X509 dir name as a string of the format "C=AU, ST=Victoria", or
         * some such, converting it into an ordered set of name attributes. If reverse
         * is true, create the encoded version of the sequence starting from the
         * last element in the string.
         */
        public X509Name(
            bool reverse,
            string  dirName)
             : this(reverse, DefaultLookUp, dirName)
        {
        }
    
        /**
         * Takes an X509 dir name as a string of the format "C=AU, ST=Victoria", or
         * some such, converting it into an ordered set of name attributes with each
         * string value being converted to its associated ASN.1 type using the passed
         * in converter. If reverse is true the ASN.1 sequence representing the DN will
         * be built by starting at the end of the string, rather than the start.
         */
        public X509Name(
            bool                 reverse,
            string                  dirName,
            X509NameEntryConverter  converter)
             : this(reverse, DefaultLookUp, dirName, converter)
        {
        }
        
        /**
         * Takes an X509 dir name as a string of the format "C=AU, ST=Victoria", or
         * some such, converting it into an ordered set of name attributes. lookUp 
         * should provide a table of lookups, indexed by lowercase only strings and
         * yielding a DERObjectIdentifier, other than that OID. and numeric oids
         * will be processed automatically.
         * <br>
         * If reverse is true, create the encoded version of the sequence
         * starting from the last element in the string.
         * @param reverse true if we should start scanning from the end (RFC 2553).
         * @param lookUp table of names and their oids.
         * @param dirName the X.500 string to be parsed.
         */
        public X509Name(
            bool     reverse,
            Hashtable   lookUp,
            string      dirName)
             : this(reverse, lookUp, dirName, new X509DefaultEntryConverter())
        {
        }
        
        private DERObjectIdentifier decodeOID(
            string      name,
            Hashtable   lookUp)
        {
            if (name.ToUpper().StartsWith("OID."))
            {
                return new DERObjectIdentifier(name.Substring(4));
            }
            else if (name[0] >= '0' && name[0] <= '9')
            {
                return new DERObjectIdentifier(name);
            }
    
            DERObjectIdentifier oid = (DERObjectIdentifier)lookUp[name.ToLower()];
            if (oid == null)
            {
                throw new ArgumentException("Unknown object id - " + name + " - passed to distinguished name");
            }
            
            return oid;
        }
        
        /**
         * Takes an X509 dir name as a string of the format "C=AU, ST=Victoria", or
         * some such, converting it into an ordered set of name attributes. lookUp 
         * should provide a table of lookups, indexed by lowercase only strings and
         * yielding a DERObjectIdentifier, other than that OID. and numeric oids
         * will be processed automatically. The passed in converter is used to convert the
         * string values to the right of each equals sign to their ASN.1 counterparts.
         * <br>
         * @param reverse true if we should start scanning from the end, false otherwise.
         * @param lookUp table of names and oids.
         * @param dirName the string dirName
         * @param converter the converter to convert string values into their ASN.1 equivalents
         */
        public X509Name(
            bool                 reverse,
            Hashtable               lookUp,
            string                  dirName,
            X509NameEntryConverter  converter)
        {
            this.converter = converter;
            X509NameTokenizer   nTok = new X509NameTokenizer(dirName);
    
            while (nTok.hasMoreTokens())
            {
                string  token = nTok.nextToken();
                int     index = token.IndexOf('=');
    
                if (index == -1)
                {
                    throw new ArgumentException("badly formated directory string");
                }
    
                string              name = token.Substring(0, index);
                string              value = token.Substring(index + 1);
                DERObjectIdentifier oid = decodeOID(name, lookUp);
    
                if (value.IndexOf('+') > 0)
                {
                    X509NameTokenizer   vTok = new X509NameTokenizer(value, '+');
                    
                    this.ordering.Add(oid);
                    this.values.Add(vTok.nextToken());
                    this.added.Add(false);
                    
                    while (vTok.hasMoreTokens())
                    {
                        string  sv = vTok.nextToken();
                        int     ndx = sv.IndexOf('=');
    
                        string  nm = sv.Substring(0, ndx);
                        string  vl = sv.Substring(ndx + 1);
                        this.ordering.Add(decodeOID(nm, lookUp));
                        this.values.Add(vl);
                        this.added.Add(true);
                    }
                }
                else
                {
                    this.ordering.Add(oid);
                    this.values.Add(value);
                    this.added.Add(false);
                }
            }
    
            if (reverse)
            {
                ArrayList  o = new ArrayList();
                ArrayList  v = new ArrayList();
                ArrayList  a = new ArrayList();
    
                for (int i = this.ordering.Count - 1; i >= 0; i--)
                {
                    o.Add(this.ordering[i]);
                    v.Add(this.values[i]);
                    a.Add(this.added[i]);
                }
    
                this.ordering = o;
                this.values = v;
                this.added = a;
            }
        }
    
        /**
         * return a vector of the oids in the name, in the order they were found.
         */
        public ArrayList getOIDs()
        {
            ArrayList  v = new ArrayList();
    
            for (int i = 0; i != ordering.Count; i++)
            {
                v.Add(ordering[i]);
            }
    
            return v;
        }
    
        /**
         * return a vector of the values found in the name, in the order they
         * were found.
         */
        public ArrayList getValues()
        {
            ArrayList  v = new ArrayList();
    
            for (int i = 0; i != values.Count; i++)
            {
                v.Add(values[i]);
            }
    
            return v;
        }
    
        public override ASN1Object toASN1Object()
        {
            if (seq == null)
            {
                ASN1EncodableVector  vec = new ASN1EncodableVector();
                ASN1EncodableVector  sVec = new ASN1EncodableVector();
                DERObjectIdentifier  lstOid = null;
                
                for (int i = 0; i != ordering.Count; i++)
                {
                    ASN1EncodableVector     v = new ASN1EncodableVector();
                    DERObjectIdentifier     oid = (DERObjectIdentifier)ordering[i];
    
                    v.add(oid);
    
                    string  str = (string)values[i];
    
                    v.add(converter.getConvertedValue(oid, str));
     
                    if (lstOid == null 
                        || (bool) this.added[i])
                    {
                        sVec.add(new DERSequence(v));
                    }
                    else
                    {
                        vec.add(new DERSet(sVec));
                        sVec = new ASN1EncodableVector();
                        
                        sVec.add(new DERSequence(v));
                    }
                    
                    lstOid = oid;
                }
                
                vec.add(new DERSet(sVec));
                
                seq = new DERSequence(vec);
            }
    
            return seq;
        }
    
        /**
         * @param inOrder if true the order of both X509 names must be the same,
         * as well as the values associated with each element.
         */
        public bool Equals(object _obj, bool inOrder) 
        {
            if (_obj == this)
            {
                return true;
            }
    
            if (!inOrder)
            {
                return this.Equals(_obj);
            }
    
            if (_obj == null || !(_obj is X509Name))
            {
                return false;
            }
            
            X509Name _oxn          = (X509Name)_obj;
            int      _orderingSize = ordering.Count;
    
            if (_orderingSize != _oxn.ordering.Count) 
            {
                return false;
            }
            
            for(int i = 0; i < _orderingSize; i++) 
            {
                string  _oid   = ((DERObjectIdentifier)ordering[i]).getId();
                string  _val   = (string)values[i];
                
                string _oOID = ((DERObjectIdentifier)_oxn.ordering[i]).getId();
                string _oVal = (string)_oxn.values[i];
    
                if (_oid.Equals(_oOID))
                {
                    _val = _val.Trim().ToLower();
                    _oVal = _oVal.Trim().ToLower();
                    if (_val.Equals(_oVal))
                    {
                        continue;
                    }
                    else
                    {
                        StringBuilder    v1 = new StringBuilder();
                        StringBuilder    v2 = new StringBuilder();
    
                        if (_val.Length != 0)
                        {
                            char    c1 = _val[0];
    
                            v1.Append(c1);
    
                            for (int k = 1; k < _val.Length; k++)
                            {
                                char    c2 = _val[k];
                                if (!(c1 == ' ' && c2 == ' '))
                                {
                                    v1.Append(c2);
                                }
                                c1 = c2;
                            }
                        }
    
                        if (_oVal.Length != 0)
                        {
                            char    c1 = _oVal[0];
    
                            v2.Append(c1);
    
                            for (int k = 1; k < _oVal.Length; k++)
                            {
                                char    c2 = _oVal[k];
                                if (!(c1 == ' ' && c2 == ' '))
                                {
                                    v2.Append(c2);
                                }
                                c1 = c2;
                            }
                        }
    
                        if (!v1.ToString().Equals(v2.ToString()))
                        {
                            return false;
                        }
                    }
                }
                else
                {
                    return false;
                }
            }
    
            return true;
        }
    
        /**
         * test for equality - note: case is ignored.
         */
        public override bool Equals(object _obj) 
        {
            if (_obj == this)
            {
                return true;
            }
    
            if (_obj == null || !(_obj is X509Name))
            {
                return false;
            }
            
            X509Name _oxn          = (X509Name)_obj;
            
            if (this.toASN1Object().Equals(_oxn.toASN1Object()))
            {
                return true;
            }
            
            int      _orderingSize = ordering.Count;
    
            if (_orderingSize != _oxn.ordering.Count) 
            {
                return false;
            }
            
            bool[] _indexes = new bool[_orderingSize];
    
            for(int i = 0; i < _orderingSize; i++) 
            {
                bool _found = false;
                string  _oid   = ((DERObjectIdentifier)ordering[i]).getId();
                string  _val   = (string)values[i];
                
                for(int j = 0; j < _orderingSize; j++) 
                {
                    if(_indexes[j] == true)
                    {
                        continue;
                    }
                    
                    string _oOID = ((DERObjectIdentifier)_oxn.ordering[j]).getId();
                    string _oVal = (string)_oxn.values[j];
    
                    if (_oid.Equals(_oOID))
                    {
                        _val = _val.Trim().ToLower();
                        _oVal = _oVal.Trim().ToLower();
                        if (_val.Equals(_oVal))
                        {
                            _indexes[j] = true;
                            _found      = true;
                            break;
                        }
                        else
                        {
                            StringBuilder    v1 = new StringBuilder();
                            StringBuilder    v2 = new StringBuilder();
    
                            if (_val.Length != 0)
                            {
                                char    c1 = _val[0];
    
                                v1.Append(c1);
    
                                for (int k = 1; k < _val.Length; k++)
                                {
                                    char    c2 = _val[k];
                                    if (!(c1 == ' ' && c2 == ' '))
                                    {
                                        v1.Append(c2);
                                    }
                                    c1 = c2;
                                }
                            }
    
                            if (_oVal.Length != 0)
                            {
                                char    c1 = _oVal[0];
    
                                v2.Append(c1);
    
                                for (int k = 1; k < _oVal.Length; k++)
                                {
                                    char    c2 = _oVal[k];
                                    if (!(c1 == ' ' && c2 == ' '))
                                    {
                                        v2.Append(c2);
                                    }
                                    c1 = c2;
                                }
                            }
    
                            if (v1.ToString().Equals(v2.ToString()))
                            {
                                _indexes[j] = true;
                                _found      = true;
                                break;
                            }
                        }
                    }
                }
    
                if(!_found)
                {
                    return false;
                }
            }
            
            return true;
        }
        
        public override int GetHashCode()
        {
            ASN1Sequence  seq = (ASN1Sequence)this.toASN1Object();
            IEnumerator   e = seq.getObjects();
            int           hashCode = 0;
    
            while (e.MoveNext())
            {
                hashCode ^= e.Current.GetHashCode();
            }
    
            return hashCode;
        }
    
        private void appendValue(
            StringBuilder        buf,
            Hashtable           oidSymbols,
            DERObjectIdentifier oid,
            string              value)
        {
            string  sym = (string)oidSymbols[oid];
    
            if (sym != null)
            {
                buf.Append(sym);
            }
            else
            {
                buf.Append(oid.getId());
            }
    
            buf.Append("=");
    
            int     index = buf.Length;
    
            buf.Append(value);
    
            int     end = buf.Length;
    
            while (index != end)
            {
                if ((buf[index] == ',')
                    || (buf[index] == '"')
                    || (buf[index] == '\\')
                    || (buf[index] == '+')
                    || (buf[index] == '<')
                    || (buf[index] == '>')
                    || (buf[index] == ';'))
                {
                    buf.Insert(index, "\\");
                    index++;
                    end++;
                }
    
                index++;
            }
        }
    
        /**
         * convert the structure to a string - if reverse is true the
         * oids and values are listed out starting with the last element
         * in the sequence (ala RFC 2253), otherwise the string will begin
         * with the first element of the structure. If no string definition
         * for the oid is found in oidSymbols the string value of the oid is
         * added. Two standard symbol tables are provided DefaultSymbols, and
         * RFC2253Symbols as part of this class.
         *
         * @param reverse if true start at the end of the sequence and work back.
         * @param oidSymbols look up table strings for oids.
         */
        public string ToString(
            bool     reverse,
            Hashtable   oidSymbols)
        {
            StringBuilder            buf = new StringBuilder();
            bool                 first = true;
    
            if (reverse)
            {
                for (int i = ordering.Count - 1; i >= 0; i--)
                {
                    if (first)
                    {
                        first = false;
                    }
                    else
                    {
                        if ((bool)added[i + 1])
                        {
                            buf.Append("+");
                        }
                        else
                        {
                            buf.Append(",");
                        }
                    }
    
                    appendValue(buf, oidSymbols, 
                                (DERObjectIdentifier)ordering[i],
                                (string)values[i]);
                }
            }
            else
            {
                for (int i = 0; i < ordering.Count; i++)
                {
                    if (first)
                    {
                        first = false;
                    }
                    else
                    {
                        if ((bool)added[i])
                        {
                            buf.Append("+");
                        }
                        else
                        {
                            buf.Append(",");
                        }
                    }
    
                    appendValue(buf, oidSymbols, 
                                (DERObjectIdentifier)ordering[i],
                                (string)values[i]);
                }
            }
    
            return buf.ToString();
        }

        /// <summary>
        /// Return the value for a given OID.
        /// </summary>
        /// <param name="ident">The oid of the entry.</param>
        /// <returns>The string representation of the value.</returns>
        public ASN1Object getRDN(DERObjectIdentifier ident)
        {
            int i = ordering.IndexOf(ident);
            if (i == -1) return null;
            return (ASN1Object)values[i];
        }


        /// <summary>
        /// Retrieve oid and value pairs as a hashtable.
        /// </summary>
        /// <returns>Hashtable with name an value pairs.</returns>
        public Hashtable getRDNTable()
        {
            Hashtable ht = new Hashtable();
            for (int t = 0; t < ordering.Count; t++)
            {
                ht[ordering[t]] = values[t];
            }
            return ht;
        }

        /// <summary>
        /// Return an array list containing the object in order.
        /// </summary>
        /// <returns>A array list.</returns>
        public ArrayList getOrdering()
        {
            return ordering;
        }




        public override string ToString()
        {
            return ToString(DefaultReverse, DefaultSymbols);
        }
    }
}
