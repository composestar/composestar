using org.bouncycastle.util.encoders;

using System;
using System.Collections;
using System.Text;

namespace org.bouncycastle.asn1.util
{
    public class ASN1Dump
    {
        private const string  TAB = "    ";
    
        /**
         * dump a DER object as a formatted string with indentation
         *
         * @param obj the ASN1Object to be dumped out.
         */
        public static string _dumpAsString(
            string      indent,
            ASN1Object   obj)
        {
            if (obj is ASN1Sequence)
            {
                StringBuilder    buf = new StringBuilder();
                IEnumerator     e = ((ASN1Sequence)obj).getObjects();
                string          tab = indent + TAB;
    
                buf.Append(indent);
                if (obj is DERSequence)
                {
                    buf.Append("DER Sequence");
                }
                else if (obj is BERSequence)
                {
                    buf.Append("BER Sequence");
                }
                else
                {
                    buf.Append("Sequence");
                }
    
                buf.Append(Environment.NewLine);
    
                while (e.MoveNext())
                {
                    object  o = e.Current;
    
                    if (o == null || o.Equals(new DERNull()))
                    {
                        buf.Append(tab);
                        buf.Append("NULL");
                        buf.Append(Environment.NewLine);
                    }
                    else if (o is ASN1Object)
                    {
                        buf.Append(_dumpAsString(tab, (ASN1Object)o));
                    }
                    else
                    {
                        buf.Append(_dumpAsString(tab, ((ASN1Encodable)o).toASN1Object()));
                    }
                }
                return buf.ToString();
            }
            else if (obj is DERTaggedObject)
            {
                StringBuilder    buf = new StringBuilder();
                string          tab = indent + TAB;
    
                buf.Append(indent);
                if (obj is BERTaggedObject)
                {
                    buf.Append("BER Tagged [");
                }
                else
                {
                    buf.Append("Tagged [");
                }
    
                DERTaggedObject o = (DERTaggedObject)obj;

                buf.Append(((int)o.getTagNo()).ToString());
                buf.Append("]");
    
                if (!o.isExplicit())
                {
                    buf.Append(" IMPLICIT ");
                }
    
                buf.Append(Environment.NewLine);
    
                if (o.isEmpty())
                {
                    buf.Append(tab);
                    buf.Append("EMPTY");
                    buf.Append(Environment.NewLine);
                }
                else
                {
                    buf.Append(_dumpAsString(tab, o.getObject()));
                }
    
                return buf.ToString();
            }
            else if (obj is BERSet)
            {
                StringBuilder    buf = new StringBuilder();
                IEnumerator     e = ((ASN1Set)obj).getObjects();
                string          tab = indent + TAB;
    
                buf.Append(indent);
                buf.Append("BER Set");
                buf.Append(Environment.NewLine);
    
                while (e.MoveNext())
                {
                    object  o = e.Current;
    
                    if (o == null)
                    {
                        buf.Append(tab);
                        buf.Append("NULL");
                        buf.Append(Environment.NewLine);
                    }
                    else if (o is ASN1Object)
                    {
                        buf.Append(_dumpAsString(tab, (ASN1Object)o));
                    }
                    else
                    {
                        buf.Append(_dumpAsString(tab, ((ASN1Encodable)o).toASN1Object()));
                    }
                }
                return buf.ToString();
            }
            else if (obj is DERSet)
            {
                StringBuilder    buf = new StringBuilder();
                IEnumerator     e = ((ASN1Set)obj).getObjects();
                string          tab = indent + TAB;
    
                buf.Append(indent);
                buf.Append("DER Set");
                buf.Append(Environment.NewLine);
    
                while (e.MoveNext())
                {
                    object  o = e.Current;
    
                    if (o == null)
                    {
                        buf.Append(tab);
                        buf.Append("NULL");
                        buf.Append(Environment.NewLine);
                    }
                    else if (o is ASN1Object)
                    {
                        buf.Append(_dumpAsString(tab, (ASN1Object)o));
                    }
                    else
                    {
                        buf.Append(_dumpAsString(tab, ((ASN1Encodable)o).toASN1Object()));
                    }
                }
                return buf.ToString();
            }
            else if (obj is DERObjectIdentifier)
            {
                return indent + "ObjectIdentifier(" + ((DERObjectIdentifier)obj).getId() + ")" + Environment.NewLine;
            }
            else if (obj is DERBoolean)
            {
                return indent + "Boolean(" + ((DERBoolean)obj).isTrue() + ")" + Environment.NewLine;
            }
            else if (obj is DERInteger)
            {
                return indent + "Integer(" + ((DERInteger)obj).getValue() + ")" + Environment.NewLine;
            }
            else if (obj is DEROctetString)
            {
                return indent + obj.ToString() + "[" + ((ASN1OctetString)obj).getOctets().Length + "] " + Environment.NewLine;
            }
            else if (obj is DERIA5String)
            {
                return indent + "IA5String(" + ((DERIA5String)obj).getString() + ") " + Environment.NewLine;
            }
            else if (obj is DERPrintableString)
            {
                return indent + "PrintableString(" + ((DERPrintableString)obj).getString() + ") " + Environment.NewLine;
            }
            else if (obj is DERVisibleString)
            {
                return indent + "VisibleString(" + ((DERVisibleString)obj).getString() + ") " + Environment.NewLine;
            }
            else if (obj is DERBMPString)
            {
                return indent + "BMPString(" + ((DERBMPString)obj).getString() + ") " + Environment.NewLine;
            }
            else if (obj is DERT61String)
            {
                return indent + "T61String(" + ((DERT61String)obj).getString() + ") " + Environment.NewLine;
            }
            else if (obj is DERUTCTime)
            {
                return indent + "UTCTime(" + ((DERUTCTime)obj).getTime() + ") " + Environment.NewLine;
            }
            else if (obj is DERUnknownTag)
            {
                return indent + "Unknown " + ((int)((DERUnknownTag)obj).getTag()).ToString("X") + " "
                    + ByteArray2String(Hex.encode(((DERUnknownTag)obj).getData())) + Environment.NewLine;
            }
            else
            {
                return indent + obj.ToString() + Environment.NewLine;
            }
        }
    
        /**
         * dump out a DER object as a formatted string
         *
         * @param obj the ASN1Object to be dumped out.
         */
        public static string dumpAsString(
            object   obj)
        {
            if (obj is ASN1Object)
            {
                return _dumpAsString("", (ASN1Object)obj);
            }
            else if (obj is ASN1Encodable)
            {
                return _dumpAsString("", ((ASN1Encodable)obj).toASN1Object());
            }
    
            return "unknown object type " + obj.ToString();
        }

        private static string ByteArray2String(byte[] bs)
		{
			StringBuilder s = new StringBuilder();
			for (int i = 0; i < bs.Length; i++) 
			{
				s.Append(Convert.ToChar(bs[i]));
			}
			return s.ToString();
		}

    }
}
